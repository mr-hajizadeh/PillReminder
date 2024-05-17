package com.example.pillreminder.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.pillreminder.Model.Reminder;
import com.example.pillreminder.Model.Time;

@Database(entities = {Reminder.class, Time.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class ReminderDatabase extends RoomDatabase {

    private static ReminderDatabase instance;

    public abstract ReminderDao reminderDao();

    public static synchronized ReminderDatabase getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(
                    context,
                    ReminderDatabase.class, "ReminderDatabase").build();
        return instance;
    }

}
