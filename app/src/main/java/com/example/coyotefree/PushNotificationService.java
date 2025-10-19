package com.example.coyotefree;

import  static android.content.ContentValues.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;

import java.io.IOException;

public class PushNotificationService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "general_channel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "onMessageReceived called");

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        //checking language preference option
        SharedPreferences langpreference3 = getSharedPreferences("langpreference", MODE_PRIVATE);
        String preferenceLang = langpreference3.getString("lpref", "en");

        // Handle data payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");

            Log.d(TAG, "Extracted title: " + title);
            Log.d(TAG, "Extracted body: " + body);

            String title_new = "";
            String body_new = "";

            if(preferenceLang.equalsIgnoreCase("en")){
                title_new = title;
                body_new = body;
            }else{
                // Translation with try/catch and logging
                try {
                    if (title != null) {
                        title_new = MyMemoryTranslate.translateText(title, "en", preferenceLang);
                        Log.d(TAG, "Translated title: " + title_new);
                    }
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "Title translation failed: " + e.getMessage(), e);
                }

                try {
                    if (body != null) {
                        body_new = MyMemoryTranslate.translateText(body, "en", preferenceLang);
                        Log.d(TAG, "Translated body: " + body_new);
                    }
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "Body translation failed: " + e.getMessage(), e);
                }
            }

            showNotification(title_new, body_new);
        }

        // Skip notification payload (because you’re sending only data payload now)
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body (ignored): " + remoteMessage.getNotification().getBody());
        }
    }

    private void showNotification(String title, String body) {
        Log.d(TAG, "Preparing notification");

        // Create channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "Creating notification channel (O+)");
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "General Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created");
            } else {
                Log.e(TAG, "NotificationManager is null!");
            }
        }

        // Open MainActivity when notification is tapped
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int flag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                : PendingIntent.FLAG_UPDATE_CURRENT;

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, flag);

        Log.d(TAG, "Building notification with title: " + title + " body: " + body);

        // Use BigTextStyle for longer body
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.animal10) // change to your app icon
                .setContentTitle(title != null ? title : "Default Title")
                .setContentText(body != null ? body : "Default Body") // small version (collapsed)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body != null ? body : "Default Body")) // expanded full text
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Show it
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "POST_NOTIFICATIONS permission not granted!");
            return;
        }

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
        Log.d(TAG, "Notification shown with ID: " + notificationId);
    }

}

