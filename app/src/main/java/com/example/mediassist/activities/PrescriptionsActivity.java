package com.example.mediassist.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mediassist.R;
import com.example.mediassist.adapters.PrescriptionAdapter;
import com.example.mediassist.database.DatabaseHelper;
import com.example.mediassist.models.Prescription;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class PrescriptionsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvNoPrescriptions;
    private FloatingActionButton fabAddPrescription;
    private DatabaseHelper databaseHelper;
    private PrescriptionAdapter adapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescriptions);

        // Configuration de la barre d'outils
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.prescriptions);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialisation des vues
        recyclerView = findViewById(R.id.recyclerView);
        tvNoPrescriptions = findViewById(R.id.tvNoPrescriptions);
        fabAddPrescription = findViewById(R.id.fabAddPrescription);

        // Initialisation de la base de données
        databaseHelper = new DatabaseHelper(this);

        // Configuration du RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 1) Récupération du user_id une seule fois
        SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        Log.d("PrescriptionsActivity", "userId chargé = " + userId);

        // Chargement des ordonnances
        loadPrescriptions();

        // Gestionnaire d'événements pour le bouton d'ajout
        fabAddPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PrescriptionsActivity.this, AddPrescriptionActivity.class));
            }
        });
    }

    private void loadPrescriptions() {

        if (userId < 0) {
            Toast.makeText(this, "Erreur : utilisateur non identifié", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Prescription> prescriptionList = databaseHelper.getAllPrescriptions(userId);
        Log.d("Prescriptions", "Nombre de prescriptions récupérées : " + prescriptionList.size());


        if (prescriptionList.isEmpty()) {
            tvNoPrescriptions.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoPrescriptions.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new PrescriptionAdapter(this, prescriptionList);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharger les ordonnances à chaque reprise de l'activité
        loadPrescriptions();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showImageOverlay(String imagePath) {
        FrameLayout imageOverlay = findViewById(R.id.imageOverlay);
        ImageView enlargedImage = findViewById(R.id.enlargedImage);

        imageOverlay.setVisibility(View.VISIBLE);
        enlargedImage.setImageURI(Uri.parse(imagePath));

        // Fermer l'overlay au clic sur l'image
        imageOverlay.setOnClickListener(v -> {
            imageOverlay.setVisibility(View.GONE);
        });
    }
}

