# Entwicklungs-Roadmap

> [!IMPORTANT]
> **WICHTIGER HINWEIS ZUM WORKFLOW:**
> Bitte hake jede erledigte Aufgabe sofort ab, indem du das Kästchen ankreuzt (`- [x]`). 
> Nach **jeder** erledigten Aufgabe (oder einem kleinen, zusammenhängenden Block) sollst du einen **Commit & Push** (`git commit` + `git push`) durchführen, um deinen Fortschritt kontinuierlich zu sichern!

---

## Projektstand (Mai 2026)

| Bereich | Status | Details |
|---------|--------|---------|
| Konzept & Inhalte | ✅ Fertig | `App_Inhalte.md`, `TechStack.md`, `Konzept_UX.md` |
| Sprüche-Pool | ✅ Fertig | 360 Sprüche (S001–S360) in `sprueche.json` |
| Design-System Handoff | ✅ Fertig | `handoff/` komplett mit allen Kotlin-Theme-Dateien |
| Phase 1: Projekt-Setup | ✅ Fertig | Projekt ✅, Package ✅, Dependencies ✅, Permissions ✅, Firebase ✅, RevenueCat ✅ |
| Phase 2: Design-System einbinden | ✅ Fertig | Alle Theme-Dateien eingebunden, `MainActivity.kt` verdrahtet, RevenueCat-Key in BuildConfig |
| Phase 3: Authentication & Onboarding | ✅ Fertig | `model/User.kt`, `AuthRepository.kt`, `AuthViewModel.kt`, `LoginScreen.kt`, `RegisterScreen.kt`, `OnboardingScreen.kt` |
| Phase 4: Navigation & App Shell | ✅ Fertig | `Screen.kt`, `NavGraph.kt`, `BottomNavBar.kt`, `AppScaffold.kt` |
| Phase 5: Kern-Feature — Tageseintrag | ✅ Fertig | `Entry.kt`, `EntryRepository.kt`, `EntryViewModel.kt`, `HeuteViewModel.kt`, alle Section-Komponenten, `EntryScreen.kt`, `HeuteScreen.kt`, `guided_questions.json` |
| Phase 6: Spruch-Logik & Favoriten | ✅ Fertig | `Quote.kt`, `QuoteLoader.kt`, `QuoteRepository.kt`, `QuoteViewModel.kt`, `QuoteScreen.kt` |
| Phase 7: Push-Benachrichtigungen | ✅ Fertig | `AchtsameMessagingService.kt`, `NotificationRepository.kt`, `NotificationSettingsScreen.kt` |
| Phase 8: Rotierende Fragen & Rückblicke | ✅ Fertig | `ReviewRepository.kt`, `WeeklyReviewScreen.kt`, `MonthlyReviewScreen.kt` |
| Phase 9–13 | ❌ Offen | — |

---

## Phase 1: Projekt-Setup & Abhängigkeiten

### Android Studio Projekt anlegen

- [x] Android Studio **Hedgehog 2023.1.1** oder neuer
- [x] Template: **Empty Activity** (nicht "Empty Compose Activity" — das ist veraltet)
- [x] Sprache: **Kotlin**
- [x] Minimum SDK: **API 26** (Android 8.0) — deckt ~95% aller aktiven Geräte ab; Voraussetzung für WorkManager
- [x] Package Name: `com.elliewonderland.achtsamkeit`
- [x] Build System: **Gradle (Kotlin DSL)** → alle Build-Dateien enden auf `.kts`

### `app/build.gradle.kts` — Alle Abhängigkeiten

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    compileSdk = 35
    defaultConfig { minSdk = 26 }
    buildFeatures { compose = true }
}

dependencies {
    // Compose BOM — verwaltet alle Compose-Versionen automatisch
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.9.3")

    // Navigation zwischen Screens
    implementation("androidx.navigation:navigation-compose:2.8.4")

    // ViewModel + LiveData/StateFlow für MVVM
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // DataStore — Theme-Einstellung der Nutzerin persistent speichern
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Google Fonts (Instrument Serif + Geist) zur Laufzeit laden
    implementation("androidx.compose.ui:ui-text-google-fonts")

    // Firebase — Datenbank, Auth, Push
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    // RevenueCat — In-App-Käufe und Abonnements
    implementation("com.revenuecat.purchases:purchases:7.10.0")

    // JSON-Parsing für sprueche.json und guided_questions.json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Charts für Statistiken
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.0")

    // Nur für Debug-Builds (Compose Layout Inspector)
    debugImplementation("androidx.compose.ui:ui-tooling")
}
```

### `build.gradle.kts` (Project-Level) — Plugins registrieren

```kotlin
plugins {
    id("com.android.application") version "8.7.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" apply false
}
```

### `AndroidManifest.xml` — Notwendige Permissions

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
<!-- POST_NOTIFICATIONS ist ab Android 13 (API 33) Pflicht für Push-Nachrichten -->
```

