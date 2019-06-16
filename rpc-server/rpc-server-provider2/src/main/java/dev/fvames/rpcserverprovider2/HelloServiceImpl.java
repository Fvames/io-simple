package dev.fvames.rpcserverprovider2;

import dev.fvames.rpcserverapi.IHelloService;
import dev.fvames.rpcserverapi.User;
import lombok.extern.java.Log;

@Log
@RpcService(value = IHelloService.class, version = "1.0")
public class HelloServiceImpl implements IHelloService {

    @Override
    public String sayHello(String content) {
        log.warning(" sayHello-1.O："+content);
        return "Server SayHello-1.O "+content;
    }

    @Override
    public String saveUser(User user) {
        return "SUCCESS";
    }
}
