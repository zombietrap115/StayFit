package com.example.stayfit.Settings;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.stayfit.R;
import com.example.stayfit.WeightManagement.WeightInputActivity;

/**
 * Diese Klasse ist ein BroadcastReceiver, der darauf reagiert, wenn ein Alarm ausgelöst wird.
 * Sie erstellt und zeigt eine Benachrichtigung, die den Benutzer dazu auffordert, sein Gewicht zu verfolgen.
 */
public class AlarmReceiver extends BroadcastReceiver {

    /**
     * Diese Methode wird aufgerufen, wenn der BroadcastReceiver einen Broadcast-Intent empfängt.
     *
     * @param context Der Context, in dem der Receiver läuft.
     * @param intent  Der Intent, der empfangen wurde.
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        // Erstellen eines Intents für die Gewichtseingabe-Aktivität
        Intent i = new Intent(context, WeightInputActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Erstellen eines ausstehenden Intents, das später von der Benachrichtigung verwendet werden kann
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);

        // Erstellen des Notification-Builder mit Einstellungen für die Benachrichtigung
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "weightinput")
                .setSmallIcon(R.drawable.ic_launcher_background) // Icon der Benachrichtigung setzen
                .setContentTitle("Weight tracking") // Titel der Benachrichtigung setzen
                .setContentText("Don't forget to track your weight") // Text der Benachrichtigung setzen
                .setAutoCancel(true) // Benachrichtigung beim Anklicken automatisch schließen
                .setDefaults(NotificationCompat.DEFAULT_ALL) // Standardbenachrichtigungseinstellungen verwenden
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Priorität der Benachrichtigung setzen
                .setContentIntent(pendingIntent); // ausstehender Intent setzen

        // Erhalten der NotificationManagerCompat-Instanz
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        // Anzeigen der Benachrichtigung mit einer eindeutigen ID (in diesem Fall 123)
        notificationManagerCompat.notify(123, builder.build());
    }
}