### Firebase einrichten (Schritt für Schritt)

1. [x] `console.firebase.google.com` → **Neues Projekt** → Name: "Achtsamkeit in 3 Minuten"
2. [x] Google Analytics: bewusste DSGVO-Entscheidung treffen (kann später aktiviert werden)
3. [x] **Android-App hinzufügen** → Package Name eintragen → `google-services.json` herunterladen → in `app/` Ordner legen
4. [x] **Firestore Database** → "Datenbank erstellen" → Produktionsmodus → Region: **`europe-west3` (Frankfurt)** — Pflicht für DSGVO-konformen EU-Speicherort
5. [x] **Authentication** → "Jetzt starten" → Sign-in-Methoden aktivieren: **E-Mail/Passwort** + **Google**

### Firestore Security Rules (Firebase Console → Firestore → Regeln)

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```
→ Jede Nutzerin kann ausschließlich ihre eigenen Daten lesen und schreiben.

### Firestore-Datenmodell

Dieses Modell jetzt festlegen — spätere Änderungen erfordern Datenmigration:

```
users/{userId}
  ├── email: String
  ├── display_name: String
  ├── created_at: Timestamp
  ├── onboarding_complete: Boolean
  ├── notification_morning: String        // z.B. "08:00"
  ├── notification_evening: String        // z.B. "21:00"
  │
  ├── entries/{entryId}
  │     ├── type: String                  // "morning" | "evening" | "weekly_review" | "monthly_review"
  │     ├── created_at: Timestamp
  │     ├── date_str: String              // "2026-05-15" — für einfache Datumsabfragen
  │     ├── energy_level: String          // "full" | "medium" | "low" | "empty"
  │     ├── mood: String                  // "joy" | "stress" | "balance" | "sadness"
  │     ├── gratitude_areas: Array<String>
  │     ├── day_rating: Number            // 1 | 3 | 5
  │     ├── self_care: Array<String>
  │     ├── mindfulness_focus: String     // "past" | "future" | "present"
  │     ├── mindfulness_pause: String     // "yes_pure" | "yes_distracted" | "no"
  │     ├── tags: Array<String>           // abgeleitet aus Checkboxen, z.B. ["Stress", "Dankbarkeit"]
  │     ├── guided_question: String       // die angezeigte rotierende Frage (gespeichert für späteres Lesen)
  │     ├── guided_answer: String
  │     └── free_text: String
  │
  ├── quote_cooldowns/{quoteId}           // quoteId = z.B. "S001"
  │     └── shown_at: Timestamp
  │
  └── favorites/{quoteId}
        ├── saved_at: Timestamp
        └── quote_text: String            // denormalisiert — schnelle Anzeige ohne extra Abfrage
```

### RevenueCat einrichten

1. [x] Account anlegen: `app.revenuecat.com` → New Project → Android
2. [x] In Google Play Console: Abonnement anlegen (`premium_monthly`, z.B. 2,99 €/Monat)
3. [x] RevenueCat Public SDK Key notieren (wird in Phase 12 gebraucht)

### Verifizierung Phase 1
`./gradlew assembleDebug` läuft fehlerfrei. App startet auf Emulator. In Logcat erscheint keine Firebase-Fehlermeldung.

---

## Phase 2: Design System einbinden

> [!NOTE]
> **Handoff-Dateien sind fertig und bereit zum Übertragen.** Alle Kotlin-Dateien in `handoff/ui/theme/` wurden bereits mit dem Package-Namen `com.elliewonderland.achtsamkeit` versehen. Voraussetzung: Package-Rename aus Phase 1 muss zuerst erledigt sein.

Das komplette Design-System liegt fertig in `handoff/`. Diese Phase bedeutet: kopieren, Package-Namen anpassen, verdrahten.

### Schritt 1 — Kotlin-Dateien in den Android-Projektbaum kopieren

In Android Studio folgende Ordner anlegen und Dateien aus `handoff/` einkopieren:

```
app/src/main/java/{dein.package}/
  ui/
    theme/
      Color.kt              ← aus handoff/ui/theme/Color.kt
      Shape.kt              ← aus handoff/ui/theme/Shape.kt
      Theme.kt              ← aus handoff/ui/theme/Theme.kt
      ThemePreferences.kt   ← aus handoff/ui/theme/ThemePreferences.kt
      Type.kt               ← aus handoff/ui/theme/Type.kt
    screens/
      ThemePickerScreen.kt  ← aus handoff/ui/screens/ThemePickerScreen.kt
