package com.example.pillreminder.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "Reminders")
public class Reminder implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String Description;
    private String reminderHour;
    private String startReminderHour;
    private int audioType;
    private String audioPath;
    private boolean limitedDay;
    private int alarmCounter;
    private int days;
    private List<Long> hours;
    private long startMilliSecond;
    private long endMilliSecond;

    protected Reminder(Parcel in) {
        id = in.readInt();
        title = in.readString();
        Description = in.readString();
        reminderHour = in.readString();
        startReminderHour = in.readString();
        audioType = in.readInt();
        audioPath = in.readString();
        limitedDay = in.readByte() != 0;
        alarmCounter = in.readInt();
        days = in.readInt();
        startMilliSecond = in.readLong();
        endMilliSecond = in.readLong();
    }

    public Reminder() {
    }

    public static final Creator<Reminder> CREATOR = new Creator<Reminder>() {
        @Override
        public Reminder createFromParcel(Parcel in) {
            return new Reminder(in);
        }

        @Override
        public Reminder[] newArray(int size) {
            return new Reminder[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getReminderHour() {
        return reminderHour;
    }

    public void setReminderHour(String reminderHour) {
        this.reminderHour = reminderHour;
    }

    public String getStartReminderHour() {
        return startReminderHour;
    }

    public void setStartReminderHour(String startReminderHour) {
        this.startReminderHour = startReminderHour;
    }

    public int getAudioType() {
        return audioType;
    }

    public void setAudioType(int audioType) {
        this.audioType = audioType;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public boolean isLimitedDay() {
        return limitedDay;
    }

    public void setLimitedDay(boolean limitedDay) {
        this.limitedDay = limitedDay;
    }

    public int getAlarmCounter() {
        return alarmCounter;
    }

    public void setAlarmCounter(int alarmCounter) {
        this.alarmCounter = alarmCounter;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public List<Long> getHours() {
        return hours;
    }

    public void setHours(List<Long> hours) {
        this.hours = hours;
    }

    public long getStartMilliSecond() {
        return startMilliSecond;
    }

    public void setStartMilliSecond(long startMilliSecond) {
        this.startMilliSecond = startMilliSecond;
    }

    public long getEndMilliSecond() {
        return endMilliSecond;
    }

    public void setEndMilliSecond(long endMilliSecond) {
        this.endMilliSecond = endMilliSecond;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(Description);
        dest.writeString(reminderHour);
        dest.writeString(startReminderHour);
        dest.writeInt(audioType);
        dest.writeString(audioPath);
        dest.writeByte((byte) (limitedDay ? 1 : 0));
        dest.writeInt(alarmCounter);
        dest.writeInt(days);
        dest.writeLong(startMilliSecond);
        dest.writeLong(endMilliSecond);
    }
}
