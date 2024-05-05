package com.example.pillreminder.Database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.pillreminder.Model.Reminder;

import java.util.List;

public class ReminderViewModel extends AndroidViewModel {

    private final ReminderRepository repository;

    public ReminderViewModel(@NonNull Application application) {
        super(application);
        repository = new ReminderRepository(application);
    }

    public void insertReminder(Reminder reminder) {
        repository.insertReminder(reminder);
    }

    public void updateReminder(Reminder reminder) {
        repository.updateReminder(reminder);
    }

    public void deleteReminder(Reminder reminder) {
        repository.deleteReminder(reminder);
    }

    public LiveData<Reminder> getReminderById(int id) {
        return repository.getReminderById(id);
    }

    public LiveData<List<Reminder>> getReminders(long currentMilliSecond) {
        return repository.getReminders(currentMilliSecond);
    }
}
