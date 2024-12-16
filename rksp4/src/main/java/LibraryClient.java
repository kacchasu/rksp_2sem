import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;

public class LibraryClient {

    public static void main(String[] args) {
        RSocket socket = RSocketConnector.create()
                .connect(TcpClientTransport.create("localhost", 7000))
                .block();

        // Request-Response: Получение книги по ID
        socket.requestResponse(DefaultPayload.create("1"))
                .map(Payload::getDataUtf8)
                .doOnNext(System.out::println)
                .block();

        // Fire-and-Forget: Добавление новой книги
        socket.fireAndForget(DefaultPayload.create("4;The Great Gatsby;F. Scott Fitzgerald"))
                .doOnTerminate(() -> System.out.println("Fire-and-Forget отправлен"))
                .block();

        // Request-Stream: Получение списка всех книг
        socket.requestStream(DefaultPayload.create(""))
                .map(Payload::getDataUtf8)
                .doOnNext(System.out::println)
                .blockLast();

        // Channel: Двусторонняя коммуникация
        Flux<Payload> clientMessages = Flux.just("Привет", "Как дела?", "До свидания")
                .map(DefaultPayload::create);

        socket.requestChannel(clientMessages)
                .map(Payload::getDataUtf8)
                .doOnNext(System.out::println)
                .blockLast();

        socket.dispose();
    }
}
