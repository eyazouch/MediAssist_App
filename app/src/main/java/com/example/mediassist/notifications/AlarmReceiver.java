package com.example.mediassist.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.example.mediassist.R;
import com.example.mediassist.activities.MainActivity;
import java.io.File;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notification_id", 0);
        String channelId = intent.getStringExtra("channel_id");
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        String type = intent.getStringExtra("type");

        Log.d(TAG, "channel_id: " + channelId);
        Log.d(TAG, "title: " + title);
        Log.d(TAG, "message: " + message);

        // Vérifier que les données essentielles sont présentes
        if (channelId == null || title == null || message == null) {
            Log.e(TAG, "Missing essential notification data");
            return;
        }

        // Intent pour ouvrir l'application lorsque l'utilisateur clique sur la notification
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Son de notification par défaut
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Création du constructeur de notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 500, 250, 500}); // Ajouter une vibration

        // Personnalisation supplémentaire pour les rappels de médicaments
        if ("medication".equals(type)) {
            String medicationName = intent.getStringExtra("medication_name");
            String medicationDosage = intent.getStringExtra("medication_dosage");
            String medicationImagePath = intent.getStringExtra("medication_image_path");

            // Style étendu pour la notification
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                    .bigText(message)
                    .setBigContentTitle(title)
                    .setSummaryText(medicationDosage);

            notificationBuilder.setStyle(bigTextStyle);

            // Ajouter l'image du médicament si disponible
            if (medicationImagePath != null && !medicationImagePath.isEmpty()) {
                File imgFile = new File(medicationImagePath);
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    if (bitmap != null) {
                        notificationBuilder.setLargeIcon(bitmap);
                    }
                }
            }

            // Ajouter des actions pour la notification de médicament
            // Action "Marquer comme pris"
            Intent takenIntent = new Intent(context, NotificationActionReceiver.class);
            takenIntent.setAction("ACTION_MEDICATION_TAKEN");
            takenIntent.putExtra("notification_id", notificationId);
            PendingIntent takenPendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId * 3,
                    takenIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            notificationBuilder.addAction(
                    R.drawable.ic_check,
                    context.getString(R.string.mark_as_taken),
                    takenPendingIntent
            );

            // Action "Manger quelque chose"
            Intent eatIntent = new Intent(context, NotificationActionReceiver.class);
            eatIntent.setAction("ACTION_EAT_SOMETHING");
            eatIntent.putExtra("notification_id", notificationId);
            PendingIntent eatPendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId * 3 + 1,
                    eatIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            notificationBuilder.addAction(
                    R.drawable.ic_food,
                    context.getString(R.string.eat_something),
                    eatPendingIntent
            );

            // Action "Reporter"
            Intent snoozeIntent = new Intent(context, NotificationActionReceiver.class);
            snoozeIntent.setAction("ACTION_SNOOZE");
            snoozeIntent.putExtra("notification_id", notificationId);
            snoozeIntent.putExtra("medication_name", medicationName);
            snoozeIntent.putExtra("medication_dosage", medicationDosage);
            snoozeIntent.putExtra("medication_image_path", medicationImagePath);
            PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId * 3 + 2,
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            notificationBuilder.addAction(
                    R.drawable.ic_snooze,
                    context.getString(R.string.snooze),
                    snoozePendingIntent
            );
        }

        // Envoyer la notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}