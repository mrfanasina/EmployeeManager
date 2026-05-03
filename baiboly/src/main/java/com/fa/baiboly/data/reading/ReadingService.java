package com.fa.baiboly.data.reading;

import android.content.Context;
import android.database.Cursor;

import java.util.Calendar;

public class ReadingService {

    private final ReadingRepository repository;

    public ReadingService(Context context) {
        this.repository = new ReadingRepository(context);
    }

    // =========================
    // 📅 Lecture du jour
    // =========================
    public String getTodayReading() {

        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int monthNumber = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        Cursor cursor = repository.getReadingForDay(year, monthNumber, day);

        String result = "Tsy misy vakiteny androany";

        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getString(0);
            cursor.close();
        }

        return result;
    }
}