package com.fa.baiboly.data.perikopa;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fa.baiboly.data.DatabaseHelper;

public class PerikopaRepository {

    private final DatabaseHelper dbHelper;

    public PerikopaRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    // 📅 Tous les versets d’un jour
    public Cursor getPerikopaForDate(String date) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT d.name, s.season, r.verse " +
                        "FROM perikopa_days d " +
                        "LEFT JOIN liturgical_seasons s ON s.id = d.season_id " +
                        "LEFT JOIN perikopa_readings r ON r.day_id = d.id " +
                        "WHERE d.date = ?",
                new String[]{date}
        );
    }

    public Cursor getLohahevitra(String month) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT theme FROM perikopa_months WHERE month_name = ?",
                new String[]{month}
        );
    }
}