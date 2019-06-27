package dev.fvames.nio.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 客户端
 * 1.支持多人登录
 *
 * @version 2019/6/27 11:09
 */

public class NIOChatClient {

    private Selector selector;
    private String nickName = "";
    private SocketChannel socketChannel;
    private String USER_CONTENT_SPLIT = "#@#";
    private Charset charset = Charset.forName("UTF-8");
    private static String USER_EXIST = "系统提示：该昵称已经存在，请换一个昵称";
    private final InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 8080);

    public NIOChatClient() throws IOException {
        selector = Selector.open();

        socketChannel = SocketChannel.open(inetSocketAddress);
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);

    }

    public void session() {
        new Read().start();
        new Write().start();
    }

    private class Read extends Thread {

        @Override
        public void run() {

            while (true) {
                try {

                    int select = selector.select();
                    if (select == 0) {
                        continue;
                    }

                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {

                        SelectionKey next = iterator.next();
                        iterator.remove();

                        process(next);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void process(SelectionKey next) throws IOException {
        if (next.isReadable()) {

            SocketChannel channel = (SocketChannel) next.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            String content = "";
            while (channel.read(byteBuffer) > 0) {

                byteBuffer.flip();
                content += charset.decode(byteBuffer);
            }

            if (USER_EXIST.equals(content)) {
                nickName = "";
            }

            System.out.println(content);

            next.interestOps(SelectionKey.OP_READ);
        }
    }

    private class Write extends Thread {

        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {

                String nextLine = scanner.nextLine();
                if (null == nextLine || "".equals(nextLine)) {

                    System.out.println("请输入用户名");
                    continue;
                }
                if (nickName == "") {

                    nickName = nextLine;
                    nextLine = nickName + USER_CONTENT_SPLIT;
                } else {

                    nextLine = nickName + USER_CONTENT_SPLIT + nextLine;
                }

                try {
                    socketChannel.write(charset.encode(nextLine));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            scanner.close();
        }
    }

    public static void main(String[] args) throws IOException {

        new NIOChatClient().session();
    }
}
