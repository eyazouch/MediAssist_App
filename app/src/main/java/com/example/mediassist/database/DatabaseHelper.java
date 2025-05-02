package com.example.mediassist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.mediassist.models.Appointment;
import com.example.mediassist.models.EmergencyContact;
import com.example.mediassist.models.Medication;
import com.example.mediassist.models.Prescription;
import com.example.mediassist.models.ScheduleEvent;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mediassist.db";
    private static final int DATABASE_VERSION = 5;

    // Tables
    public static final String TABLE_USERS = "users";
    public static final String TABLE_MEDICATIONS = "medications";
    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String TABLE_PRESCRIPTIONS = "prescriptions";
    public static final String TABLE_EMERGENCY_CONTACTS = "emergency_contacts";

    // Colonnes communes
    public static final String COLUMN_ID = "id";

    // Colonnes pour TABLE_USERS
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_BLOOD_TYPE = "blood_type";
    public static final String COLUMN_ALLERGIES = "allergies";
    public static final String COLUMN_CHRONIC_DISEASES = "chronic_diseases";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_ADDRESS = "address";

    // Colonnes pour TABLE_MEDICATIONS
    //Test
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_MED_NAME = "med_name";
    public static final String COLUMN_DOSAGE = "dosage";
    public static final String COLUMN_FREQUENCY = "frequency";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_IMAGE_PATH = "image_path";

    // Colonnes pour TABLE_APPOINTMENTS
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME_APPOINTMENT = "time";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DOCTOR = "doctor";
    public static final String COLUMN_NOTES = "notes";

    // Colonnes pour TABLE_PRESCRIPTIONS
    public static final String COLUMN_PRESCRIPTION_DATE = "prescription_date";
    public static final String COLUMN_PRESCRIPTION_IMAGE = "prescription_image";
    public static final String COLUMN_DESCRIPTION = "description";

    // Colonnes pour TABLE_EMERGENCY_CONTACTS
    public static final String COLUMN_CONTACT_NAME = "contact_name";
    public static final String COLUMN_CONTACT_PHONE = "contact_phone";
    public static final String COLUMN_CONTACT_IMAGE = "contact_image";

    // Création des tables
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_EMAIL + " TEXT UNIQUE,"
            + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_AGE + " TEXT,"
            + COLUMN_WEIGHT + " TEXT,"
            + COLUMN_HEIGHT + " TEXT,"
            + COLUMN_GENDER + " TEXT,"
            + COLUMN_BLOOD_TYPE + " TEXT,"
            + COLUMN_ALLERGIES + " TEXT,"
            + COLUMN_CHRONIC_DISEASES + " TEXT,"
            + COLUMN_PHONE + " TEXT,"
            + COLUMN_ADDRESS + " TEXT"
            + ")";

    private static final String CREATE_TABLE_MEDICATIONS = "CREATE TABLE " + TABLE_MEDICATIONS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_MED_NAME + " TEXT,"
            + COLUMN_DOSAGE + " TEXT,"
            + COLUMN_FREQUENCY + " TEXT,"
            + COLUMN_TIME + " TEXT,"
            + COLUMN_IMAGE_PATH + " TEXT,"
            //Test
            + COLUMN_USER_ID+ " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
            + ")";

    private static final String CREATE_TABLE_APPOINTMENTS = "CREATE TABLE " + TABLE_APPOINTMENTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_DATE + " TEXT,"
            + COLUMN_TIME_APPOINTMENT + " TEXT,"
            + COLUMN_TYPE + " TEXT,"
            + COLUMN_DOCTOR + " TEXT,"
            + COLUMN_NOTES + " TEXT,"
            + COLUMN_USER_ID+ " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
            + ")";

    private static final String CREATE_TABLE_PRESCRIPTIONS = "CREATE TABLE " + TABLE_PRESCRIPTIONS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PRESCRIPTION_DATE + " TEXT,"
            + COLUMN_PRESCRIPTION_IMAGE + " TEXT,"
            + COLUMN_DESCRIPTION + " TEXT,"
            + COLUMN_USER_ID+ " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
            + ")";

    private static final String CREATE_TABLE_EMERGENCY_CONTACTS = "CREATE TABLE " + TABLE_EMERGENCY_CONTACTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CONTACT_NAME + " TEXT,"
            + COLUMN_CONTACT_PHONE + " TEXT,"
            + COLUMN_CONTACT_IMAGE + " TEXT,"
            + COLUMN_USER_ID+ " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Création de toutes les tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_MEDICATIONS);
        db.execSQL(CREATE_TABLE_APPOINTMENTS);
        db.execSQL(CREATE_TABLE_PRESCRIPTIONS);
        db.execSQL(CREATE_TABLE_EMERGENCY_CONTACTS);
        // Ajouter un utilisateur par défaut pour les tests
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, "test");
        values.put(COLUMN_EMAIL, "test@test.com");
        values.put(COLUMN_PASSWORD, "123456");
        db.insert(TABLE_USERS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // En cas de mise à jour de la base de données
        // Suppression des tables existantes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRESCRIPTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMERGENCY_CONTACTS);

        // Recréation des tables
        onCreate(db);
    }

    // ============================== MÉTHODES POUR LA TABLE USERS ==============================
    public long addUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_NAME + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }

    //Test
    public int getUserId(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_NAME + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            cursor.close();
            db.close();
            return userId;
        }

        cursor.close();
        db.close();
        return -1;
    }
    public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_USERS,
                null,                           // toutes les colonnes
                COLUMN_ID + " = ?",             // WHERE id = ?
                new String[]{ String.valueOf(userId) },  // Argument : userId (converti en String)
                null,
                null,
                null
        );
    }

    //test2
    public String getEmailByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{"email"}, "name=?", new String[]{username}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String email = cursor.getString(0);
            cursor.close();
            return email;
        }
        else{
            return "example@gmail.com";}
    }

    public boolean updateUserProfile(int id, String name, String age, String weight, String height,
                                     String gender, String bloodType, String allergies,
                                     String chronicDiseases, String phone, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_BLOOD_TYPE, bloodType);
        values.put(COLUMN_ALLERGIES, allergies);
        values.put(COLUMN_CHRONIC_DISEASES, chronicDiseases);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_ADDRESS, address);

        int result = db.update(TABLE_USERS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        return result > 0;
    }

    // ============================== MÉTHODES POUR LA TABLE MEDICATIONS ==============================
    public long addMedication(int userId, String name, String dosage, String frequency, String time, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //Test
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_MED_NAME, name);
        values.put(COLUMN_DOSAGE, dosage);
        values.put(COLUMN_FREQUENCY, frequency);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_IMAGE_PATH, imagePath);

        long id = db.insert(TABLE_MEDICATIONS, null, values);
        db.close();

        return id;
    }

    public List<Medication> getAllMedications(int userId) {
        List<Medication> medicationList = new ArrayList<>();
        //Test
        String selectQuery = "SELECT m.* FROM " + TABLE_MEDICATIONS + " m " +
                "JOIN " + TABLE_USERS + " u ON m." + COLUMN_USER_ID + " = u." + COLUMN_ID +
                " WHERE u." + COLUMN_ID + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Medication medication = new Medication();
                medication.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                medication.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MED_NAME)));
                medication.setDosage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOSAGE)));
                medication.setFrequency(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FREQUENCY)));
                medication.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)));
                medication.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)));

                medicationList.add(medication);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return medicationList;
    }

    public boolean updateMedication(int id, String name, String dosage, String frequency, String time, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MED_NAME, name);
        values.put(COLUMN_DOSAGE, dosage);
        values.put(COLUMN_FREQUENCY, frequency);
        values.put(COLUMN_TIME, time);
        if (imagePath != null && !imagePath.isEmpty()) {
            values.put(COLUMN_IMAGE_PATH, imagePath);
        }

        int rows = db.update(TABLE_MEDICATIONS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }


    public boolean deleteMedication(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_MEDICATIONS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        return result > 0;
    }

    // ============================== MÉTHODES POUR LA TABLE APPOINTMENTS ==============================
    public long addAppointment(int userId, String date, String time, String type, String doctor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME_APPOINTMENT, time);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_DOCTOR, doctor);


        long id = db.insert(TABLE_APPOINTMENTS, null, values);
        db.close();

        return id;
    }

    public List<Appointment> getAllAppointments(int userId) {
        List<Appointment> appointmentList = new ArrayList<>();

        // Dans ta méthode de récupération des rendez-vous (DatabaseHelper.java)
        String selectQuery =
                "SELECT a.* FROM " + TABLE_APPOINTMENTS + " a " +
                        "INNER JOIN " + TABLE_USERS + " u ON a." + COLUMN_USER_ID + " = u." + COLUMN_ID + " " +
                        "WHERE u." + COLUMN_ID + " = ? " +
                        "ORDER BY a." + COLUMN_DATE + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Appointment appointment = new Appointment();
                appointment.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                appointment.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                appointment.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME_APPOINTMENT)));
                appointment.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
                appointment.setDoctor(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR)));

                appointmentList.add(appointment);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return appointmentList;
    }

    public boolean updateAppointment(int id, String date, String time, String type, String doctor, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME_APPOINTMENT, time);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_DOCTOR, doctor);

        int result = db.update(TABLE_APPOINTMENTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        return result > 0;

    }

    public boolean deleteAppointment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_APPOINTMENTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        return result > 0;
    }

    // ============================== MÉTHODES POUR LA TABLE PRESCRIPTIONS ==============================
    public long addPrescription(int userId, String date, String imagePath, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRESCRIPTION_DATE, date);
        //values.put(COLUMN_PRESCRIPTION_IMAGE, imagePath);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_USER_ID, userId);

        if (imagePath != null && !imagePath.isEmpty()) {
            values.put(COLUMN_PRESCRIPTION_IMAGE, imagePath);
        }

        long id = db.insert(TABLE_PRESCRIPTIONS, null, values);
        db.close();

        return id;
    }

    public boolean updatePrescription(int prescriptionId, String date, String description, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRESCRIPTION_DATE, date);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_PRESCRIPTION_IMAGE, imagePath);

        int rowsAffected = db.update(TABLE_PRESCRIPTIONS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(prescriptionId)});
        db.close();

        return rowsAffected > 0;
    }

    public List<Prescription> getAllPrescriptions(int userId) {
        List<Prescription> prescriptionList = new ArrayList<>();

        // Requête pour récupérer les prescriptions de l'utilisateur connecté
        String selectQuery = "SELECT p.* FROM " + TABLE_PRESCRIPTIONS + " p " +
                "JOIN " + TABLE_USERS + " u ON p." + COLUMN_USER_ID + " = u." + COLUMN_ID +
                " WHERE u." + COLUMN_ID + " = ? " +
                "ORDER BY p." + COLUMN_PRESCRIPTION_DATE + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});


        if (cursor.moveToFirst()) {
            do {
                Prescription prescription = new Prescription();
                prescription.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                prescription.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRESCRIPTION_DATE)));
                prescription.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRESCRIPTION_IMAGE)));
                prescription.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));

                prescriptionList.add(prescription);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return prescriptionList;
    }

    public boolean deletePrescription(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_PRESCRIPTIONS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        return result > 0;
    }

    // ============================== MÉTHODES POUR LA TABLE EMERGENCY_CONTACTS ==============================
    public long addEmergencyContact(String name, String phone, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACT_NAME, name);
        values.put(COLUMN_CONTACT_PHONE, phone);
        values.put(COLUMN_CONTACT_IMAGE, imagePath);

        long id = db.insert(TABLE_EMERGENCY_CONTACTS, null, values);
        db.close();

        return id;
    }

    public List<EmergencyContact> getAllEmergencyContacts() {
        List<EmergencyContact> contactList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EMERGENCY_CONTACTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                EmergencyContact contact = new EmergencyContact();
                contact.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_NAME)));
                contact.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_PHONE)));
                contact.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_IMAGE)));

                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return contactList;
    }

    public boolean updateEmergencyContact(int id, String name, String phone, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACT_NAME, name);
        values.put(COLUMN_CONTACT_PHONE, phone);
        if (imagePath != null && !imagePath.isEmpty()) {
            values.put(COLUMN_CONTACT_IMAGE, imagePath);
        }

        int result = db.update(TABLE_EMERGENCY_CONTACTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        return result > 0;
    }

    public boolean deleteEmergencyContact(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_EMERGENCY_CONTACTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        return result > 0;
    }

    // ============================== MÉTHODES POUR LE CALENDRIER ==============================
    public List<ScheduleEvent> getEventsForDate(int userId, String date) {
        Log.d("ScheduleCheck", "Date reçue pour getEventsForDate = " + date);
        List<ScheduleEvent> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Récupérer les médicaments pour la date sélectionnée (tous les jours)
        String medicationQuery = "SELECT m.* FROM " + TABLE_MEDICATIONS + " m " +
                "JOIN " + TABLE_USERS + " u ON m." + COLUMN_USER_ID + " = u." + COLUMN_ID +
                " WHERE u." + COLUMN_ID + " = ?";
        Cursor medicationCursor = db.rawQuery(medicationQuery, new String[]{String.valueOf(userId)});

        if (medicationCursor.moveToFirst()) {
            do {
                ScheduleEvent event = new ScheduleEvent();
                event.setId(medicationCursor.getInt(medicationCursor.getColumnIndexOrThrow(COLUMN_ID)));
                event.setTitle(medicationCursor.getString(medicationCursor.getColumnIndexOrThrow(COLUMN_MED_NAME)));
                event.setTime(medicationCursor.getString(medicationCursor.getColumnIndexOrThrow(COLUMN_TIME)));
                event.setDescription(medicationCursor.getString(medicationCursor.getColumnIndexOrThrow(COLUMN_DOSAGE)));
                event.setType("medication");

                events.add(event);
            } while (medicationCursor.moveToNext());
        }
        medicationCursor.close();

        // Récupérer les rendez-vous pour la date sélectionnée
        String appointmentQuery = "SELECT a.* FROM " + TABLE_APPOINTMENTS + " a " +
        "WHERE a." + COLUMN_USER_ID + " = ? AND a." + COLUMN_DATE + " = ? " +
                "ORDER BY a." + COLUMN_TIME_APPOINTMENT + " ASC";

                /*"SELECT a.* FROM " + TABLE_APPOINTMENTS + " a " +
                "JOIN " + TABLE_USERS + " u ON a." + COLUMN_USER_ID + " = u." + COLUMN_ID + " " +
                "WHERE u." + COLUMN_ID + " = ? AND date(a." + COLUMN_DATE + ") = ? " +
                "ORDER BY a." + COLUMN_TIME_APPOINTMENT + " ASC";*/
        Cursor appointmentCursor = db.rawQuery(appointmentQuery, new String[]{String.valueOf(userId),date});

        if (appointmentCursor.moveToFirst()) {
            do {
                ScheduleEvent event = new ScheduleEvent();
                event.setId(appointmentCursor.getInt(appointmentCursor.getColumnIndexOrThrow(COLUMN_ID)));
                event.setTitle(appointmentCursor.getString(appointmentCursor.getColumnIndexOrThrow(COLUMN_DOCTOR)) +
                        " - " + appointmentCursor.getString(appointmentCursor.getColumnIndexOrThrow(COLUMN_TYPE)));
                event.setTime(appointmentCursor.getString(appointmentCursor.getColumnIndexOrThrow(COLUMN_TIME_APPOINTMENT)));
                event.setDescription(appointmentCursor.getString(appointmentCursor.getColumnIndexOrThrow(COLUMN_TYPE)));
                event.setType("appointment");

                events.add(event);
            } while (appointmentCursor.moveToNext());
        }
        appointmentCursor.close();
        db.close();

        return events;
    }
}