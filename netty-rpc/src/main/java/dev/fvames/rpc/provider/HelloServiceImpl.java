package dev.fvames.rpc.provider;

import dev.fvames.rpc.api.IHelloService;

/**
 * @version 2019/7/3 14:34
 */

public class HelloServiceImpl implements IHelloService {

	public String sayHello(String userName) {
		System.out.println(">>> sayHello method");
		return String.format(" %s say Hello World.", userName);
	}
}
