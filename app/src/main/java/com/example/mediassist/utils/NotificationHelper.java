package com.example.mediassist.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.mediassist.R;
import com.example.mediassist.notifications.AlarmReceiver;
import java.util.Calendar;

public class NotificationHelper {

    private static final String CHANNEL_ID_MEDICATIONS = "medications_channel";
    private static final String CHANNEL_ID_APPOINTMENTS = "appointments_channel";
    private Context context;
    private AlarmManager alarmManager;
    private static final String TAG = "NotificationHelper";

    public NotificationHelper(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        // Création des canaux de notification (requis pour Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Canal pour les rappels de médicaments
            NotificationChannel medicationsChannel = new NotificationChannel(
                    CHANNEL_ID_MEDICATIONS,
                    "Rappels de médicaments",
                    NotificationManager.IMPORTANCE_HIGH
            );
            medicationsChannel.setDescription("Notifications pour les rappels de médicaments");
            medicationsChannel.enableVibration(true);

            // Canal pour les rappels de rendez-vous
            NotificationChannel appointmentsChannel = new NotificationChannel(
                    CHANNEL_ID_APPOINTMENTS,
                    "Rappels de rendez-vous",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            appointmentsChannel.setDescription("Notifications pour les rappels de rendez-vous");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(medicationsChannel);
                notificationManager.createNotificationChannel(appointmentsChannel);
                Log.d(TAG, "Notification channels created successfully");
            } else {
                Log.e(TAG, "NotificationManager is null");
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    public void scheduleMedicationReminder(int id, String medicationName, String dosage, Calendar time) {
        // Création de l'intent pour le broadcast receiver
       Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("notification_id", id);
        intent.putExtra("channel_id", CHANNEL_ID_MEDICATIONS);
        intent.putExtra("title", context.getString(R.string.medication_reminder));
        intent.putExtra("message", context.getString(R.string.time_to_take) + " " + medicationName + " - " + dosage);
        intent.putExtra("medication_name", medicationName);
        intent.putExtra("medication_dosage", dosage);
        intent.putExtra("type", "medication");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );




        // Configuration de l'alarme récurrente
        // Définir l'alarme pour l'heure spécifiée aujourd'hui
        Calendar currentTime = Calendar.getInstance();
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
        alarmTime.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
        alarmTime.set(Calendar.SECOND, 0);

        // Si l'heure est déjà passée aujourd'hui, programmer pour demain
        if (alarmTime.before(currentTime)) {
            alarmTime.add(Calendar.DAY_OF_MONTH, 1);
            Log.d(TAG, "Time already passed today, scheduling for tomorrow");
        }

        // Programmer l'alarme exacte
        try {
            // Programmer l'alarme exacte
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime.getTimeInMillis(),
                    pendingIntent
            );

            Log.d(TAG, "Medication reminder scheduled successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error scheduling medication reminder: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    public void scheduleAppointmentReminder(int id, String title, String type, Calendar time) {
        // Création de l'intent pour le broadcast receiver
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("notification_id", id);
        intent.putExtra("channel_id", CHANNEL_ID_APPOINTMENTS);
        intent.putExtra("title", context.getString(R.string.appointment_reminder));
        intent.putExtra("message", context.getString(R.string.upcoming_appointment) + " - " + title);
        intent.putExtra("appointment_title", title);
        intent.putExtra("appointment_type", type);
        intent.putExtra("type", "appointment");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                id + 1000, // Offset pour éviter les conflits avec les IDs de médicaments
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Programmer la notification pour 1 heure avant le rendez-vous
        Calendar reminderTime = (Calendar) time.clone();
        reminderTime.add(Calendar.HOUR_OF_DAY, -1);

        Calendar currentTime = Calendar.getInstance();

        // Programmer l'alarme exacte
        if (reminderTime.after(currentTime)) {
            try {
                // Programmer l'alarme exacte
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime.getTimeInMillis(),
                        pendingIntent
                );
                Log.d(TAG, "Appointment reminder scheduled successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error scheduling appointment reminder: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Appointment time is in the past, not scheduling reminder");
        }
    }

    public void cancelMedicationReminder(int id) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }

    public void cancelAppointmentReminder(int id) {
        try {
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    id + 1000,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Log.d(TAG, "Appointment reminder cancelled successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error cancelling appointment reminder: " + e.getMessage());
        }
    }
}