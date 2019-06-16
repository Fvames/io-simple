package dev.fvames.rpcclient;

import dev.fvames.rpcserverapi.IHelloService;

public class App {

    public static void main(String[] args) {
        RpcProxyClient proxyClient = new RpcProxyClient();
        IHelloService helloService = proxyClient.clientProxy(IHelloService.class, "localhost", 8080);

        String result = helloService.sayHello("liao 😝");
        System.out.println("result："+result);
    }
}
