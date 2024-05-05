package com.example.pillreminder.Adapter;

import com.example.pillreminder.Model.Reminder;

public interface OnReminderItemClick {

    void onItemClick(Reminder reminder);

    void onItemDelete(Reminder reminder);

}
