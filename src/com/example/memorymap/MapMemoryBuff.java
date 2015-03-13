package com.example.memorymap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MapMemoryBuff {

    static String srcFile = "test.txt";
    static String destFile = "outFile.txt";

    public static void main(String[] args) {
        writeFileByMappedByteBuffer();
        writeFileByBuffer();
        writeFileByBufferedInputOutputStream();
    }

    public static void forTest() {
        ByteBuffer byteBuf = ByteBuffer.allocate(1024 * 14 * 1024);
        byte[] bbb = new byte[14 * 1024 * 1024];
        File f = new File(srcFile);
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel fc = null;
        try {
            fis = new FileInputStream(f);
            fos = new FileOutputStream(destFile);
            fc = fis.getChannel();

            System.out.println("File length = " + f.length());
            long timeStar = System.currentTimeMillis();// 得到当前的时间
            // fc.read(byteBuf);// 1 读取
            MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, f.length());
            long timeEnd = System.currentTimeMillis();// 得到当前的时间
            System.out.println("Read time :" + (timeEnd - timeStar) + "ms");

            timeStar = System.currentTimeMillis();
            // fos.write(bbb);// 2 写入
            mbb.flip();
            timeEnd = System.currentTimeMillis();
            System.out.println("Write time :" + (timeEnd - timeStar) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.flush();
                fc.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void writeFileByMappedByteBuffer() {
        RandomAccessFile raInputFile = null;
        RandomAccessFile raOutputFile = null;

        FileChannel fInputChannel = null;
        FileChannel fOutputChannel = null;

        deleteFile(destFile);
        try {
            raInputFile = new RandomAccessFile(srcFile, "r");
            raOutputFile = new RandomAccessFile(destFile, "rw");

            fInputChannel = raInputFile.getChannel();
            fOutputChannel = raOutputFile.getChannel();

            long size = fInputChannel.size();
            MappedByteBuffer mbbi = fInputChannel.map(FileChannel.MapMode.READ_ONLY, 0, size);
            MappedByteBuffer mbbo = fOutputChannel.map(FileChannel.MapMode.READ_WRITE, 0, size);

            long start = System.currentTimeMillis();
            for (int i = 0; i < size; i++) {
                byte b = mbbi.get(i);
                mbbo.put(i, b);
            }
            System.out.println("Spend1: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (raInputFile != null) {
                    raInputFile.close();
                }
                if (raOutputFile != null) {
                    raOutputFile.close();
                }
                if (fInputChannel != null) {
                    fInputChannel.close();
                }

                if (fOutputChannel != null) {
                    fOutputChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFileByBuffer() {
        RandomAccessFile rafi = null;
        RandomAccessFile rafo = null;

        deleteFile(destFile);
        try {
            rafi = new RandomAccessFile(srcFile, "r");
            rafo = new RandomAccessFile(destFile, "rw");
            byte[] buf = new byte[1024 * 8];
            long start = System.currentTimeMillis();
            int c = rafi.read(buf);
            while (c > 0) {
                if (c == buf.length) {
                    rafo.write(buf);
                } else {
                    rafo.write(buf, 0, c);
                }
                c = rafi.read(buf);
            }
            System.out.println("Spend2: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rafi != null) {
                    rafi.close();
                }
                if (rafo != null) {
                    rafo.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void writeFileByBufferedInputOutputStream() {
        FileInputStream rafi = null;
        FileOutputStream rafo = null;

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        deleteFile(destFile);
        try {
            rafi = new FileInputStream(srcFile);
            rafo = new FileOutputStream(destFile);

            bis = new BufferedInputStream(rafi, 8192);
            bos = new BufferedOutputStream(rafo, 8192);
            long size = rafi.available();

            long start = System.currentTimeMillis();

            for (int i = 0; i < size; i++) {
                byte b = (byte) bis.read();
                bos.write(b);
            }
            System.out.println("Spend3: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");
        } catch (Exception e) {
        } finally {
            try {
                if (rafi != null) {
                    rafi.close();
                }
                if (rafo != null) {
                    rafo.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteFile(final String fileName) {
        if (fileName.isEmpty()) {
            return;
        }
        try {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
        }
    }
}