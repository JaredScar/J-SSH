package com.j_ssh.model.managers;

import lombok.Getter;

import java.util.ArrayList;

public class AsyncManager {
    private static AsyncManager asyncManager = new AsyncManager();
    public static AsyncManager get() {
        return asyncManager;
    }
    @Getter
    private ArrayList<Thread> activeThreads = new ArrayList<>();

    public void addThread(Thread thread) {
        this.activeThreads.add(thread);
        thread.start();
    }
}
