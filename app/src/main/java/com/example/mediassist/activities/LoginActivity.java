package com.example.mediassist.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mediassist.R;
import com.example.mediassist.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialisation des vues
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        // Initialisation de la base de données
        databaseHelper = new DatabaseHelper(this);


        // Gestionnaire d'événements pour le bouton de connexion
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Redirection vers l'écran d'inscription
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation des champs
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Solution temporaire pour faciliter les tests
        if (username.equals("test") && password.equals("123456")) {
            // Enregistrer l'état de connexion dans les préférences partagées
            SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("username", username);
            editor.apply();

            Toast.makeText(this, "Connexion réussie!", Toast.LENGTH_SHORT).show();

            // Redirection vers l'écran principal
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finishAffinity(); // Ferme toutes les activités précédentes
            return;
        }

        // Vérification des identifiants dans la base de données
        boolean isValid = databaseHelper.checkUser(username, password);

        if (isValid) {
            //Test
            int userId = databaseHelper.getUserId(username, password);
            String email = databaseHelper.getEmailByUsername(username);
            // Enregistrer l'état de connexion dans les préférences partagées
            SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("username", username);
            // Ajoute cette ligne après avoir vali
            // dé la connexion
            //test2
            editor.putString("email", email);
            editor.putInt("user_id", userId);
            editor.apply();

            Toast.makeText(this, "Connexion réussie!", Toast.LENGTH_SHORT).show();

            // Redirection vers l'écran principal
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finishAffinity(); // Ferme toutes les activités précédentes
        } else {
            Toast.makeText(this, "Identifiants invalides", Toast.LENGTH_SHORT).show();
        }
    }
}