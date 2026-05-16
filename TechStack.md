# Tech Stack

## Technologien

*   **Frontend (UI & Logik):** Kotlin mit Jetpack Compose.
    Das ist der aktuelle Goldstandard für native Android-Entwicklung. Jetpack Compose macht es unheimlich leicht, genau die schönen, modernen und flüssigen UIs zu bauen, die eine Achtsamkeits-App braucht.
*   **Backend & Datenbank:** Firebase (Firestore & Authentication).
    Firestore ist eine NoSQL-Datenbank, die extrem schnell ist und Out-of-the-Box offline funktioniert (die Nutzerin kann ihr Tagebuch im Flugzeug schreiben, es synchronisiert sich später). Firebase Authentication übernimmt das sichere Login.
*   **Push-Benachrichtigungen:** Firebase Cloud Messaging (FCM).
    Verschickt die täglichen Morgen- und Abend-Erinnerungen. Die Nutzerin stellt die gewünschten Zeiten in der App ein, FCM liefert die Benachrichtigung zuverlässig aus.
*   **Monetarisierung:** RevenueCat.
    Übernimmt die komplette Logik für In-App-Käufe und Abos (Subscriptions), was die direkte Implementierung der Google Play Billing Library erspart.
*   **Architekturmuster:** MVVM (Model-View-ViewModel).
    Trennt Benutzeroberfläche sauber von Datenlogik für übersichtlichen Code.

## Strategische Hinweise

> **Plattform-Strategie:** Die App startet als native Android-App (Google Play). iOS (via Kotlin Multiplatform oder React Native) ist für eine spätere Phase angedacht, da die Zielgruppe Frauen 20–50 iOS überproportional stark nutzt.

> **DSGVO-Hinweis:** Firebase speichert Daten auf US-amerikanischen Google-Servern. Da die App emotionale Gesundheitsdaten verarbeitet, ist ein Auftragsverarbeitungsvertrag (AVV) mit Google, eine vollständige Datenschutzerklärung und ein Account-Löschungs-Feature (inkl. aller Firestore-Daten) vor dem Release Pflicht.
