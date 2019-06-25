package dev.fvames.nio.buffer;

import java.nio.IntBuffer;

/**
 * 缓冲区分片
 * 分片与源数据是同一个引用
 *
 * @version 2019/6/25 20:02
 */

public class BufferSlice {

    public static void main(String[] args) {

        //slice();

        readOnly();
    }

    private static void readOnly() {
        IntBuffer intBuffer = IntBuffer.allocate(10);

        // 缓存数据
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i);
        }

        // 只读
        IntBuffer readOnlyBuffer = intBuffer.asReadOnlyBuffer();

        // 改变源缓冲区内容
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i, intBuffer.get(i) * 10);
        }

        // Exception in thread "main" java.nio.ReadOnlyBufferException
        // readOnlyBuffer.put(2);

        // 复位，打印源缓冲区内容
        readOnlyBuffer.position(0);
        readOnlyBuffer.limit(readOnlyBuffer.capacity());

        while (readOnlyBuffer.hasRemaining()) {
            System.out.println(readOnlyBuffer.get());
        }
    }

    private static void slice() {
        IntBuffer intBuffer = IntBuffer.allocate(10);

        // 缓存数据
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i);
        }

        // 分片
        intBuffer.position(3);
        intBuffer.limit(6);

        IntBuffer sliceBuffer = intBuffer.slice();

        // 改变 slice 缓冲区内容
        for (int i = 0; i < sliceBuffer.capacity(); i++) {
            sliceBuffer.put(i, sliceBuffer.get(i) * 10);
        }

        // 复位，打印源缓冲区内容
        intBuffer.position(0);
        intBuffer.limit(intBuffer.capacity());

        while (intBuffer.remaining() > 0) {
            System.out.println(intBuffer.get());
        }
    }

}
