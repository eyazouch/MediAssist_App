package com.example.mediassist.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mediassist.R;
import com.example.mediassist.adapters.MedicationAdapter;
import com.example.mediassist.database.DatabaseHelper;
import com.example.mediassist.models.Medication;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MedicationsActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    //recyclerView : Un composant UI pour afficher la liste des médicaments.
    private TextView tvNoMedications;
    //tvNoMedications : Un TextView qui affichera un message quand il n'y a pas de médicaments.
    private FloatingActionButton fabAddMedication;
    //fabAddMedication : Un bouton flottant pour ajouter un médicament.
    private DatabaseHelper databaseHelper;
    private MedicationAdapter adapter;
    //adapter : Un adaptateur pour lier les médicaments à la vue RecyclerView.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView définir le fichier de mise en page XML : activity_medications.xml
        setContentView(R.layout.activity_medications);

        // Configuration de la barre d'outils
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.medications);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialisation des vues
        recyclerView = findViewById(R.id.recyclerView);
        tvNoMedications = findViewById(R.id.tvNoMedications);
        fabAddMedication = findViewById(R.id.fabAddMedication);

        // Initialisation de la base de données
        databaseHelper = new DatabaseHelper(this);


        // Configuration du RecyclerView : dispose les éléments en liste verticale.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Chargement des médicaments
        loadMedications();

        // Gestionnaire d'événements pour le bouton d'ajout
        fabAddMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MedicationsActivity.this, AddMedicationActivity.class));
            }
        });
    }

    private void loadMedications() {
        //récupèrer tous les médicaments enregistrés dans la base de données et les stocke dans une liste.
        //Test
        SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        List<Medication> medicationList = databaseHelper.getAllMedications(userId);

        if (medicationList.isEmpty()) {
            tvNoMedications.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoMedications.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new MedicationAdapter(this, medicationList);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharger les médicaments à chaque reprise de l'activité
        loadMedications();
    }

    @Override
    //Gestion du bouton retour (Home)
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}