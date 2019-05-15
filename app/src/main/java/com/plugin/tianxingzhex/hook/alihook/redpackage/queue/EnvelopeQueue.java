package com.plugin.tianxingzhex.hook.alihook.redpackage.queue;


import com.plugin.tianxingzhex.hook.alihook.redpackage.task.EnvelopeRunnable;

public class EnvelopeQueue extends WheelQueue<EnvelopeRunnable> {
    private static EnvelopeQueue instance;

    private EnvelopeQueue() {
    }

    public synchronized static EnvelopeQueue getInstance() {
        if (instance == null) instance = new EnvelopeQueue();
        return instance;
    }
}