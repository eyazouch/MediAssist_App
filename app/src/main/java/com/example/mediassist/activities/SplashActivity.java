package com.example.mediassist.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mediassist.R;

public class SplashActivity extends AppCompatActivity {

    //la durée d’affichage de l’écran splash
    private static final int SPLASH_TIMEOUT = 2000; // 2 secondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Utiliser le nouveau Handler avec Looper explicite (recommandé pour les versions récentes d'Android)
        //Looper.getMainLooper() garantit que le code s’exécute sur le thread principal de l’interface utilisateur.
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Vérifie si l'utilisateur est déjà connecté
                SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
                boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

                Intent intent;
                if (isLoggedIn) {
                    // Si l'utilisateur est connecté, aller directement à l'écran principal
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    // Sinon, aller à l'écran de bienvenue
                    intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                }
                startActivity(intent);
                finish(); // Ferme l'activité actuelle
            }
        }, SPLASH_TIMEOUT);
    }
}