```

In jeder kopierten Datei die erste Zeile anpassen:
`package de.lina.achtsamkeit.ui.theme` → `package com.elliewonderland.achtsamkeit.ui.theme`
(Die Handoff-Dateien wurden bereits aktualisiert — beim Kopieren ins Android-Projekt stimmt der Package-Name bereits.)

### Schritt 2 — Schriften einrichten (Instrument Serif + Geist)

**Variante A — Google Fonts (empfohlen, keine APK-Größe):**
Googles offiziellen Guide *"Use downloadable fonts in Compose"* folgen. Eine Datei `app/src/main/res/values/font_certs.xml` anlegen. In `Type.kt` die Placeholder-Zeile `R_FONT_CERTS` durch `R.array.com_google_android_gms_fonts_certs` ersetzen.

**Variante B — Statisch ausliefern (einfacher für den Start):**
Schriftdateien von fonts.google.com herunterladen:
- [x] Instrument Serif: Regular + Italic
- [x] Geist: Light, Regular, Medium, SemiBold, Bold

In `app/src/main/res/font/` ablegen (Dateinamen: nur Kleinbuchstaben + Unterstrich, z.B. `geist_medium.ttf`). In `Type.kt` alle `GoogleFont("…")`-Aufrufe durch `Font(R.font.geist_medium, FontWeight.Medium)` etc. ersetzen.

### Schritt 3 — `sprueche.json` in Assets legen

```
app/src/main/assets/sprueche.json   ← Datei aus dem Projekt-Root hierher kopieren
```

### Schritt 4 — `MainActivity.kt` verdrahten

Den `setContent { … }`-Block aus `handoff/MainActivity.kt` übernehmen:

```kotlin
val initial = ThemeChoice(Variant.HAIN, Palette.SALBEI)
val choice by ThemePreferences.flow(this).collectAsStateWithLifecycle(initial)
AppTheme(variant = choice.variant, palette = choice.palette) {
    AppNavHost(choice)  // Stub — wird in Phase 4 implementiert
}
```

### Theme im Code verwenden

```kotlin
val mat = MaterialTheme.colorScheme  // Standard Material 3 (primary, surface, …)
val app = AppTheme.colors            // Unsere Extras (inkSoft, hair, accent3, mood…)

Surface(color = mat.background) {
    Text("Guten Morgen", color = app.ink, style = MaterialTheme.typography.displayMedium)
    Text("Donnerstag",   color = app.inkSoft, style = MaterialTheme.typography.labelSmall)
}
```

Das Live-Referenzdokument für das Design ist `handoff/README.md`.

### Verifizierung Phase 2
App zeigt einen Screen mit dem Hain-Salbei-Theme. Kein Build-Fehler. Schriften laden korrekt.

---

## Phase 3: Authentication & Onboarding

### Dateistruktur anlegen

```
app/src/main/java/{paket}/
  model/
    User.kt
  data/repository/
    AuthRepository.kt
  ui/auth/
    AuthViewModel.kt
    LoginScreen.kt
    RegisterScreen.kt
  ui/onboarding/
    OnboardingScreen.kt
