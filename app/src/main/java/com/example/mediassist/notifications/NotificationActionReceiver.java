package com.example.mediassist.notifications;

/*import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mediassist.R;

public class NotificationActionReceiver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_action_receiver);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}*/

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.mediassist.R;

import java.util.Calendar;

public class NotificationActionReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationActionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Action received: " + intent.getAction());

        try {
            String action = intent.getAction();
            int notificationId = intent.getIntExtra("notification_id", -1);

            if (notificationId == -1) {
                Log.e(TAG, "Invalid notification ID");
                return;
            }

            // Annuler la notification actuelle
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(notificationId);
                Log.d(TAG, "Cancelled notification with ID: " + notificationId);
            }

            if (action == null) {
                Log.e(TAG, "Action is null");
                return;
            }

            switch (action) {
                case "ACTION_MEDICATION_TAKEN":
                    Log.d(TAG, "Medication marked as taken");
                    Toast.makeText(context, "Médicament marqué comme pris", Toast.LENGTH_SHORT).show();
                    // Ici, vous pourriez ajouter du code pour enregistrer que le médicament a été pris
                    break;

                case "ACTION_EAT_SOMETHING":
                    Log.d(TAG, "Eat something action triggered");
                    Toast.makeText(context, "N'oubliez pas de manger quelque chose avant de prendre ce médicament", Toast.LENGTH_SHORT).show();
                    break;

                case "ACTION_SNOOZE":
                    Log.d(TAG, "Snooze action triggered");

                    // Récupérer les informations du médicament
                    String medicationName = intent.getStringExtra("medication_name");
                    String medicationDosage = intent.getStringExtra("medication_dosage");
                    String medicationImagePath = intent.getStringExtra("medication_image_path");

                    // Reporter la notification de 15 minutes
                    snoozeNotification(context, notificationId, medicationName, medicationDosage, medicationImagePath);

                    Toast.makeText(context, "Rappel reporté de 15 minutes", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Log.d(TAG, "Unknown action: " + action);
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing action: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void snoozeNotification(Context context, int notificationId, String medicationName, String medicationDosage, String medicationImagePath) {
        try {
            // Créer un intent pour le AlarmReceiver
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("notification_id", notificationId);
            intent.putExtra("channel_id", "medications_channel");
            intent.putExtra("title", context.getString(R.string.medication_reminder));
            intent.putExtra("message", context.getString(R.string.time_to_take) + " " + medicationName + " - " + medicationDosage);
            intent.putExtra("medication_name", medicationName);
            intent.putExtra("medication_dosage", medicationDosage);
            intent.putExtra("medication_image_path", medicationImagePath);
            intent.putExtra("type", "medication");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // Programmer l'alarme pour 15 minutes plus tard
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, 15);

                Log.d(TAG, "Scheduling snoozed notification for: " + calendar.getTime().toString());

                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );

                Log.d(TAG, "Snoozed notification scheduled successfully");
            } else {
                Log.e(TAG, "AlarmManager is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error snoozing notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
}