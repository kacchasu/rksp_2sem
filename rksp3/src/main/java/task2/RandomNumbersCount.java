package task2;

import io.reactivex.rxjava3.core.Observable;
import java.util.Random;

public class RandomNumbersCount {

    public static void main(String[] args) {
        Random random = new Random();

        // Генерируем случайное количество (от 0 до 1000)
        int count = random.nextInt(1001);

        // Создаем поток из случайного количества случайных чисел
        Observable<Integer> randomNumbers = Observable.range(0, count)
                .map(i -> random.nextInt());

        // Преобразуем поток в поток, содержащий количество чисел
        Observable<Long> countObservable = randomNumbers.count().toObservable();

        countObservable.subscribe(c -> System.out.println("Количество чисел в потоке: " + c));
    }
}
