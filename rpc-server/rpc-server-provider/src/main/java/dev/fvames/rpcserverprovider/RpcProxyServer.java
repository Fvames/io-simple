package dev.fvames.rpcserverprovider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcProxyServer {

    ExecutorService executorService = Executors.newFixedThreadPool(10);

    public void publisher(Object service, int port) {
        ServerSocket serverSocket = null;

        try {

            serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                // 交给线程池处理
                executorService.execute(new ProcessorHandler(socket, service));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
