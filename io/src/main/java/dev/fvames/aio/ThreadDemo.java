package dev.fvames.aio;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by James on 2018/11/1.
 */
public class ThreadDemo extends Thread {


    @Override
    public void run() {
        super.run();
        String[] s = new String[3];
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
    }
}