```

### `model/User.kt`

```kotlin
data class User(
    val uid: String,
    val email: String,
    val displayName: String,
)
```

### `data/repository/AuthRepository.kt`

Kapselt alle Firebase-Auth-Aufrufe — ViewModel ruft nur Repository auf, nie direkt Firebase:

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

### `ui/auth/AuthViewModel.kt`

```kotlin
sealed class AuthUiState {
    object Idle    : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()
    val uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            uiState.value = AuthUiState.Loading
            repo.loginWithEmail(email, password).fold(
                onSuccess = { uiState.value = AuthUiState.Success },
                onFailure = { uiState.value = AuthUiState.Error(it.message ?: "Fehler") }
            )
        }
    }
}
```

### `ui/auth/LoginScreen.kt`

Enthält: E-Mail-Feld, Passwort-Feld, "Anmelden"-Button, "Mit Google anmelden"-Button, Link zu `RegisterScreen`.
Zeigt `Snackbar` wenn `uiState` == `Error`.
Navigiert zu `OnboardingScreen` wenn `onboarding_complete == false`, sonst zu `HeuteScreen`.

### `ui/auth/RegisterScreen.kt`

Enthält: Name-Feld, E-Mail-Feld, Passwort-Feld, Passwort-Wiederholung-Feld, "Registrieren"-Button.
Client-seitige Validierung vor dem API-Aufruf (Passwörter identisch? E-Mail-Format korrekt?).

### `ui/onboarding/OnboardingScreen.kt`

Erscheint beim ersten App-Start (wenn `onboarding_complete == false` in Firestore).
Enthält:
- [x] 2–3 Intro-Screens (HorizontalPager) mit App-Erklärung
- [x] Datenschutzeinwilligung mit klickbarem Link zur Datenschutzerklärung
- [x] "Ich stimme zu und möchte loslegen"-Button → setzt `onboarding_complete: true` in Firestore → navigiert zu `HeuteScreen`

### Verifizierung Phase 3
Login mit Test-Account → Firebase Console → Authentication zeigt neuen User. Firestore zeigt `users/{uid}`-Dokument mit korrekten Feldern.

---

## Phase 4: Navigation & App Shell

### Dateistruktur anlegen

```
ui/navigation/
  Screen.kt
  NavGraph.kt
  BottomNavBar.kt
ui/scaffold/
  AppScaffold.kt
```

### `ui/navigation/Screen.kt` — alle Routen zentral

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
        fun createRoute(type: String) = "entry/$type"   // type = "morning" | "evening"
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

### `ui/navigation/NavGraph.kt`

```kotlin
@Composable
fun AppNavHost(choice: ThemeChoice) {
    val navController = rememberNavController()
    val authRepo  = remember { AuthRepository() }
    val startDest = if (authRepo.getCurrentUser() != null) Screen.Heute.route
                    else Screen.Login.route

    NavHost(navController, startDestination = startDest) {
        composable(Screen.Login.route)         { LoginScreen(navController) }
        composable(Screen.Register.route)      { RegisterScreen(navController) }
        composable(Screen.Onboarding.route)    { OnboardingScreen(navController) }
        composable(Screen.Heute.route)         { HeuteScreen(navController) }
        composable(Screen.Tagebuch.route)      { TagebuchScreen(navController) }
        composable(Screen.Statistik.route)     { StatistikScreen(navController) }
        composable(Screen.Profil.route)        { ProfilScreen(navController, choice) }
        composable(Screen.ThemePicker.route)   { ThemePickerScreen(choice) }
        composable(Screen.NotifSettings.route) { NotificationSettingsScreen(navController) }
        composable(Screen.WeeklyReview.route)  { WeeklyReviewScreen(navController) }
        composable(Screen.MonthlyReview.route) { MonthlyReviewScreen(navController) }
        composable(Screen.Entry.route) { back ->
            val type = back.arguments?.getString("type") ?: "morning"
            EntryScreen(navController, type)
        }
        composable(Screen.Quote.route) { back ->
            val entryId = back.arguments?.getString("entryId") ?: ""
            QuoteScreen(navController, entryId)
        }
        composable(Screen.EntryDetail.route) { back ->
            val entryId = back.arguments?.getString("entryId") ?: ""
            EntryDetailScreen(navController, entryId)
        }
    }
}
```

### `ui/navigation/BottomNavBar.kt`

4 Tabs mit Icon + Label:

| Tab        | Icon (Material)          | Route                 |
|------------|--------------------------|-----------------------|
| Heute      | `Icons.Outlined.Today`   | `Screen.Heute`        |
| Tagebuch   | `Icons.Outlined.Book`    | `Screen.Tagebuch`     |
| Statistiken| `Icons.Outlined.BarChart`| `Screen.Statistik`    |
| Profil     | `Icons.Outlined.Person`  | `Screen.Profil`       |

Aktiver Tab = `AppTheme.colors.accent`, inaktiver Tab = `AppTheme.colors.inkSoft`.
Bottom Nav nur auf den 4 Haupt-Tabs anzeigen — bei `Login`, `Entry`, `Quote` etc. ausblenden.

### Verifizierung Phase 4
Bottom Nav sichtbar. Tippen auf Tabs navigiert zu (noch leeren) Screens. Back-Button-Verhalten korrekt.

---

## Phase 5: Kern-Feature — Tageseintrag

### Dateistruktur anlegen

```
model/
  Entry.kt
data/repository/
  EntryRepository.kt
