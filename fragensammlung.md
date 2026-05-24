# Fragensammlung für die Morgen- und Abendroutine

Dieses Dokument enthält den neuen Entwurf für die Fragen und vorgegebenen Antwortmöglichkeiten der **Morgen- und Abendroutine** in der Achtsamkeits-App.

### Design-Ziele dieses Entwurfs:
1. **Klare Trennung:** Morgen- und Abendroutine erhalten eigenständige, auf den jeweiligen Tageszeitpunkt abgestimmte Antwortmöglichkeiten.
2. **Emotionale Tiefe & Ausdruckskraft:** Weg von langweiligen Standardformulierungen hin zu emotional ausgereiften Beschreibungen, die echte menschliche Gefühle, Sorgen und Alltagsrealitäten widerspiegeln.
3. **Vermeidung von toxischer Positivität:** Hinzufügen von bewussten „Negativ-Optionen“. Wenn ein Tag grau war, man sich selbst vernachlässigt hat oder einfach keine Dankbarkeit empfinden kann, darf und soll das ehrlich eingegeben werden. Die App fängt dies mit tiefem Selbstmitgefühl und Trost auf.
4. **Statistik-Kompatibilität:** Alle Antwortmöglichkeiten sind mit eindeutigen technischen Keys versehen, um sie sauber in der Firestore-Datenbank zu speichern und in den Compose-Charts darzustellen.
5. **Vielfalt an geführten Fragen:** Ein stark erweiterter Pool von jeweils 30 tiefgründigen Impulsfragen für morgens und abends.

---

## 1. Die Morgenroutine (Morgen-Check-in)

Die Morgenroutine fokussiert sich auf Absichten, den aktuellen Zustand nach dem Aufwachen und die Vorbereitung auf den Tag.

### A. Stimmung (Mood)
*Welches Gefühl begleitet mich heute Morgen?* (Einfachauswahl)

| Technischer Key | Anzeige-Label in der App | Psychologischer Hintergrund / Gefühlswelt |
| :--- | :--- | :--- |
| `excitement` | 🌅 **Vorfreude / Elan** – *Voller Tatendrang, motiviert & bereit für den Tag* | Positive Aufregung, Tatkraft, Vorfreude |
| `peace` | 🍃 **Gelassenheit / Frieden** – *Ruhig, zentriert & im Einklang mit mir* | Innere Balance, Zufriedenheit, Ruhe |
| `tiredness` | ☕ **Trägheit / Erschöpfung** – *Noch sehr müde, schwerfällig, sehne mich nach Ruhe* | Physische/mentale Ermüdung nach dem Aufwachen |
| `anxiety` | 🌪️ **Sorge / Anspannung** – *Unruhig, besorgt wegen anstehender Aufgaben oder Hürden* | Nervosität, Zukunftsangst, Stressbereitschaft |
| `melancholy` | 🌧️ **Schwermut / Lustlosigkeit** – *Bedrückt, nachdenklich, mir fehlt gerade der Antrieb* | Traurigkeit, Melancholie, emotionale Schwere |

### B. Energielevel (Energy Level)
*Wie starte ich heute in den Tag?* (Einfachauswahl)

| Technischer Key | Anzeige-Label in der App |
| :--- | :--- |
| `full` | ⚡ **Voller Akku** – *Klarer Kopf, erholt und bereit für alles.* |
| `medium` | 🔋 **Solide Basis** – *Ganz okay, bereit anzufangen (aber erst mal einen Kaffee).* |
| `low` | 🪫 **Im Schonmodus** – *Schwerfällig, Akku recht niedrig, ich gehe es langsam an.* |
| `empty` | 🚨 **Komplett leer** – *Ausgelaugt, jede Bewegung kostet Kraft, brauche dringend Pausen.* |

### C. Dankbarkeit (Gratitude Areas)
*Wofür bin ich heute oder generell in meinem Leben dankbar?* (Mehrfachauswahl)

