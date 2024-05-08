package com.example.pillreminder.Utils;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.pillreminder.Activity.MainActivity;
import com.example.pillreminder.Model.Reminder;
import com.example.pillreminder.R;

import java.io.IOException;
import java.util.Objects;


public class NotificationService extends Service {

    private static final String CHANNEL_ID = "channel_id";
    public static final String STOP = "com.example.pill-reminder.STOP_REMINDER";
    public static final String SNOOZE = "com.example.pill-reminder.SNOOZE_REMINDER";
    private static final int NOTIFICATION_ID = 3698;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint({"ForegroundServiceType", "NotificationPermission"})
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle data = intent.getExtras();
        Log.i("TAG", "onReceive:Action " + intent.getAction());
        if (data != null) {
            Reminder reminder = data.getParcelable("reminder");

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            String musicPath = Objects.requireNonNull(reminder).getAudioPath();
            Log.i("TAG", "onStartCommand: " + musicPath);

            try {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();

                if (musicPath != null && !musicPath.isEmpty()) {
                    mediaPlayer.setAudioAttributes(audioAttributes);
                    mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(musicPath));
                    mediaPlayer.setOnPreparedListener(MediaPlayer::start);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.prepareAsync();
                    Log.i("TAG", "onReceive: " + musicPath);
                } else {
                    mediaPlayer = MediaPlayer.create(this, Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.notif_sound));
                    mediaPlayer.setAudioAttributes(audioAttributes);
                    mediaPlayer.setOnPreparedListener(MediaPlayer::start);
                    mediaPlayer.setLooping(true);
                }
            } catch (IOException e) {
                Log.i("TAG", "onReceive: " + e.getMessage());
                e.printStackTrace();
            }
            Intent stopIntent = new Intent(this, AlarmReceiver.class);
            stopIntent.setAction(STOP);
            PendingIntent stopPendingIntent =
                    PendingIntent.getBroadcast(this, 9999, stopIntent, PendingIntent.FLAG_IMMUTABLE);

            Intent snoozeIntent = new Intent(this, AlarmReceiver.class);
            snoozeIntent.setAction(SNOOZE);
            snoozeIntent.putExtra("reminder", reminder);
            PendingIntent snoozePendingIntent =
                    PendingIntent.getBroadcast(this, 8888, snoozeIntent, PendingIntent.FLAG_IMMUTABLE);

            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1111, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle(reminder.getTitle());

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_pause)
                    .setContentTitle("زمان مصرف دارو شما رسیده")
                    .setContentText(reminder.getTitle())
                    .setStyle(bigTextStyle)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false)
                    .setSilent(true)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .addAction(R.drawable.ic_play, "وقفه", snoozePendingIntent)
                    .addAction(R.drawable.ic_play, "بستن", stopPendingIntent);

            notificationManager.notify(NOTIFICATION_ID, builder.build());
            startForeground(NOTIFICATION_ID, builder.build());

        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
