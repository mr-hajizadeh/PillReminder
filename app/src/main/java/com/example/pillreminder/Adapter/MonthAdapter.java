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

import com.example.pillreminder.Model.Month;
import com.example.pillreminder.R;
import com.example.pillreminder.databinding.MonthItemBinding;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MonthHolder> {

    private final List<Month> monthList;
    private final OnMonthItemListener onMonthItemListener;
    private Context context;

    public MonthAdapter(List<Month> monthList, OnMonthItemListener onMonthItemListener) {
        this.monthList = monthList;
        this.onMonthItemListener = onMonthItemListener;
    }

    @NonNull
    @Override
    public MonthHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new MonthHolder(MonthItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull MonthHolder holder, int position) {

        Month month = monthList.get(position);
        holder.titleTxt.setText(month.getTitle());

        Calendar calendar = Calendar.getInstance();
        Date currentDate = new Date(calendar.getTimeInMillis());

        if (currentDate.after(month.getStartOfMonthDate()) && currentDate.before(month.getEndOfMonthDate())) {
            holder.itemBg.setBackground(context.getDrawable(R.drawable.active_month_background));
            holder.titleTxt.setTextColor(ContextCompat.getColor(context, com.google.android.material.R.color.design_default_color_primary));
        }

        holder.itemView.setOnClickListener(v -> onMonthItemListener.onClick(month));

    }

    @Override
    public int getItemCount() {
        return monthList.size();
    }

    static class MonthHolder extends RecyclerView.ViewHolder {

        private final TextView titleTxt;
        private final LinearLayout itemBg;

        public MonthHolder(MonthItemBinding binding) {
            super(binding.getRoot());

            titleTxt = binding.monthTitleTxt;
            itemBg = binding.itemBg;

        }
    }

}
