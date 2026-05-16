# Handoff · Achtsamkeit in 3 Minuten

Diese Mappe enthält **alles, was du brauchst, um das Design in dein Jetpack-Compose-Projekt zu übertragen**. Drei visuelle Stile (Hain · Velvet · Aura) × vier Farbpaletten (Salbei · Lavendel · Nebel · Pfirsich) = **12 Themes**, zwischen denen Lina später in den Einstellungen wechseln kann. Ihre Auswahl bleibt erhalten (DataStore).

> **Den vollen visuellen Stand siehst du jederzeit live** in `index.html` (im Browser öffnen). Die Kotlin-Dateien hier sind die 1:1-Übersetzung der Design-Tokens — Single Source of Truth ist `design-tokens.json`.

---

## 1 · Was hier liegt

```
handoff/
├── README.md                            ← du bist hier
├── design-tokens.json                   ← Werte-Quelle (Farben, Typo, Radien)
├── MainActivity.kt                      ← Beispiel-Verkabelung
└── ui/
    ├── theme/
    │   ├── Color.kt                     ← rohe Farb-Konstanten
    │   ├── Theme.kt                     ← AppTheme + AppColors + CompositionLocal
    │   ├── Type.kt                      ← Instrument Serif + Geist
    │   ├── Shape.kt                     ← Eckenradien
    │   └── ThemePreferences.kt          ← DataStore-Persistence
    └── screens/
        └── ThemePickerScreen.kt         ← die UI für „Stil & Palette wählen"
```

---

## 2 · Übertragung in Android Studio — Schritt für Schritt

### a) Dateien einsortieren

Kopiere den Inhalt von `handoff/ui/theme/` nach `app/src/main/java/de/lina/achtsamkeit/ui/theme/` (oder deinem Package-Pfad — passe in den Dateien die `package`-Zeile an). Analog für `ui/screens/` und `MainActivity.kt`.

### b) Gradle-Abhängigkeiten

In `app/build.gradle.kts`:

```kotlin
dependencies {
    // Compose BOM (falls noch nicht da) — Version aktuell halten
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.activity:activity-compose:1.9.3")

    // Themes über System-Settings persistieren
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Lifecycle-aware StateFlow → State
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // Google Fonts zur Laufzeit laden
    implementation("androidx.compose.ui:ui-text-google-fonts")
}
```

### c) Schriften (Instrument Serif + Geist)

Zwei Wege — wähle einen:

**Variante A · Google Fonts via Play Services (empfohlen)**
Folge der offiziellen Compose-Anleitung *Use downloadable fonts*. Du musst eine Certs-Resource (`res/values/font_certs.xml`) anlegen und in `Type.kt` darauf zeigen (im Header der Datei steht eine Notiz). Vorteil: keine APK-Größe, immer aktuelle Schnitte.

**Variante B · Statisch ausliefern**
Lade die `.ttf`-Dateien von Google Fonts herunter:
- Instrument Serif — Regular + Italic
- Geist — Light, Regular, Medium, SemiBold, Bold

Lege sie in `app/src/main/res/font/` ab (Kleinbuchstaben, nur `_` und `0-9`, z. B. `geist_medium.ttf`). In `Type.kt` ersetzt du dann die `GoogleFont(…)`-Aufrufe durch `Font(R.font.geist_medium, FontWeight.Medium)` etc.

### d) Im Code verwenden

Im Root deiner App:

```kotlin
val initial = ThemeChoice(Variant.HAIN, Palette.SALBEI)
val choice by ThemePreferences.flow(context).collectAsStateWithLifecycle(initial)

AppTheme(variant = choice.variant, palette = choice.palette) {
    AppNavHost()
}
```

In jedem beliebigen Composable:

```kotlin
val mat = MaterialTheme.colorScheme    // Standard-M3 (primary, surface, …)
val app = AppTheme.colors              // unsere Extras (inkSoft, hair, accent3, mood…)

Surface(color = mat.background) {
    Text("Guten Morgen", color = app.ink, style = MaterialTheme.typography.displayMedium)
    Text("Donnerstag",   color = app.inkSoft, style = MaterialTheme.typography.labelSmall)
}
```

In den Einstellungen einfach `ThemePickerScreen(current = choice)` einbauen — der Picker schreibt direkt in DataStore und löst ein automatisches Re-Theming der gesamten App aus.

---

## 3 · Was die 12 Themes sind

|              | Salbei         | Lavendel        | Nebel          | Pfirsich        |
|--------------|----------------|-----------------|----------------|-----------------|
| **Hain**     | hell · grün    | hell · violett  | hell · blau    | hell · warm     |
| **Velvet**   | dunkel · grün  | dunkel · violett| dunkel · blau  | dunkel · warm   |
| **Aura**     | aurora · grün  | aurora · violett| aurora · blau  | aurora · warm   |

- **Hain** = ruhiger Standard, viel Weiß, klar.
- **Velvet** = Nachtmodus, lädt zur Abendreflexion ein.
- **Aura** = lebendige Gradients, für Tage mit mehr Lust auf Farbe.

Lina kann Stil & Palette **unabhängig** wechseln — also z. B. Velvet · Pfirsich = warmes Nacht-Theme.

---

## 4 · Aurora-Hintergrund nachbauen

Im Web haben wir den Aurora-Hintergrund mit drei weichgezeichneten Farb-Blobs realisiert. In Compose:

```kotlin
@Composable
fun AuroraBackground(modifier: Modifier = Modifier) {
    val c = AppTheme.colors
    Canvas(modifier) {
        // sehr großer Blur-Radius über drei radiale Gradients
        drawCircle(
            brush = Brush.radialGradient(
                listOf(c.accent.copy(alpha = 0.55f), Color.Transparent),
                center = Offset(size.width * 0.2f, size.height * 0.1f),
                radius = size.minDimension * 0.7f,
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(c.accent2.copy(alpha = 0.55f), Color.Transparent),
                center = Offset(size.width * 0.9f, size.height * 0.3f),
                radius = size.minDimension * 0.7f,
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(c.accent3.copy(alpha = 0.55f), Color.Transparent),
                center = Offset(size.width * 0.5f, size.height * 1.05f),
                radius = size.minDimension * 0.7f,
            )
        )
    }
}
```

Wenn du noch weicher willst: `graphicsLayer { renderEffect = BlurEffect(40f, 40f) }` darüberlegen (API 31+).

---

## 5 · Was nicht in dieser Mappe steckt

Bewusst weggelassen, weil sie in jedem Projekt anders aussehen:

- **Navigation Graph** — Bottom Bar mit *Heute · Tagebuch · Insights · Profil* baust du am besten mit `NavHost` selbst.
- **Firestore-Schema** — siehe deine `Roadmap.md` Phase 1.
- **Sprüche-Datenbank** — `uploads/sprueche.json` ist bereits dein Inhalt; lade ihn als seed in Firestore oder bundle ihn in den Assets.
- **Icons** — wir nutzen im Web SVG-Pfade; in Android nimmst du am besten `Material Icons Extended` oder eigene Vector Drawables. Die Pfade in `shared.jsx` (`ICONS`-Objekt) sind direkt nach `pathData` in `vector_*.xml` portierbar.

---

## 6 · Live-Referenz

Wann immer du dir unsicher bist, wie etwas aussehen soll: `index.html` öffnen, im Tweaks-Panel die gewünschte Palette wählen, das jeweilige Screen-Artboard fokussieren (Klick auf das Phone-Label). Pixel-genau ablesen.

Viel Erfolg beim Übertragen — frag mich gerne nach weiteren Screens (Onboarding, Wochenrückblick, Profil) oder Komponenten-Spec.
