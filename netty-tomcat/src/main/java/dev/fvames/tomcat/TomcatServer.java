package dev.fvames.tomcat;

import dev.fvames.tomcat.http.Request;
import dev.fvames.tomcat.http.Response;
import dev.fvames.tomcat.http.Servlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @version 2019/6/28 17:27
 */

public class TomcatServer {

    public static final int port = 8080;
    private Map<String, Servlet> servletMap = new HashMap<>();

    public void init() {

        String path = this.getClass().getResource("/").getPath();
        try {

            FileInputStream inputStream = new FileInputStream(path + "web.properties");
            Properties properties = new Properties();
            properties.load(inputStream);

            for (Object k : properties.keySet()) {

                String key = k.toString();
                if (key.endsWith(".url")) {

                    String url = properties.getProperty(key);
                    String serverName = key.replaceAll("\\.url", "");
                    String className = properties.getProperty(serverName + ".className");
                    Servlet servlet = (Servlet) Class.forName(className).newInstance();

                    servletMap.put(url, servlet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {

        init();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        // 服务
        ServerBootstrap server = new ServerBootstrap();
        // 配置
        server.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                        ch.pipeline().addLast(new HttpResponseEncoder());
                        ch.pipeline().addLast(new HttpRequestDecoder());
                        ch.pipeline().addLast(new TomcatHandler());
                    }
                })
                // 主线程分配线程最大数量
                .option(ChannelOption.SO_BACKLOG, 128)
                // 子线程保持长链接
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        // 启动服务
        try {

            ChannelFuture future = server.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


    private class TomcatHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            if (msg instanceof HttpRequest) {

                HttpRequest req = (HttpRequest) msg;

                Request request = new Request(ctx, req);
                Response response = new Response(ctx);

                String url = request.getUrl();

                if (servletMap.containsKey(url)) {
                    servletMap.get(url).service(request, response);
                } else {
                    response.write("404 - Not Found");
                }
            }
        }
    }

    public static void main(String[] args) {

        new TomcatServer().start();
    }
}
