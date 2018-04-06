package com.example.wyf.suidaodemo.syncpicture;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.FileObserver;
import android.util.Log;

import java.io.File;

public class RecordUploadService extends BaseWorkerService {
    private String TAG = getClass().getCanonicalName();
    public static final String ACTION_UPLOAD = "ACTION_UPLOAD";
    private RecordFileListener recordFileListener = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public String getWorkerTag() {
        return RecordUploadService.class.getSimpleName();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recordFileListener != null)
            recordFileListener.stopWatching();
    }

    @Override
    public void onWorkerRequest(Intent intent, int startId) {
        Log.d(TAG, "receive Request");
        if (ACTION_UPLOAD.equals(intent.getAction())) {
//            String observeDir = Config.AUDIO_DIR;
            String observeDir = "";
//            String remoteDir = intent.getStringExtra("remoteDir");
            if (recordFileListener != null) {
                recordFileListener.stopWatching();
                recordFileListener = null;
            }
            recordFileListener = new RecordFileListener(observeDir);
            recordFileListener.startWatching();
            Log.d(TAG, "start watching directory " + observeDir);
        }
    }

    private class RecordFileListener extends FileObserver {
        private String recordDirName;
//        private String remoteDir;

        public RecordFileListener(String path) {
            super(path);
            recordDirName = path;
//            this.remoteDir = remoteDir;
        }

        @Override
        public void onEvent(int event, String path) {
            switch (event) {
                case FileObserver.CREATE:
                    Log.d(TAG, "Record has been created: " + path);
                    // 如果wifi连通，上传至OSS服务器
                    if (isWifiConnected()) {
                        File recordDir = new File(recordDirName);
                        if (recordDir.exists() && recordDir.isDirectory()) {
                            for (File file : recordDir.listFiles()) {
//                                upload(file);
// demo的话也就是在manifest中添加RecordUploadService服务
// 然后在activity启动RecordUploadService 就可以了
                            }
                        }
                    }
                    break;
            }
        }
    }

    public boolean isWifiConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWiFiNetworkInfo != null && mWiFiNetworkInfo.isAvailable();
    }
}