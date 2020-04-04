package dev.fvames.nio.timeDemo;

public class TimeServer {

    public static void main(String[] args) {

        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(8080);
        new Thread(timeServer).start();
    }

}
