package task1;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SensorSystem {

    public static void main(String[] args) throws InterruptedException {
        // Создаем генераторы случайных чисел для датчиков
        Random tempRandom = new Random();
        Random co2Random = new Random();

        // Датчик температуры (значения от 15 до 30)
        Observable<Integer> temperatureSensor = Observable.interval(1, TimeUnit.SECONDS)
                .map(tick -> tempRandom.nextInt(16) + 15);

        // Датчик CO2
        Observable<Integer> co2Sensor = Observable.interval(1, TimeUnit.SECONDS)
                .map(tick -> co2Random.nextInt(71) + 30);

        // Сигнализация
        Observable.combineLatest(temperatureSensor, co2Sensor, (temp, co2) -> new int[]{temp, co2})
                .subscribe(new DisposableObserver<int[]>() {
                    @Override
                    public void onNext(int[] readings) {
                        int temp = readings[0];
                        int co2 = readings[1];
                        boolean tempAlert = temp > 25;
                        boolean co2Alert = co2 > 70;

                        if (tempAlert && co2Alert) {
                            System.out.println("ALARM!!! Температура: " + temp + ", CO2: " + co2);
                        } else if (tempAlert || co2Alert) {
                            System.out.print("Предупреждение: ");
                            if (tempAlert) System.out.print("Температура выше нормы (" + temp + ") ");
                            if (co2Alert) System.out.print("CO2 выше нормы (" + co2 + ") ");
                            System.out.println();
                        } else {
                            System.out.println("Все в норме. Температура: " + temp + ", CO2: " + co2);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.err.println("Ошибка: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Система остановлена.");
                    }
                });

        // Даем системе поработать 10 секунд
        Thread.sleep(10000);
    }
}
