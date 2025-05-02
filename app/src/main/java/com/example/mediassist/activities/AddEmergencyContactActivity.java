package com.example.mediassist.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.mediassist.R;
import com.example.mediassist.database.DatabaseHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEmergencyContactActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;

    private EditText etName, etPhone;
    private ImageView ivContactImage;
    private Button btnTakePhoto, btnGallery, btnSave;
    private DatabaseHelper databaseHelper;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_emergency_contact);

        // Configuration de la barre d'outils
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.add_emergency_contact);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialisation des vues
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        ivContactImage = findViewById(R.id.ivContactImage);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnGallery = findViewById(R.id.btnGallery);
        btnSave = findViewById(R.id.btnSave);

        // Initialisation de la base de données
        databaseHelper = new DatabaseHelper(this);

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

        // Gestionnaire d'événements pour le bouton de sauvegarde
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContact();
            }
        });
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
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // La photo a été prise avec succès
                ivContactImage.setImageURI(Uri.parse(currentPhotoPath));
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                // Une image a été sélectionnée depuis la galerie
                Uri selectedImage = data.getData();
                currentPhotoPath = getRealPathFromURI(selectedImage);
                ivContactImage.setImageURI(selectedImage);
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        // Méthode pour obtenir le chemin réel à partir d'un URI
        // Cette implémentation est simplifiée et pourrait nécessiter une approche plus robuste
        return contentUri.getPath();
    }

    private void saveContact() {
        // Récupération des valeurs saisies
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Validation des champs
        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, R.string.fields_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        // Sauvegarde du contact dans la base de données
        long result = databaseHelper.addEmergencyContact(name, phone, currentPhotoPath);

        if (result > 0) {
            Toast.makeText(this, R.string.contact_added, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erreur lors de l'ajout du contact", Toast.LENGTH_SHORT).show();
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