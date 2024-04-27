package com.example.pillreminder.Model;

import androidx.annotation.NonNull;

import java.util.Date;

import saman.zamani.persiandate.PersianDate;

public class Day {

    private int id;
    private String title;
    private Date dayDate;
    private PersianDate persianDate;
    private boolean isActive;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDayDate() {
        return dayDate;
    }

    public void setDayDate(Date dayDate) {
        this.dayDate = dayDate;
    }

    public PersianDate getPersianDate() {
        return persianDate;
    }

    public void setPersianDate(PersianDate persianDate) {
        this.persianDate = persianDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @NonNull
    @Override
    public String toString() {
        return "Day{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", dayDate=" + dayDate +
                ", persianDate=" + persianDate +
                ", isActive=" + isActive +
                '}';
    }
}
