package com.example.pillreminder.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pillreminder.Model.Reminder;
import com.example.pillreminder.Model.Time;

import java.util.List;

@Dao
public interface ReminderDao {

    @Insert
    long insertReminder(Reminder reminder);

    @Insert
    void insertTimes(Time time);

    @Update
    void updateReminder(Reminder reminder);

    @Delete
    void deleteReminder(Reminder reminder);

    @Query("SELECT * FROM Reminders WHERE id = :id")
    LiveData<Reminder> getReminderById(int id);

    @Query("SELECT * FROM Reminders " +
            "WHERE startMilliSecond < :currentMilliSecond AND endMilliSecond >= :currentMilliSecond")
    LiveData<List<Reminder>> getReminderByDate(Long currentMilliSecond);

    @Query("SELECT * FROM times WHERE reminder_id = :reminderId AND time <= :endTime AND time >= :startTime")
    LiveData<List<Time>> getTimesByReminderId(int reminderId, long startTime, long endTime);

    @Query("DELETE FROM times WHERE reminder_id = :reminderId")
    void deleteTimes(int reminderId);

    @Query("SELECT time FROM times WHERE time >= :currentTime LIMIT 1")
    LiveData<Long> firstUpcomingTime(long currentTime);

}
