package com.example.rksp.practice1.Task2;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static ExecutorService executorService = Executors.newFixedThreadPool(5);

    static int getDelay(){
        Random random = new Random();
        return random.nextInt(1, 6) * 1000;
    }

    static int getSquare(int a){
        try {
            Thread.sleep(getDelay());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return a * a;
    }

    static void handleNumber(int a){

        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return getSquare(a);
            }
        };
        Future future = executorService.submit(callable);
        try {
            System.out.println(future.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println("Введите числа, которые надо возвести в квадрат");
        Scanner scanner = new Scanner(System.in);

        while (true){
            String userInput = scanner.nextLine();
            if ("exit".equalsIgnoreCase(userInput)) {
                break;
            }
            int number = Integer.parseInt(userInput);
            handleNumber(number);
        }
        executorService.shutdown();
    }
}