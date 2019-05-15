package com.plugin.tianxingzhex.hook.alihook.redpackage.queue;

import java.util.ArrayList;
import java.util.HashMap;

public class WheelQueue<T extends Runnable> {
    protected static ArrayList<Runnable> taskStack = new ArrayList<>();
    private static HashMap<Runnable, RunCallback> callbackHashMap = new HashMap<>(1);

    public synchronized void add(T T, RunCallback runCallback) {
        taskStack.add(T);
        if (taskStack.size() == 1) {
            T.run();
            runCallback.run(T);
            return;
        }
        callbackHashMap.put(T, runCallback);
    }

    public synchronized boolean add(T T) {
        taskStack.add(T);
        if (taskStack.size() == 1) {
            T.run();
            return true;
        }
        return false;
    }

    public synchronized void next() {
        if (taskStack.size() == 0) return;
//        taskStack.remove(0);
        if (taskStack.size() > 0) {
            T t = (T) taskStack.get(0);
            t.run();
            RunCallback runCallback = callbackHashMap.get(t);
            if (runCallback != null) {
                runCallback.run(t);
                callbackHashMap.remove(t);
            }
        }
    }

    public synchronized T just() {
        if (taskStack.size() == 0) return null;
        return (T) taskStack.get(0);
    }

    public synchronized T pop() {
        if (taskStack.size() == 0) return null;
        return (T) taskStack.remove(0);
    }

    public interface RunCallback<T extends Runnable> {
        void run(T t);
    }

    public int size() {
        return taskStack.size();
    }
}