ui/entry/
  EntryScreen.kt
  EntryViewModel.kt
  components/
    EnergySection.kt
    MoodSection.kt
    GratitudeSection.kt
    RatingSection.kt
    SelfCareSection.kt
    MindfulnessSection.kt
    GuidedQuestionSection.kt
    FreeTextSection.kt
app/src/main/assets/
  guided_questions.json
```

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

### `app/src/main/assets/guided_questions.json`

```json
{
  "morning": [
    "Wie möchte ich mich heute fühlen?",
    "Was ist meine wichtigste Absicht für heute?",
    "Was freut mich heute ganz besonders?",
    "Welche Herausforderung könnte heute auf mich warten — und wie gehe ich damit um?",
    "Was wäre ein kleines Zeichen, dass dieser Tag gut war?"
  ],
  "evening": [
    "Was war der schönste Moment heute?",
    "Was habe ich heute gelernt?",
    "Wofür bin ich heute besonders dankbar?",
    "Was hätte ich heute anders machen können?",
    "Was nehme ich aus dem heutigen Tag mit?"
  ]
}
```

Rotation-Logik: `val index = LocalDate.now().dayOfYear % questions.size`
→ Alle Nutzerinnen sehen am gleichen Tag die gleiche Frage.

### Checkbox → Tag-Mapping (`data/repository/EntryRepository.kt`)

```kotlin
fun deriveTags(entry: Entry): List<String> {
    val tags = mutableListOf<String>()
    when (entry.mood) {
        "stress"   -> tags.add("Stress")
        "joy"      -> tags.add("Freude")
        "balance"  -> tags.add("Ausgeglichenheit")
        "sadness"  -> tags.add("Traurigkeit")
    }
    when (entry.energyLevel) {
        "full"   -> tags.add("Energie")
        "empty"  -> tags.add("Stress")
    }
    if ("achievement" in entry.gratitudeAreas) tags.add("Dankbarkeit")
    if (entry.dayRating >= 3)                  tags.add("Dankbarkeit")
    if ("breathing" in entry.selfCare ||
        "outside" in entry.selfCare)           tags.add("Selbstfürsorge")
    return tags.distinct()
}
```

### `data/repository/EntryRepository.kt` — Eintrag speichern

```kotlin
suspend fun saveEntry(userId: String, entry: Entry): String {
    val withTags = entry.copy(
        tags      = deriveTags(entry),
        createdAt = System.currentTimeMillis(),
        dateStr   = LocalDate.now().toString(),
    )
    val ref = Firebase.firestore
        .collection("users").document(userId)
        .collection("entries").document()
    ref.set(withTags).await()
    return ref.id  // wird an QuoteScreen übergeben
}

suspend fun hasEntryToday(userId: String, type: String): Boolean {
    val startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val snap = Firebase.firestore
        .collection("users").document(userId)
        .collection("entries")
        .whereEqualTo("type", type)
        .whereGreaterThan("created_at", startOfDay)
        .get().await()
    return !snap.isEmpty
}
```

### `ui/entry/EntryScreen.kt` — Aufbau

```
Column (vertikal scrollbar)
  ├── Header: "Guten Morgen, {Name}" oder "Guten Abend, {Name}"
  │   Uhrzeit-Logik: vor 17:00 Uhr = morning-Modus, ab 17:00 Uhr = evening-Modus
  ├── EnergySection     (Radio-Buttons)
  ├── MoodSection       (Radio-Buttons)
  ├── GratitudeSection  (Checkboxen, Mehrfachauswahl)
  ├── RatingSection     (3 auswählbare Sterne-Optionen)
  ├── SelfCareSection   (Checkboxen, Mehrfachauswahl)
  ├── MindfulnessSection(Radio-Buttons, 2 Fragen)
  ├── GuidedQuestionSection (Label + TextField)
  └── FreeTextSection   ("Weitere Gedanken..." TextField, optional)
      └── "Fertig"-Button → saveEntry() → navigate(QuoteScreen)
```

### `ui/heute/HeuteScreen.kt`

Prüft via `EntryRepository.hasEntryToday()`:
- [x] Morgen-Eintrag fehlt → Button "Morgen starten" aktiv
- [x] Abend-Eintrag fehlt → Button "Abend starten" aktiv (erst ab 17 Uhr sichtbar)
- [x] Beide vorhanden → "Heute abgeschlossen ✓" + ggf. Wochenrückblick-Button

### Verifizierung Phase 5
Eintrag vollständig ausfüllen → Firestore zeigt neues Dokument in `users/{uid}/entries/` mit korrekt abgeleiteten Tags.

---

## Phase 6: Spruch-Logik & Favoriten

### Dateistruktur anlegen

```
model/
  Quote.kt
