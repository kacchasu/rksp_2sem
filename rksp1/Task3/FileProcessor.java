package com.example.rksp.practice1.Task3;

import java.util.concurrent.BlockingQueue;

class FileProcessor implements Runnable {
    private BlockingQueue<File> queue;
    private String allowedFileType;

    public FileProcessor(BlockingQueue<File> queue, String allowedFileType) {
        this.queue = queue;
        this.allowedFileType = allowedFileType;
    }

    @Override
    public void run() {
        while (true) {
            try {
                File file = queue.take(); // Получаем файл из очереди
                if (file.getFileType().equals(allowedFileType)) {
                    long processingTime = file.getFileSize() * 7;
                    Thread.sleep(processingTime);
                    System.out.println("Обработан файл типа " + file.getFileType() +
                            " с размером " + file.getFileSize() + ". Время обработки: " +
                            processingTime + " мс.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
