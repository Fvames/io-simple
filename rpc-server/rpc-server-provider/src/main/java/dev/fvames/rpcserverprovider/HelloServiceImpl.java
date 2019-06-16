package dev.fvames.rpcserverprovider;

import dev.fvames.rpcserverapi.IHelloService;
import dev.fvames.rpcserverapi.User;
import lombok.extern.java.Log;

@Log
public class HelloServiceImpl implements IHelloService {

    @Override
    public String sayHello(String content) {
        log.info("request in sayHello：" + content);
        return "Say Hello：" + content;
    }

    @Override
    public String saveUser(User user) {
        log.info("request in saveUser： " + user);
        return "SUCCESS";
    }
}
