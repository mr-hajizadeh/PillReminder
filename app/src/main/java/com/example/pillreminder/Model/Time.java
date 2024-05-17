package com.example.pillreminder.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "times",
        foreignKeys = {
                @ForeignKey(
                        entity = Reminder.class,
                        parentColumns = "id",
                        childColumns = "reminder_id",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                )
        }
)
public class Time {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private int reminder_id;
    private long time;

    public Time(int reminder_id, long time) {
        this.reminder_id = reminder_id;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getReminder_id() {
        return reminder_id;
    }

    public void setReminder_id(int reminder_id) {
        this.reminder_id = reminder_id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @NonNull
    @Override
    public String toString() {
        return "Time{" +
                "id=" + id +
                ", reminder_id=" + reminder_id +
                ", time=" + time +
                '}';
    }
}