data/local/
  QuoteLoader.kt
data/repository/
  QuoteRepository.kt
ui/quote/
  QuoteScreen.kt
```

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
    private val db = Firebase.firestore

    suspend fun pickQuote(userId: String, tags: List<String>): Quote {
        val cooldowns  = loadCooldowns(userId)              // Map<quoteId, shownAt>
        val cooldownMs = 90L * 24 * 60 * 60 * 1000         // 3 Monate in Millisekunden
        val now        = System.currentTimeMillis()

        val eligible = loader.quotes.filter { q ->
            (now - (cooldowns[q.id] ?: 0L)) > cooldownMs
        }
        val matching = eligible.filter { q -> q.tags.any { it in tags } }
        val pool     = matching.ifEmpty { eligible }.ifEmpty { loader.quotes }  // Fallback

        val picked   = pool.random()
        saveCooldown(userId, picked.id)
        return picked
    }

    private suspend fun loadCooldowns(userId: String): Map<String, Long> {
        val snap = db.collection("users").document(userId)
            .collection("quote_cooldowns").get().await()
        return snap.documents.associate { it.id to (it.getTimestamp("shown_at")?.toDate()?.time ?: 0L) }
    }

    private suspend fun saveCooldown(userId: String, quoteId: String) {
        db.collection("users").document(userId)
            .collection("quote_cooldowns").document(quoteId)
            .set(mapOf("shown_at" to FieldValue.serverTimestamp())).await()
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

### `ui/quote/QuoteScreen.kt`

```
Column
  ├── Spruch-Text (SerifItalic-Style aus Type.kt)
  ├── Autorenzeile (inkSoft-Farbe)
  ├── Row
  │   ├── Herzchen-Button → QuoteRepository.toggleFavorite()
  │   └── Teilen-Button → Android Intent.ACTION_SEND mit Spruch-Text
  └── "Weiter"-Button → navController.navigate(Screen.Heute.route)
```

### Verifizierung Phase 6
Eintrag speichern → QuoteScreen erscheint mit passendem Spruch. Herzchen tippen → Firestore zeigt `favorites/S001`-Dokument. Erneuter Tap → Dokument verschwindet wieder.

---

## Phase 7: Push-Benachrichtigungen (FCM)

### Dateistruktur anlegen

```
service/
  AchtsameMessagingService.kt
data/repository/
  NotificationRepository.kt
ui/settings/
  NotificationSettingsScreen.kt
```

### `service/AchtsameMessagingService.kt`

```kotlin
class AchtsameMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: "Zeit für dich"
        val body  = message.notification?.body  ?: "Dein Eintrag wartet."
        val notif = NotificationCompat.Builder(this, "achtsam_reminder")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)   // eigenes Icon anlegen
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(this)
            .notify(System.currentTimeMillis().toInt(), notif)
    }

    override fun onNewToken(token: String) {
        // Token in Firestore speichern (für serverseitiges Senden)
        val uid = Firebase.auth.currentUser?.uid ?: return
        Firebase.firestore.collection("users").document(uid)
            .update("fcm_token", token)
    }
}
```

In `AndroidManifest.xml` registrieren:
```xml
<service android:name=".service.AchtsameMessagingService" android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT"/>
    </intent-filter>
</service>
```

### Notification Channel (in `MainActivity.onCreate`)

```kotlin
NotificationManagerCompat.from(this).createNotificationChannel(
    NotificationChannelCompat
        .Builder("achtsam_reminder", NotificationManagerCompat.IMPORTANCE_DEFAULT)
        .setName("Tägliche Erinnerungen")
        .setDescription("Morgen- und Abend-Erinnerungen für dein Tagebuch")
        .build()
)
```

### `ui/settings/NotificationSettingsScreen.kt`

- [x] Permission-Check: `rememberPermissionState(POST_NOTIFICATIONS)` → Permission-Dialog auslösen, bevor Erinnerungen aktiviert werden
- [x] Zeitwähler für Morgen + Abend (Android `TimePickerDialog`)
- [x] Gewählte Zeiten in Firestore speichern (`notification_morning`, `notification_evening`)

> **Hinweis Versand-Logik:** Für zeitgesteuertes Versenden gibt es zwei Wege:
> - **MVP (lokal):** `WorkManager` + `AlarmManager` direkt auf dem Gerät — kein Server nötig, aber Benachrichtigung bleibt aus wenn App zu lange nicht geöffnet wurde.
> - **Produktiv (empfohlen):** Firebase Cloud Functions, die täglich laufen und FCM-Tokens aller Nutzerinnen zur gewünschten Uhrzeit triggern.

### Verifizierung Phase 7
Firebase Console → Cloud Messaging → "Testnachricht senden" → Benachrichtigung erscheint auf dem Gerät. Tippen auf Benachrichtigung öffnet App.

---

## Phase 8: Rotierende Fragen & Rückblicke

### Rotierende Leitfragen verdrahten

`guided_questions.json` wurde bereits in Phase 5 angelegt. Ladelogik analog zu `QuoteLoader.kt`.

Rotation-Formel:
```kotlin
val questions   = questionLoader.morning  // oder .evening
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

