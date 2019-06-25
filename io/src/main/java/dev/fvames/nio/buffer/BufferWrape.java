package dev.fvames.nio.buffer;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * 手动分配缓冲区
 *
 * @version 2019/6/25 20:21
 */

public class BufferWrape {

    public static void main(String[] args) {
        // 分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);

        // 包装指定大小的数组
        char[] chars = new char[10];
        CharBuffer wrap = CharBuffer.wrap(chars);

    }

}
