package com.example.pillreminder.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pillreminder.Model.Day;
import com.example.pillreminder.R;
import com.example.pillreminder.databinding.DayItemBinding;

import java.util.List;

import saman.zamani.persiandate.PersianDateFormat;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.MonthHolder> {

    private List<Day> dayList;
    private Context context;

    @SuppressLint("NotifyDataSetChanged")
    public void setDayList(List<Day> dayList) {
        this.dayList = dayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MonthHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new MonthHolder(DayItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false));
    }

    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull MonthHolder holder, @SuppressLint("RecyclerView") int position) {

        Day day = dayList.get(position);
        holder.dateTxt.setText(PersianDateFormat.format(
                day.getPersianDate(), "j",
                PersianDateFormat.PersianDateNumberCharacter.FARSI));
        holder.titleTxt.setText(day.getTitle().substring(0, 1));

        if (day.isActive()) {
            holder.itemBg.setBackground(context.getDrawable(R.drawable.active_day_background));
            int colorId = ContextCompat.getColor(context, com.google.android.material.R.color.design_default_color_on_primary);
            holder.dateTxt.setTextColor(colorId);
            holder.titleTxt.setTextColor(colorId);
        } else {
            holder.itemBg.setBackground(context.getDrawable(R.drawable.day_background));
            int colorId = ContextCompat.getColor(context, android.R.color.secondary_text_light);
            holder.dateTxt.setTextColor(colorId);
            holder.titleTxt.setTextColor(colorId);
        }

    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    static class MonthHolder extends RecyclerView.ViewHolder {

        private final TextView titleTxt;
        private final TextView dateTxt;
        private final LinearLayout itemBg;

        public MonthHolder(DayItemBinding binding) {
            super(binding.getRoot());

            titleTxt = binding.dayTitleTxt;
            dateTxt = binding.dateTxt;
            itemBg = binding.itemBg;

        }
    }

}
