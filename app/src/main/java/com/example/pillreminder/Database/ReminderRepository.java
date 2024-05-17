package com.example.pillreminder.Database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.pillreminder.Model.Reminder;
import com.example.pillreminder.Model.Time;

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

    public void insertTimes(Time time) {
        new InsertTimeAsyncTask(reminderDao).execute(time);
    }

    public void updateReminder(Reminder reminder) {
        new UpdateAsyncTask(reminderDao).execute(reminder);
    }

    public void deleteReminder(Reminder reminder) {
        new DeleteAsyncTask(reminderDao).execute(reminder);
    }

    public void deleteTimes(int reminderId) {
        new DeleteTimeAsyncTask(reminderDao).execute(reminderId);
    }

    public LiveData<List<Reminder>> getReminders(long currentMilliSecond) {
        return reminderDao.getReminderByDate(currentMilliSecond);
    }

    public LiveData<List<Time>> getTimeByReminderId(int reminderId, long startTime, long endTime) {
        return reminderDao.getTimesByReminderId(reminderId, startTime, endTime);
    }

    public LiveData<Long> firstUpcomingTime(long currentTime) {
        return reminderDao.firstUpcomingTime(currentTime);
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

    private static class InsertTimeAsyncTask extends AsyncTask<Time, Void, Void> {

        private final ReminderDao reminderDao;

        public InsertTimeAsyncTask(ReminderDao reminderDao) {
            this.reminderDao = reminderDao;
        }

        @Override
        protected Void doInBackground(Time... times) {
            reminderDao.insertTimes(times[0]);
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

    private static class DeleteTimeAsyncTask extends AsyncTask<Integer, Void, Void> {

        private final ReminderDao reminderDao;

        public DeleteTimeAsyncTask(ReminderDao reminderDao) {
            this.reminderDao = reminderDao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            reminderDao.deleteTimes(integers[0]);
            return null;
        }
    }

}
