package dev.fvames.nio.buffer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 直接缓冲区，零拷贝减少拷贝的过程
 * Java 虚拟机内存储的是缓冲区的引用地址
 *
 * @version 2019/6/25 20:31
 */

public class BufferDirect {

    public static void main(String[] args) throws Exception {

        // 输入
        String inFilePath = "D:\\myCodeSpace\\io-simple\\io\\src\\main\\resources\\test.txt";
        FileInputStream fin = new FileInputStream(inFilePath);
        FileChannel channel = fin.getChannel();

        // 输出
        String outFilePath = "D:\\myCodeSpace\\io-simple\\io\\src\\main\\resources\\output.txt";
        FileOutputStream fout = new FileOutputStream(outFilePath);
        FileChannel outChannel = fout.getChannel();

        // 设置直接缓冲区（byte 支持）
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);

        while (true) {
            // 指定缓冲区不够用
            byteBuffer.clear();

            int tag = channel.read(byteBuffer);
            if (tag == -1) {
                break;
            }

            byteBuffer.flip();
            outChannel.write(byteBuffer);

        }

    }

}
