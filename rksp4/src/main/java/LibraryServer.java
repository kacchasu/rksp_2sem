import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

public class LibraryServer {

    public static void main(String[] args) {
        // Настройка подключения к PostgreSQL
        PostgresqlConnectionConfiguration config = PostgresqlConnectionConfiguration.builder()
                .host("localhost")
                .port(5432)
                .username("postgres")
                .password("postgres")
                .database("prac4")
                .build();

        PostgresqlConnectionFactory connectionFactory = new PostgresqlConnectionFactory(config);
        DatabaseClient databaseClient = DatabaseClient.builder()
                .connectionFactory(connectionFactory)
                .build();

        BookRepository bookRepository = new BookRepository(databaseClient);
        bookRepository.initDatabase().block();

        // Запуск RSocket сервера
        RSocketServer.create((setup, sendingSocket) -> Mono.just(new LibraryService(bookRepository)))
                .bindNow(TcpServerTransport.create("localhost", 7000))
                .onClose()
                .block();
    }
}
