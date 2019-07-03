package dev.fvames.rpc.consumer;

import dev.fvames.rpc.protocol.InvokerProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @version 2019/7/3 15:35
 */

public class RpcProxy {


	public static <T> T create(Class<T> obj) {

		MethodProxy methodProxy = new MethodProxy(obj);
		Class<?>[] interfaces = obj.isInterface() ? new Class[]{obj} : obj.getInterfaces();
		T result = (T) Proxy.newProxyInstance(obj.getClassLoader(), interfaces, methodProxy);

		return result;
	}

	private static class MethodProxy implements InvocationHandler {

		private Class<?> clazz;

		public <T> MethodProxy(Class<T> obj) {
			this.clazz = obj;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (Object.class.equals(method.getDeclaringClass())) {

				return method.invoke(this, args);
			} else {

				return rpcInvoke(proxy, method, args);
			}
		}

		private Object rpcInvoke(Object proxy, Method method, Object[] args) {

			InvokerProtocol protocol = new InvokerProtocol();
			protocol.setClassName(this.clazz.getName());
			protocol.setMethodName(method.getName());
			protocol.setParames(method.getParameterTypes());
			protocol.setValues(args);

			final RpcProxyHandler consumerHandler = new RpcProxyHandler();
			EventLoopGroup group = new NioEventLoopGroup();

			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group)
					.channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {

							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast("frameDecoder",
									new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
							pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
							pipeline.addLast("encoder", new ObjectEncoder());
							pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
							pipeline.addLast("handler", consumerHandler);
						}
					});

			try {

				ChannelFuture future = bootstrap.connect("localhost", 8080).sync();
				future.channel().writeAndFlush(protocol).sync();
				future.channel().closeFuture().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				group.shutdownGracefully();
			}

			return consumerHandler.getResponse();
		}
	}
}
