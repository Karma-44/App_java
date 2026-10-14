package com.example.keepaliveapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvSelectedApp;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "KeepAlivePrefs";
    private static final String KEY_TARGET_PACKAGE = "target_package";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSelectedApp = findViewById(R.id.tvSelectedApp);
        Button btnSelectApp = findViewById(R.id.btnSelectApp);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String savedPackage = sharedPreferences.getString(KEY_TARGET_PACKAGE, null);

        if (savedPackage != null) {
            tvSelectedApp.setText("Surveille : " + savedPackage);
            startKeepAliveService();
        }

        btnSelectApp.setOnClickListener(v -> showInstalledAppsDialog());
    }

    private void showInstalledAppsDialog() {
        PackageManager pm = getPackageManager();
        // Récupérer toutes les applications installées
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<String> appNames = new ArrayList<>();
        List<String> packageNames = new ArrayList<>();

        for (ApplicationInfo packageInfo : packages) {
            // Filtrer pour ne garder que les apps qui ont une interface (launchable)
            if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                String appName = pm.getApplicationLabel(packageInfo).toString();
                appNames.add(appName);
                packageNames.add(packageInfo.packageName);
            }
        }

        // Trier par ordre alphabétique
        // (Optionnel mais plus propre, on associe les deux listes ou on affiche les noms)
        CharSequence[] namesArray = appNames.toArray(new CharSequence[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sélectionnez l'application à maintenir");
        builder.setItems(namesArray, (dialog, which) -> {
            String selectedPackage = packageNames.get(which);
            String selectedName = appNames.get(which);

            // Sauvegarder le choix
            sharedPreferences.edit().putString(KEY_TARGET_PACKAGE, selectedPackage).apply();
            tvSelectedApp.setText("Surveille : " + selectedName);

            Toast.makeText(this, "Application enregistrée !", Toast.LENGTH_SHORT).show();

            // Lancer le service
            startKeepAliveService();
        });
        builder.show();
    }

    private void startKeepAliveService() {
        Intent serviceIntent = new Intent(this, KeepAliveService.class);
        startService(serviceIntent);
    }
}
