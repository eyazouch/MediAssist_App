package com.example.mediassist.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mediassist.R;
import com.example.mediassist.adapters.ScheduleAdapter;
import com.example.mediassist.database.DatabaseHelper;
import com.example.mediassist.models.ScheduleEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private TextView tvSelectedDate, tvNoEvents;
    private LinearLayout eventContainer;
    private DatabaseHelper databaseHelper;
    private ScheduleAdapter adapter;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Configuration de la barre d'outils
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.schedule);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialisation des vues
        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recyclerView);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvNoEvents = findViewById(R.id.tvNoEvents);
        eventContainer = findViewById(R.id.eventContainer);

        SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        // Initialisation de la base de données
        databaseHelper = new DatabaseHelper(this);



        // Configuration du RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Définir la date sélectionnée par défaut (aujourd'hui)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(new Date());
        tvSelectedDate.setText(formatDateForDisplay(selectedDate));

        // Charger les événements pour aujourd'hui
        loadEventsForDate(selectedDate);

        // Gestionnaire d'événements pour le changement de date dans le calendrier
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                selectedDate = sdf.format(calendar.getTime());
                tvSelectedDate.setText(formatDateForDisplay(selectedDate));
                loadEventsForDate(selectedDate);
            }
        });
    }
    private String formatDateForDisplay(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            e.printStackTrace();
            return date;
        }
    }

    private void loadEventsForDate(String date) {
        // Charger les médicaments et les rendez-vous pour la date sélectionnée
        SharedPreferences prefs = getSharedPreferences("MediAssistPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        String convertedDate = formatDateForDisplay(selectedDate);
        Log.d("DB", "Saving appointment date = "+ convertedDate);
        List<ScheduleEvent> events = databaseHelper.getEventsForDate(userId, convertedDate);

        if (events.isEmpty()) {
            tvNoEvents.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoEvents.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new ScheduleAdapter(this, events);
            recyclerView.setAdapter(adapter);
            Log.d("ScheduleActivity", "Events loaded: " + events.size());  // Afficher le nombre d'événements trouvés
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