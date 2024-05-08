package com.example.pillreminder.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.pillreminder.Model.Reminder;

public class AlarmReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle data = intent.getExtras();
        Intent startService = new Intent(context, NotificationService.class);
        if (intent.getAction() != null) {
            if (intent.getAction().equals(NotificationService.STOP)) {
                context.stopService(startService);
            } else if (intent.getAction().equals(NotificationService.SNOOZE)) {
//                Reminder reminder = data.getParcelable("reminder");
//                Toast.makeText(context, String.valueOf(reminder.getId()), Toast.LENGTH_SHORT).show();
//                changeStatus(reminder.getId(), 1, context);
                context.stopService(startService);
            } else if (intent.getAction().equals("NO")) {
//                Reminder reminder = data.getParcelable("reminder");
//                Toast.makeText(context, String.valueOf(reminder.getId()), Toast.LENGTH_SHORT).show();
//                changeStatus(reminder.getId(), 2, context);
//                context.stopService(startService);
            }
        } else {
            if (data != null) {
                Reminder reminder = data.getParcelable("reminder");
                Log.i("TAG", "onReceive:Reminder " + reminder);
                startService.putExtra("reminder", reminder);
                context.startForegroundService(startService);
                context.startService(startService);
            }
        }
    }
}
