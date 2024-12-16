package task4;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FileProcessingSystem {

    enum FileType { XML, JSON, XLS }

    static class File {
        private FileType type;
        private int size;

        public File(FileType type, int size) {
            this.type = type;
            this.size = size;
        }

        public FileType getType() { return type; }
        public int getSize() { return size; }

        @Override
        public String toString() {
            return "File{type=" + type + ", size=" + size + '}';
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<File> queue = new LinkedBlockingQueue<>(5);
        Random random = new Random();

        // Генератор файлов
        Observable<File> fileGenerator = Observable.<File>create(emitter -> {
            try {
                while (!emitter.isDisposed()) {
                    int delay = random.nextInt(901) + 100; // 100–1000 мс
                    Thread.sleep(delay);
                    FileType type = FileType.values()[random.nextInt(FileType.values().length)];
                    int size = random.nextInt(91) + 10; // 10–100
                    File file = new File(type, size);
                    queue.put(file);
                    System.out.println("Сгенерирован: " + file);
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.newThread());

        // Обработчик файлов для каждого типа
        for (FileType fileType : FileType.values()) {
            Observable<File> fileProcessor = Observable.<File>create(emitter -> {
                try {
                    while (!emitter.isDisposed()) {
                        File file = queue.take();
                        if (file.getType() == fileType) {
                            System.out.println("Обработка файла типа " + fileType + ": " + file);
                            Thread.sleep(file.getSize() * 7);
                            System.out.println("Обработан файл типа " + fileType + ": " + file);
                        } else {
                            // Если файл не того типа, возвращаем в очередь
                            queue.put(file);
                        }
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }).subscribeOn(Schedulers.newThread());

            // Запуск обработчика
            fileProcessor.subscribe();
        }

        // Запуск генератора
        fileGenerator.subscribe();

        // Даем системе поработать 15 секунд
        Thread.sleep(15000);
    }
}
