package com.example.keepaliveapp;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class KeepAliveService extends Service {

    private final Handler handler = new Handler();
    private Runnable monitorRunnable;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "KeepAlivePrefs";
    private static final String KEY_TARGET_PACKAGE = "target_package";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        startMonitoring();
        return START_STICKY;
    }

    private void startMonitoring() {
        monitorRunnable = new Runnable() {
            @Override
            public void run() {
                checkAndRestartTargetApp();
                // Vérifie toutes les 5 secondes
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(monitorRunnable);
    }

    private void checkAndRestartTargetApp() {
        String targetPackage = sharedPreferences.getString(KEY_TARGET_PACKAGE, null);
        if (targetPackage == null) return;

        PackageManager pm = getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(targetPackage);

        if (launchIntent != null) {
            // Vous pouvez ajouter ici la logique si vous souhaitez relancer l'app 
            // lorsqu'elle n'est plus active en arrière-plan.
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && monitorRunnable != null) {
            handler.removeCallbacks(monitorRunnable);
        }
        Intent restartIntent = new Intent(getApplicationContext(), KeepAliveService.class);
        startService(restartIntent);
    }
}
