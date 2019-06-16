package dev.fvames.rpcserverprovider2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "dev.fvames.rpcserverprovider2")
public class SpringConfig {

    @Bean(name = "rpcProxyServer")
    public RpcProxyServer getRpcServer() {
        return new RpcProxyServer(8080);
    }

}
