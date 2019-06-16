package dev.fvames.rpcclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean(value = "rpcProxyClient")
    public RpcProxyClient getRpcProxyClient() {
        return new RpcProxyClient();
    }

}
