package com.example.pillreminder.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pillreminder.Model.Reminder;
import com.example.pillreminder.R;
import com.example.pillreminder.databinding.ReminderItemBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import saman.zamani.persiandate.PersianDate;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderHolder> {

    private List<Reminder> reminderList;
    private PersianDate currentDate;
    private final OnReminderItemClick onReminderItemClick;
    private Context context;

    public ReminderAdapter(OnReminderItemClick onReminderItemClick) {
        this.onReminderItemClick = onReminderItemClick;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setReminderList(List<Reminder> reminderList) {
        this.reminderList = reminderList;
        notifyDataSetChanged();
    }

    public void setCurrentDate(PersianDate currentDate) {
        this.currentDate = currentDate;
    }

    @NonNull
    @Override
    public ReminderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ReminderHolder(ReminderItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ReminderHolder holder, int position) {

        Reminder reminder = reminderList.get(position);
        holder.titleTxt.setText(reminder.getTitle());
        holder.repeatTimeTxt.setText(reminder.getReminderHour() + " " + context.getString(R.string.hour));
        holder.descriptionTxt.setText(reminder.getDescription());

        HourAdapter hourAdapter = new HourAdapter(
                calculateHours(reminder.getHours(), new Date(currentDate.getTime())));
        holder.hoursView.setAdapter(hourAdapter);

        holder.deleteBtn.setOnClickListener(v -> onReminderItemClick.onItemDelete(reminder));
        holder.itemView.setOnClickListener(v -> onReminderItemClick.onItemClick(reminder));

    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    static class ReminderHolder extends RecyclerView.ViewHolder {

        private final TextView titleTxt;
        private final TextView repeatTimeTxt;
        private final TextView descriptionTxt;
        private final ImageView deleteBtn;
        private final RecyclerView hoursView;

        public ReminderHolder(@NonNull ReminderItemBinding binding) {
            super(binding.getRoot());

            titleTxt = binding.titleTxt;
            repeatTimeTxt = binding.repeatTimeTxt;
            descriptionTxt = binding.descriptionTxt;
            deleteBtn = binding.deleteBtn;
            hoursView = binding.hoursView;

        }
    }

    @SuppressLint("NewApi")
    private List<Long> calculateHours(List<Long> hourList, Date date) {
        List<Long> hours = new ArrayList<>();

        Calendar startOfDay = Calendar.getInstance();
        startOfDay.setTime(date);
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        long startOfDayMillis = startOfDay.getTimeInMillis();

        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTime(date);
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);
        long endOfDayMillis = endOfDay.getTimeInMillis();

        for (long eachMilliSecond : hourList) {
            if (eachMilliSecond >= startOfDayMillis && eachMilliSecond <= endOfDayMillis) {
                hours.add(eachMilliSecond);
            }
        }
        return hours;
    }
}
