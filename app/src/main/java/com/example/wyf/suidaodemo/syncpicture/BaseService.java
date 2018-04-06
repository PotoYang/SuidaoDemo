package com.example.wyf.suidaodemo.syncpicture;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

public abstract class BaseService extends Service {
    abstract protected void onWorkerRequest(Message msg);

    @Override
    public void onStart(Intent intent, int startId) {
        handleStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleStart(intent, startId);
        return START_NOT_STICKY;
    }

    abstract protected void handleStart(Intent intent, int startId);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected final class WorkerHandler extends Handler {
        public WorkerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onWorkerRequest(msg);
        }
    }

    protected HandlerThread mWorker = null;
    protected WorkerHandler mHandler = null;

    protected void startWorker(String tag) {
        if (mWorker == null) {
            mWorker = null;
            mWorker = new HandlerThread(tag);
            mWorker.start();
            mHandler = null;
            mHandler = new WorkerHandler(mWorker.getLooper());
        } else if (mWorker.getState() == Thread.State.NEW) {
            mWorker.start();
            mHandler = null;
            mHandler = new WorkerHandler(mWorker.getLooper());
        } else if (mWorker.getState() == Thread.State.WAITING) {
            mHandler = null;
            mHandler = new WorkerHandler(mWorker.getLooper());
        } else if (mWorker.getState() == Thread.State.TERMINATED) {
            mWorker = null;
            mWorker = new HandlerThread(tag);
            mWorker.start();
            mHandler = null;
            mHandler = new WorkerHandler(mWorker.getLooper());
        }
    }

    protected void endWorker() {
        mHandler = null;
        HandlerThread snap = mWorker;
        mWorker = null;
        snap.quit();
        snap.interrupt();
    }

    protected WorkerHandler getWorkerHandler() {
        return mHandler;
    }
}
