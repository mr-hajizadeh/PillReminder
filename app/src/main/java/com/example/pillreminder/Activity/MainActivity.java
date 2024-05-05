package com.example.pillreminder.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pillreminder.Adapter.DayAdapter;
import com.example.pillreminder.Adapter.MonthAdapter;
import com.example.pillreminder.Adapter.OnDayItemListener;
import com.example.pillreminder.Adapter.OnMonthItemListener;
import com.example.pillreminder.Adapter.OnReminderItemClick;
import com.example.pillreminder.Adapter.ReminderAdapter;
import com.example.pillreminder.Database.ReminderViewModel;
import com.example.pillreminder.Model.Day;
import com.example.pillreminder.Model.Month;
import com.example.pillreminder.Model.Reminder;
import com.example.pillreminder.databinding.ActivityMainBinding;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import saman.zamani.persiandate.PersianDate;

public class MainActivity extends AppCompatActivity implements OnMonthItemListener, OnDayItemListener, OnReminderItemClick {

    private final int[] jalaliDayInMonth = {31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29};
    private ActivityMainBinding binding;
    private DayAdapter dayAdapter;
    private HashMap<Integer, List<Day>> monthWithDay;
    private PersianDate persianDate;
    private ReminderViewModel reminderViewModel;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);
        initViews();

        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        } else {
            createNotificationChannel();
        }

    }

    private void initViews() {

        monthWithDay = new HashMap<>();
        persianDate = new PersianDate();

        RecyclerView monthView = binding.monthView;
        monthView.setAdapter(new MonthAdapter(generateMonthNames(), this));
        monthView.scrollToPosition(persianDate.getShMonth() - 1);

        RecyclerView dayView = binding.dayView;
        dayAdapter = new DayAdapter(this);
        dayAdapter.setDayList(generateDay(persianDate));
        dayView.setAdapter(dayAdapter);
        dayView.scrollToPosition(persianDate.getShDay() - 1);

        initBottomAppBar();
        initReminderView();

    }

    private void initReminderView() {

        RecyclerView reminderView = binding.reminderView;
        reminderViewModel.getReminders(persianDate.getTime()).observe(this, reminders -> {
            if (reminders != null) {
                ReminderAdapter reminderAdapter = new ReminderAdapter(reminders, persianDate, this);
                reminderView.setAdapter(reminderAdapter);
            }
        });

    }

    private void initBottomAppBar() {

        BottomAppBar bottomAppBar = binding.bottomAppBar;
        bottomAppBar.setNavigationOnClickListener(v -> Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show());

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddActivity.class);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(persianDate.getTime());
            intent.putExtra("date", persianDate.getTime());
            startActivity(intent);
        });

    }


    private List<Month> generateMonthNames() {
        List<Month> monthNames = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            PersianDate persianDate = new PersianDate();
            persianDate.setShMonth(i);
            Month month = new Month();
            month.setTitle(persianDate.getMonthName());
            month.setMonthNumber(i);
            month.setStartOfMonthDate(getStartOfMonthDate(persianDate));
            month.setEndOfMonthDate(getEndOfMonthDate(persianDate));
            month.setPersianDate(persianDate);
            monthNames.add(month);
        }
        return monthNames;
    }

    @NonNull
    private Date getStartOfMonthDate(PersianDate persianDate) {
        PersianDate startOfMonthDate = new PersianDate(persianDate.toDate());

        startOfMonthDate.setShDay(1);
        startOfMonthDate.setHour(0);
        startOfMonthDate.setMinute(0);
        startOfMonthDate.setSecond(0);

        return new Date(startOfMonthDate.getTime());
    }

    private Date getEndOfMonthDate(PersianDate persianDate) {

        int startYear = persianDate.getShYear();
        int startMonth = persianDate.getShMonth();

        int endDay;

        if (startMonth >= 1 && startMonth <= 6)
            endDay = 31;
        else if (startMonth >= 7 && startMonth <= 11)
            endDay = 30;
        else if (persianDate.isLeap())
            endDay = 30;
        else
            endDay = 29;


        PersianDate endDate = new PersianDate();
        endDate.setShYear(startYear);
        endDate.setShMonth(startMonth);
        endDate.setShDay(endDay);
        endDate.setHour(23);
        endDate.setMinute(59);
        endDate.setSecond(59);

        return new Date(endDate.getTime());
    }

    private List<Day> generateDay(PersianDate persianDate) {

        if (monthWithDay == null || monthWithDay.get(persianDate.getShMonth()) == null) {
            List<Day> daysMonth = new ArrayList<>();
            for (int i = 1; i <= jalaliDayInMonth[persianDate.getShMonth() - 1]; i++) {
                PersianDate currentPersianDate = new PersianDate(persianDate.toDate());
                currentPersianDate.setShDay(i);
                Day day = new Day();
                day.setId(i);
                day.setTitle(currentPersianDate.dayName());
                day.setDayDate(new Date(currentPersianDate.getTime()));
                day.setPersianDate(currentPersianDate);

                Calendar startCalendar = Calendar.getInstance();
                startCalendar.set(Calendar.HOUR_OF_DAY, 0);
                startCalendar.set(Calendar.MINUTE, 0);
                startCalendar.set(Calendar.SECOND, 0);
                Date startDate = new Date(startCalendar.getTimeInMillis());

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.set(Calendar.HOUR_OF_DAY, 23);
                endCalendar.set(Calendar.MINUTE, 59);
                endCalendar.set(Calendar.SECOND, 59);
                Date endDate = new Date(endCalendar.getTimeInMillis());

                Date currentDate = day.getDayDate();

                day.setActive(currentDate.after(startDate) && currentDate.before(endDate));
                daysMonth.add(day);
            }
            monthWithDay.put(persianDate.getShMonth(), daysMonth);
        }

        return monthWithDay.get(persianDate.getShMonth());

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "reminder";
            String description = "reminderDescription";
            int importance = NotificationManager.IMPORTANCE_HIGH; // تنظیم اهمیت به HIGH
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted)
                    createNotificationChannel();
            }
    );

    @Override
    public void onClick(Month month) {
        dayAdapter.setDayList(generateDay(month.getPersianDate()));
    }

    @Override
    public void onChange(Month month) {
        persianDate.setShMonth(month.getPersianDate().getShMonth());
        initReminderView();
    }

    @Override
    public void onChange(Day day) {
        persianDate.setShDay(day.getPersianDate().getShDay());
        initReminderView();
    }

    @Override
    public void onItemClick(Reminder reminder) {
        Intent editIntent = new Intent(this, AddActivity.class);
        editIntent.putExtra("id", reminder.getId());
        startActivity(editIntent);
    }

    @Override
    public void onItemDelete(Reminder reminder) {
        reminderViewModel.deleteReminder(reminder);
    }
}