| Technischer Key | Anzeige-Label in der App | Details / Beispiele |
| :--- | :--- | :--- |
| `relations` | 👥 **Menschen & Beziehungen** | *Familie, enge Freunde, Liebe, eine treue Partnerschaft* |
| `comfort` | 🏡 **Sicherheit & Komfort** | *Ein warmes Bett, ein sicheres Zuhause, Frieden, Privilegien* |
| `health` | 🩺 **Gesundheit & Vitalität** | *Körperliche Gesundheit, atmen können, schmerzfreier Zustand* |
| `nature` | 🌲 **Natur & Umgebung** | *Morgensonne, Vogelgezwitscher, frische Luft, Jahreszeiten* |
| `opportunity` | 🚀 **Chancen & Neubeginn** | *Ein neuer Tag, die Möglichkeit zu lernen, zu arbeiten oder zu gestalten* |
| `self_compassion`| 🌸 **Selbstannahme & eigener Weg**| *Die eigene Resilienz, gemachte Fortschritte, Geduld mit sich selbst* |
| `struggled` | 🌧️ **Dankbarkeit fällt mir heute schwer** | *Es gibt Tage, an denen alles grau ist. Das ist völlig okay und darf sein.* |

### D. Selbstfürsorge-Absicht (Self-Care Intentions)
*Was nehme ich mir heute vor, um gut für mich zu sorgen?* (Mehrfachauswahl)

| Technischer Key | Anzeige-Label in der App | Details / Auswirkung |
| :--- | :--- | :--- |
| `physical` | 💧 **Körper & Pflege** | *Ausreichend Wasser trinken, gesund essen, sanft bewegen* |
| `boundaries` | 🛑 **Gesunde Grenzen** | *Bewusst 'Nein' sagen, Überlastung vermeiden, rechtzeitig stoppen* |
| `digital_detox` | 📱 **Digitaler Schutz** | *Den Morgen/Tag ohne sinnloses Scrollen auf dem Handy verbringen* |
| `soul` | 🎨 **Seelennahrung** | *Etwas tun, das mir Freude bringt (Musik, Lesen, Hobby, Kreativität)* |
| `stillness` | 🧘 **Ruhemomente** | *Tiefes Durchatmen, eine kurze Meditation oder Dehnen einbauen* |
| `compassion` | 🕊️ **Selbstmitgefühl** | *Nett zu mir selbst sprechen, mir Fehler verzeihen, Druck rausnehmen* |
| `no_energy` | 🪫 **Keine Kraft für Vorsätze** | *Ich bin heute im reinen Überlebensmodus und erlege mir keinen Druck auf.* |

### E. Achtsamkeit im Hier und Jetzt (Mindfulness Focus & Pause)
**1. Fokus:** *Bin ich heute Morgen im Moment angekommen?* (Einfachauswahl)
* `present` ➔ 🍃 **Ganz im Jetzt:** *Ich spüre meinen Körper, atme ruhig und bin präsent.*
* `future` ➔ 📅 **Aufgaben-Tunnel:** *Meine Gedanken kreisen schon hektisch um die To-Dos des Tages.*
* `past` ➔ 💭 **Gedankenschwere:** *Ich hänge emotional noch bei gestrigen Erlebnissen oder Sorgen fest.*

**2. Pause:** *Habe ich mir heute Morgen einen bewussten, ruhigen Moment gegönnt?* (Einfachauswahl)
* `yes_pure` ➔ 🧘 **Bewusster Start:** *Ja, in vollkommener Stille oder bei Tee/Kaffee ohne Ablenkung.*
* `yes_distracted` ➔ 📱 **Nebengeschäftigt:** *Ein bisschen, aber mit Handy, Nachrichten oder Podcasts nebenbei.*
* `no` ➔ 🏃 **Direkt im Trubel:** *Nein, ich bin direkt vom Bett in den Autopiloten und die Hektik gesprungen.*

---

## 2. Die Abendroutine (Abend-Check-in)

Die Abendroutine reflektiert den Tag, hilft beim Loslassen und fördert das sanfte Ausklingenlassen des Tages.

### A. Stimmung (Mood)
*Welches Grundgefühl hat meinen Tag heute dominiert?* (Einfachauswahl)

