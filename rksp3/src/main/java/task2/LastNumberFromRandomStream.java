package task2;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import java.util.Random;

public class LastNumberFromRandomStream {

    public static void main(String[] args) {
        Random random = new Random();

        // Генерируем случайное количество элементов от 0 до 1000
        int count = random.nextInt(1001);

        System.out.println("Сгенерировано " + count + " случайных чисел.");

        // Создаем поток из случайного количества случайных чисел
        Observable<Integer> randomNumbersStream = Observable.range(0, count)
                .map(i -> random.nextInt(1000)); // Случайные числа от 0 до 999

        // Получаем последнее число из потока
        randomNumbersStream
                .lastElement() // Возвращает Maybe<Integer>
                .toObservable() // Преобразуем Maybe в Observable для удобства
                .subscribe(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer number) {
                        System.out.println("Последнее число в потоке: " + number);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.err.println("Произошла ошибка: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Обработка завершена.");
                    }
                });
    }
}
