package com.fa.baiboly.data.bible;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.fa.baiboly.data.mapper.BookMapper;
import com.fa.baiboly.models.Book;
import com.fa.baiboly.models.Chapter;
import com.fa.baiboly.models.Verse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BibleService {

    private final BibleRepository repository;

    public BibleService(Context context) {
        this.repository = new BibleRepository(context);
    }

    // =========================
    // 📚 BOOKS
    // =========================
    public List<Book> getBooks(String testament, String lang) {

        List<Book> list = new ArrayList<>();

        Cursor c = repository.getBooks(testament, lang);

        if (c != null && c.moveToFirst()) {
            do {

                int id = c.getInt(0);
                String shortName = c.getString(1);
                String longName = c.getString(2);
                String color = c.getString(3);

                list.add(new Book(id, color, shortName, longName));

            } while (c.moveToNext());

            c.close();
        }

        return list;
    }

    // =========================
    // 📖 CHAPTERS
    // =========================
    public List<Chapter> getChapters(int bookId) {

        List<Chapter> list = new ArrayList<>();

        Cursor c = repository.getChapters(bookId);

        if (c != null && c.moveToFirst()) {
            do {
                list.add(new Chapter(
                        bookId,
                        c.getInt(0),
                        c.getInt(1)
                ));
            } while (c.moveToNext());

            c.close();
        }

        return list;
    }

    // =========================
    // 🔍 SEARCH
    // =========================
    public List<String> search(String query) {

        List<String> list = new ArrayList<>();

        Cursor c = repository.search(query);

        if (c != null && c.moveToFirst()) {
            do {
                list.add(c.getInt(0) + ". " + c.getString(1));
            } while (c.moveToNext());

            c.close();
        }

        return list;
    }
    // =========================
    // 📖 READING OBJECTS (For RecyclerView Adapter)
    // =========================
    public List<Verse> getVerseObjectsFromReading(String reading) {

        List<Verse> list = new ArrayList<>();

        try {
            if (reading == null || reading.trim().isEmpty()) return list;

            // =========================
            // 🔧 CLEAN INPUT
            // =========================
            reading = reading.trim()
                    .replace("–", "-")
                    .replace(",", ":")
                    .replace(" ", "")
                    .replaceAll("\\s+", " ");

            Log.d("INPUT", reading);

            // =========================
            // 🔥 REGEX EXTRACTION (FIX ALL CASES)
            // =========================

            // match: book + reference
            Pattern pattern = Pattern.compile("^(.*?)(\\d+[:\\d\\-]*)$");
            Matcher matcher = pattern.matcher(reading);

            if (!matcher.find()) {
                Log.e("PARSE", "Invalid format: " + reading);
                return list;
            }

            String book = matcher.group(1).trim();
            String ref = matcher.group(2).trim();

            book = book.replace(" ", "");

            Log.d("BOOK", book);
            Log.d("REF", ref);

            // =========================
            // 📖 DEFAULT VALUES
            // =========================
            int startChapter;
            int startVerse;
            int endChapter;
            int endVerse;

            // =========================
            // 📖 PARSE RANGE SAFELY
            // =========================
            if (ref.contains("-")) {

                String[] range = ref.split("-");

                String start = range[0];
                String end = range[1];

                String[] startSplit = start.split(":");
                String[] endSplit = end.split(":");

                startChapter = Integer.parseInt(startSplit[0]);
                startVerse = startSplit.length > 1 ? Integer.parseInt(startSplit[1]) : 1;

                // IMPORTANT FIX HERE
                if (endSplit.length == 1) {
                    endChapter = startChapter;
                    endVerse = Integer.parseInt(endSplit[0]);
                } else {
                    endChapter = Integer.parseInt(endSplit[0]);
                    endVerse = Integer.parseInt(endSplit[1]);
                }

            } else {

                String[] single = ref.split(":");

                startChapter = Integer.parseInt(single[0]);
                startVerse = single.length > 1 ? Integer.parseInt(single[1]) : 1;

                endChapter = startChapter;
                endVerse = startVerse;
            }

            // =========================
            // 🔍 DEBUG
            // =========================
            Log.d("PARSED",
                    "book=" + book +
                            " start=" + startChapter + ":" + startVerse +
                            " end=" + endChapter + ":" + endVerse);

            // =========================
            // 🗄 DB CALL
            // =========================
            Cursor c = repository.getVersesFromReading(
                    book,
                    startChapter,
                    startVerse,
                    endChapter,
                    endVerse
            );

            if (c != null && c.moveToFirst()) {

                do {
                    int number = c.getInt(0);
                    String text = c.getString(1);
                    String title = c.getString(2);

                    list.add(new Verse(number, text, title));

                } while (c.moveToNext());

                c.close();
            }

        } catch (Exception e) {
            Log.e("PARSE_ERROR", "Error parsing: " + reading, e);
        }

        return list;
    }

    public Book findBookByBookName(String bookName) {

        if (bookName == null) return null;

        Integer bookId = repository.resolveBookNumber(bookName);

        if (bookId == null) return null;

        Cursor c = repository.getBookById(bookId);

        if (c == null) return null;

        Book book = null;

        if (c.moveToFirst()) {
            book = BookMapper.fromCursor(c);
        }

        c.close();

        return book;
    }
}