| Technischer Key | Anzeige-Label in der App | Psychologischer Hintergrund / Gefühlswelt |
| :--- | :--- | :--- |
| `satisfaction` | 🥰 **Zufriedenheit / Erfüllung** – *Dankbar für den Tag, glücklich mit kleinen Momenten* | Erfüllung, soziale Wärme, Stolz |
| `relief` | 🍃 **Erleichterung / Entspannung** – *Der Tag ist geschafft, ich komme endlich zur Ruhe* | Stressabbau, Loslassen, Feierabendstimmung |
| `exhaustion` | 🔋 **Erschöpfung / Müdigkeit** – *Körperlich oder mental völlig ausgelaugt vom Tag* | Überlastung, physisches oder kognitives Limit |
| `overwhelmed` | 🌀 **Überforderung / Unruhe** – *Viele kreisende Gedanken, gestresst, kann schwer abschalten* | Akuter Stress, Gedankenkarussell, Sorgen |
| `loneliness` | 🌧️ **Traurigkeit / Einsamkeit** – *Fühle mich missverstanden, allein gelassen oder melancholisch* | Trauer, soziale Isolation, emotionale Kälte |

### B. Energielevel (Energy Level)
*Wie fühle ich mich jetzt nach dem Tag?* (Einfachauswahl)

| Technischer Key | Anzeige-Label in der App |
| :--- | :--- |
| `satisfied_tired` | 🌙 **Zufrieden erschöpft** – *Angenehm müde nach einem produktiven oder ereignisreichen Tag.* |
| `wired` | 🔌 **Unter Strom** – *Körperlich müde, aber mein Geist rattert noch und steht unter Spannung.* |
| `low` | 🪫 **Im roten Bereich** – *Sehr geringe Restenergie, der Tag war anstrengend und kräftezehrend.* |
| `empty` | 🚨 **Völlig ausgebrannt** – *Absolut leer, ich sehne mich nur noch nach Schlaf, Dunkelheit und Ruhe.* |

### C. Dankbarkeit (Gratitude Areas)
*Aus welchem Lebensbereich kam mein heutiger Dankbarkeits-Moment?* (Mehrfachauswahl)

| Technischer Key | Anzeige-Label in der App | Details / Beispiele |
| :--- | :--- | :--- |
| `encounter` | 💬 **Wertvolle Begegnung** | *Ein tiefes Gespräch, ein Lächeln, unerwartete Hilfe, nette Gesten* |
| `micro_joys` | ☕ **Kleine Alltagsfreuden** | *Ein gutes Essen, warme Dusche, Lieblingslied, gemütliche Decke* |
| `achievement` | 🏆 **Erfolg & Fortschritt** | *Ein gelöstes Problem, etwas Erledigtes, Stolz auf das eigene Schaffen* |
| `learning` | 💡 **Erkenntnis & Wachstum** | *Etwas Wichtiges gelernt (auch aus Fehlern oder schweren Zeiten)* |
| `comfort_received`| 🛡️ **Trost & Beistand** | *Ein sicherer Hafen, Mitgefühl erhalten, die Hürde wurde bewältigt* |
| `connection` | 🤝 **Gelungenes Miteinander**| *Ein geklärtes Missverständnis, tiefe Verbundenheit mit Partner/Familie* |
| `none` | 🌧️ **Keiner – mir fiel Dankbarkeit heute extrem schwer** | *Heute gab es keinen Lichtblick und alles war anstrengend. Das ist okay.* |

### D. Selbstfürsorge-Umsetzung (Self-Care Actions)
*Wie habe ich heute für mein Wohlbefinden gesorgt?* (Mehrfachauswahl)

