package dev.fvames.nio.buffer;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * IO 映射缓冲区
 * 把缓冲区跟文件系统做一个映射关系， 只要操作缓冲区的内容，文件内容也会跟着改变
 *
 * @version 2019/6/25 20:52
 */

public class MappedBuffer {

    public static final int start = 0;
    public static final int size = 26;

    public static void main(String[] args) throws Exception {

        String outFilePath = "D:\\myCodeSpace\\io-simple\\io\\src\\main\\resources\\output.txt";
        RandomAccessFile randomAccessFile = new RandomAccessFile(outFilePath, "rw");
        FileChannel channel = randomAccessFile.getChannel();

        // 建立关系
        MappedByteBuffer mbb = channel.map(FileChannel.MapMode.READ_WRITE, start, size);

        mbb.put(0, (byte) 97);
        mbb.put(25, (byte) 122);

        channel.close();
    }

}
