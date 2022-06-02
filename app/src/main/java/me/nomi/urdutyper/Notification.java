package me.nomi.urdutyper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class Notification {


    public static void create(Context context, String message, Uri uri) {
        Bitmap image = null;
        try {
            image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("UrduTyper",
                    "UrduTyper",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Your file has been saved!");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 1000, 200, 340});
            channel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder; // clear notification after click
        mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), "UrduTyper")
                .setSmallIcon(R.mipmap.ic_launcher_icons_custom) // notification icon
                .setLargeIcon(image)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(image)
                        .bigLargeIcon(null))
                .setContentTitle("Your file has been saved!") // title for notification
                .setContentText(message) // message for notification
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100, 1000, 200, 340})
                .setTicker("UrduTyper - " + "Your file has been saved!")
                .setAutoCancel(true);
//
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setDataAndType(uri, "image/*");
        PendingIntent pi = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify((int) System.currentTimeMillis() % 10000, mBuilder.build());
    }
}
