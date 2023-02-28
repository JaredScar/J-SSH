package com.j_ssh.model.managers;

import lombok.Getter;

import java.util.ArrayList;

public class AsyncManager {
    private static AsyncManager asyncManager = new AsyncManager();
    public static AsyncManager get() {
        ArrayList<Thread> removeThread = new ArrayList<>();
        for (Thread thread : asyncManager.activeThreads) {
            if (thread.getState() == Thread.State.TERMINATED)
                removeThread.add(thread);
        }
        for (Thread thread : removeThread) {
            asyncManager.activeThreads.remove(thread);
        }
        return asyncManager;
    }
    @Getter
    private ArrayList<Thread> activeThreads = new ArrayList<>();

    public void addThread(Thread thread) {
        this.activeThreads.add(thread);
        thread.start();
    }
}
