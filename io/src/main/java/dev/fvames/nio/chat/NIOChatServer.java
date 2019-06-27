package dev.fvames.nio.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 聊天室服務端
 * 1.登录时输入用户名，用户名重复提示已存在
 * 2.记录用户名、聊天室人数并广播通知
 * 3.服务端收到客户端消息后通知给其他客户端
 *
 * @version 2019/6/26 16:43
 */

public class NIOChatServer {

    private int port = 8080;
    private Selector selector;
    private String USER_CONTENT_SPLIT = "#@#";
    private Charset charset = Charset.forName("UTF-8");
    // 记录在线人数及人员昵称
    private Set<String> users = new HashSet<>();
    private static String USER_EXIST = "系统提示：该昵称已经存在，请换一个昵称";

    public NIOChatServer(int port) throws IOException {
        this.port = port;

        // serverSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 绑定端口、设置阻塞模式
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);

        // 注册 selector
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.printf("服务已启动，监听的端口为： %s \n", this.port);
    }

    // 开始监听
    public void listen() throws IOException {

        while (true) {

            int wait = selector.select();
            if (wait == 0) {
                continue;
            }

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {

                SelectionKey next = iterator.next();
                iterator.remove();

                process(next);
            }
        }
    }

    // 处理 selectionKey
    private void process(SelectionKey key) throws IOException {

        // 是否接受
        if (key.isAcceptable()) {

            // 获取 channel
            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
            // 接收
            SocketChannel accept = channel.accept();
            // 阻塞模式
            accept.configureBlocking(false);
            // 注册选择器，并设置读模式
            accept.register(selector, SelectionKey.OP_READ);

            // 复位接收其他请求
            key.interestOps(SelectionKey.OP_ACCEPT);
            // 输出提示
            accept.write(charset.encode("请输入你的昵称。"));
        }

        // 读取状态
        if (key.isReadable()) {

            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            StringBuilder sb = new StringBuilder();

            while (socketChannel.read(byteBuffer) > 0) {

                byteBuffer.flip();
                sb.append(charset.decode(byteBuffer));
            }

            key.interestOps(SelectionKey.OP_READ);

            // 关闭通道
            key.cancel();
            if (key.channel() != null) {

                key.channel().close();
            }

            if (sb.length() > 0) {

                String[] arrayContent = sb.toString().split(USER_CONTENT_SPLIT);

                // 首次进入
                if (null != arrayContent && arrayContent.length == 1) {

                    // 重名检查
                    String nickName = arrayContent[0];
                    if (users.contains(nickName)) {

                        socketChannel.write(charset.encode(USER_EXIST));
                    } else {

                        users.add(nickName);
                        long onlineCount = onlineCount();
                        String message = String.format("欢迎 %s 进入xxx，当前在线人数：%s ", nickName, onlineCount);
                        // 通知所有人员
                        broadCast(null, message);
                    }

                } else if (null != arrayContent && arrayContent.length > 1) {

                    // 分发数据
                    String nickName = arrayContent[0];
                    String message = sb.substring(nickName.length() + USER_CONTENT_SPLIT.length());
                    message = String.format("%s 说 %s", nickName, message);

                    if (users.contains(nickName)) {
                        broadCast(socketChannel, message);
                    }
                }
            }
        }
    }

    private void broadCast(SocketChannel client, String message) throws IOException {

        for (SelectionKey key : selector.keys()) {

            Channel channel = key.channel();
            if (channel instanceof SocketChannel && channel != client) {

                ((SocketChannel) channel).write(charset.encode(message));
            }
        }
    }

    // 在线人数
    private long onlineCount() {

        return selector.keys().stream().filter(p -> p.channel() instanceof SocketChannel).count();
    }

    public static void main(String[] args) throws IOException {
        new NIOChatServer(8080).listen();
    }

}
