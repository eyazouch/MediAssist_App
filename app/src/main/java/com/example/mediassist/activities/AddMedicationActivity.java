package com.example.mediassist.activities;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import com.example.mediassist.R;
import com.example.mediassist.database.DatabaseHelper;
import com.example.mediassist.utils.NotificationHelper;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddMedicationActivity extends AppCompatActivity {

    //Constantes pour identifier les deux types d’Intent: prise de photo et choix depuis la galerie.
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;

    private EditText etMedicationName, etDosage;
    private Spinner spinnerFrequency;
    private TextView tvTime;
    private ImageView ivMedicationImage;
    private Button btnSelectTime, btnTakePhoto, btnGallery, btnSave;
    private DatabaseHelper databaseHelper;
    private String currentPhotoPath;
    private Calendar selectedTime;
    private NotificationHelper notificationHelper;
    private boolean isEditing = false;
    private int MedicationId= -1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        // Configuration de la barre d'outils
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.add_medication);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialisation des vues
        etMedicationName = findViewById(R.id.etMedicationName);
        etDosage = findViewById(R.id.etDosage);
        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        tvTime = findViewById(R.id.tvTime);
        ivMedicationImage = findViewById(R.id.ivMedicationImage);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnGallery = findViewById(R.id.btnGallery);
        btnSave = findViewById(R.id.btnSave);

        // Initialisation de la base de données et de l'aide aux notifications
        databaseHelper = new DatabaseHelper(this);
        notificationHelper = new NotificationHelper(this);

        // Initialisation du temps sélectionné
        //nitialise l’objet Calendar à l’heure courante.
        selectedTime = Calendar.getInstance();


        //Test3
        // Récupération des données si elles sont passées via l'Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("medication_id")) {
            isEditing = true;
            MedicationId= intent.getIntExtra("medication_id", -1);
            String name = intent.getStringExtra("name");
            String dosage = intent.getStringExtra("dosage");
            String frequency = intent.getStringExtra("frequency");
            String time = intent.getStringExtra("time");
            String imagePath = intent.getStringExtra("image_path");

            // Remplir les champs avec les données récupérées
            etMedicationName.setText(name);
            etDosage.setText(dosage);
            tvTime.setText(time);
            currentPhotoPath = imagePath;

            // Définir l'image si elle existe
            if (imagePath != null && !imagePath.isEmpty()) {
                ivMedicationImage.setImageURI(Uri.parse(imagePath));
            }

            // Sélectionner la bonne fréquence dans le Spinner
            for (int i = 0; i < spinnerFrequency.getCount(); i++) {
                if (spinnerFrequency.getItemAtPosition(i).toString().equals(frequency)) {
                    spinnerFrequency.setSelection(i);
                    break;
                }
            }

            // Mettre à jour l'heure sélectionnée pour la notification
            if (time != null && !time.isEmpty()) {
                String[] parts = time.split(":");
                if (parts.length == 2) {
                    int hour = Integer.parseInt(parts[0]);
                    int minute = Integer.parseInt(parts[1]);
                    selectedTime.set(Calendar.HOUR_OF_DAY, hour);
                    selectedTime.set(Calendar.MINUTE, minute);
                }
            }
            btnSave.setText("Modifier");
        } else {
            btnSave.setText("Enregistrer");
        }

        btnSave.setOnClickListener(v -> {
            String name = etMedicationName.getText().toString().trim();
            String dosage = etDosage.getText().toString().trim();
            String frequency = spinnerFrequency.getSelectedItem().toString();
            String time = tvTime.getText().toString().trim(); // Si tu as un champ d’heure
            String photoPath = currentPhotoPath; // Si tu ajoutes une image, sinon tu peux l’ignorer

            if (MedicationId != -1) {
                databaseHelper.updateMedication(MedicationId, name, dosage, frequency, time, photoPath);
                //Toast.makeText(this, "Médicament mis à jour", Toast.LENGTH_SHORT).show();
                //finish();
            } else {
                saveMedication();
            }
        });



        // Gestionnaire d'événements pour le bouton de sélection de l'heure
        btnSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // Gestionnaire d'événements pour le bouton de prise de photo
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // Gestionnaire d'événements pour le bouton de sélection depuis la galerie
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPickImageIntent();
            }
        });

        /*// Gestionnaire d'événements pour le bouton de sauvegarde
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMedication();
            }
        });*/
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);
                        updateTimeDisplay();
                    }
                },
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void updateTimeDisplay() {
        //Formate l’heure sélectionnée en “HH:mm” et l’affiche dans tvTime.
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tvTime.setText(sdf.format(selectedTime.getTime()));
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Erreur lors de la création du fichier image", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.mediassist.fileprovider",
                        photoFile);
                //stocke la photo dans ce fichier précis
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Création d'un nom de fichier unique
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(
                imageFileName,  // Préfixe
                ".jpg",         // Suffixe
                storageDir      // Répertoire
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchPickImageIntent() {
        //Lance la galerie pour choisir une image, sans spécifier de fichier de sortie.
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // La photo a été prise avec succès
                ivMedicationImage.setImageURI(Uri.parse(currentPhotoPath));
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                // Une image a été sélectionnée depuis la galerie
                Uri selectedImage = data.getData();
                currentPhotoPath = getRealPathFromURI(selectedImage);
                ivMedicationImage.setImageURI(selectedImage);
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        // Méthode pour obtenir le chemin réel à partir d'un URI
        // Implémentation à compléter selon vos besoins
        return contentUri.getPath();
    }

    /*private void saveMedication() {
        // Récupération des valeurs saisies
        String name = etMedicationName.getText().toString().trim();
        String dosage = etDosage.getText().toString().trim();
        String frequency = spinnerFrequency.getSelectedItem().toString();
        String time = tvTime.getText().toString().trim();

        // Validation des champs
        if (name.isEmpty() || dosage.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, R.string.fields_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        //Test
        SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        // Sauvegarde du médicament dans la base de données
        long result = databaseHelper.addMedication(userId,name, dosage, frequency, time, currentPhotoPath);
        if (getIntent().hasExtra("medication_id")) {
            int medicationId = getIntent().getIntExtra("medication_id", -1);
            boolean isUpdated = databaseHelper.updateMedication(medicationId, name, dosage, frequency, time, currentPhotoPath);

            if (isUpdated) {
                notificationHelper.scheduleMedicationReminder(medicationId, name, dosage, selectedTime);
                Toast.makeText(this, "Médicament mis à jour", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
            }
        }
        if (result > 0) {
            // Planification de la notification pour ce médicament
            notificationHelper.scheduleMedicationReminder(
                    (int) result,  // ID de la notification
                    name,          // Nom du médicament
                    dosage,        // Dosage
                    selectedTime   // Heure programmée
            );

            Toast.makeText(this, R.string.medication_added, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erreur lors de l'ajout du médicament", Toast.LENGTH_SHORT).show();
        }
    }*/
    private void saveMedication() {
        // Récupération des valeurs saisies
        String name = etMedicationName.getText().toString().trim();
        String dosage = etDosage.getText().toString().trim();
        String frequency = spinnerFrequency.getSelectedItem().toString();
        String time = tvTime.getText().toString().trim();
        String photoPath = currentPhotoPath;

        // Validation des champs
        if (name.isEmpty() || dosage.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, R.string.fields_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        // Récupération de l'utilisateur connecté
        SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        // Création de l'heure pour la notification
        Calendar medicationTime = Calendar.getInstance();
        medicationTime.set(Calendar.HOUR_OF_DAY, selectedTime.get(Calendar.HOUR_OF_DAY));
        medicationTime.set(Calendar.MINUTE, selectedTime.get(Calendar.MINUTE));
        medicationTime.set(Calendar.SECOND, 0);

        // === MODE MODIFICATION ===

        if (isEditing && MedicationId != -1) {
            boolean isUpdated = databaseHelper.updateMedication(MedicationId, name, dosage, frequency, time, photoPath);

            if (isUpdated) {
                notificationHelper.scheduleMedicationReminder(
                        MedicationId,
                        name,
                        dosage,
                        medicationTime
                );
                Toast.makeText(this, "Médicament mis à jour", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de la mise à jour du médicament", Toast.LENGTH_SHORT).show();
            }

        } else {
            // === MODE AJOUT ===
            long result = databaseHelper.addMedication(userId, name, dosage, frequency, time, photoPath);

            if (result > 0) {

                notificationHelper.scheduleMedicationReminder(
                        (int) result,
                        name,
                        dosage,
                        medicationTime
                );
                Toast.makeText(this, R.string.medication_added, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de l'ajout du médicament", Toast.LENGTH_SHORT).show();
            }
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

//Un URI (Uniform Resource Identifier) est une chaîne de caractères qui identifie de manière unique une ressource sur le web ou dans un système local.