package com.fa.baiboly.data.parser;

import com.fa.baiboly.data.bible.BibleService;
import com.fa.baiboly.models.Book;
import com.fa.baiboly.models.Reading;
import com.fa.baiboly.data.bible.BibleRepository;

public class ReadingParser {

    private BibleService service;


    public Reading parse(String input) {

        if (input == null) return null;
        input = input.replace(",", ":");
        // =========================
        // EX: "Jao 1 : 1-5"
        // =========================

        String[] parts = input.split(":");

        String bookPart = parts[0].trim();   // "Jao 1"
        String rangePart = parts[1].trim();  // "1-5"

        String[] bookSplit = bookPart.split(" ");

        String bookShort = bookSplit[0];
        int startChapter = Integer.parseInt(bookSplit[1]);

        int startVerse;
        int endChapter = startChapter;
        int endVerse;

        // =========================
        // CASE 1: 1-5
        // =========================
        if (rangePart.contains("-") && !rangePart.contains(":")) {

            String[] v = rangePart.split("-");

            startVerse = Integer.parseInt(v[0].trim());
            endVerse = Integer.parseInt(v[1].trim());
        }

        // =========================
        // CASE 2: 28 - 2 : 3
        // =========================
        else if (rangePart.contains("-") && rangePart.contains(":")) {

            String[] v = rangePart.split("-");

            startVerse = Integer.parseInt(v[0].trim());

            String[] end = v[1].trim().split(":");

            endChapter = Integer.parseInt(end[0].trim());
            endVerse = Integer.parseInt(end[1].trim());
        }

        // =========================
        // CASE 3: single verse
        // =========================
        else {
            startVerse = Integer.parseInt(rangePart.trim());
            endVerse = startVerse;
        }

        Book book = service.findBookByBookName(bookShort);

        if (book == null) return null;

        return new Reading(
                book,
                startChapter,
                startVerse,
                endChapter,
                endVerse
        );
    }
    public void setService(BibleService service) {
        this.service = service;
    }
}