package com.example.booki;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to:
 * 1. Get + save FCM token on login
 * 2. Show local notifications manually from anywhere in the app
 *
 * USAGE:
 *   // On login success — call this to save token
 *   NotificationHelper.saveFcmToken();
 *
 *   // To show a local notification from code
 *   NotificationHelper.showLocal(context, "Order Placed!", "Your order has been confirmed.");
 */
public class NotificationHelper {

    private static final String CHANNEL_ID = "booki_channel";
    private static int notifId = 2000;

    // ─── Save FCM token to Firestore after login ──────────────────────────────
    public static void saveFcmToken() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();

        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("fcmToken", token);

                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(userId)
                            .update(data);
                });
    }

    // ─── Show a local notification from anywhere in the app ──────────────────
    public static void showLocal(Context context, String title, String body) {

        Intent intent = new Intent(context, Dashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Booki Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        manager.notify(notifId++, builder.build());
    }
}