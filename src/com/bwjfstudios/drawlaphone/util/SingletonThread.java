package com.bwjfstudios.drawlaphone.util;

/**
 * Object that only execute one thread at a time. If it is given a runnable it will run it only if
 * it doesn't have another thread at the time. Used to prevent people from logging in twice or sending
 * two answers at once.
 */
public class SingletonThread {

    private Thread thread; // Thread being accessed

    // Starts a thread if the current thread is dead
    public void startThread(Runnable runnable) {
        if (!this.isAlive()) {
            this.thread = new Thread(runnable);
            thread.start();
        }
    }

    // Determines if the current thread is null or terminated
    boolean isAlive() {
        return thread != null && this.thread.getState() != Thread.State.TERMINATED;
    }
}