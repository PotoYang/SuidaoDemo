package com.example.wyf.suidaodemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MyGpsReceiver extends BroadcastReceiver {
    private IBroadcastInteraction broadcastInteraction;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if ("".equals(bundle.getString("lat"))) {
                Toast.makeText(context, "Bundle exist,data is none!", Toast.LENGTH_SHORT).show();
            } else {
                broadcastInteraction.setLat(bundle.getString("lat"));
                broadcastInteraction.setLng(bundle.getString("lon"));
                Toast.makeText(context, "Get lat lon!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "No bundle", Toast.LENGTH_SHORT).show();
        }
    }

    public interface IBroadcastInteraction {
        void setLat(String lat);

        void setLng(String lng);
    }

    public IBroadcastInteraction getBroadcastInteraction() {
        return broadcastInteraction;
    }

    public void setBroadcastInteraction(IBroadcastInteraction broadcastInteraction) {
        this.broadcastInteraction = broadcastInteraction;
    }
}
