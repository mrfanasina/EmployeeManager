package com.fa.baiboly.data.bible;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fa.baiboly.data.DatabaseHelper;
import com.fa.baiboly.models.Book;

public class BibleRepository {

    private final DatabaseHelper dbHelper;
    public BibleRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    // =========================
    // 📚 BOOKS (CORRIGÉ)
    // =========================
    public Cursor getBooks(String testament, String lang) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT " +
                        "b.numero AS id, " +
                        "b." + lang + " AS short_name, " +
                        "f." + lang + " AS long_name, " +
                        "CAST(s.color AS TEXT) AS color " +
                        "FROM bible_books b " +
                        "JOIN bible_sections s ON b.section_id = s.id " +
                        "LEFT JOIN bible_books_full f ON b.numero = f.numero " +
                        "WHERE s.testament=? " +
                        "ORDER BY b.numero",
                new String[]{testament}
        );
    }

    // =========================
    // 📖 CHAPTERS
    // =========================
    public Cursor getChapters(int bookId) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT chapter, COUNT(verse) " +
                        "FROM bible_verses " +
                        "WHERE book_number=? " +
                        "GROUP BY chapter " +
                        "ORDER BY chapter",
                new String[]{String.valueOf(bookId)}
        );
    }


    // =========================
    // 📜 VERSES
    // =========================
    public Cursor getVerses(int bookId, int chapter) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT verse, text " +
                        "FROM bible_verses " +
                        "WHERE book_number=? AND chapter=? " +
                        "ORDER BY verse",
                new String[]{
                        String.valueOf(bookId),
                        String.valueOf(chapter)
                }
        );
    }


    public Cursor getBookById(int id) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT " +
                        "b.numero AS id, " +
                        "b.mg AS short_name, " +
                        "f.mg AS long_name, " +
                        "CAST(s.color AS TEXT) AS color " +
                        "FROM bible_books b " +
                        "JOIN bible_sections s ON b.section_id = s.id " +
                        "LEFT JOIN bible_books_full f ON b.numero = f.numero " +
                        "WHERE b.numero=?",
                new String[]{String.valueOf(id)}
        );
    }
    public Integer resolveBookNumber(String input) {

        if (input == null) return null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String clean = input
                .toLowerCase()
                .replaceAll("\\s+", "")
                .replace(",", "");

        Log.d("BOOK_RESOLVE", "inpust=" + clean);
        if (clean.equals("apo")){
            return 66;
        }
        Cursor c = db.rawQuery(
                // 🔥 SEARCH BOTH TABLES LOGICALLY
                "SELECT numero FROM bible_books " +
                        "WHERE LOWER(REPLACE(mg,' ','')) LIKE ? " +

                        "UNION " +

                        "SELECT numero FROM bible_books_full " +
                        "WHERE LOWER(REPLACE(mg,' ','')) LIKE ? " +

                        "LIMIT 1",
                new String[]{
                        "%" + clean + "%",
                        "%" + clean + "%"
                }
        );

        Integer result = null;

        if (c != null && c.moveToFirst()) {
            result = c.getInt(0);
        }

        if (c != null) c.close();

        Log.d("BOOK_RESOLVE", "result=" + result);

        return result;
    }
    // =========================
    // 📖 RANGE VERSES
    // =========================


    public Cursor getVersesFromReading(
            String bookShort,
            int startChapter,
            int startVerse,
            int endChapter,
            int endVerse
    ) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Integer bookNumber = resolveBookNumber(bookShort);

        Log.d("BIBLE_DEBUG",
                "book=" + bookShort +
                        " → bookNumber=" + bookNumber +
                        " start=" + startChapter + ":" + startVerse +
                        " end=" + endChapter + ":" + endVerse);

        if (bookNumber == null) {
            return null;
        }

        // =========================
        // 📖 SAME CHAPTER OPTIMIZED
        // =========================
        if (startChapter == endChapter) {

            return db.rawQuery(
                    "SELECT verse, text, title " +
                            "FROM bible_verses " +
                            "WHERE book_number=? " +
                            "AND chapter=? " +
                            "AND verse BETWEEN ? AND ? " +
                            "ORDER BY verse",
                    new String[]{
                            String.valueOf(bookNumber),
                            String.valueOf(startChapter),
                            String.valueOf(startVerse),
                            String.valueOf(endVerse)
                    }
            );
        }

        // =========================
        // 📖 MULTI-CHAPTER (FIXED VERSION)
        // =========================
        return db.rawQuery(
                "SELECT verse, text, title " +
                        "FROM bible_verses " +
                        "WHERE book_number=? " +

                        // 🔥 RANGE LOGIC (SAFE + CLEAN)
                        "AND ( " +

                        // first chapter
                        "(chapter = ? AND verse >= ?) " +

                        // middle chapters
                        "OR (chapter > ? AND chapter < ?) " +

                        // last chapter
                        "OR (chapter = ? AND verse <= ?) " +

                        ") " +

                        "ORDER BY chapter ASC, verse ASC",
                new String[]{

                        String.valueOf(bookNumber),

                        // start boundary
                        String.valueOf(startChapter),
                        String.valueOf(startVerse),

                        // middle range
                        String.valueOf(startChapter),
                        String.valueOf(endChapter),

                        // end boundary
                        String.valueOf(endChapter),
                        String.valueOf(endVerse)
                }
        );
    }
    // =========================
    // 🔍 SEARCH
    // =========================
    public Cursor search(String query) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT id, text FROM bible_fts WHERE text MATCH ?",
                new String[]{query}
        );
    }
}