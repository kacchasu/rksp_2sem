package task2;

import io.reactivex.rxjava3.core.Observable;
import java.util.Arrays;
import java.util.Random;

public class ParallelStreams {

    public static void main(String[] args) {
        Random random = new Random();

        // Первый поток из 1000 случайных цифр
        Observable<Integer> stream1 = Observable.range(0, 1000)
                .map(i -> random.nextInt(10));

        // Второй поток из 1000 случайных цифр
        Observable<Integer> stream2 = Observable.range(0, 1000)
                .map(i -> random.nextInt(10));

        // Объединяем потоки, интерливируя элементы
        Observable<Integer> mergedStream = Observable.zip(
                stream1,
                stream2,
                (num1, num2) -> Arrays.asList(num1, num2)
        ).flatMapIterable(nums -> nums);

        // Выводим результат
        mergedStream.subscribe(num -> System.out.print(num + " "));
    }
}
