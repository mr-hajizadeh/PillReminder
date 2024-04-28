package com.example.pillreminder.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pillreminder.Adapter.DayAdapter;
import com.example.pillreminder.Adapter.MonthAdapter;
import com.example.pillreminder.Adapter.OnMonthItemListener;
import com.example.pillreminder.Model.Day;
import com.example.pillreminder.Model.Month;
import com.example.pillreminder.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import saman.zamani.persiandate.PersianDate;

public class MainActivity extends AppCompatActivity implements OnMonthItemListener {

    private final int[] jalaliDayInMonth = {31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29};
    private ActivityMainBinding binding;
    private DayAdapter dayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();

    }

    private void initViews() {

        PersianDate persianDate = new PersianDate();

        RecyclerView monthView = binding.monthView;
        monthView.setAdapter(new MonthAdapter(generateMonthNames(), this));
        monthView.scrollToPosition(persianDate.getShMonth() - 1);

        RecyclerView dayView = binding.dayView;
        dayAdapter = new DayAdapter();
        dayAdapter.setDayList(generateDay(persianDate));
        dayView.setAdapter(dayAdapter);
        dayView.scrollToPosition(persianDate.getShDay() - 1);

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

        return daysMonth;

    }

    @Override
    public void onClick(Month month) {
        dayAdapter.setDayList(generateDay(month.getPersianDate()));
    }
}