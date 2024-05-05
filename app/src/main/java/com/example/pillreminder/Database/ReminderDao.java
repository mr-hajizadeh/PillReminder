package com.example.pillreminder.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pillreminder.Model.Reminder;

import java.util.List;

@Dao
public interface ReminderDao {

    @Insert
    void insertReminder(Reminder reminder);

    @Update
    void updateReminder(Reminder reminder);

    @Delete
    void deleteReminder(Reminder reminder);

    @Query("SELECT * FROM Reminders WHERE id = :id")
    LiveData<Reminder> getReminderById(int id);

    @Query("SELECT * FROM Reminders " +
            "WHERE startMilliSecond < :currentMilliSecond AND endMilliSecond >= :currentMilliSecond")
    LiveData<List<Reminder>> getReminderByDate(Long currentMilliSecond);


}
