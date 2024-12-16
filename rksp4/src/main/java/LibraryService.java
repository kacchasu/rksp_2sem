import io.rsocket.RSocket;
import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class LibraryService implements RSocket {

    private final BookRepository bookRepository;

    public LibraryService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Request-Response: Получить книгу по ID
    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        int bookId = Integer.parseInt(payload.getDataUtf8());
        return bookRepository.findById(bookId)
                .map(book -> "Книга: " + book.getTitle() + " автор: " + book.getAuthor())
                .map(DefaultPayload::create)
                .doFinally(signal -> System.out.println("Request-Response обработан для книги ID: " + bookId));
    }

    // Fire-and-Forget: Добавить новую книгу
    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        String[] data = payload.getDataUtf8().split(";", 3);
        Book book = new Book(Integer.parseInt(data[0]), data[1], data[2]);
        return bookRepository.save(book)
                .doOnTerminate(() -> System.out.println("Добавлена новая книга: " + book.getTitle()));
    }

    // Request-Stream: Получить список всех книг
    @Override
    public Flux<Payload> requestStream(Payload payload) {
        return bookRepository.findAll()
                .map(book -> "Книга: " + book.getTitle() + " автор: " + book.getAuthor())
                .map(DefaultPayload::create)
                .doFinally(signal -> System.out.println("Request-Stream завершен"));
    }

    // Channel: Двусторонняя коммуникация
    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return Flux.from(payloads)
                .map(Payload::getDataUtf8)
                .map(msg -> "Сервер получил: " + msg)
                .map(DefaultPayload::create)
                .doFinally(signal -> System.out.println("Channel завершен"));
    }

    // Метод metadataPush, если необходим
    @Override
    public Mono<Void> metadataPush(Payload payload) {
        return Mono.empty();
    }

    // Можно также реализовать методы из интерфейса Closeable, если необходимо
    // Но если они не требуются, их можно опустить
}
