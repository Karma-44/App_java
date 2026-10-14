package com.example.keepaliveapp;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class KeepAliveService extends Service {

    private final Handler handler = new Handler();
    private Runnable monitorRunnable;
    
    // Remplacez par le nom du package de l'application que vous souhaitez maintenir active
    private static final String TARGET_PACKAGE_NAME = "com.example.monautreapp"; 

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Lancer la boucle de surveillance
        startMonitoring();
        return START_STICKY;
    }

    private void startMonitoring() {
        monitorRunnable = new Runnable() {
            @Override
            public void run() {
                checkAndRestartTargetApp();
                // Vérifier toutes les 5 secondes (5000 ms)
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(monitorRunnable);
    }

    private void checkAndRestartTargetApp() {
        PackageManager pm = getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(TARGET_PACKAGE_NAME);

        if (launchIntent != null) {
            // Optionnel : Vous pouvez ajouter une logique pour vérifier si l'app tourne déjà, 
            // sinon la relancer automatiquement :
            // launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // startActivity(launchIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && monitorRunnable != null) {
            handler.removeCallbacks(monitorRunnable);
        }
        // Tenter de redémarrer le service s'il est détruit
        Intent restartIntent = new Intent(getApplicationContext(), KeepAliveService.class);
        startService(restartIntent);
    }
}
