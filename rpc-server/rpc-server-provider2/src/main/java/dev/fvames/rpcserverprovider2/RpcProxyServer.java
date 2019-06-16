package dev.fvames.rpcserverprovider2;

import org.omg.PortableServer.POA;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class RpcProxyServer implements ApplicationContextAware, InitializingBean {

    private int port;
    private Map<String, Object> handlerMap = new HashMap<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public RpcProxyServer(int port) {
        this.port = port;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket socket = serverSocket.accept();
            executorService.execute(new ProcessorHandler(socket, handlerMap));
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (!beansMap.isEmpty()) {
            for (Object serverBean : beansMap.values()) {
                RpcService rpcService = serverBean.getClass().getAnnotation(RpcService.class);

                String version = rpcService.version();
                String serverName = rpcService.value().getName();

                if (!StringUtils.isEmpty(version)) {
                    serverName += "-" + version;
                }
                handlerMap.put(serverName, serverBean);
            }
        }

    }
}
