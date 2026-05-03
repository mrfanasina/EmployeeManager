package com.fa.baiboly.data.reading;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fa.baiboly.data.DatabaseHelper;

public class ReadingRepository {

    private final DatabaseHelper dbHelper;

    public ReadingRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public Cursor getReadingForDay(int year, int month, int day) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT rd.verse " +
                        "FROM reading_days rd " +
                        "JOIN reading_months rm ON rm.id = rd.month_id " +
                        "WHERE rm.year=? AND rm.month_number=? AND rd.day=?",
                new String[]{
                        String.valueOf(year),
                        String.valueOf(month),
                        String.valueOf(day)
                }
        );
    }
}