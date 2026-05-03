package com.fa.baiboly.data.fihirana;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fa.baiboly.data.DatabaseHelper;

public class FihiranaRepository {

    private final DatabaseHelper dbHelper;

    public FihiranaRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    // =========================
    // 🎵 ALL SONGS
    // =========================
    public Cursor getAllSongs() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT id, laharana, sokajy, lohateny " +
                        "FROM songs " +
                        "ORDER BY laharana",
                null
        );
    }

    // =========================
    // 🎵 SONG BY CATEGORY
    // =========================
    public Cursor getSongsByCategory(String category) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT id, laharana, sokajy, lohateny " +
                        "FROM songs " +
                        "WHERE sokajy=? " +
                        "ORDER BY laharana",
                new String[]{category}
        );
    }
    // =========================
// 🎵 SONG BY NUMBER + CATEGORY
// =========================
    public Cursor getSongByNumberAndCategory(int number, String category) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT id, laharana, sokajy, lohateny " +
                        "FROM songs " +
                        "WHERE laharana = ? AND sokajy = ?",
                new String[]{
                        String.valueOf(number),
                        category
                }
        );
    }

    // =========================
    // 🎶 VERSES OF SONG
    // =========================
    public Cursor getSongVerses(String songId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT andininy, tononkira, fiverenany " +
                        "FROM verses " +
                        "WHERE song_id=? " +
                        "ORDER BY andininy",
                new String[]{songId}
        );
    }

    // =========================
    // 👤 AUTHORS
    // =========================
    public Cursor getAuthors(String songId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT a.name " +
                        "FROM authors a " +
                        "JOIN song_authors sa ON a.id = sa.author_id " +
                        "WHERE sa.song_id=?",
                new String[]{songId}
        );
    }
    // =========================
// 📂 CATEGORIES (types chants)
// =========================
    public Cursor getCategories() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT DISTINCT sokajy FROM songs ORDER BY sokajy",
                null
        );
    }

    // =========================
    // 🔍 SEARCH (FTS)
    // =========================
    public Cursor search(String query) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT s.id, s.laharana, s.sokajy, s.lohateny " +
                        "FROM songs s " +
                        "JOIN verses_fts f ON f.rowid IN (SELECT id FROM verses WHERE song_id = s.id) " +
                        "WHERE verses_fts MATCH ? " +
                        "GROUP BY s.id " +
                        "ORDER BY s.laharana",
                new String[]{query + "*"}
        );
    }

    public Cursor getSongById() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery(
                "SELECT id, laharana, sokajy, lohateny  FROM songs ",null
        );
    }
}