package com.example.mediassist.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.mediassist.R;
import com.example.mediassist.database.DatabaseHelper;
import com.example.mediassist.utils.NotificationHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AddAppointmentActivity extends AppCompatActivity {

    private TextView tvDate, tvTime;
    private EditText etDoctor, etNotes;
    private RadioGroup rgType;
    private RadioButton rbConsultation, rbAnalysis;
    private Button btnSelectDate, btnSelectTime, btnCreate;
    private DatabaseHelper databaseHelper;
    private Calendar selectedDate;
    private Calendar selectedTime;
    private NotificationHelper notificationHelper;
    private boolean isEditing = false;
    private int appointmentId = -1;


// ...

    // Au début de la classe :
    private static final SimpleDateFormat UI_DATE_FORMAT =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final SimpleDateFormat DB_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        // Configuration de la barre d'outils
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.add_appointment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialisation des vues
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        etDoctor = findViewById(R.id.etDoctor);
        etNotes = findViewById(R.id.etNotes);
        rgType = findViewById(R.id.rgType);
        rbConsultation = findViewById(R.id.rbConsultation);
        rbAnalysis = findViewById(R.id.rbAnalysis);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnCreate = findViewById(R.id.btnCreate);


        // Initialisation de la base de données et de l'aide aux notifications
        databaseHelper = new DatabaseHelper(this);
        notificationHelper = new NotificationHelper(this);


        // Initialisation des dates et heures sélectionnées
        selectedDate = Calendar.getInstance();
        selectedTime = Calendar.getInstance();

       Intent intent = getIntent();
        if (intent != null && intent.hasExtra("appointment_id")) {
            appointmentId = intent.getIntExtra("appointment_id", -1);
            String date = intent.getStringExtra("date");
            String time = intent.getStringExtra("time");
            String doctor = intent.getStringExtra("doctor");
            String notes = intent.getStringExtra("notes");
            String type = intent.getStringExtra("type");

            tvDate.setText(date);
            tvTime.setText(time);
            etDoctor.setText(doctor);
            etNotes.setText(notes);

            if ("Consultation".equalsIgnoreCase(type)) {
                rbConsultation.setChecked(true);
            } else {
                rbAnalysis.setChecked(true);
            }

            if (date != null && !date.isEmpty() && time != null && !time.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date parsedDate = sdf.parse(date);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(parsedDate);
                    selectedDate.set(Calendar.YEAR, cal.get(Calendar.YEAR));
                    selectedDate.set(Calendar.MONTH, cal.get(Calendar.MONTH));
                    selectedDate.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));

                    String[] parts = time.split(":");
                    if (parts.length == 2) {
                        int hour = Integer.parseInt(parts[0]);
                        int minute = Integer.parseInt(parts[1]);
                        selectedTime.set(Calendar.HOUR_OF_DAY, hour);
                        selectedTime.set(Calendar.MINUTE, minute);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            btnCreate.setText("Modifier");
        } else {
            btnCreate.setText("créer un événement");
        }

        //btnSelectDate.setOnClickListener(v -> showDatePickerDialog());
        //btnSelectTime.setOnClickListener(v -> showTimePickerDialog());

        btnCreate.setOnClickListener(v -> {
            String date = tvDate.getText().toString().trim();
            String time = tvTime.getText().toString().trim();
            String doctor = etDoctor.getText().toString().trim();
            String notes = etNotes.getText().toString().trim();
            String type = rbConsultation.isChecked() ? "Consultation" : "Analyse";

            if (appointmentId != -1) {
                databaseHelper.updateAppointment(appointmentId, date, time, type, doctor, notes);
            } else {
                saveAppointment();
            }
        });


        // Gestionnaire d'événements pour le bouton de sélection de date
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Gestionnaire d'événements pour le bouton de sélection d'heure
        btnSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        /*// Gestionnaire d'événements pour le bouton de création
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAppointment();
            }
        });*/
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateDisplay();
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        // Définir la date minimale à aujourd'hui
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
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

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvDate.setText(sdf.format(selectedDate.getTime()));
    }

    private void updateTimeDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tvTime.setText(sdf.format(selectedTime.getTime()));
    }

    private void saveAppointment() {
        // Récupération des valeurs saisies
        String date = tvDate.getText().toString().trim();
        String time = tvTime.getText().toString().trim();
        String doctor = etDoctor.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String type = rbConsultation.isChecked() ? "Consultation" : "Analyse";

        // Validation des champs
        if (date.isEmpty() || time.isEmpty() || doctor.isEmpty()) {
            Toast.makeText(this, R.string.fields_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("DB", "Saving appointment date = [" + date + "]");

        SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        // Création d'un calendrier pour la notification
        Calendar appointmentDateTime = Calendar.getInstance();
        appointmentDateTime.set(
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH),
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE)
        );
        /*String rawDate = tvDate.getText().toString().trim();
        String dbDate = "";
        try {
            Date parsedDate = UI_DATE_FORMAT.parse(rawDate);  // Convertir en Date
            dbDate = DB_DATE_FORMAT.format(parsedDate);  // Convertir en format "yyyy-MM-dd"
        } catch (ParseException e) {
            e.printStackTrace();
            dbDate = rawDate;  // Si une erreur survient, conserver la valeur brute (à éviter)
        }
*/
        // === MODE MODIFICATION ===
        if (isEditing && appointmentId != -1) {
            boolean isUpdated = databaseHelper.updateAppointment(appointmentId, date, time, type, doctor, notes);

            if (isUpdated) {
                notificationHelper.scheduleAppointmentReminder(
                        appointmentId,
                        doctor + " - " + type,
                        type,
                        appointmentDateTime
                );
                Toast.makeText(this, "Rendez-vous mis à jour", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de la mise à jour du rendez-vous", Toast.LENGTH_SHORT).show();
            }

        } else {

            // === MODE AJOUT ===
            long result = databaseHelper.addAppointment(userId, date, time, type, doctor);

            if (result > 0) {
                notificationHelper.scheduleAppointmentReminder(
                        (int) result,
                        doctor + " - " + type,
                        type,
                        appointmentDateTime
                );
                Toast.makeText(this, R.string.appointment_added, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de l'ajout du rendez-vous", Toast.LENGTH_SHORT).show();
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