package dev.fvames.rpcserverapi;

public interface IHelloService {

    String sayHello(String content);

    String saveUser(User user);
}
