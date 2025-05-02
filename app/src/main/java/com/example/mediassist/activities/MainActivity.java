package com.example.mediassist.activities;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mediassist.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private CardView cardProfile, cardMedications, cardAppointments, cardPrescriptions, cardEmergency, cardSchedule;
    private TextView tvWelcome, tvQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }

        // Supprimez l'utilisation de setOnApplyWindowInsetsListener à la ligne 20

        // Configuration de la barre d'outils
        // toolbar et la barre horizontale qui contient l'icone de menu et le nom de l'app en haut de la page
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialisation des vues
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        cardProfile = findViewById(R.id.cardProfile);
        cardMedications = findViewById(R.id.cardMedications);
        cardAppointments = findViewById(R.id.cardAppointments);
        cardPrescriptions = findViewById(R.id.cardPrescriptions);
        cardEmergency = findViewById(R.id.cardEmergency);
        cardSchedule = findViewById(R.id.cardSchedule);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvQuote = findViewById(R.id.tvQuote);

        // Configuration du menu de navigation latéral
        // ActionBarDrawerToggle est un objet qui Connecte la Toolbar avec le DrawerLayout et affiche automatiquement l'icône ☰
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        //Cette méthode synchronise le bouton (☰) avec l'état actuel du DrawerLayout.
        //Si le tiroir est déjà ouvert au démarrage, il montre ←.
        //Sinon, il montre ☰.
        toggle.syncState();
        //permet d’indiquer que la méthode onNavigationItemSelected() de la classe MainActivity sera appelée à chaque clic sur un élément du menu de navigation (selon les if)
        navigationView.setNavigationItemSelectedListener(this);

        // Récupération des informations de l'utilisateur dans les préférences
        SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
        //récupérer une donnée stockée sous la clé "username"
        String username = prefs.getString("username", "");

        //Récuperation des informations de gmail
        String userEmail = prefs.getString("email", "example@gmail.com");

        // Affichage du message de bienvenue personnalisé dans la page d'accueil
        tvWelcome.setText("Bienvenue " + username);
        tvQuote.setText("Prenez soin de votre santé avec MediAssist");

        // Affichage du nom d'utilisateur dans l'entête du menu de navigation
        //la vue d'en-tête du menu
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserName = headerView.findViewById(R.id.tvUserName);
        TextView navEmail = headerView.findViewById(R.id.nav_email);
        //On y insère dynamiquement le nom d’utilisateur
        tvUserName.setText(username);
        navEmail.setText(userEmail);

        // Gestionnaires d'événements pour les cartes
        // démarrer une nouvelle activité lorsque l'utilisateur clique sur la carte
        cardProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        cardMedications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MedicationsActivity.class));
            }
        });

        cardAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AppointmentsActivity.class));
            }
        });

        cardPrescriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PrescriptionsActivity.class));
            }
        });

        cardEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EmergencyContactsActivity.class));
            }
        });

        cardSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScheduleActivity.class));
            }
        });
    }

    @Override
    //Lorsqu'un utilisateur clique sur un élément du menu, cette méthode est appelée pour effectuer l'action appropriée (dans le drawerLayout :le menu a gauche):
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //récupère l'ID de l'élément du menu sélectionné
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Reste sur la page d'accueil
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        }  else if (id == R.id.nav_share) {
            // Partager l'application
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "MediAssist");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Essayez MediAssist, l'application parfaite pour gérer vos médicaments et rendez-vous médicaux !");
            startActivity(Intent.createChooser(shareIntent, "Partager via"));
        }  else if (id == R.id.nav_logout) {
            // Déconnexion
            SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
            //ouvre un éditeur qui permet de modifier les données dans SharedPreferences. L'éditeur est utilisé pour ajouter, modifier ou supprimer des paires clé-valeur dans le fichier.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", false);
            // Sauvegarde les changements
            editor.apply();

            Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finishAffinity(); // Ferme toutes les activités dans la pile
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    //gérer le comportement du bouton "Retour"
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            //i le tiroir est ouvert, alors cette ligne ferme le tiroir
        } else {
            super.onBackPressed();
            //invoquer la méthode par défaut onBackPressed() de l'Activity (comme quitter l'application ou revenir à la précédente activité).
        }
    }
}

//Un Intent (événement) est un objet utilisé pour lancer des activités, passer des données entre les activités, ou démarrer des services ou des récepteurs de diffusion.
//Une Activity est une écran dans une application Android.
//SharedPreferences est une classe Android utilisée pour enregistrer des données sous forme de paires clé-valeur (key-value pairs) dans une mémoire persistante (stockage local dans Android)