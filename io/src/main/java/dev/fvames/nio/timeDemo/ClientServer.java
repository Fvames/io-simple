package dev.fvames.nio.timeDemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ClientServer implements Runnable {

    private int port;
    private Selector selector;
    private SocketChannel channel;
    private String host = "127.0.0.1";
    private static final String command = "QUERY TIME ORDER";

    public ClientServer(int port) {

        this.port = port;

        try {
            selector = Selector.open();
            channel = SocketChannel.open();
            channel.configureBlocking(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // 连接
        try {

            doConnect();

            // 轮询 key
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {

                SelectionKey next = iterator.next();
                iterator.remove();

                try {

                    handlerInput(next);
                } catch (IOException e) {

                    e.printStackTrace();
                    next.cancel();
                    if (next.channel() != null) {
                        next.channel().close();
                    }
                }
            }
        } catch (IOException e) {

            e.printStackTrace();
            System.exit(1);
        }


        if (selector != null) {

            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handlerInput(SelectionKey next) throws IOException {

        // 执行请求，读取消息
        if (!next.isValid()) {

            return;
        }

        SocketChannel channel = (SocketChannel) next.channel();
        if (next.isConnectable()) {

            if (channel.finishConnect()) {

                channel.register(selector, SelectionKey.OP_READ);
                dowrite(channel);
            }
        } else {

            System.exit(1);
        }

        if (next.isReadable()) {

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int read = channel.read(byteBuffer);
            if (read > 0) {

                byteBuffer.flip();
                byte[] bytes = new byte[byteBuffer.remaining()];
                byteBuffer.get(bytes);

                String content = new String(bytes, "UTF-8");
                System.out.println("Now is :" + content);
            } else {

                next.cancel();
                channel.close();
            }
        }
    }

    private void dowrite(SocketChannel channel) throws IOException {
        byte[] bytes = command.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.flip();

        channel.write(byteBuffer);
        if (!byteBuffer.hasRemaining()) {
            System.out.println("Send order 2 server succeed.");
        }
    }

    private void doConnect() throws IOException {

        if (channel.connect(new InetSocketAddress(host, port))) {

            channel.register(selector, SelectionKey.OP_READ);
        } else {

            channel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    public static void main(String[] args) {
        new Thread(new ClientServer(8080)).start();
    }

}
