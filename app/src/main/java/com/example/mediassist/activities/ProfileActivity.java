package com.example.mediassist.activities;

import static com.example.mediassist.database.DatabaseHelper.COLUMN_ADDRESS;
import static com.example.mediassist.database.DatabaseHelper.COLUMN_AGE;
import static com.example.mediassist.database.DatabaseHelper.COLUMN_ALLERGIES;
import static com.example.mediassist.database.DatabaseHelper.COLUMN_BLOOD_TYPE;
import static com.example.mediassist.database.DatabaseHelper.COLUMN_CHRONIC_DISEASES;
import static com.example.mediassist.database.DatabaseHelper.COLUMN_GENDER;
import static com.example.mediassist.database.DatabaseHelper.COLUMN_HEIGHT;
import static com.example.mediassist.database.DatabaseHelper.COLUMN_NAME;
import static com.example.mediassist.database.DatabaseHelper.COLUMN_PHONE;
import static com.example.mediassist.database.DatabaseHelper.COLUMN_WEIGHT;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.mediassist.R;
import com.example.mediassist.database.DatabaseHelper;

public class ProfileActivity extends AppCompatActivity {

    //Champs texte pour saisir les informations du profil utilisateur.
    private EditText etName, etAge, etWeight, etHeight, etBloodType, etAllergies,
            etChronicDiseases, etPhone, etAddress;
    //Liste déroulante pour sélectionner le genre.
    private Spinner spinnerGender;
    private Button btnSave;

    //Un objet pour interagir avec la base de données
    private DatabaseHelper databaseHelper;
    private String username;
    private int userId;

    @Override
    //Bundle savedInstanceState est un objet qui contient l’état précédemment enregistré d’une activité Android
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Charger le fichier activity_profile.xml pour afficher l'interface graphique.
        setContentView(R.layout.activity_profile);

        // Configuration de la barre d'outils
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Configurer cette Toolbar comme étant la barre d'action (ActionBar) de l'activité
        setSupportActionBar(toolbar);
        //Définir le titre affiché dans la barre d'action à "Profil"
        getSupportActionBar().setTitle(R.string.profile);
        //Afficher un bouton de retour
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialisation des vues
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        spinnerGender = findViewById(R.id.spinnerGender);
        etBloodType = findViewById(R.id.etBloodType);
        etAllergies = findViewById(R.id.etAllergies);
        etChronicDiseases = findViewById(R.id.etChronicDiseases);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnSave = findViewById(R.id.btnSave);

        // Initialisation de la base de données
        databaseHelper = new DatabaseHelper(this);

        // Récupération du nom d'utilisateur dans les préférences
        SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
        username = prefs.getString("username", "");

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);


        // Chargement des informations du profil
        //remplir les champs du formulaire à partir des données enregistrées.
        loadProfileData();


      // Gestionnaire d'événements pour le bouton de sauvegarde
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
            }
        });
      /* // --- Toujours en mode « Enregistrer » pour mettre à jour ---
        btnSave.setOnClickListener(v -> {
            String name  = etName.getText().toString().trim();
            String age   = etAge.getText().toString().trim();
            String weight= etWeight.getText().toString().trim();
            String height= etHeight.getText().toString().trim();
            String gender= spinnerGender.getSelectedItem().toString();
            String blood = etBloodType.getText().toString().trim();
            String allergies = etAllergies.getText().toString().trim();
            String diseases   = etChronicDiseases.getText().toString().trim();
            String phone      = etPhone.getText().toString().trim();
            String address    = etAddress.getText().toString().trim();

            boolean result= databaseHelper.updateUserProfile(userId, name, age, weight, height, gender, blood, allergies, diseases, phone, address);

            if (result) {
                Toast.makeText(this, "Profil mis à jour", Toast.LENGTH_SHORT).show();
                loadProfileData();
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void loadProfileData() {
        // Récupération des données du profil depuis la base de données
        // et remplissage des champs du formulaire
        // Ce code dépend de l'implémentation de votre base de données
        // 1) Récupérer le username depuis SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        Cursor cursor = databaseHelper.getUserById(userId);
        Log.d("ProfileActivity", "User ID: " + userId);
        if (cursor != null && cursor.moveToFirst()) {
            // 3) Lire chaque colonne
            String name    = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String age     = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AGE));
            String weight  = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT));
            String height  = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT));
            String gender  = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER));
            String blood   = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BLOOD_TYPE));
            String allergy = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALLERGIES));
            String disease = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHRONIC_DISEASES));
            String phone   = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
            String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));

            // 4) Mettre à jour les vues
            etName.setText(name);
            etAge.setText(age);
            etWeight.setText(weight);
            etHeight.setText(height);
            etBloodType.setText(blood);
            etAllergies.setText(allergy);
            etChronicDiseases.setText(disease);
            etPhone.setText(phone);
            etAddress.setText(address);

            // 5) Spinner genre
            String[] genders = getResources().getStringArray(R.array.gender_array);
            for (int i = 0; i < genders.length; i++) {
                if (genders[i].equalsIgnoreCase(gender)) {
                    spinnerGender.setSelection(i);
                    break;
                }
            }
            cursor.close();
        }
    }

    private void saveProfileData() {

        SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        // Récupération des valeurs saisies
        //La fonction .trim() sert à supprimer les espaces inutiles au début et à la fin d'une chaîne de caractères.
        String name = etName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();
        String height = etHeight.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();
        String bloodType = etBloodType.getText().toString().trim();
        String allergies = etAllergies.getText().toString().trim();
        String chronicDiseases = etChronicDiseases.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();



        boolean result=databaseHelper.updateUserProfile(userId, name, age, weight, height, gender, bloodType, allergies, chronicDiseases, phone, address);

        // Sauvegarde des données dans la base de données
        // Ce code dépend de l'implémentation de votre base de données

        if(result){
            Toast.makeText(this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
            loadProfileData();
            finish(); }
        else{
            Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
        }
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