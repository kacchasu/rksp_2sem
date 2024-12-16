package com.example.rksp.practice2.Task3;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ControlSum {
    public static short getControlSum(String filePath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        FileChannel fileChannel = fileInputStream.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(2);
        short checksum = 0;
        while (fileChannel.read(buffer) != -1) {
            buffer.flip();
            if (buffer.remaining() == 1) {
                checksum ^= buffer.get();
            } else {
                checksum ^= buffer.getShort();
            }
            buffer.clear();
        }

        return checksum;
    }

    public static void main(String[] args) {
        String filePath = "src/main/resources/source.txt";
        try {
            short checksum = getControlSum(filePath);
            System.out.printf("Контрольная сумма файла %s: 0x%04X%n", filePath, checksum);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}