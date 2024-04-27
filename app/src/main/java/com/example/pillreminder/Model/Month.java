package com.example.pillreminder.Model;

import java.util.Date;

import saman.zamani.persiandate.PersianDate;

public class Month {

    private int id;
    private String title;
    private int monthNumber;
    private Date startOfMonthDate;
    private Date endOfMonthDate;
    private PersianDate persianDate;

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

    public int getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(int monthNumber) {
        this.monthNumber = monthNumber;
    }

    public Date getStartOfMonthDate() {
        return startOfMonthDate;
    }

    public void setStartOfMonthDate(Date startOfMonthDate) {
        this.startOfMonthDate = startOfMonthDate;
    }

    public Date getEndOfMonthDate() {
        return endOfMonthDate;
    }

    public void setEndOfMonthDate(Date endOfMonthDate) {
        this.endOfMonthDate = endOfMonthDate;
    }

    public PersianDate getPersianDate() {
        return persianDate;
    }

    public void setPersianDate(PersianDate persianDate) {
        this.persianDate = persianDate;
    }
}
