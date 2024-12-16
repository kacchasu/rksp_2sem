package com.example.rksp.practice2.Task4;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import org.apache.commons.collections4.CollectionUtils;  // Для subtract


public class Main {
    private static final String DIRECTORY_PATH = "src/main/resources/task2.4";

    private static Map<String, List<String>> files;

    private static void addFile(String path) {
        Path file = Paths.get(path);
        try {
            if (Files.exists(file) && !path.endsWith("~")) {  // Игнорируем временные файлы
                files.put(path, Files.readAllLines(file));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printChanges(String path) {
        Path file = Paths.get(path);
        try {
            if (files.containsKey(path)) {
                List<String> oldContent = files.get(path);
                List<String> newContent = Files.readAllLines(file);

                for (String elem : CollectionUtils.subtract(newContent, oldContent)) {
                    System.out.println("Добавлена строка: " + elem);
                }

                for (String elem : CollectionUtils.subtract(oldContent, newContent)) {
                    System.out.println("Удалена строка: " + elem);
                }
            } else {
                System.out.println("Ранее файл не отслеживался, чтобы были показаны изменения.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        files = new HashMap<>();

        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            WatchKey key = Paths.get(DIRECTORY_PATH)
                    .register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    String filePath = DIRECTORY_PATH + "\\" + event.context().toString();
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        System.out.println("Создан: " + event.context());
                        addFile(filePath);
                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        System.out.println("Изменен: " + event.context());
                        if (files.containsKey(filePath)) {
                            printChanges(filePath);
                        } else {
                            System.out.println("Ранее файл не отслеживался, чтобы были показаны изменения.");
                        }
                        addFile(filePath);
                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                        System.out.println("Удален: " + event.context());
                        files.remove(filePath);
                    }
                }
                key.reset();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
