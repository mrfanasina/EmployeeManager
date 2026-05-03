package com.fa.baiboly.data.scraper;

import android.util.Log;

import com.fa.baiboly.data.parser.ReadingParser;
import com.fa.baiboly.data.parser.SongParser;
import com.fa.baiboly.models.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MofonainaScraper {

    private ReadingParser readingParser;
    private SongParser songParser;

    public MofonainaScraper(ReadingParser readingParser,
                            SongParser songParser) {
        this.readingParser = readingParser;
        this.songParser = songParser;
    }

    public MofonainaData scrape(Document doc) {

        MofonainaData data = new MofonainaData();

        Element content = doc.selectFirst("div.post-content");
        if (content == null) return data;

        Element dateEl = content.selectFirst("h5");
        if (dateEl != null) {
            data.setDate(dateEl.text().trim());
        }
        // =========================
        // 🔥 TITLE
        // =========================
        Element titleEl = content.selectFirst("h2");
        if (titleEl != null) {
            data.setTitle(titleEl.text().trim());
        }

        // =========================
        // 🔥 LITURGY
        // =========================
        Element liturgyEl = content.selectFirst("p em");

        if (liturgyEl != null) {

            String rawText = liturgyEl.text();
            Log.d("MOFONAINA", "RAW LITURGY = " + rawText);

            String[] parts = rawText.split("/");

            Log.d("MOFONAINA", "PARTS COUNT = " + parts.length);

            for (int i = 0; i < parts.length; i++) {
                Log.d("MOFONAINA", "PART[" + i + "] = " + parts[i]);
            }

            // =========================
            // 📖 READING 1
            // =========================
            try {
                if (parts.length > 0) {

                    Log.d("MOFONAINA", "Trying Reading1 with: " + parts[0]);

                    Reading r = safeReading(parts[0]);

                    if (r != null) {
                        Log.d("MOFONAINA", "Reading1 OK = " + r.toString());
                        data.setBibleReading1(r);
                    } else {
                        Log.d("MOFONAINA", "Reading1 NULL ❌");
                    }
                }
            } catch (Exception e) {
                Log.e("MOFONAINA", "Reading1 ERROR ❌", e);
            }

            // =========================
            // 🎵 SONG 1
            // =========================
            try {
                if (parts.length > 1) {

                    Log.d("MOFONAINA", "Trying Song1 with: " + parts[1]);

                    Song s1 = safeSong(parts[1]);

                    if (s1 != null) {
                        Log.d("MOFONAINA", "Song1 OK = " + s1.getId());
                        data.setSong1(s1);
                    } else {
                        Log.d("MOFONAINA", "Song1 NULL ❌");
                    }
                }
            } catch (Exception e) {
                Log.e("MOFONAINA", "Song1 ERROR ❌", e);
            }

            // =========================
            // 🎵 SONG 2
            // =========================
            try {
                if (parts.length > 5) {

                    Log.d("MOFONAINA", "Trying Song2 with: " + parts[5]);

                    Song s2 = safeSong(parts[5]);

                    if (s2 != null) {
                        Log.d("MOFONAINA", "Song2 OK = " + s2.getId());
                        data.setSong2(s2);
                    } else {
                        Log.d("MOFONAINA", "Song2 NULL ❌");
                    }
                } else {
                    Log.d("MOFONAINA", "Song2 skipped (parts.length <= 5)");
                }
            } catch (Exception e) {
                Log.e("MOFONAINA", "Song2 ERROR ❌", e);
            }
        }
        // =========================
        // 🔥 BIBLE TEXT
        // =========================
        Element bibleBlock = content.selectFirst("div.soratra-masina");

        if (bibleBlock != null) {

            Element h5 = bibleBlock.selectFirst("h5");
            if (h5 != null) {
                data.setVerseOfDay(
                        safeReading(h5.text())
                );
            }

            // garder le texte biblique
            data.setBibleText(bibleBlock.html());
        }
        // =========================
        // 🔥 REFLECTION + QUESTION
        // =========================
        Element reflectionBlock = content.selectFirst("div.mb-3");

        if (reflectionBlock != null) {

            StringBuilder reflection = new StringBuilder();
            StringBuilder question = new StringBuilder();

            boolean isQuestion = false;

            for (Element p : reflectionBlock.select("p")) {

                String text = p.text().trim();

                if (text.toLowerCase().contains("fanontaniana")) {
                    isQuestion = true;
                }

                if (isQuestion) {
                    question.append(text).append("\n\n");
                } else {
                    reflection.append(text).append("\n\n");
                }
            }

            data.setReflection(reflection.toString().trim());
            data.setQuestion(question.toString().trim());
        }

        return data;
    }

    // =========================
    // 🔧 SAFE PARSERS
    // =========================

    private Reading safeReading(String text) {
        try {
            if (text == null) return null;

            text = text.trim();

            // fix format
            text = text.replace(",", ":");
            text = text.replace(".", ":");

            if (!text.contains(":")) return null;

            return readingParser.parse(text);

        } catch (Exception e) {
            return null;
        }
    }

    private Song safeSong(String text) {
        try {
            if (text == null) return null;

            text = text.trim();
            return songParser.parse(text);

        } catch (Exception e) {
            return null;
        }
    }
}