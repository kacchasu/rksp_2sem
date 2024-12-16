package com.example.rksp.practice2.Task2;

import java.io.*;

import org.apache.commons.io.*;

import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;


@FunctionalInterface
interface Copy {
    public void copy();
}

public class Main {
    private static final String FILE_FROM_PATH = "src/main/resources/source.txt";
    private static final String FILE_TO_PATH = "src/main/resources/destination";

    private static void copyWithFileIOStream() {
        File fileFrom = new File(FILE_FROM_PATH);
        File fileTo = new File(FILE_TO_PATH + "_IO.txt");
        try {
            FileInputStream fromStream = new FileInputStream(fileFrom);
            FileOutputStream toStream = new FileOutputStream(fileTo);

            int buffer = fromStream.read();
            while (buffer != -1) {
                toStream.write(buffer);
                buffer = fromStream.read();
            }
            System.out.println("Файл скопирован 1-м способом");
            fromStream.close();
            toStream.close();
        } catch (Exception e) {
            System.out.println("Произошла ошибка при попытке копировать файл 1-м способом: " + e.getMessage());
        }
    }

    private static void copyWithFileChannel() {
        try {
            RandomAccessFile fromFile = new RandomAccessFile(FILE_FROM_PATH, "rw");
            RandomAccessFile toFile = new RandomAccessFile(FILE_TO_PATH + "_channel.txt", "rw");

            FileChannel fromChannel = fromFile.getChannel();
            FileChannel toChannel = toFile.getChannel();

            toChannel.transferFrom(fromChannel, 0, fromChannel.size());
            fromFile.close();
            toFile.close();
            System.out.println("Файл скопирован 2-м способом");
        } catch (Exception e) {
            System.out.println("Произошла ошибка при попытке копировать файл 2-м способом:" + e.getMessage());
        }
    }

    public static void copyWithApacheCommonsIO() {
        File fileFrom = new File(FILE_FROM_PATH);
        File fileTo = new File(FILE_TO_PATH + "_ApacheIO.txt");
        try {
            FileUtils.copyFile(fileFrom, fileTo);
            System.out.println("Файл скопирован 3-м способом");
        } catch (IOException e) {
            System.out.println("Произошла ошибка при попытке копировать файл 3-м способом: " + e.getMessage());
        }

    }

    private static void copyWithFilesClass() {
        File fileFrom = new File(FILE_FROM_PATH);
        File fileTo = new File(FILE_TO_PATH + "_files.txt");
        try {
            Files.copy(fileFrom.toPath(), fileTo.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Файл скопирован 4-м способом");
        } catch (IOException e) {
            System.out.println("Произошла ошибка при попытке копировать файл 4-м способом: " + e.getMessage());
        }
    }

    private static void calculateTime(Copy copy) {
        Runtime runtime = Runtime.getRuntime();
        LocalTime start = LocalTime.now();

        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        copy.copy();
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();

        Long time = ChronoUnit.MILLIS.between(start, LocalTime.now());

        long memoryUsed = usedMemoryAfter - usedMemoryBefore;

        System.out.println("Время копирования: " + time + " миллисекунд");
        System.out.println("Память использована: " + memoryUsed + " байт");

        runtime.gc();
    }

    public static void main(String[] args) {
        calculateTime(Main::copyWithFileIOStream);
        calculateTime(Main::copyWithFileChannel);
        calculateTime(Main::copyWithApacheCommonsIO);
        calculateTime(Main::copyWithFilesClass);
    }
}