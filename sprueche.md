# Achtsame Sprüche, Affirmationen & Impulse

Dieses Dokument dient als zentrale Übersicht und Dokumentation für alle in der App verwendeten Sprüche, Affirmationen und mitfühlenden Impulse. 

Durch die Auslagerung in JSON-Dateien können alle Texte hier flexibel angepasst, erweitert oder korrigiert werden, ohne tief in den Kotlin-Code der App eingreifen zu müssen.

---

## 1. Der „Achtsame Impuls“ (Statistik-Tipps)

Diese mitfühlenden Impulse werden dem Nutzer auf dem Statistik-Bildschirm basierend auf seiner durchschnittlichen Tagesbewertung (`avgRating`) der letzten Tage (z. B. 7, 30 oder 90 Tage) vorgeschlagen. Sie sollen Trost spenden, Grenzen setzen helfen oder positive Phasen bestärken.

**Dateipfad der Konfiguration:** `app/src/main/assets/mindful_impulses.json`

| Bewertungsbereich | Psychologischer Hintergrund / Intention | Text des Impulses |
| :--- | :--- | :--- |
| **Keine Einträge** (Rating <= 0.0) | Einstieg / Erste Pause ermöglichen | Nimm dir heute 3 Minuten Zeit, um einfach nur zu atmen. Kein Ziel, keine Leistung, nur du. |
| **Schwere Phase** (Rating < 3.0) | Akzeptanz, Trost & Selbstfürsorge, Entlastung | Die letzten Tage waren spürbar schwer für dich. Das ist vollkommen okay. Sei besonders sanft und liebevoll zu dir selbst. Setze heute bewusste Grenzen und lass allen Druck los. |
| **Ausgeglichene Phase** (Rating < 4.0) | Selbstmitgefühl bei kleinen Hürden, Achtsamkeit | Du erlebst eine ausgewogene Zeit mit kleinen Hürden. Vergiss nicht, dir selbst Vergebung zu schenken, wenn etwas nicht perfekt war. Jede kleine Pause ist ein Erfolg! |
| **Gute / Kraftvolle Phase** (Rating >= 4.0) | Verankerung positiver Emotionen, Energie-Speicherung | Dein Kompass zeigt auf viel Klarheit und Zufriedenheit. Atme diese positive Energie ein und speichere sie ab – für Tage, an denen die Wolken wieder dichter stehen. |

---

## 2. Die täglichen Affirmationen & Zitate („Dein Spruch heute“)

Am Ende einer Routine oder auf dem Startbildschirm erhält der Nutzer einen Spruch des Tages. Diese Sprüche sind darauf ausgelegt, die jeweilige Stimmung des Nutzers abzufangen (besonders an schweren Tagen Trost und Selbstmitgefühl zu spenden, anstatt toxischer Positivität).

**Dateipfad der Konfiguration:** `app/src/main/assets/sprueche.json`

### JSON-Datenstruktur:
Jedes Zitat ist wie folgt aufgebaut:
```json
{
  "id": "S001",
  "text": "Mein Atem ist mein Anker, der mich auch in stürmischen Zeiten im Hier und Jetzt hält.",
  "author": "Eigene Affirmation",
  "tags": [
    "Stress",
    "Achtsamkeit"
  ]
}
```

### Zuweisung über Tags:
In der Codebase (`EntryRepository.kt` -> `deriveTags`) werden den getätigten Tagebucheinträgen basierend auf den Antworten des Nutzers bestimmte Tags zugeordnet. Der Algorithmus sucht anschließend einen Spruch aus der `sprueche.json`, der zu diesen Tags passt:
* **Stress / Angst (`anxiety`, `overwhelmed`):** Zitate mit Tags `"Stress"`, `"Angst"`.
* **Freude / Elan (`excitement`, `satisfaction`):** Zitate mit Tag `"Freude"`.
* **Ausgeglichenheit / Erleichterung (`peace`, `relief`):** Zitate mit Tag `"Ausgeglichenheit"`.
* **Schwere Tage / Keine Kraft (`struggled`, `none`, `neglected`, `no_energy`):** Zitate mit Tags `"Trost"`, `"Selbstfürsorge"`, `"Traurigkeit"`.
