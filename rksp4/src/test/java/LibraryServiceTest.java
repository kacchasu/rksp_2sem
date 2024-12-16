import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import org.junit.jupiter.api.*;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class LibraryServiceTest {

    private static DatabaseClient databaseClient;
    private static BookRepository bookRepository;
    private static LibraryService libraryService;

    @BeforeAll
    public static void setUp() {
        // Настройка подключения к тестовой базе данных
        PostgresqlConnectionConfiguration config = PostgresqlConnectionConfiguration.builder()
                .host("localhost")
                .port(5432)
                .username("postgres")
                .password("postgres")
                .database("prac4")
                .build();

        PostgresqlConnectionFactory connectionFactory = new PostgresqlConnectionFactory(config);
        databaseClient = DatabaseClient.builder()
                .connectionFactory(connectionFactory)
                .build();

        bookRepository = new BookRepository(databaseClient);
        libraryService = new LibraryService(bookRepository);
    }

    @BeforeEach
    public void initDatabase() {
        // Очистка и инициализация базы данных перед каждым тестом
        databaseClient.sql("DROP TABLE IF EXISTS books").then().block();
        bookRepository.initDatabase().block();
    }

    @AfterEach
    public void cleanUp() {
        // Очистка базы данных после каждого теста
        databaseClient.sql("DROP TABLE IF EXISTS books").then().block();
    }

    @Test
    public void testRequestResponse() {
        Mono<Payload> response = libraryService.requestResponse(DefaultPayload.create("1"));

        StepVerifier.create(response)
                .expectNextMatches(payload -> payload.getDataUtf8().contains("1984"))
                .verifyComplete();
    }

    @Test
    public void testRequestStream() {
        Flux<Payload> response = libraryService.requestStream(DefaultPayload.create(""));

        StepVerifier.create(response)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void testFireAndForget() {
        Mono<Void> response = libraryService.fireAndForget(DefaultPayload.create("5;Moby Dick;Herman Melville"));

        StepVerifier.create(response)
                .verifyComplete();

        // Проверяем, что книга добавлена
        Mono<Book> bookMono = bookRepository.findById(5);
        StepVerifier.create(bookMono)
                .expectNextMatches(book -> book.getTitle().equals("Moby Dick"))
                .verifyComplete();
    }
}
