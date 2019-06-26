package dev.fvames.nio.buffer;

import java.io.FileInputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @version 2019/6/25 19:39
 */

public class BufferDemo {

    public static void main(String[] args) throws Exception {
        FileInputStream fin = new FileInputStream("D:\\myCodeSpace\\io-simple\\io\\src\\main\\resources\\test.txt");

        // 获取 channel
        FileChannel channel = fin.getChannel();

        // 获取 buffer 并设置空间
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        printBufferInfo("初始化", byteBuffer);

        // buffer 加载 channel 的内容
        channel.read(byteBuffer);
        printBufferInfo("调用 read 方法", byteBuffer);

        // 获取之前锁定
        byteBuffer.flip();
        printBufferInfo("调用 flip 方法", byteBuffer);


        // 判断是否有数据
        int i = 0;
        while (byteBuffer.remaining() > 0) {
            if (i++ == 2) {
                byteBuffer.mark(); // 对 position 打标记，调用 reset 时，复位到标记位置 2，当调用 flip、remind、clear 时会删除 mark 标记
                printBufferInfo("调用 mark 方法", byteBuffer);
            }


            // 获取数据
            System.out.println((char) byteBuffer.get());
        }

        printBufferInfo("调用 get 方法", byteBuffer);

        // 清除释放 buffer
        byteBuffer.reset(); // 没有调用过 mark 方法时会报错
        printBufferInfo("调用 reset 方法", byteBuffer); // position 复位到 2

        // 清除释放 buffer
        byteBuffer.clear();
        printBufferInfo("调用 clear 方法", byteBuffer);

        // 关闭 channel
        channel.close();
    }

    public static void printBufferInfo(String tag, Buffer buffer) {
        System.out.printf(">>>>>>>> %s \n", tag);

        // 容量 capacity 大小
        System.out.printf(" capacity：%s \n", buffer.capacity());

        // position
        System.out.printf(" position：%s \n", buffer.position());

        // 数据操作范围只能在 position - limit 之间
        System.out.printf(" limit：%s \n", buffer.limit());

    }
}
