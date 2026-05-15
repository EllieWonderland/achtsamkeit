# Tech Stack & Entwicklungs-Roadmap

## Tech Stack
*   **Frontend (UI & Logik):** Kotlin mit Jetpack Compose.
    Das ist der aktuelle Goldstandard für native Android-Entwicklung. Jetpack Compose macht es unheimlich leicht, genau die schönen, modernen und flüssigen UIs zu bauen, die eine Achtsamkeits-App braucht.
*   **Backend & Datenbank:** Firebase (Firestore & Authentication).
    Firestore ist eine NoSQL-Datenbank, die extrem schnell ist und Out-of-the-Box offline funktioniert (die Nutzerin kann ihr Tagebuch im Flugzeug schreiben, es synchronisiert sich später). Firebase Authentication übernimmt das sichere Login.
*   **Monetarisierung:** RevenueCat.
    Übernimmt die komplette Logik für In-App-Käufe und Abos (Subscriptions), was die direkte Implementierung der Google Play Billing Library erspart.
*   **Architekturmuster:** MVVM (Model-View-ViewModel).
    Trennt Benutzeroberfläche sauber von Datenlogik für übersichtlichen Code.

## Roadmap (To-Do Liste)

### Phase 1: Projekt-Setup & Grundgerüst
- [ ] Android Studio einrichten und neues Jetpack Compose Projekt anlegen.
- [ ] Firebase-Projekt erstellen und mit der Android-App verknüpfen.
- [ ] Firebase Authentication implementieren (z. B. Login via Google oder E-Mail).
- [ ] Grundlegendes Datenmodell (Collections & Documents) für Firestore konzipieren.
- [ ] RevenueCat-Account anlegen und SDK in die App integrieren.

### Phase 2: Navigation & Basis-UI
- [ ] Bottom Navigation Bar bauen (Tabs: Heute, Tagebuch-Historie, Statistiken, Profil).
- [ ] Leere Screens für die jeweiligen Tabs anlegen.
- [ ] Logik für "Morgen" und "Abend" Ansicht erstellen (App erkennt die Uhrzeit und zeigt den entsprechenden Screen).

### Phase 3: Core Features (Dateneingabe & Speicherung)
- [ ] UI für die Checkboxen bauen.
- [ ] UI für die Freitextfelder bauen.
- [ ] Logik: Die Checkbox-Auswahl in unsichtbare Tags übersetzen und zusammen mit dem Freitext in Firestore speichern.
- [ ] Spruch-Logik: Algorithmus schreiben, der basierend auf den Tags einen passenden Spruch anzeigt und einen "Cooldown" (z. B. 3 Monate) für diesen Spruch setzt.

### Phase 4: Content & Fragestellungen
- [ ] Baustelle 1: Das Tag- und Checkbox-System final definieren und in die Datenbank/App einpflegen.
- [ ] Baustelle 2: Den Pool der offenen Leitfragen (Morgen/Abend) ausarbeiten und rotierend in die UI einbinden.
- [ ] Baustelle 3: Die Struktur und Fragen für die Wochen- und Monatsrückblicke festlegen.
- [ ] Logik für die Rückblicke programmieren (Überprüfen: "Hat die Nutzerin diese Woche schon 3 Einträge? Wenn ja -> Button freischalten").

### Phase 5: Tagebuch-Historie & Statistiken
- [ ] Listenansicht (RecyclerView/LazyColumn) für vergangene Tagebucheinträge bauen.
- [ ] Filter- und Suchfunktion anhand der versteckten Tags implementieren.
- [ ] UI für die Statistiken entwerfen (z. B. Balkendiagramme für Stimmung, Kuchendiagramme für Auslöser).
- [ ] Daten aus Firestore auslesen und in den Diagrammen visualisieren.

### Phase 6: Polish & Release
- [ ] Design-Feinschliff (Farben, Typografie, abgerundete Ecken, kleine Animationen).
- [ ] App-Icon und Splash-Screen erstellen.
- [ ] In-App-Produkte (z. B. Premium-Abo für erweiterte Statistiken) in der Google Play Console anlegen und mit RevenueCat verknüpfen.
- [ ] Interner Testlauf (App auf dem eigenen Smartphone installieren und mehrere Tage nutzen).
- [ ] Release im Google Play Store.
