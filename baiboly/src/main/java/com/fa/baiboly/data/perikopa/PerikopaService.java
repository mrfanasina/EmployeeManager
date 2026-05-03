package com.fa.baiboly.data.perikopa;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;

import com.fa.baiboly.models.History;
import com.fa.baiboly.models.PerikopaDay;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PerikopaService {

    private final PerikopaRepository repository;

    public PerikopaService(Context context) {
        this.repository = new PerikopaRepository(context);
    }

    // =========================
    // 📅 PERIKOPA DU JOUR
    // =========================
    public PerikopaDay getTodayPerikopa() {

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        return getPerikopaByDate(today);
    }
    public String getTodayLohahevitra() {
        String theme = "";

        Cursor c = repository.getLohahevitra("May");
        if (c.moveToFirst()) {
            do {
                theme = c.getString(0);
            } while (c.moveToNext());
        }
        return theme;
    }

    // =========================
    // 📅 PERIKOPA PAR DATE
    // =========================
    public PerikopaDay getPerikopaByDate(String date) {

        Cursor cursor = repository.getPerikopaForDate(date);

        PerikopaDay day = new PerikopaDay();
        day.setDate(date);
        day.setVerses(new ArrayList<>());

        if (cursor != null && cursor.moveToFirst()) {

            // 🧠 metadata (répétée mais identique sur toutes les lignes)
            day.setName(cursor.getString(0));
            day.setSeason(cursor.getString(1));

            // 📖 versets (multi lignes)
            do {
                String verse = cursor.getString(2);

                if (verse != null) {
                    day.getVerses().add(verse);
                }

            } while (cursor.moveToNext());

            cursor.close();

        } else {
            // fallback propre
            day.setName("Tsy misy perikopa");
            day.setSeason("");
        }

        return day;
    }

    // =========================
    // 📅 VERSE DU JOUR (si tu veux compatibilité simple UI)
    // =========================
    public String getTodayFirstVerse() {

        PerikopaDay day = getTodayPerikopa();

        if (day.getVerses() != null && !day.getVerses().isEmpty()) {
            return day.getVerses().get(0);
        }

        return "Tsy misy vakiteny androany";
    }
    private String mapMonth(int month) {

        switch (month) {
            case 1: return "Janoary";
            case 2: return "Febroary";
            case 3: return "Martsa";
            case 4: return "Aprily";
            case 5: return "Mey";
            case 6: return "Jona";
            case 7: return "Jolay";
            case 8: return "Aogositra";
            case 9: return "Septambra";
            case 10: return "Oktobra";
            case 11: return "Novambra";
            case 12: return "Desambra";
            default: return "None";
        }
    }
}