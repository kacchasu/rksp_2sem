package com.example.rksp.practice1.Task1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;


public class Main {
    public static List<Integer> generateArray10000() {
        List<Integer> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            int randomNumber = random.nextInt();
            list.add(randomNumber);
        }
        return list;
    }

    public static int findSum(List<Integer> list) throws InterruptedException {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Список пуст или равен null");
        }
        int sum = 0;
        for (int number : list) {
            Thread.sleep(1);
            sum += number;
        }
        return sum;
    }

    public static int findSumMnogopotok(List<Integer> list) throws InterruptedException, ExecutionException {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Список пуст или равен null");
        }
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Callable<Integer>> tasks = new ArrayList<>();
        int batchSize = list.size() / numberOfThreads;
        for (int i = 0; i < numberOfThreads; i++) {
            final int startIndex = i * batchSize;
            final int endIndex = (i == numberOfThreads - 1) ? list.size() : (i + 1) * batchSize;
            tasks.add(() -> findSumInRange(list.subList(startIndex, endIndex)));
        }
        List<Future<Integer>> futures = executorService.invokeAll(tasks);

        int sum = 0;
        for (Future<Integer> future : futures) {
            int partialSum = future.get();
            Thread.sleep(1);
            sum += partialSum;
        }
        executorService.shutdown();
        return sum;
    }

    private static int findSumInRange(List<Integer> sublist) throws InterruptedException {
        int sum = 0;
        for (int number : sublist) {
            Thread.sleep(1);
            sum += number;
        }
        return sum;
    }

    public static int findSumFork(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Список пуст или равен null");
        }
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        SumFinderTask task = new SumFinderTask(list, 0, list.size());
        return forkJoinPool.invoke(task);
    }

    static class SumFinderTask extends RecursiveTask<Integer> {
        private List<Integer> list;
        private int start;
        private int end;
        SumFinderTask(List<Integer> list, int start, int end) {
            this.list = list;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            if (end - start <= 1000) {
                try {
                    return findSumInRange(list.subList(start, end));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            int middle = start + (end - start) / 2;
            SumFinderTask leftTask = new SumFinderTask(list, start, middle);
            SumFinderTask rightTask = new SumFinderTask(list, middle, end);
            leftTask.fork();
            int rightResult = rightTask.compute();
            int leftResult = leftTask.join();
            return leftResult + rightResult;
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        List<Integer> testList = generateArray10000();
        Runtime runtime = Runtime.getRuntime();

        long startTime = System.nanoTime();
        long startMemory = runtime.totalMemory() - runtime.freeMemory();
        int result = findSum(testList);
        long endMemory = runtime.totalMemory() - runtime.freeMemory();
        long endTime = System.nanoTime();
        long durationInMilliseconds = (endTime - startTime) / 1_000_000;
        long memoryUsed = (endMemory - startMemory);
        System.out.println("Время выполнения последовательной функции: " +
                durationInMilliseconds + " миллисекунд. Результат - " + result);
        System.out.println("Затраты по памяти: " + memoryUsed);

        startTime = System.nanoTime();
        startMemory = runtime.totalMemory() - runtime.freeMemory();
        result = findSumMnogopotok(testList);
        endMemory = runtime.totalMemory() - runtime.freeMemory();
        endTime = System.nanoTime();
        durationInMilliseconds = (endTime - startTime) / 1_000_000;
        memoryUsed = (endMemory - startMemory);
        System.out.println("Время выполнения многопоточной функции: " +
                durationInMilliseconds + " миллисекунд. Результат - " + result);
        System.out.println("Затраты по памяти: " + memoryUsed);

        startTime = System.nanoTime();
        startMemory = runtime.totalMemory() - runtime.freeMemory();
        result = findSumFork(testList);
        endMemory = runtime.totalMemory() - runtime.freeMemory();
        endTime = System.nanoTime();
        durationInMilliseconds = (endTime - startTime) / 1_000_000;
        memoryUsed = (endMemory - startMemory);
        System.out.println("Время выполнения форк функции: " +
                durationInMilliseconds + " миллисекунд. Результат - " + result);
        System.out.println("Затраты по памяти: " + memoryUsed);
    }
}