### `ui/weekly/WeeklyReviewScreen.kt`

- [x] Sichtbar auf `HeuteScreen` wenn `isWeeklyReviewUnlocked() == true`
- [x] 5–7 offene Freitextfragen (aus `App_Inhalte.md` übernehmen)
- [x] Wird als `type: "weekly_review"` in Firestore gespeichert

### `ui/monthly/MonthlyReviewScreen.kt`

- [x] Sichtbar in der letzten Woche des Monats
- [x] Tiefere Reflexionsfragen für den Monatsrückblick
- [x] Wird als `type: "monthly_review"` in Firestore gespeichert

### Verifizierung Phase 8
Wochenrückblick-Button erscheint auf HeuteScreen nach ≥ 3 Einträgen in dieser Woche. Rückblick speichern → Firestore zeigt Dokument mit `type: "weekly_review"`.

---

## Phase 9: Tagebuch-Historie & Suche

### Dateistruktur anlegen

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

> ⚠️ Firestore erlaubt nur **ein** `whereArrayContains` pro Query. Für Multi-Tag-Filterung: entweder `whereArrayContainsAny` (OR-Logik) oder lokal nachfiltern.

### `ui/history/TagebuchScreen.kt`

```
Column
  ├── Suchleiste / Filter-Bereich
  │   └── TagFilterChips: auswählbare Chips für alle vorhandenen Tags
  └── LazyColumn
      └── EntryListItem pro Eintrag:
            Datum | Typ (Morgen/Abend) | Stimmungs-Emoji | erste 60 Zeichen Freitext
```

Tippen auf `EntryListItem` → `EntryDetailScreen`

### `ui/history/EntryDetailScreen.kt`

Zeigt alle Felder eines vergangenen Eintrags **lesend** an. Keine Bearbeitungsfunktion (bewusste Design-Entscheidung — Achtsamkeit ist kein Revisionsprotokoll).

### Verifizierung Phase 9
Nach 5 Testeinträgen erscheinen diese in der Liste. Filtern nach "Stress" zeigt nur Stress-Einträge.

---

## Phase 10: Statistiken

### Dateistruktur anlegen

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
        .whereIn("type", listOf("morning", "evening"))
        .get().await()
        .groupingBy { it.getString("mood") ?: "unknown" }
        .eachCount()
}

suspend fun getCurrentStreak(userId: String): Int {
    // Tage rückwärts zählen bis ein Tag ohne Eintrag gefunden wird
    var streak = 0
    var date   = LocalDate.now()
    while (hasEntryOnDate(userId, date)) { streak++; date = date.minusDays(1) }
    return streak
}
```

### `ui/stats/StatistikScreen.kt`

Zeigt (Zeitraum-Wechsler: 7 / 30 / 90 Tage):
- [ ] **StreakCard:** "🔥 12 Tage in Folge" — motivierendes Kern-Element
- [ ] **MoodBarChart:** Stimmungsverteilung — Balken in den Farben aus `MoodColors` (`Color.kt`)
- [ ] **GratitudePieChart:** Kreisdiagramm der Dankbarkeits-Kategorien
- [ ] Charts mit der Vico-Bibliothek bauen (`com.patrykandpatrick.vico:compose-m3`)

### Verifizierung Phase 10
Nach ≥ 7 Testeinträgen mit verschiedenen Stimmungen werden korrekte Balken angezeigt. Streak zählt korrekt.

---

## Phase 11: Account & Datenschutz

### `ui/profil/ProfilScreen.kt` — Aufbau

```
Column
  ├── Profilbild / Name / E-Mail
  ├── Button "Aussehen anpassen" → ThemePickerScreen
  ├── Button "Benachrichtigungen" → NotificationSettingsScreen
  ├── Button "Favorisierte Sprüche" → FavoritesScreen (LazyColumn der favoriten Quotes)
  ├── Divider
  ├── Button "Datenschutzerklärung" → öffnet URL im Browser (Intent.ACTION_VIEW)
  ├── Button "Meine Daten exportieren" → Alle Einträge als JSON via Share Intent
  ├── Button "Abmelden" → AuthRepository.logout() → navigate(Login)
  └── Button "Konto löschen" (rot) → AlertDialog mit Bestätigung → deleteAccount()
