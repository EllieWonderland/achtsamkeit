# Entwicklungs-Roadmap

> [!IMPORTANT]
> **WICHTIGER HINWEIS ZUM WORKFLOW:**
> Bitte hake jede erledigte Aufgabe sofort ab, indem du das Kästchen ankreuzt (`- [x]`).
> Nach **jeder** erledigten Aufgabe (oder einem kleinen, zusammenhängenden Block) sollst du einen **Commit & Push** (`git commit` + `git push`) durchführen, um deinen Fortschritt kontinuierlich zu sichern!

---

## 🎯 Offene Aufgaben

### 🚨 Pre-Release-Blocker

> [!WARNING]
> Diese Aufgaben müssen zwingend vor dem Play Store Release abgeschlossen werden!

- [ ] **Impressum: Echte Steuernummer eintragen** — Platzhalter `123 456 789 0` in `ImpressumScreen.kt` durch echte Steuernummer ersetzen. Vollständige Pflichtangaben nach § 5 TMG ergänzen. *(Produktname „CapiVision" ist bereits gefixt)*
- [ ] **Datenschutzerklärung** — Finalisieren und in der App sowie in der Play Console verlinken.
- [ ] **Firestore Composite Indexes** — In der Firebase Console anlegen für alle Compound-Queries (z.B. `type + created_at`, `tags + created_at`). Verhindert stumme Abstürze bei Filterungen in der Tagebuch-Historie.
- [ ] **Interner Testlauf** — Mindestens 7 Tage echte Nutzung auf dem eigenen Gerät (Streak, Spruch-Cooldown, Rückblick-Unlock testen).
- [ ] **Play Store Vorbereitung:**
  - [ ] Release APK/AAB signieren (Keystore erstellen + sicher aufbewahren — bei Verlust kein Update mehr möglich!).
  - [ ] Screenshots (Pflicht: mind. 2 Phone-Screenshots), Kurz- und Langbeschreibung auf Deutsch.
  - [ ] Release Track: **Intern → Geschlossen (Beta) → Produktion**.

### 🚀 Backlog (Nach MVP)

- [ ] **Premium:** Favoriten-Liste ohne Limit (noch fehlend aus Phase 12).
- [ ] **Jahresrückblick:** Vollständiger Jahresrückblick mit tiefen Reflexionsfragen (geplant für Dezember).
- [ ] **Monatsrückblick ausbauen:** Mehr Visualisierungen (Mood-Verlauf, Streak-Jahreskalender) direkt im Monatsrückblick.
- [ ] **Weitere Anpassungsmöglichkeiten:** Eigene Akzentfarbe wählen (über die 4 Paletten hinaus).
- [ ] **Weitere Statistiken:** Wochentag-Analyse (an welchen Tagen bin ich am ausgeglichensten?), Energie-Trend, Selbstfürsorge-Häufigkeit.
- [ ] **Sprüche-Pool erweitern:** Neue Sprüche zu bestehenden Tags.
- [ ] **Umstellung der Sprache auf Englisch.**
- [ ] **Daumen-runter für Sprüche:** Sprüche, die man nicht mag, dauerhaft ausblenden (Firestore: `disliked/{quoteId}`).
- [ ] **Neue rotierende Fragen:** Fragen-Pool weiter ausbauen.
- [ ] **Widget:** Android-Home-Screen-Widget.

### 🔧 Tech Debt (Niedrige Priorität)

- [x] **Zeitzonen-Problematik (UTC):** `date_str = LocalDate.now().toString()` ist lokale Zeit. Bei Reisen (z.B. nach Japan/USA) kann ein Abend-Eintrag zum falschen Tag gehören. Langfristig: Zeitstempel in UTC speichern, nur für die UI in Lokalzeit umrechnen. *(Für MVP als bekannte Einschränkung akzeptiert und in `EntryRepository.kt` dokumentiert. Daten-Migration post-MVP.)*
- [x] **`deriveTags` auf Konstanten umstellen:** `model/EntryKeys.kt` eingeführt mit `MoodKey`, `EnergyKey`, `GratitudeKey`, `SelfCareKey`. Alle Usages in `deriveTags`, `entryToScore`, Section-Komponenten und Chart-Komponenten auf Konstanten umgestellt.

---

## 📊 Projektstand & Metriken (Mai 2026)

| Bereich | Status | Details |
|---------|--------|---------|
| 1. Konzept, Design & Setup | ✅ Fertig | UX-Konzept, Handoff, Projekt-Infrastruktur, Firebase, RevenueCat |
| 2. Kern-Features (Tagebuch) | ✅ Fertig | Morgen-/Abendroutine, Historie, Spruch-Logik (432 Sprüche) |
| 3. User Engagement | ✅ Fertig | Push-Notifications (lokal), Statistiken, Weekly/Monthly Reviews |
| 4. Polish, Fixes & Premium | ✅ Fertig | Offline-Support, RevenueCat Paywall, Account-Löschung, Export |
| 5. Release-Vorbereitung | ⏳ In Arbeit | ~~App-Icon ✅~~, Firestore Indizes, Store-Metadaten, Testing |
| 6. Fragensammlung & Statistik-Redesign | ✅ Fertig | Neue Mood/Energy/Gratitude/SelfCare-Keys, 30+30 Leitfragen, Multi-Key-Charts |
| 7. Bug-Fixes aus Code-Review | ✅ Fertig | Scoring-Bug, Offline-Save, QuoteLoader async, Zeit-Lock live |

### 📝 Sprüchezähler

> Zählt wie viele Sprüche es pro Tag in `sprueche.json` gibt.
> **Gesamt: 432 Sprüche (S001–S432)**

| Tag | Anzahl | In `deriveTags` genutzt? | Bewertung |
|-----|--------|--------------------------|-----------|
| Achtsamkeit | 195 | ✅ (implizit via Fallback) | ✅ Sehr gut |
| Dankbarkeit | 160 | ✅ (`achievement`-Checkbox, Rating ≥ 3) | ✅ Gut |
| Liebe | 120 | ❌ (nicht in `deriveTags`) | ℹ️ Wird nie gezielt ausgespielt |
| Freude | 63 | ✅ (`excitement`, `satisfaction`, `relief`, `joy`) | ✅ Gut |
| Angst | 60 | ✅ (`anxiety` → Tag `"Angst"`) | ✅ Ausgebaut |
| Freundschaft | 60 | ❌ (nicht in `deriveTags`) | ℹ️ Wird nie gezielt ausgespielt |
| Stress | 41 | ✅ (`stress`, `anxiety`, `overwhelmed`) | ✅ Ausgebaut |
| Trauer | 30 | ✅ (`sadness`, `melancholy`, `loneliness`) | ✅ Workaround aktiv |
| Ausgeglichenheit | 30 | ✅ (`balance`, `peace`) | ✅ Ausgebaut |
| Selbstfürsorge | 28 | ✅ (`breathing`/`outside` Self-Care) | ✅ Ausgebaut |
| Energie | 20 | ✅ (`full`, `satisfied_tired`) | ✅ Ausgebaut |
| Traurigkeit | 3 | ✅ (`sadness`, `melancholy`, `loneliness`) | 🔴 Nur 3 Sprüche! Workaround aktiv |
| Motivation | 1 | ❌ (nicht in `deriveTags`) | ⚠️ Fast nicht vorhanden |

- [x] **🔴 Ausgeglichenheit/Selbstfürsorge/Energie ausgebaut:** 72 neue Sprüche (S361–S432).
- [x] **⚠️ Tag-Inkonsistenz gefixt:** `deriveTags` vergibt bei `sadness`-Familie sowohl `"Traurigkeit"` als auch `"Trauer"`.
- [x] **ℹ️ Entschieden:** `Angst` (60 Quotes) → `deriveTags` bei `anxiety` ergänzt. `Liebe`, `Freundschaft`, `Motivation` bleiben als allgemeiner Fallback-Pool.
- [x] **⚠️ Stress:** Von 20 auf 41 Sprüche erhöht.

---

## ✅ Abgeschlossene Phasen (neueste zuerst)

---

## Phase 17 — Bug-Fixes aus Code-Review (Mai 2026)

### Kritische Bugs

- [x] **Stimmungs-Scoring komplett falsch (alle Statistiken kaputt):** `entryToScore()` in `HeuteViewModel.kt` nutzte veraltete Keys (`joy`, `balance`, `stress`, `sadness`). Jeder Eintrag landete im `else → 50`, Stimmungskurve und Monatstrend zeigten für alle Nutzerinnen immer einen flachen Verlauf. Alle 10 aktuellen Mood-Keys (Morgen: `excitement`, `peace`, `tiredness`, `anxiety`, `melancholy`; Abend: `satisfaction`, `relief`, `exhaustion`, `overwhelmed`, `loneliness`) sowie Energy-Modifier für `satisfied_tired`, `wired`, `low`, `empty` korrekt auf Scores 20–90 gemappt.
- [x] **Blockierendes Speichern bei Offline-Nutzung:** `ref.set(map).await()` in `EntryRepository.saveEntry` frierte die UI bei schlechtem/fehlendem Netz ein — `EntrySaveState.Saving` blieb permanent, der „Fertig"-Button lud ewig. `.await()` entfernt; Firestore-SDK erzeugt die Document-ID lokal, Sync erfolgt im Hintergrund.

### Mittlere Bugs

- [x] **Synchroner QuoteLoader-Zugriff:** `QuoteRepository.getQuoteById()` war non-suspend und konnte beim ersten Aufruf die 120 KB große `sprueche.json` auf dem Main-Thread laden (→ Jank). Zu `suspend fun` + `withContext(Dispatchers.IO)` umgestellt.
- [x] **Zeit-Lock nicht live aktualisiert:** `LocalTime.now().hour` im `HeuteScreen` wurde einmalig beim Composable-Aufbau berechnet. Wer die App kurz vor 17:00 Uhr öffnete, sah den Abendroutine-Unlock erst nach einem App-Neustart. `produceState` mit minutengenauer Aktualisierung (Delay bis zur nächsten vollen Minute) ersetzt — Lock-Status reagiert jetzt live auf den 17:00-Übergang.

---

## Phase 16 — Fragensammlung & Statistik-Redesign

Vollständige Überarbeitung der Morgen- und Abendroutine-Formulare sowie der Statistik-Charts auf Basis von `fragensammlung.md`.

### Ziele
- Klare inhaltliche Trennung von Morgen- und Abendroutine auf Antwort-Ebene
- Emotionale Tiefe & Ehrlichkeit (inkl. "negativer" Optionen ohne toxische Positivität)
- Erweiterte Statistiken durch Multi-Key-Aggregierung in den Charts

### Neue Stimmungs-Keys (MoodSection.kt)
- [x] **Morgen:** `excitement`, `peace`, `tiredness`, `anxiety`, `melancholy`
- [x] **Abend:** `satisfaction`, `relief`, `exhaustion`, `overwhelmed`, `loneliness`
- [x] Rückwärtskompatibilität: Alte Keys (`joy`, `stress`, `balance`, `sadness`) bleiben in `deriveTags` und Charts unterstützt

### Neue Energie-Keys (EnergySection.kt)
- [x] **Morgen:** `full`, `medium`, `low`, `empty` — neue beschreibende Labels mit Emojis
- [x] **Abend:** `satisfied_tired`, `wired`, `low`, `empty` — zwei neue abendspezifische Keys

### Neue Dankbarkeits-Keys (GratitudeSection.kt)
- [x] **Morgen (7 Optionen):** `relations`, `comfort`, `health`, `nature`, `opportunity`, `self_compassion`, `struggled`
- [x] **Abend (7 Optionen):** `encounter`, `micro_joys`, `achievement`, `learning`, `comfort_received`, `connection`, `none`
- [x] Rückwärtskompatibilität: Alte Keys (`people`, `body`, `pleasure`) in Charts weiter unterstützt

### Neue Selbstfürsorge-Keys (SelfCareSection.kt)
- [x] **Morgen (7 Intentionen):** `physical`, `boundaries`, `digital_detox`, `soul`, `stillness`, `compassion`, `no_energy`
- [x] **Abend (7 Aktionen):** `needs_met`, `boundaries_kept`, `unplugged`, `joyful_moment`, `release`, `forgiveness`, `neglected`

### Achtsamkeits-Sektion (MindfulnessSection.kt)
- [x] Neue emotionalere Labels für alle Optionen (Morgen & Abend)
- [x] Neue Abend-Option `autopilot` beim Fokus-Feld: *„Der Tag ist wie ein Film an mir vorbeigezogen."*

### deriveTags erweitert (EntryRepository.kt)
- [x] Alle neuen Mood-Keys → korrekte Tag-Zuweisung
- [x] Neue Gratitude/SelfCare-Keys → Dankbarkeit & Selbstfürsorge-Tags
- [x] `struggled`, `none`, `neglected`, `no_energy` → Tags `"Trost"`, `"Selbstfürsorge"`, `"Traurigkeit"` (mitfühlende Sprüche an schweren Tagen)

### Statistik-Charts neu (Multi-Key-Aggregierung)
- [x] **MoodBarChart.kt:** 4 Gruppen (Positiv / Neutral / Herausfordernd / Schwer) — jede Gruppe fasst mehrere Keys zusammen
- [x] **EnergyBarChart.kt:** 4 Gruppen (Voll / Mittel / Niedrig / Leer) — `satisfied_tired` → Voll, `wired` → Mittel
- [x] **GratitudePieChart.kt:** 7 Dankbarkeits-Säulen (Beziehungen / Körper / Erfolg / Natur / Genuss / Selbstfürsorge / Schwere Tage)

### Guided Questions (guided_questions.json)
- [x] Morgen-Fragen: 15 → **30** tiefgründige Impulsfragen (Fokus: Ausrichtung, innere Stärken, Selbstmitgefühl)
- [x] Abend-Fragen: 15 → **30** tiefgründige Impulsfragen (Fokus: Reflexion, Loslassen, Verarbeitung)

---

## Phase 15 — UX-Überarbeitung & Sprach-Umbenennung

### Priorität 1 — Formular morgens/abends inhaltlich differenzieren

- [x] `type`-Parameter an alle Section-Komponenten durchreichen (`type: String` — `"morning"` oder `"evening"`)
- [x] `RatingSection` in `EntryScreen.kt` nur bei `type == "evening"` anzeigen
- [x] `SelfCareSection`: Titel per `type` steuern (Intention vs. Rückblick)
- [x] `GratitudeSection`: Titel per `type` steuern
- [x] `MoodSection`: Titel per `type` steuern
- [x] `EnergySection`: Titel per `type` steuern
- [x] `MindfulnessSection`: Beide Fragen per `type` umformulieren

### Priorität 1 — Umbenennung: „Morgen" → „Morgenroutine" / „Abend" → „Abendroutine"

- [x] `HeuteScreen.kt`: Button-Texte und Lock-Meldungen angepasst
- [x] `EntryScreen.kt`: Greeting-Logik angepasst
- [x] `TagebuchScreen.kt` / `EntryListItem.kt`: Preview-Labels angepasst
- [x] `EntryDetailScreen.kt`: Typ-Label angepasst

### Priorität 1 — Begrüßungstext je nach Tageszeit

- [x] `HeuteScreen.kt`: Subtitle mit `isEvening`-Flag angepasst

### Priorität 2 — Guided Questions ausgebaut

- [x] `guided_questions.json`: Morgen-Fragen auf **15** erhöht
- [x] `guided_questions.json`: Abend-Fragen auf **15** erhöht

### Priorität 2 — Soft-Validierung beim Speichern

- [x] `EntryViewModel.kt`: Dialog bei fehlendem `mood` / `energyLevel` vor dem Speichern

### Priorität 2 — Streak-Freeze (1× pro Monat)

- [x] Neues Firestore-Feld `streak_freeze_used_month: String`
- [x] In `EntryRepository.updateStreak()`: Freeze-Logik implementiert
- [x] `StreakCard.kt`: Hinweis auf verbleibenden Gnadentag

### Priorität 2 — Mitternacht-Problem: Tagesbeginn auf 04:00 Uhr

- [x] `EntryRepository.kt` + `ReviewRepository.kt`: Tagesbeginn auf 04:00 Uhr gesetzt

### Priorität 3 — `deriveTags`: „empty" Energie ≠ Stress

- [x] `EntryRepository.kt`: `"empty"` Energie nur noch als Stress-Tag, wenn auch `mood` eine Stress-Stimmung ist

### Priorität 3 — Benachrichtigung nach Eintrag stummschalten / Lokale Notifications

- [x] `NotificationScheduler`, `NotificationReceiver`, `BootReceiver` implementiert
- [x] `EntryViewModel.markNotificationDoneToday()` setzt SharedPreferences-Flag

---

## Phase 14 — Verbesserungen & Bugfixes basierend auf Code-Review

### Kritische Bugs

- [x] **Offline-Bug: Blockierendes Speichern** — `ref.set(map)` ohne `.await()` in `EntryRepository.saveEntry` *(erster Fix; in Phase 17 als abgeschlossen bestätigt)*
- [x] **Streak-Abfrage optimieren** — `StatsRepository.getCurrentStreak` hatte bis zu 365 sequentielle Firestore-Abfragen. Fix: `current_streak` + `last_entry_date` als Felder im User-Dokument; `saveEntry` aktualisiert den Streak beim Speichern.
- [x] **JSON-Dateien asynchron laden** — `guided_questions.json`-Leselogik in `EntryViewModel.initType` mit `withContext(Dispatchers.IO)` *(`getQuoteById` in Phase 17 nachgezogen)*
- [x] **Eintrag löschen** — Lösch-Button mit Bestätigungsdialog in `EntryDetailScreen`. `HistoryRepository.deleteEntry()` ergänzt.

### Fehlende Features

- [x] **Benachrichtigungen smart stummschalten** — SharedPreferences-Flag nach erfolgreichem Speichern
- [x] **FCM → lokale Notifications migrieren** — `NotificationReceiver`, `BootReceiver`, `NotificationScheduler` mit `AlarmManager.setAndAllowWhileIdle()`
- [x] **Datenexport (DSGVO)** — JSON/PDF/Excel-Export im Profilscreen

### Verbesserungen & Hinweise

- [x] **Firestore Composite Indexes** — *(Offen in Aktuelle Prioritäten)*
- [x] **Fehlerlogging verbessern** — `onFailure { Log.e(...) }` vor allen `getOrDefault`-Aufrufen ergänzt
- [x] **Zeitzonen-Absicherung** — Verifiziert: `System.currentTimeMillis()` = UTC-Epoch, `date_str` = lokale Zeit. Handlungsbedarf als Tech-Debt dokumentiert.
- [x] **Paywall-Timing** — Verifiziert: Paywall erscheint nie beim App-Start, nur bei explizitem Klick auf Premium-Feature.
- [x] **Datenmigrations-Fallbacks** — `Entry.kt` Kotlin-Defaults für alle Felder, `toEntry()` überall mit `?: ""` / `?: emptyList()`.

---

## Phase 13 — Polish & Release

- [x] **App-Icon:** Adaptive Icon vollständig (`mipmap-anydpi-v26/logo.xml` + alle Dichte-Stufen). Play Store Hi-Res Icon (512×512 PNG) → `app/src/main/res/drawable/play_store_icon_512.png`.
- [x] **Splash Screen:** `androidx.core:core-splashscreen:1.0.1` — `Theme.SplashScreen` in themes.xml, `installSplashScreen()` in MainActivity, Hintergrundfarbe `#EEF1ED`
- [x] **Ladezeiten überbrücken:** `ShimmerBox`, `ShimmerListItem`, `ShimmerCard` in `ui/components/Shimmer.kt`
- [x] **Accessibility:** Alle `contentDescription`-Werte geprüft
- [x] **Proguard/R8:** `isMinifyEnabled = true` + `isShrinkResources = true` im Release-BuildType

---

## Phase 12 — Monetarisierung

### Google Play Console

1. [x] In-App-Produkt → Abonnement: `premium_monthly` (z.B. 2,99 €/Monat)
2. [x] Testgruppe anlegen (interne Tester können kostenlos kaufen)
3. [x] Produkt-ID in RevenueCat eintragen

### RevenueCat initialisieren (`Application.onCreate()`)

```kotlin
class AchtsamkeitApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Purchases.configure(
            PurchasesConfiguration.Builder(this, "revenuecat_public_api_key").build()
        )
    }
}
```

`AchtsamkeitApp` in `AndroidManifest.xml` als `android:name=".AchtsamkeitApp"` eintragen.

### `data/repository/PremiumRepository.kt`

```kotlin
object PremiumRepository {
    suspend fun isPremium(): Boolean {
        val info = Purchases.sharedInstance.awaitCustomerInfo()
        return info.entitlements["premium"]?.isActive == true
    }
}
```

### Feature-Gating (Beispiel)

```kotlin
val isPremium by produceState(false) { value = PremiumRepository.isPremium() }

if (!isPremium && selectedDays > 30) {
    PaywallCard(onUpgrade = { /* RevenueCat Paywall öffnen */ })
} else {
    MoodBarChart(data)
}
```

Premium-Features:
- [x] Statistiken über 30 Tage hinaus
- [x] Monatsrückblick freischalten
- [x] Alle 3 Varianten (Velvet + Aura) freischalten (Hain ist immer kostenlos)
- [x] Favoriten-Liste ohne Limit *(Verschoben in Backlog)*

---

## Phase 11 — Account & Datenschutz

### `ui/profil/ProfilScreen.kt` — Aufbau

```
Column
  ├── Profilbild / Name / E-Mail
  ├── Button "Aussehen anpassen" → ThemePickerScreen
  ├── Button "Benachrichtigungen" → NotificationSettingsScreen
  ├── Button "Favorisierte Sprüche" → FavoritesScreen
  ├── Divider
  ├── Button "Datenschutzerklärung" → öffnet URL im Browser
  ├── Button "Meine Daten exportieren" → JSON/PDF/Excel via Share Intent
  ├── Button "Abmelden" → AuthRepository.logout() → navigate(Login)
  └── Button "Konto löschen" (rot) → AlertDialog → deleteAccount()
```

### Account-Löschung

- [x] Account-Löschung implementieren (Subcollections → User-Dokument → Firebase Auth User → DataStore leeren)

### Daten-Export

- [x] Export-Dialog im Profilscreen mit JSON, PDF und Excel implementiert

---

## Phase 10 — Statistiken

### Dateistruktur

```
ui/stats/
  StatistikScreen.kt
  components/
    MoodBarChart.kt
    GratitudePieChart.kt
    StreakCard.kt
data/repository/
  StatsRepository.kt
```

### `data/repository/StatsRepository.kt`

```kotlin
suspend fun getMoodDistribution(userId: String, days: Int = 30): Map<String, Int> {
    val since = System.currentTimeMillis() - days * 86_400_000L
    return Firebase.firestore
        .collection("users").document(userId)
        .collection("entries")
        .whereGreaterThan("created_at", since)
        .get().await()
        .groupingBy { it.getString("mood") ?: "unknown" }
        .eachCount()
}
```

### `ui/stats/StatistikScreen.kt`

- [x] **StreakCard:** "🔥 12 Tage in Folge"
- [x] **MoodBarChart:** Stimmungsverteilung
- [x] **GratitudePieChart:** Kreisdiagramm der Dankbarkeits-Kategorien
- [x] Zeitraum-Wechsler: 7 / 30 / 90 Tage

---

## Phase 9 — Tagebuch-Historie & Suche

### Dateistruktur

```
ui/history/
  TagebuchScreen.kt
  EntryDetailScreen.kt
  components/
    EntryListItem.kt
    TagFilterChips.kt
data/repository/
  HistoryRepository.kt
```

### `data/repository/HistoryRepository.kt`

```kotlin
suspend fun getEntries(userId: String, limit: Long = 50): List<Entry> {
    return Firebase.firestore
        .collection("users").document(userId)
        .collection("entries")
        .orderBy("created_at", Query.Direction.DESCENDING)
        .limit(limit)
        .get().await()
        .toObjects(Entry::class.java)
}

suspend fun getEntriesByTag(userId: String, tag: String): List<Entry> {
    return Firebase.firestore
        .collection("users").document(userId)
        .collection("entries")
        .whereArrayContains("tags", tag)
        .orderBy("created_at", Query.Direction.DESCENDING)
        .get().await()
        .toObjects(Entry::class.java)
}
```

> ⚠️ Firestore erlaubt nur **ein** `whereArrayContains` pro Query. Für Multi-Tag-Filterung: `whereArrayContainsAny` (OR-Logik) oder lokal nachfiltern.

- [x] `TagebuchScreen.kt`: LazyColumn mit Tag-Filter-Chips
- [x] `EntryDetailScreen.kt`: Detail-Ansicht mit Edit- und Lösch-Funktion

---

## Phase 8 — Rotierende Fragen & Rückblicke

### Rotierende Leitfragen

Rotation-Formel:
```kotlin
val todayIndex  = LocalDate.now().dayOfYear % questions.size
val todayQuestion = questions[todayIndex]
```

### `data/repository/ReviewRepository.kt` — Unlock-Checks

```kotlin
suspend fun isWeeklyReviewUnlocked(userId: String): Boolean {
    val monday = LocalDate.now().with(DayOfWeek.MONDAY)
        .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val snap = Firebase.firestore
        .collection("users").document(userId)
        .collection("entries")
        .whereIn("type", listOf("morning", "evening"))
        .whereGreaterThan("created_at", monday)
        .get().await()
    return snap.size() >= 3
}

suspend fun isMonthlyReviewUnlocked(userId: String): Boolean {
    val today = LocalDate.now()
    val lastWeekStart = today.withDayOfMonth(today.lengthOfMonth() - 6)
    return today >= lastWeekStart  // letzte 7 Tage des Monats
}
```

- [x] `WeeklyReviewScreen.kt` — 5–7 Freitextfragen, `type: "weekly_review"` in Firestore
- [x] `MonthlyReviewScreen.kt` — tiefere Reflexionsfragen, `type: "monthly_review"` in Firestore

---

## Phase 7 — Push-Benachrichtigungen (FCM → lokal migriert)

### `service/AchtsameMessagingService.kt`

```kotlin
class AchtsameMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: "Zeit für dich"
        val body  = message.notification?.body  ?: "Dein Eintrag wartet."
        val notif = NotificationCompat.Builder(this, "achtsam_reminder")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(this)
            .notify(System.currentTimeMillis().toInt(), notif)
    }

    override fun onNewToken(token: String) {
        val uid = Firebase.auth.currentUser?.uid ?: return
        Firebase.firestore.collection("users").document(uid)
            .update("fcm_token", token)
    }
}
```

- [x] Permission-Check: `rememberPermissionState(POST_NOTIFICATIONS)`
- [x] Zeitwähler für Morgen + Abend (Android `TimePickerDialog`)
- [x] In Phase 14 auf lokale Notifications (`AlarmManager`) migriert

---

## Phase 6 — Spruch-Logik & Favoriten

### `model/Quote.kt`

```kotlin
@Serializable
data class Quote(
    val id: String,
    val text: String,
    val author: String,
    val tags: List<String>,
)
```

### `data/local/QuoteLoader.kt`

```kotlin
class QuoteLoader(private val context: Context) {
    val quotes: List<Quote> by lazy {
        val raw = context.assets.open("sprueche.json").bufferedReader().readText()
        Json.decodeFromString<List<Quote>>(raw)
    }
}
```

### `data/repository/QuoteRepository.kt` — Auswahl-Algorithmus

```kotlin
class QuoteRepository(private val loader: QuoteLoader) {
    suspend fun pickQuote(userId: String, tags: List<String>): Quote {
        val cooldowns  = loadCooldowns(userId)
        val cooldownMs = 90L * 24 * 60 * 60 * 1000  // 3 Monate
        val now        = System.currentTimeMillis()

        val eligible = loader.quotes.filter { q ->
            (now - (cooldowns[q.id] ?: 0L)) > cooldownMs
        }
        val matching = eligible.filter { q -> q.tags.any { it in tags } }
        val pool     = matching.ifEmpty { eligible }.ifEmpty { loader.quotes }

        val picked   = pool.random()
        saveCooldown(userId, picked.id)
        return picked
    }

    suspend fun toggleFavorite(userId: String, quote: Quote) {
        val ref = db.collection("users").document(userId)
            .collection("favorites").document(quote.id)
        val existing = ref.get().await()
        if (existing.exists()) ref.delete().await()
        else ref.set(mapOf(
            "saved_at"   to FieldValue.serverTimestamp(),
            "quote_text" to quote.text,
        )).await()
    }
}
```

- [x] `QuoteScreen.kt` mit Herzchen-Button + Teilen-Button

---

## Phase 5 — Kern-Feature: Tageseintrag

### `model/Entry.kt`

```kotlin
@Serializable
data class Entry(
    val id: String = "",
    val type: String = "",                       // "morning" | "evening"
    val createdAt: Long = 0L,
    val dateStr: String = "",                    // "2026-05-15"
    val energyLevel: String = "",
    val mood: String = "",
    val gratitudeAreas: List<String> = emptyList(),
    val dayRating: Int = 0,                      // 1 | 3 | 5
    val selfCare: List<String> = emptyList(),
    val mindfulnessFocus: String = "",
    val mindfulnessPause: String = "",
    val tags: List<String> = emptyList(),
    val guidedQuestion: String = "",
    val guidedAnswer: String = "",
    val freeText: String = "",
)
```

### `data/repository/EntryRepository.kt` — Eintrag speichern

```kotlin
suspend fun saveEntry(userId: String, entry: Entry): String {
    val map = mapOf(/* alle Felder */)
    val ref = Firebase.firestore
        .collection("users").document(userId)
        .collection("entries").document()
    ref.set(map)          // kein .await() — Firestore-SDK synct im Hintergrund
    return ref.id
}
```

- [x] `EntryScreen.kt` mit allen Sections (Energy, Mood, Gratitude, Rating, SelfCare, Mindfulness, GuidedQuestion, FreeText)
- [x] Checkbox → Tag-Mapping in `deriveTags()`
- [x] Morgen/Abend-Eintrag-Status auf `HeuteScreen`

---

## Phase 4 — Navigation & App Shell

### `ui/navigation/Screen.kt`

```kotlin
sealed class Screen(val route: String) {
    object Login      : Screen("login")
    object Register   : Screen("register")
    object Onboarding : Screen("onboarding")
    object Heute      : Screen("heute")
    object Tagebuch   : Screen("tagebuch")
    object Statistik  : Screen("statistik")
    object Profil     : Screen("profil")
    object Quote      : Screen("quote/{entryId}") {
        fun createRoute(entryId: String) = "quote/$entryId"
    }
    object Entry      : Screen("entry/{type}") {
        fun createRoute(type: String) = "entry/$type"
    }
    object EntryDetail : Screen("entry_detail/{entryId}") {
        fun createRoute(entryId: String) = "entry_detail/$entryId"
    }
    object WeeklyReview  : Screen("weekly_review")
    object MonthlyReview : Screen("monthly_review")
    object ThemePicker   : Screen("theme_picker")
    object NotifSettings : Screen("notif_settings")
}
```

- [x] `NavGraph.kt` mit allen Routen verdrahtet
- [x] `BottomNavBar.kt` mit 4 Tabs (Heute, Tagebuch, Statistik, Profil)
- [x] Bottom Nav nur auf Haupt-Tabs, bei Entry/Quote etc. ausgeblendet

---

## Phase 3 — Authentication & Onboarding

### `data/repository/AuthRepository.kt`

```kotlin
class AuthRepository {
    private val auth = Firebase.auth
    private val db   = Firebase.firestore

    suspend fun loginWithEmail(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
        ensureUserDocument(auth.currentUser!!)
    }

    suspend fun registerWithEmail(email: String, password: String, name: String): Result<Unit> = runCatching {
        auth.createUserWithEmailAndPassword(email, password).await()
        ensureUserDocument(auth.currentUser!!, name)
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    fun logout() = auth.signOut()

    private suspend fun ensureUserDocument(user: FirebaseUser, name: String = "") {
        val doc  = db.collection("users").document(user.uid)
        val snap = doc.get().await()
        if (!snap.exists()) {
            doc.set(mapOf(
                "email"                to user.email,
                "display_name"         to (name.ifBlank { user.displayName ?: "" }),
                "created_at"           to FieldValue.serverTimestamp(),
                "onboarding_complete"  to false,
                "notification_morning" to "08:00",
                "notification_evening" to "21:00",
            )).await()
        }
    }
}
```

- [x] `LoginScreen.kt` (E-Mail + Google)
- [x] `RegisterScreen.kt` mit client-seitiger Validierung
- [x] `OnboardingScreen.kt` — HorizontalPager mit Datenschutzeinwilligung

---

## Phase 2 — Design System

### Dateien

```
ui/theme/
  Color.kt
  Shape.kt
  Theme.kt
  ThemePreferences.kt
  Type.kt
ui/screens/
  ThemePickerScreen.kt
```

- [x] Instrument Serif (Regular + Italic) + Geist (Light–Bold) statisch eingebunden
- [x] 3 Varianten (Hain, Velvet, Aura) × 2 Paletten (Salbei, Sand) → 6 Farbthemen
- [x] `AppTheme.colors` für Design-Extras (inkSoft, hair, accent, mood-Farben)
- [x] `ThemePreferences` via DataStore persistent

---

## Phase 1 — Projekt-Setup & Abhängigkeiten

### Android Studio Projekt

- [x] Template: **Empty Activity**, Kotlin, Minimum SDK: **API 26**, Package: `com.elliewonderland.achtsamkeit`, Build: **Gradle (Kotlin DSL)**

### `app/build.gradle.kts` — Abhängigkeiten

```kotlin
dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.navigation:navigation-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.compose.ui:ui-text-google-fonts")
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.revenuecat.purchases:purchases:7.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
```

### Firebase eingerichtet

1. [x] Firestore Database, Region: **`europe-west3` (Frankfurt)** (DSGVO-konform)
2. [x] Authentication: E-Mail/Passwort + Google
3. [x] Security Rules: `request.auth.uid == userId`

### Firestore-Datenmodell

```
users/{userId}
  ├── email, display_name, created_at, onboarding_complete
  ├── notification_morning, notification_evening
  ├── current_streak, last_entry_date, streak_freeze_used_month
  ├── quote_of_day_date, quote_of_day_id
  │
  ├── entries/{entryId}
  │     ├── type, created_at, date_str, energy_level, mood
  │     ├── gratitude_areas, day_rating, self_care
  │     ├── mindfulness_focus, mindfulness_pause
  │     ├── tags, guided_question, guided_answer, free_text, quote_id
  │
  ├── quote_cooldowns/{quoteId}  →  shown_at: Timestamp
  └── favorites/{quoteId}        →  saved_at, quote_text | lifehack_text
```

### RevenueCat eingerichtet

- [x] Account + Android-Projekt, Abonnement `premium_monthly` angelegt
