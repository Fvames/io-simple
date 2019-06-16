package dev.fvames.rpcclient;

import java.lang.reflect.Proxy;

public class RpcProxyClient {

    public <T> T clientProxy(final Class<T> tClass, final String host, final int port) {
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(),
                new Class<?>[]{tClass},
                new RemoteInvocationHandler(host, port));
    }

}