```

### Account-Löschung — korrekte Reihenfolge
- [ ] Account-Löschung implementieren:

```kotlin
suspend fun deleteAccount(userId: String) {
    // 1. Subcollections löschen (Firestore löscht Parent NICHT automatisch)
    deleteCollection("users/$userId/entries")
    deleteCollection("users/$userId/quote_cooldowns")
    deleteCollection("users/$userId/favorites")
    // 2. User-Dokument selbst löschen
    Firebase.firestore.collection("users").document(userId).delete().await()
    // 3. Firebase Auth User löschen
    Firebase.auth.currentUser?.delete()?.await()
    // 4. App-seitig: lokale DataStore-Daten leeren, zu LoginScreen navigieren
}
```

> ⚠️ `deleteCollection()` muss Dokumente in Batches löschen (max. 500 pro Batch-Operation). Firebase-Dokumentation: *"Deleting collections from a mobile client is not recommended"* — alternativ via Cloud Function ausführen.

### Daten-Export
- [ ] Daten-Export Funktion implementieren:

```kotlin
suspend fun exportUserData(userId: String): String {
    val entries   = getEntries(userId, limit = 10_000)
    val favorites = getFavorites(userId)
    val exportObj = mapOf("entries" to entries, "favorites" to favorites)
    return Json.encodeToString(exportObj)   // als JSON-String
}
 // Dann via Share Intent als .json-Datei teilen
```

### Verifizierung Phase 11
Konto anlegen → Daten exportieren → JSON korrekt. Konto löschen → Firebase Console zeigt keinen User mehr, Firestore zeigt keine Nutzerdaten mehr.

---

## Phase 12: Monetarisierung

### Google Play Console

1. [ ] In-App-Produkt → Abonnement: `premium_monthly` (z.B. 2,99 €/Monat)
2. [ ] Testgruppe anlegen (interne Tester können kostenlos kaufen)
3. [ ] Produkt-ID in RevenueCat eintragen

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
// In StatistikScreen.kt:
val isPremium by produceState(false) { value = PremiumRepository.isPremium() }

if (!isPremium && selectedDays > 30) {
    PaywallCard(onUpgrade = { /* RevenueCat Paywall öffnen */ })
} else {
    MoodBarChart(data)
}
```

Premium-Features (Vorschlag):
- [ ] Statistiken über 30 Tage hinaus
- [ ] Monatsrückblick freischalten
- [ ] Alle 3 Varianten (Velvet + Aura) freischalten (Hain ist immer kostenlos)
- [ ] Favoriten-Liste ohne Limit

### Verifizierung Phase 12
Mit internem Test-Account Abo abschließen → `isPremium()` gibt `true` zurück. Premium-Features sichtbar.

---

## Phase 13: Polish & Release

- [ ] **App-Icon:** 512×512 px PNG in Android Studio → New → Image Asset → Adaptive Icon anlegen (Foreground + Background getrennt)
- [ ] **Splash Screen:** `implementation("androidx.core:core-splashscreen:1.0.1")` — Icon auf Theme-Hintergrundfarbe, kein Text
- [ ] **Ladezeiten überbrücken:** Skeleton-Composables (shimmer Effekt) während Firestore-Abfragen laufen
- [ ] **Accessibility:** `contentDescription` für alle Icons und Bilder. Test mit Android TalkBack aktivieren.
- [ ] **Proguard/R8:** `minifyEnabled = true` im Release-BuildType — reduziert APK-Größe erheblich
- [ ] **Interner Testlauf:** Mindestens 7 Tage echte Nutzung auf dem eigenen Gerät. Streak, Spruch-Cooldown, Rückblick-Unlock testen.
- [ ] **Play Store:**
  1. [ ] Release APK/AAB signieren (Keystore erstellen + sicher aufbewahren — bei Verlust kein Update mehr möglich!)
  2. [ ] Screenshots (Pflicht: mind. 2 Phone-Screenshots), Kurz- und Langbeschreibung auf Deutsch
  3. [ ] Datenschutzerklärung-URL in Play Console eintragen (Pflicht)
  4. [ ] Release Track: **Intern → Geschlossen (Beta) → Produktion**
