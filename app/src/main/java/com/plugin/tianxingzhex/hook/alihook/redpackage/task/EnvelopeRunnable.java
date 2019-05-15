package com.plugin.tianxingzhex.hook.alihook.redpackage.task;


import com.plugin.tianxingzhex.hook.alihook.redpackage.EnvelopeOrder;

public abstract class EnvelopeRunnable implements Runnable {
    protected EnvelopeOrder envelopeOrder;

    public EnvelopeRunnable(EnvelopeOrder envelopeOrder) {
        this.envelopeOrder = envelopeOrder;
    }

    public EnvelopeOrder getEnvelopeOrder() {
        return envelopeOrder;
    }
}
