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
import java.util.List;

import saman.zamani.persiandate.PersianDate;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderHolder> {

    private final List<Reminder> reminderList;
    private final PersianDate currentDate;
    private final OnReminderItemClick onReminderItemClick;
    private Context context;

    public ReminderAdapter(List<Reminder> reminderList, PersianDate currentDate, OnReminderItemClick onReminderItemClick) {
        this.reminderList = reminderList;
        this.currentDate = currentDate;
        this.onReminderItemClick = onReminderItemClick;
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

        HourAdapter hourAdapter = new HourAdapter(calculateHours(reminder.getHours()));
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

    private List<Long> calculateHours(List<Long> hourList) {
        List<Long> hours = new ArrayList<>();

        // تعیین ابتدا و انتهای روز جاری
        Calendar startCurrentDay = Calendar.getInstance();
        startCurrentDay.setTimeInMillis(currentDate.getTime());
        startCurrentDay.set(Calendar.HOUR_OF_DAY, 0);
        startCurrentDay.set(Calendar.MINUTE, 0);
        startCurrentDay.set(Calendar.SECOND, 0);

        Calendar endCurrentDay = Calendar.getInstance();
        endCurrentDay.setTimeInMillis(currentDate.getTime());
        endCurrentDay.set(Calendar.HOUR_OF_DAY, 23);
        endCurrentDay.set(Calendar.MINUTE, 59);
        endCurrentDay.set(Calendar.SECOND, 59);

        // تحقق شرط و اضافه کردن به لیست
        for (long eachMilliSecond : hourList) {
            if (startCurrentDay.getTimeInMillis() <= eachMilliSecond
                    && eachMilliSecond <= endCurrentDay.getTimeInMillis()) {
                hours.add(eachMilliSecond);
            }
        }
        return hours;
    }


}
