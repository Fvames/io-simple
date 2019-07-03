package dev.fvames.rpc.registry;

import dev.fvames.rpc.protocol.InvokerProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 2019/7/3 14:55
 */

public class RegistoryHandler extends ChannelInboundHandlerAdapter {

	private List<String> classNames = new ArrayList<String>();
	private static ConcurrentHashMap<String, Object> registryMap = new ConcurrentHashMap<String, Object>();

	public RegistoryHandler() {

		scannerClass("dev.fvames.rpc.provider");
		doRegister();
	}

	private void doRegister() {

		if (classNames.size() == 0) {
			return;
		}
		try {
			for (String className : classNames) {

				Class<?> aClass = Class.forName(className);
				Class<?> anInterface = aClass.getInterfaces()[0];

				registryMap.put(anInterface.getName(), aClass.newInstance());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void scannerClass(String packagePath) {

		URL url = this.getClass().getClassLoader().getResource(packagePath.replaceAll("\\.", "/"));
		File dir = new File(url.getFile());
		for (File file : dir.listFiles()) {

			if (file.isDirectory()) {

				scannerClass(packagePath + "." + file.getName());
			} else {

				String classPath = packagePath + "." + file.getName().replace(".class", "").trim();
				classNames.add(classPath);
			}
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		Object result = new Object();
		InvokerProtocol protocol = (InvokerProtocol) msg;
		if (registryMap.containsKey(protocol.getClassName())) {

			Object clazz = registryMap.get(protocol.getClassName());
			Method method = clazz.getClass().getMethod(protocol.getMethodName(), protocol.getParames());
			result = method.invoke(clazz, protocol.getValues());
		}

		ctx.write(result);
		ctx.flush();
		ctx.close();
	}
}
