package com.bwjfstudios.drawlaphone.util;

public class SingletonThread {

    private Thread thread;

    public void startThread(Runnable runnable) {
        if (!this.isAlive()) {
            this.thread = new Thread(runnable);
            thread.start();
        }
    }

    boolean isAlive() {
        return thread != null && this.thread.getState() != Thread.State.TERMINATED;
    }
}