| Technischer Key | Anzeige-Label in der App | Details / Auswirkung |
| :--- | :--- | :--- |
| `needs_met` | 💧 **Bedürfnisse geachtet** | *Genug getrunken, gegessen oder meinem Körper Ruhe gegönnt* |
| `boundaries_kept`| 🛑 **Grenzen gesetzt** | *Mich abgegrenzt (z. B. rechtzeitig Feierabend gemacht, 'Nein' gesagt)* |
| `unplugged` | 📱 **Abschaltzeit gegönnt** | *Offline-Zeit genossen, bewusst Abstand zu Bildschirmen gehalten* |
| `joyful_moment` | 🎨 **Seelenbalsam** | *Zeit mit Dingen verbracht, die mir Spaß machen und mich nähren* |
| `release` | 🌬️ **Druck abgelassen** | *Bewusst durchgeatmet, Stress abgeschüttelt, Tränen zugelassen oder gedehnt* |
| `forgiveness` | 🕊️ **Selbstvergebung** | *Mich so akzeptiert, wie ich heute war – auch mit Fehlern und ohne Perfektion* |
| `neglected` | 🚨 **Mich selbst vernachlässigt** | *Keine Zeit oder Kraft für mich gehabt, eigene Bedürfnisse übergangen.* |

### E. Achtsamkeit im Hier und Jetzt (Mindfulness Focus & Pause)
**1. Fokus:** *Wo waren meine Gedanken heute die meiste Zeit?* (Einfachauswahl)
* `present` ➔ 🍃 **Überwiegend im Jetzt:** *Ich war aufmerksam und konnte den Tag bewusst erleben.*
* `future` ➔ 📅 **In der Zukunft:** *Ich war gedanklich schon bei morgen, habe geplant oder mir Sorgen gemacht.*
* `past` ➔ 💭 **In der Vergangenheit:** *Ich habe viel gegrübelt, Erlebnisse analysiert oder bereut.*
* `autopilot` ➔ 🤖 **Im Autopiloten:** *Der Tag ist wie ein Film an mir vorbeigezogen, ohne dass ich richtig anwesend war.*

**2. Pause:** *Habe ich heute tagsüber bewusst eine kurze Pause eingelegt?* (Einfachauswahl)
* `yes_pure` ➔ 🌲 **Echte Pause:** *Ja, eine handyfreie, stille Auszeit genommen (Natur, Atmen, Nichtstun).*
* `yes_distracted` ➔ 📱 **Konsum-Pause:** *Ja, aber abgelenkt mit Social Media, Mails oder Podcasts.*
* `no` ➔ 🏃 **Dauer-Rauschen:** *Nein, ich war durchgehend aktiv und im Dauer-Rotations-Modus.*

**3. Tagesbewertung (Day Rating):**
*Skala von 1 bis 5 Sternen / Punkten (Wie bewertest du deinen Tag rückblickend als Ganzes?)*

---

## 3. Die geführten Impulsfragen (Guided Questions Pool)

Morgens und abends wird eine zufällige bzw. tagesabhängige tiefgründige Frage gestellt. Hier ist das neue Set aus **jeweils 30 Fragen** mit emotionalem Tiefgang.

