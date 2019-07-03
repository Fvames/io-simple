package dev.fvames.rpc.consumer;

import dev.fvames.rpc.api.IHelloService;

/**
 * @version 2019/7/3 15:33
 */

public class RpcConsumer {

	public static void main(String[] args) {

		IHelloService helloService = RpcProxy.create(IHelloService.class);
		String result = helloService.sayHello("Dibo");
		System.out.println("server result :" + result);
	}
}
