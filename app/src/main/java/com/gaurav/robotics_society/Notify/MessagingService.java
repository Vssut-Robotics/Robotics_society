package com.gaurav.robotics_society.Notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.firebase.client.Firebase;
import com.gaurav.robotics_society.R;
import com.gaurav.robotics_society.events;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by GAURAV on 11-02-2019.
 */

public class MessagingService extends FirebaseMessagingService {
    ///if()

    final SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    final String user_id = (mSharedPreference.getString("user_id", "id"));
    private Firebase mfire;

    @Override
    public void onNewToken(final String s) {
        super.onNewToken(s);
        Firebase.setAndroidContext(this);
        mfire = new Firebase("https://robotics-society-99fe7.firebaseio.com/Users/" + user_id);
        Firebase Fullname = mfire.child("token");
        Fullname.setValue(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        send_message(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
    }

    public void send_message(String body, String title) {

        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, events.class), 0);

        NotificationHelper noti;
        int NOTI_SECONDARY1 = 1202;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            noti = new NotificationHelper(this);
            Notification.Builder nb = noti.getNotification2();
            nb.setSmallIcon(R.drawable.ic_stat_name);
            nb.setContentTitle(title);
            nb.setContentText(body);
            nb.setContentIntent(pi);

            noti.notify(NOTI_SECONDARY1, nb);
        } else {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_stat_name)
                            .setContentIntent(pi)
                            .setAutoCancel(true)
                            .setContentTitle(title)
                            .setContentText(body);

            builder.setContentIntent(pi);
            // Add as notification
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());
        }
    }
}