### A. Morgen-Fragen (Fokus: Ausrichtung, Träume, Ängste, Selbstwert)
1. Wenn dieser Tag ein Kapitel in deiner Biografie wäre, welchen Titel würdest du ihm heute Morgen geben wollen?
2. Welche Sorge oder welchen Druck von gestern darfst du heute ganz bewusst an der Bettkante zurücklassen?
3. Welche kleine, liebevolle Geste möchtest du dir selbst heute im Laufe des Tages schenken?
4. Auf welche deiner inneren Stärken (z. B. Geduld, Humor, Hartnäckigkeit) möchtest du dich heute besonders verlassen?
5. Wenn dein Körper heute sprechen könnte: Welches Bedürfnis hat er nach dem Aufwachen am dringendsten?
6. Welcher Gedanke beim Aufwachen hat dir ein Lächeln oder ein Gefühl von Ruhe geschenkt?
7. Wie möchtest du heute reagieren, wenn etwas nicht nach Plan läuft? Welches innere Bild hilft dir dabei?
8. Was ist eine Wahrheit über dich selbst, an die du dich heute in stressigen Momenten erinnern möchtest?
9. Welche Beziehung in deinem Leben möchtest du heute mit ein wenig Aufmerksamkeit oder lieben Worten nähren?
10. Stell dir vor, du triffst heute Abend eine glückliche Version deiner selbst. Was hat sie heute tagsüber getan?
11. Welchen Bereich deines Zuhauses oder Lebensraums schätzt du heute Morgen ganz besonders und warum?
12. Welche Erwartung an dich selbst darfst du heute bewusst herunterschrauben, um freier atmen zu können?
13. Wenn du heute nur eine einzige Sache erledigen könntest, die dich wirklich erfüllt – welche wäre das?
14. Welche Eigenschaft an einem Mitmenschen hat dich in letzter Zeit inspiriert und wie kannst du sie heute selbst leben?
15. In welcher Situation möchtest du heute besonders achtsam und aufmerksam zuhören, statt nur zu antworten?
16. Welches Gefühl aus deinen Träumen oder der Nacht nimmst du mit in den Tag? Möchtest du es behalten oder gehen lassen?
17. Für welches scheinbar selbstverständliche Privileg in deinem Alltag bist du heute Morgen zutiefst dankbar?
18. Wie kannst du heute eine gesunde Grenze setzen, um deine eigene Energie zu schützen?
19. Welcher kleine Moment des Wartens heute (z. B. an einer Ampel oder Schlange) kann deine persönliche Oase der Ruhe werden?
20. Was macht dich heute einzigartig und wertvoll, völlig unabhängig von deiner Produktivität oder Leistung?
21. Welches Wort oder Mantra soll dich heute als roter Faden durch den Tag begleiten?
22. Wenn du heute jemandem eine Freude machen könntest, ohne Gegenleistung zu erwarten: Wer wäre das und was tust du?
23. Welches physische Gefühl der Erleichterung oder Frische spürst du gerade in diesem Moment in deinem Körper?
24. Welche Aufgabe schiebst du schon länger vor dir her – und wie kannst du heute den ersten, winzigen Schritt machen?
25. Was nährt deine Seele an einem grauen oder anstrengenden Tag am meisten? Wie bringst du das heute ein?
26. Welche vertraute Stimme oder welches Lachen in deinem Leben gibt dir sofort ein Gefühl von Sicherheit?
27. Worauf freust du dich heute am allermeisten, selbst wenn es nur eine winzige Kleinigkeit ist?
28. Wenn du heute einen Fehler machst: Welchen Satz der Vergebung möchtest du dir jetzt schon bereitlegen?
29. Was bedeutet "Erfolg" für dich am heutigen Tag im emotionalen oder mentalen Sinne?
30. Schließe kurz die Augen: Welches Gefühl der Weite oder Stille kannst du in diesem Moment in dir entdecken?

