package com.rcm.productpricelist.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rcm.productpricelist.R;
import com.rcm.productpricelist.activity.NewProductActivity;
import com.rcm.productpricelist.activity.ProductActivity;
import com.rcm.productpricelist.activity.RecentActivity;
import com.rcm.productpricelist.models.NotiData;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    private static String TAG = "MyFirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Data: " + remoteMessage.getData());
            NotiData data = new NotiData(remoteMessage.getData().get("body"),
                    remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("sound"),
                    remoteMessage.getData().get("icon"),
                    remoteMessage.getData().get("priority"),
                    remoteMessage.getData().get("Activity"),
                    remoteMessage.getData().get("ExtraValue"));

            createNotification(data);
        }
    }

    private void createNotification(NotiData data) {

        int SDK_INT = Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }

        Intent resultIntent = new Intent();
        if (data.getActivity().equals("url"))
        {
            resultIntent = new Intent(Intent.ACTION_VIEW);
            resultIntent.setData(Uri.parse(data.getExtraValue()));
        }
        else if (data.getActivity().equals("pricelist"))
        {
            resultIntent = new Intent(this, ProductActivity.class);
            resultIntent.putExtra("notify",true);
        }
        else if (data.getActivity().equals("newpricelist"))
        {
            resultIntent = new Intent(this, NewProductActivity.class);
            resultIntent.putExtra("notify",true);
        }
        else if (data.getActivity().equals("pricechange"))
        {
            resultIntent = new Intent(this, RecentActivity.class);
            resultIntent.putExtra("notify",true);
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Bitmap imageBitmap = null;
        try {
            InputStream inputStream = new URL(data.getIcon()).openStream();
            imageBitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = getApplicationContext().getString(R.string.app_name);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(data.getTitle())
                .setContentText(data.getBody())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(data.getBody()))
                .setContentIntent(resultPendingIntent);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            notificationBuilder.setPriority(Notification.PRIORITY_MAX);
        } else {
            notificationBuilder.setPriority(NotificationManager.IMPORTANCE_HIGH);
        }

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder.setSound(alarmSound).setVibrate(new long[]{100, 200, 300, 400});

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.shouldShowLights();
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }

        if (imageBitmap != null) {
            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(imageBitmap));
        }

        notificationManager.notify(1, notificationBuilder.build());
    }
}