package com.example.wyf.suidaodemo.syncpicture;

import android.content.Intent;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class BaseWorkerService extends BaseService {
    private final Lock mWorkerLock = new ReentrantLock();

    public abstract String getWorkerTag();

    public abstract void onWorkerRequest(Intent intent, int startId);

    @Override
    public void onCreate() {
        super.onCreate();

        startWorker(getWorkerTag());
    }

    @Override
    protected void handleStart(Intent intent, int startId) {
        try {
            Message msg = getWorkerHandler().obtainMessage();
            msg.what = startId;
            msg.obj = intent;
            getWorkerHandler().dispatchMessage(msg);
        } catch (Exception e) {
            Log.e("WorkerService", e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        endWorker();
    }

    @Override
    protected void onWorkerRequest(Message msg) {
        mWorkerLock.lock();
        try {
            onWorkerRequest((Intent) msg.obj, msg.what);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mWorkerLock.unlock();
        }
    }
}