### B. Abend-Fragen (Fokus: Reflexion, Loslassen, Selbstmitgefühl, Verarbeitung)
1. Was war der verletzlichste oder ehrlichste Moment, den du heute erlebt oder zugelassen hast?
2. Welchen Moment des heutigen Tages würdest du am liebsten in ein imaginäres Glas packen und für immer aufbewahren?
3. Was hat dich heute herausgefordert – und was hat dir geholfen, diese Situation durchzustehen?
4. Gab es heute eine Situation, in der du gerne anders gehandelt hättest? Schenke dir selbst Vergebung dafür. Was lernst du daraus?
5. Wer hat dir heute ein Gefühl von Zugehörigkeit, Gesehenwerden oder Wärme geschenkt?
6. Welcher Gedanke oder welches Gefühl liegt dir jetzt, wo es ruhig wird, am schwersten auf dem Herzen?
7. Auf welchen kleinen persönlicher Triumph oder welche bewiesene Stärke bist du heute stolz?
8. Was durftarzt du heute über dich selbst, andere Menschen oder das Leben lernen?
9. Welche Anspannung in deinem Körper (Schultern, Kiefer, Nacken) darfst du in diesem Moment mit dem Ausatmen loslassen?
10. Worüber konntest du heute schmunzeln, lachen oder eine unerwartete Leichtigkeit spüren?
11. Welcher Teil deines Tages fühlte sich an wie ein "Autopilot" und wie kannst du das morgen bewusster gestalten?
12. Welche unvollendete Aufgabe oder offene Frage darfst du beruhigt auf morgen verschieben, um heute gut zu schlafen?
13. Wenn du den heutigen Tag in einer einzigen Farbe malen müsstest: Welche Farbe wäre es und welches Gefühl verbindest du damit?
14. Gab es heute einen Moment, in dem du eine Grenze gesetzt hast? Wie hat sich das angefühlt?
15. Welche kleine Berührung, welches Geräusch oder welcher Geschmack hat deinen Tag heute besonders bereichert?
16. Wofür möchtest du deinem Körper heute danken, weil er dich treu durch alle Stunden getragen hat?
17. Welcher Moment heute war von tiefer Stille oder Ruhe geprägt – und wie hat er auf dich gewirkt?
18. Gab es ein Missverständnis oder einen Konflikt heute? Wie kannst du innerlich Frieden damit schließen, bevor du schläfst?
19. Welches Gefühl der Dankbarkeit breitet sich in dir aus, wenn du an dein aktuelles Zuhause denkst?
20. Was hast du heute getan, das rein deiner Freude diente, ohne Zweck oder Ziel?
21. Wenn du eine Sorge des heutigen Tages in einen Ballon stecken und davonfliegen lassen könntest: Welche wäre das?
22. Welches Buch, welches Lied oder welcher Gedanke hat dir heute einen Moment des Trostes geschenkt?
23. Inwiefern bist du heute ein Stück weiser, geduldiger oder liebevoller gewesen als gestern?
24. Was war das Beste, das du heute gegessen, getrunken oder physisch genossen hast?
25. Welcher Mensch hat heute unbewusst oder bewusst von deiner Freundlichkeit profitiert?
26. Mit welchem Gefühl schließt du diesen Tag ab? Beschreibe es in drei tiefgründigen Worten.
27. Was hätte dir heute geholfen, das du dir selbst nicht geben konntest? Wie kannst du es dir morgen schenken?
28. Welche Erkenntnis des heutigen Tages möchtest du aufschreiben, um sie niemals zu vergessen?
29. Wenn du morgen aufwachst: Welchen ersten, heilsamen Gedanken möchtest du in deinem Kopf begrüßen?
30. Atme tief ein und aus: Was ist das Schönste daran, dass dieser Tag nun vollbracht ist und du einfach nur sein darfst?

---

## 4. Integration in die Statistiken & Codebase-Struktur

Um diese reichhaltigen Fragen und Antworten sauber in der App zu verarbeiten, müssen wir die bestehenden Datenmodelle und UI-Kompomenten erweitern. 

### A. Technische Key-Mappings
Im Firestore-Dokument (`Entry` in Firebase) bleiben die Feldnamen identisch, um Rückwärtskompatibilität zu wahren. Die Werte werden jedoch flexibler interpretiert.

#### 1. Stimmung (`mood`)
Die neuen Keys werden in den Charts unter Beibehaltung der bestehenden Datenstrukturen registriert:
* **Morgen-Keys:** `excitement`, `peace`, `tiredness`, `anxiety`, `melancholy`
* **Abend-Keys:** `satisfaction`, `relief`, `exhaustion`, `overwhelmed`, `loneliness`

*Statistik-Mapping in `MoodBarChart.kt`:*
Wir fassen die Stimmungen in den Statistiken in logische Übergruppen zusammen, damit bestehende und neue Einträge harmonisch nebeneinander existieren und verglichen werden können:
* **Positiv / Freude (`joy` / `excitement` / `satisfaction` / `relief`):** Dargestellt als heitere, lebendige Töne.
* **Neutral / Ausgeglichen (`balance` / `peace`):** Dargestellt als ruhige, erdige Töne.
* **Herausfordernd / Stress (`stress` / `anxiety` / `overwhelmed`):** Dargestellt als anspannungssignalisierende Graublau-/Lila-Töne.
* **Schwer / Traurig (`sadness` / `melancholy` / `loneliness` / `tiredness` / `exhaustion`):** Dargestellt als sanfte, dämpfende Farbtöne.

