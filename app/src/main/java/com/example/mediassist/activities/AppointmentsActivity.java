package com.example.mediassist.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mediassist.R;
import com.example.mediassist.adapters.AppointmentAdapter;
import com.example.mediassist.database.DatabaseHelper;
import com.example.mediassist.models.Appointment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class AppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvNoAppointments;
    private FloatingActionButton fabAddAppointment;
    private DatabaseHelper databaseHelper;
    private AppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        // Configuration de la barre d'outils
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.appointments);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialisation des vues
        recyclerView = findViewById(R.id.recyclerView);
        tvNoAppointments = findViewById(R.id.tvNoAppointments);
        fabAddAppointment = findViewById(R.id.fabAddAppointment);

        // Initialisation de la base de données
        databaseHelper = new DatabaseHelper(this);

        // Configuration du RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        // Chargement des rendez-vous
        loadAppointments();

        // Gestionnaire d'événements pour le bouton d'ajout
        fabAddAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppointmentsActivity.this, AddAppointmentActivity.class));
            }
        });
    }

    private void loadAppointments() {
        SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        Log.d("AppointmentsDebug", "User ID utilisé : " + userId);

        List<Appointment> appointmentList = databaseHelper.getAllAppointments(userId);
        Log.d("AppointmentsActivity", "Nombre de rendez_vous récupérées : " + appointmentList.size());

        if (appointmentList.isEmpty()) {
            tvNoAppointments.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoAppointments.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new AppointmentAdapter(this, appointmentList);
            recyclerView.setAdapter(adapter);
        }
        Log.d("AppointmentsActivity", "Nombre de rendez-vous : " + appointmentList.size());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharger les rendez-vous à chaque reprise de l'activité
        loadAppointments();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}