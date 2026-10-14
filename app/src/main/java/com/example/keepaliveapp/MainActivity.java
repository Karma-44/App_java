package com.example.keepaliveapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lancement sécurisé du service KeepAlive
        Intent serviceIntent = new Intent(this, KeepAliveService.class);
        startService(serviceIntent);
    }
}
