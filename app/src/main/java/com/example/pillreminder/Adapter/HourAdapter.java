package com.example.pillreminder.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pillreminder.databinding.HourItemBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HoursHolder> {

    private final List<Long> hours;

    public HourAdapter(List<Long> hours) {
        this.hours = hours;
    }

    @NonNull
    @Override
    public HoursHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HoursHolder(HourItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HoursHolder holder, int position) {

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date(hours.get(position));
        holder.hourTxt.setText(timeFormat.format(date));

    }

    @Override
    public int getItemCount() {
        return hours.size();
    }

    static class HoursHolder extends RecyclerView.ViewHolder {
        private final TextView hourTxt;

        public HoursHolder(@NonNull HourItemBinding binding) {
            super(binding.getRoot());

            hourTxt = binding.hourTxt;
        }
    }

}
