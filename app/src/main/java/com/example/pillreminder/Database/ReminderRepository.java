package com.example.pillreminder.Database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.pillreminder.Model.Reminder;

import java.util.List;

public class ReminderRepository {

    private final ReminderDao reminderDao;

    public ReminderRepository(Application application) {
        ReminderDatabase reminderDatabase = ReminderDatabase.getInstance(application);
        reminderDao = reminderDatabase.reminderDao();
    }

    public void insertReminder(Reminder reminder, DatabaseListener databaseListener) {
        new InsertAsyncTask(reminderDao, databaseListener).execute(reminder);
    }

    public void updateReminder(Reminder reminder) {
        new UpdateAsyncTask(reminderDao).execute(reminder);
    }

    public void deleteReminder(Reminder reminder) {
        new DeleteAsyncTask(reminderDao).execute(reminder);
    }

    public LiveData<List<Reminder>> getReminders(long currentMilliSecond) {
        return reminderDao.getReminderByDate(currentMilliSecond);
    }

    public LiveData<Reminder> getReminderById(int id) {
        return reminderDao.getReminderById(id);
    }

    private static class InsertAsyncTask extends AsyncTask<Reminder, Void, Long> {

        private final ReminderDao reminderDao;
        private final DatabaseListener databaseListener;

        public InsertAsyncTask(ReminderDao reminderDao, DatabaseListener databaseListener) {
            this.reminderDao = reminderDao;
            this.databaseListener = databaseListener;
        }

        @Override
        protected Long doInBackground(Reminder... reminders) {
            databaseListener.onInsert(reminderDao.insertReminder(reminders[0]));
            return null;
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<Reminder, Void, Void> {

        private final ReminderDao reminderDao;

        public UpdateAsyncTask(ReminderDao reminderDao) {
            this.reminderDao = reminderDao;
        }

        @Override
        protected Void doInBackground(Reminder... reminders) {
            reminderDao.updateReminder(reminders[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Reminder, Void, Void> {

        private final ReminderDao reminderDao;

        public DeleteAsyncTask(ReminderDao reminderDao) {
            this.reminderDao = reminderDao;
        }

        @Override
        protected Void doInBackground(Reminder... reminders) {
            reminderDao.deleteReminder(reminders[0]);
            return null;
        }
    }

}