#### 2. Energielevel (`energy_level`)
* **Keys:** `full`, `medium`, `low`, `empty` (sowie Abend-Spezialität `satisfied_tired` und `wired`).
* *Statistik-Mapping in `EnergyBarChart.kt`:*
  * `full` / `satisfied_tired` ➔ **Voll** (Grün)
  * `medium` / `wired` ➔ **Mittel** (Gelb)
  * `low` ➔ **Niedrig** (Orange)
  * `empty` ➔ **Leer** (Pastellviolett/Grau)

#### 3. Dankbarkeit (`gratitude_areas`)
* **Morgen-Keys:** `relations`, `comfort`, `health`, `nature`, `opportunity`, `self_compassion`, `struggled`
* **Abend-Keys:** `encounter`, `micro_joys`, `achievement`, `learning`, `comfort_received`, `connection`, `none`
* *Statistik-Mapping in `GratitudePieChart.kt`:*
  Da Dankbarkeit morgens und abends andere Facetten hat, gruppieren wir die 14 neuen Kategorien für das Kuchendiagramm in **7 übergeordnete Dankbarkeits-Säulen**:
  1. **Beziehungen & Begegnung** (Keys: `relations`, `encounter`, `connection`) ➔ *Farbe: Salbeigrün*
  2. **Körper & Gesundheit** (Keys: `health`) ➔ *Farbe: Blaugrau*
  3. **Erfolg & Fortschritt** (Keys: `achievement`, `opportunity`) ➔ *Farbe: Sonnengelb*
  4. **Natur & Umgebung** (Keys: `nature`) ➔ *Farbe: Lavendel*
  5. **Genuss & Alltagsfreude** (Keys: `comfort`, `micro_joys`) ➔ *Farbe: Rosé*
  6. **Selbstfürsorge & Erkenntnis** (Keys: `self_compassion`, `learning`, `comfort_received`) ➔ *Farbe: Sanftes Apricot*
  7. **Schwere Tage / Keine Dankbarkeit** (Keys: `struggled`, `none`) ➔ *Farbe: Schiefergrau / Dunkles Taupe*

#### 4. Selbstfürsorge (`self_care`)
* **Morgen-Keys:** `physical`, `boundaries`, `digital_detox`, `soul`, `stillness`, `compassion`, `no_energy`
* **Abend-Keys:** `needs_met`, `boundaries_kept`, `unplugged`, `joyful_moment`, `release`, `forgiveness`, `neglected`
* *Statistik & Handhabung:*
  * In der Detailansicht und den wöchentlichen/monatlichen Rückblicken werden die Keys `no_energy` und `neglected` als bewusste Akzeptanztage gezählt. Im Wochenbericht erhält der Nutzer ein bestärkendes Feedback (z. B. *"Du hast an X Tagen deine Grenzen gesichert und Druck herausgenommen. Das ist wertvolle Selbstfürsorge!"*), statt eines Gefühls des Scheiterns.

### B. Anpassung der Tag-Erstellung (deriveTags)
In `EntryRepository.kt` steuert `deriveTags` die Zuweisung von Tags zur Anzeige passender achtsamer Zitate (Quotes). Das passen wir wie folgt an:
* `anxiety` oder `overwhelmed` ➔ Tag `"Stress"`, Tag `"Angst"`
* `excitement` oder `satisfaction` ➔ Tag `"Freude"`
* `peace` oder `relief` ➔ Tag `"Ausgeglichenheit"`
* `melancholy` oder `loneliness` ➔ Tag `"Traurigkeit"`, Tag `"Trauer"`
* `self_compassion` oder `learning` oder `compassion` ➔ Tag `"Selbstfürsorge"`
* `achievement` oder `encounter` or `relations` ➔ Tag `"Dankbarkeit"`
* `struggled` oder `none` oder `neglected` oder `no_energy` ➔ Tag `"Trost"`, Tag `"Selbstfürsorge"`, Tag `"Traurigkeit"`
  *(Dadurch wird sichergestellt, dass der Nutzer an schweren Tagen keine fordernden Motivationssprüche, sondern heilsame, mitfühlende und entlastende Worte angezeigt bekommt.)*
