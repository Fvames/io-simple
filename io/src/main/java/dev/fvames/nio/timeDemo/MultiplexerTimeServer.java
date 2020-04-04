package dev.fvames.nio.timeDemo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {

    private static final String command = "QUERY TIME ORDER";
    private Selector selector;
    private ServerSocketChannel channel;
    private volatile boolean stop = false;

    public MultiplexerTimeServer(int i) {

    }

    public void a(int port) {

        float a = 3.4f;
    }

    @Override
    public void run() {

        try {
            while (true) {

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                if (stop) {

                    while (iterator.hasNext()) {

                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();

                        try {

                            handlerInput(selectionKey);
                        } catch (IOException e) {

                            e.printStackTrace();

                            if (selectionKey != null) {
                                selectionKey.cancel();
                                if (selectionKey.channel() != null) {
                                    selectionKey.channel().close();
                                }
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }


        if (selector != null) {
            try {

                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void handlerInput(SelectionKey selectionKey) throws IOException {

        if (!selectionKey.isValid()) {
            return;
        }

        if (selectionKey.isAcceptable()) {

            ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
            SocketChannel accept = channel.accept();
            accept.configureBlocking(false);

            accept.register(selector, SelectionKey.OP_READ);
        }

        if (selectionKey.isReadable()) {

            SocketChannel channel = (SocketChannel) selectionKey.channel();

            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            int readBytes = channel.read(readBuffer);

            if (readBytes > 0) {

                readBuffer.flip();
                byte[] bytes = new byte[readBuffer.remaining()];
                readBuffer.get(bytes);
                String body = new String(bytes, "UTF-8");

                System.out.println("The time server receive order: " + body);

                String currentTime = command.equalsIgnoreCase(body) ?
                        LocalDateTime.now().toString() : "BAD ORDER";

                doWrite(channel, currentTime);
            } else if (readBytes < 0) {

                selectionKey.cancel();
                selector.close();
            }
        }
    }

    private void doWrite(SocketChannel channel, String currentTime) throws IOException {

        if (null == currentTime || "".equals(currentTime)) {
            return;
        }

        byte[] bytes = currentTime.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);

        byteBuffer.put(bytes);
        byteBuffer.flip();

        channel.write(byteBuffer);
    }

}
