import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.r2dbc.core.DatabaseClient;

public class BookRepository {

    private final DatabaseClient databaseClient;

    public BookRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<Void> initDatabase() {
        return databaseClient.sql("CREATE TABLE IF NOT EXISTS books (" +
                        "id INT PRIMARY KEY, " +
                        "title VARCHAR(255), " +
                        "author VARCHAR(255)" +
                        ")").then()
                .then(databaseClient.sql("TRUNCATE TABLE books").then())
                .thenMany(
                        Flux.just(
                                new Book(1, "1984", "George Orwell"),
                                new Book(2, "Brave New World", "Aldous Huxley"),
                                new Book(3, "Fahrenheit 451", "Ray Bradbury")
                        ).flatMap(this::save)
                )
                .then();
    }


    public Mono<Void> save(Book book) {
        return databaseClient.sql("INSERT INTO books (id, title, author) VALUES (:id, :title, :author)")
                .bind("id", book.getId())
                .bind("title", book.getTitle())
                .bind("author", book.getAuthor())
                .then();
    }

    public Mono<Book> findById(Integer id) {
        return databaseClient.sql("SELECT * FROM books WHERE id = :id")
                .bind("id", id)
                .map(row -> new Book(
                        row.get("id", Integer.class),
                        row.get("title", String.class),
                        row.get("author", String.class)
                )).one();
    }

    public Flux<Book> findAll() {
        return databaseClient.sql("SELECT * FROM books")
                .map(row -> new Book(
                        row.get("id", Integer.class),
                        row.get("title", String.class),
                        row.get("author", String.class)
                )).all();
    }
}
