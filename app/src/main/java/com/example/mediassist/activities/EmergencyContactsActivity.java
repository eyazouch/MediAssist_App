package com.example.mediassist.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mediassist.R;
import com.example.mediassist.adapters.EmergencyContactAdapter;
import com.example.mediassist.database.DatabaseHelper;
import com.example.mediassist.models.EmergencyContact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class EmergencyContactsActivity extends AppCompatActivity implements EmergencyContactAdapter.OnContactClickListener {

    private static final int REQUEST_CALL_PERMISSION = 1;

    private RecyclerView recyclerView;
    private TextView tvNoContacts;
    private FloatingActionButton fabAddContact;
    private DatabaseHelper databaseHelper;
    private EmergencyContactAdapter adapter;
    private String phoneNumberToCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        // Configuration de la barre d'outils
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.emergency);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialisation des vues
        recyclerView = findViewById(R.id.recyclerView);
        tvNoContacts = findViewById(R.id.tvNoContacts);
        fabAddContact = findViewById(R.id.fabAddContact);

        // Initialisation de la base de données
        databaseHelper = new DatabaseHelper(this);

        // Configuration du RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Chargement des contacts d'urgence
        loadEmergencyContacts();

        // Gestionnaire d'événements pour le bouton d'ajout
        fabAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmergencyContactsActivity.this, AddEmergencyContactActivity.class));
            }
        });
    }

    private void loadEmergencyContacts() {
        List<EmergencyContact> contactList = databaseHelper.getAllEmergencyContacts();

        if (contactList.isEmpty()) {
            tvNoContacts.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoContacts.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new EmergencyContactAdapter(this, contactList, this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onCallClick(String phoneNumber) {
        phoneNumberToCall = phoneNumber;

        // Vérifier si nous avons la permission d'appeler
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            // Nous avons déjà la permission, effectuer l'appel
            makePhoneCall();
        }
    }

    private void makePhoneCall() {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumberToCall));
            startActivity(callIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Impossible d'effectuer l'appel", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée, effectuer l'appel
                makePhoneCall();
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharger les contacts à chaque reprise de l'activité
        loadEmergencyContacts();
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