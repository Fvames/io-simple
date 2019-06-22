package dev.fvames.nio.channel;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileOutPutDemo {

    public static final byte[] message = {83, 111, 109, 101, 32, 98, 121};

    public static void main(String[] args) throws Exception {
        FileOutputStream fout = new FileOutputStream("/Users/james/IdeaProjects/1.java-code/io-simple/io/src/main/resources/test.txt");

        FileChannel fc = fout.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        for (byte b : message) {
            buffer.put(b);
        }

        buffer.flip();
        fc.write(buffer);

        fout.close();
    }

}
