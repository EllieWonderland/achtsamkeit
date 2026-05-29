package com.elliewonderland.achtsamkeit.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.data.repository.HistoryRepository
import com.elliewonderland.achtsamkeit.model.Entry
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.elliewonderland.achtsamkeit.ui.theme.SerifItalic
import com.elliewonderland.achtsamkeit.ui.theme.HandwrittenStyle
import com.elliewonderland.achtsamkeit.ui.theme.HandwrittenLabelStyle
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailScreen(navController: NavController, entryId: String) {
    val userId = Firebase.auth.currentUser?.uid ?: ""
    val repo  = remember { HistoryRepository() }
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    var entry by remember { mutableStateOf<Entry?>(null) }
    LaunchedEffect(entryId, userId) {
        if (userId.isNotBlank() && entryId.isNotBlank()) {
            entry = runCatching { repo.getEntryById(userId, entryId) }.getOrNull()
        }
    }

    var isEditing        by remember { mutableStateOf(false) }
    var editGuidedAnswer by remember { mutableStateOf("") }
    var editFreeText     by remember { mutableStateOf("") }
    var editReviewAnswers by remember { mutableStateOf<List<String>>(emptyList()) }

    val topBarTitle = entry?.let { e ->
        buildString {
            append(typeLabelFull(e.type))
            if (e.dateStr.isNotBlank()) append("  ·  ${formatDate(e.dateStr)}")
        }
    } ?: "Eintrag"

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eintrag löschen?") },
            text = { Text("Dieser Eintrag wird unwiderruflich gelöscht.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    scope.launch {
                        runCatching { repo.deleteEntry(userId, entryId) }
                        navController.popBackStack()
                    }
                }) {
                    Text("Löschen", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Abbrechen")
                }
            },
        )
    }

    Scaffold(
        containerColor = AppTheme.colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topBarTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = AppTheme.colors.ink,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Zurück",
                            tint = AppTheme.colors.ink,
                        )
                    }
                },
                actions = {
                    if (entry != null) {
                        val e = entry!!
                        val editable = e.type == "morning" || e.type == "evening" || e.type.endsWith("_review")
                        if (editable) {
                            if (isEditing) {
                                IconButton(onClick = { isEditing = false }) {
                                    Icon(Icons.Outlined.Close, contentDescription = "Abbrechen", tint = AppTheme.colors.inkSoft)
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        val updatedFreeText = if (e.type.endsWith("_review")) {
                                            val qas = parseReviewText(entry?.freeText ?: "")
                                            qas.zip(editReviewAnswers).joinToString("\n\n") { (qa, newAns) ->
                                                "${qa.first}\n${newAns.ifBlank { "—" }}"
                                            }
                                        } else {
                                            editFreeText
                                        }
                                        runCatching { repo.updateEntry(userId, entryId, editGuidedAnswer, updatedFreeText) }
                                        entry = entry?.copy(guidedAnswer = editGuidedAnswer, freeText = updatedFreeText)
                                        isEditing = false
                                    }
                                }) {
                                    Icon(Icons.Outlined.Check, contentDescription = "Speichern", tint = AppTheme.colors.accent)
                                }
                            } else {
                                IconButton(onClick = {
                                    editGuidedAnswer = entry?.guidedAnswer ?: ""
                                    editFreeText     = entry?.freeText ?: ""
                                    editReviewAnswers = parseReviewText(entry?.freeText ?: "").map { it.second }
                                    isEditing = true
                                }) {
                                    Icon(Icons.Outlined.Edit, contentDescription = "Bearbeiten", tint = AppTheme.colors.inkSoft)
                                }
                                IconButton(onClick = { showDeleteDialog = true }) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "Eintrag löschen", tint = AppTheme.colors.inkSoft)
                                }
                            }
                        } else {
                            // Rückblicke können gelöscht werden
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Outlined.Delete, contentDescription = "Eintrag löschen", tint = AppTheme.colors.inkSoft)
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        when {
            entry == null && entryId.isNotBlank() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = AppTheme.colors.accent)
                }
            }
            entry != null -> {
                val e = entry!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(padding)
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Edle Tagebuch-Papierseite
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                        ) {
                            
                            // 1. Die Narrative Tagebuch-Erzählung (nur für Routinen)
                            val prose = remember(e) { buildNarrativeProse(e) }
                            if (prose.isNotBlank()) {
                                Text(
                                    text = prose,
                                    style = HandwrittenStyle,
                                    color = AppTheme.colors.ink,
                                )
                                HorizontalDivider(color = AppTheme.colors.hair)
                            }

                            // 2. Geführte Impulsfrage
                            if (e.guidedQuestion.isNotBlank()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = e.guidedQuestion,
                                        style = HandwrittenLabelStyle,
                                        color = AppTheme.colors.inkSoft,
                                    )
                                    if (isEditing) {
                                        OutlinedTextField(
                                            value         = editGuidedAnswer,
                                            onValueChange = { editGuidedAnswer = it },
                                            modifier      = Modifier.fillMaxWidth(),
                                            minLines      = 3,
                                            textStyle     = HandwrittenStyle,
                                            colors        = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor   = AppTheme.colors.accent,
                                                unfocusedBorderColor = AppTheme.colors.hair,
                                                focusedTextColor     = AppTheme.colors.ink,
                                                unfocusedTextColor   = AppTheme.colors.ink,
                                            ),
                                        )
                                    } else {
                                        Text(
                                            text  = e.guidedAnswer.ifBlank { "—" },
                                            style = HandwrittenStyle,
                                            color = if (e.guidedAnswer.isBlank()) AppTheme.colors.inkSoft else AppTheme.colors.ink,
                                        )
                                    }
                                }
                                HorizontalDivider(color = AppTheme.colors.hair)
                            }

                            // 3. Freie Gedanken oder Frage-Antwort-Struktur bei Rückblicken
                            if (e.type.endsWith("_review")) {
                                // Q&A-Splitter für Rückblicke
                                val qas = remember(e.freeText) { parseReviewText(e.freeText) }
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    qas.forEachIndexed { index, (question, answer) ->
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(
                                                text = question,
                                                style = HandwrittenLabelStyle,
                                                color = AppTheme.colors.inkSoft,
                                            )
                                            if (isEditing) {
                                                if (index < editReviewAnswers.size) {
                                                    OutlinedTextField(
                                                        value         = editReviewAnswers[index],
                                                        onValueChange = { newAns ->
                                                            editReviewAnswers = editReviewAnswers.toMutableList().also { it[index] = newAns }
                                                        },
                                                        modifier      = Modifier.fillMaxWidth(),
                                                        minLines      = 3,
                                                        textStyle     = HandwrittenStyle,
                                                        colors        = OutlinedTextFieldDefaults.colors(
                                                            focusedBorderColor   = AppTheme.colors.accent,
                                                            unfocusedBorderColor = AppTheme.colors.hair,
                                                            focusedTextColor     = AppTheme.colors.ink,
                                                            unfocusedTextColor   = AppTheme.colors.ink,
                                                        ),
                                                    )
                                                }
                                            } else {
                                                Text(
                                                    text = answer,
                                                    style = HandwrittenStyle,
                                                    color = AppTheme.colors.ink,
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Standard Freitext
                                if (isEditing || e.freeText.isNotBlank()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = "Persönliche Notizen",
                                            style = HandwrittenLabelStyle,
                                            color = AppTheme.colors.inkSoft,
                                        )
                                        if (isEditing) {
                                            OutlinedTextField(
                                                value         = editFreeText,
                                                onValueChange = { editFreeText = it },
                                                modifier      = Modifier.fillMaxWidth(),
                                                minLines      = 3,
                                                textStyle     = HandwrittenStyle,
                                                colors        = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor   = AppTheme.colors.accent,
                                                    unfocusedBorderColor = AppTheme.colors.hair,
                                                    focusedTextColor     = AppTheme.colors.ink,
                                                    unfocusedTextColor   = AppTheme.colors.ink,
                                                ),
                                            )
                                        } else {
                                            Text(
                                                text  = e.freeText,
                                                style = HandwrittenStyle,
                                                color = AppTheme.colors.ink,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Hilfsmethoden für Prosa-Erstellung

private fun buildNarrativeProse(e: Entry): String {
    return if (e.type == "morning") {
        buildString {
            val moodSentence = when (e.mood.lowercase(Locale.ROOT).trim()) {
                "excitement" -> "Heute Morgen bin ich voller Vorfreude und Elan, motiviert und bereit für den Tag gestartet."
                "peace"      -> "Heute Morgen bin ich mit einer tiefen Gelassenheit und innerem Frieden, ganz zentriert und im Einklang mit mir gestartet."
                "tiredness"  -> "Heute Morgen bin ich noch sehr müde und schwerfällig gestartet, mich nach etwas mehr Ruhe sehnend."
                "anxiety"    -> "Heute Morgen bin ich etwas unruhig und angespannt gestartet, besorgt wegen bevorstehender Aufgaben oder Hürden."
                "melancholy" -> "Heute Morgen bin ich begleitet von einer gewissen Schwermut und Lustlosigkeit, mit recht wenig Antrieb gestartet."
                "stress", "gestresst" -> "Heute Morgen bin ich bereits ziemlich gestresst und angespannt gestartet."
                else -> {
                    if (e.mood.isNotBlank()) {
                        val capitalizedMood = e.mood.trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.GERMAN) else it.toString() }
                        "Als ich heute Morgen den Tag begann, spürte ich vor allem ein Gefühl: $capitalizedMood."
                    } else {
                        ""
                    }
                }
            }
            if (moodSentence.isNotBlank()) {
                append("$moodSentence ")
            }

            val energyStr = when (e.energyLevel) {
                "full"   -> "Mein Akku war voll geladen, mein Kopf klar und ich fühlte mich bereit für alles."
                "medium" -> "Ich startete mit einer soliden Basis – ganz okay, aber ich brauchte erst mal einen Kaffee."
                "low"    -> "Ich befand mich im Schonmodus, fühlte mich schwerfällig und ging es ganz ruhig an."
                "empty"  -> "Mein Akku war komplett leer, ich fühlte mich ausgelaugt und jede Bewegung kostete Kraft."
                else     -> ""
            }
            if (energyStr.isNotBlank()) {
                append("$energyStr ")
            }

            val focusStr = when (e.mindfulnessFocus) {
                "present" -> "Ich spürte meinen Körper, atmete ruhig und war ganz im Hier und Jetzt präsent."
                "future"  -> "Meine Gedanken kreisten schon hektisch um die To-Dos und Verpflichtungen des Tages."
                "past"    -> "Emotional hing ich noch ein wenig in der Vergangenheit fest, bei den Erlebnissen von gestern."
                else      -> ""
            }
            val pauseStr = when (e.mindfulnessPause) {
                "yes_pure"       -> "Dabei habe ich mir einen vollkommen bewussten, stillen Start ohne jede Ablenkung gegönnt."
                "yes_distracted" -> "Ich hatte zwar einen kleinen Moment für mich, war dabei aber durch mein Handy oder Mails abgelenkt."
                "no"             -> "Einen ruhigen Moment gab es nicht – ich bin direkt in den Autopiloten und den Trubel gesprungen."
                else             -> ""
            }
            if (focusStr.isNotBlank() || pauseStr.isNotBlank()) {
                if (focusStr.isNotBlank()) append(focusStr)
                if (pauseStr.isNotBlank()) {
                    if (focusStr.isNotBlank()) append(" ")
                    append(pauseStr)
                }
                append(" ")
            }

            val selfCareList = e.selfCare.mapNotNull { item ->
                when (item) {
                    "physical"      -> "auf meine körperliche Pflege zu achten (genug Wasser, Bewegung, Essen)"
                    "boundaries"    -> "gesunde Grenzen zu setzen und Überlastung zu vermeiden"
                    "digital_detox" -> "digitalen Schutz zu wahren und mein Handy bewusst wegzulegen"
                    "soul"          -> "meine Seele zu nähren (durch Musik, Lesen, Kreativität)"
                    "stillness"     -> "Ruhemomente wie Atmen, Meditation oder Dehnen einzubauen"
                    "compassion"    -> "voller Selbstmitgefühl zu sein und Druck herauszunehmen"
                    "no_energy"     -> "mir gar keine Vorsätze aufzuerlegen, da ich mich im reinen Überlebensmodus befand"
                    else            -> null
                }
            }
            if (selfCareList.isNotEmpty()) {
                append("Für mein Wohlbefinden hatte ich mir heute vorgenommen, ")
                append(joinWithAnd(selfCareList))
                append(". ")
            }

            val gratitudeList = e.gratitudeAreas.mapNotNull { area ->
                when (area) {
                    "relations"       -> "liebevolle Menschen und wertvolle Beziehungen"
                    "comfort"         -> "Sicherheit, Komfort und mein warmes Zuhause"
                    "health"          -> "meine körperliche Gesundheit und meinen Atem"
                    "nature"          -> "die Natur, die Morgensonne und die frische Luft"
                    "opportunity"     -> "neue Chancen, das Lernen und diesen neuen Tag"
                    "self_compassion" -> "Selbstannahme, meine Resilienz und meinen eigenen Weg"
                    "struggled"       -> "das Positive (obwohl es mir heute Morgen sehr schwerfiel, Dankbarkeit zu empfinden)"
                    else              -> null
                }
            }
            if (gratitudeList.isNotEmpty()) {
                append("Zutiefst dankbar war ich in diesem Moment für ")
                append(joinWithAnd(gratitudeList))
                append(".")
            }
        }
    } else if (e.type == "evening") {
        buildString {
            val ratingStr = when (e.dayRating) {
                1    -> "einen sehr schweren und herausfordernden"
                2    -> "einen eher unruhigen und anstrengenden"
                3    -> "einen ganz passablen, ausgeglichenen"
                4    -> "einen schönen und friedvollen"
                5    -> "einen wunderschönen, zutiefst erfüllten"
                else -> "einen ereignisreichen"
            }
            append("Ich blicke heute Abend auf $ratingStr Tag zurück. ")

            val moodSentence = when (e.mood.lowercase(Locale.ROOT).trim()) {
                "satisfaction" -> "Den heutigen Tag habe ich vor allem mit einem Gefühl von Zufriedenheit und tiefer Erfüllung verbracht, getragen von sozialer Wärme und Freude."
                "relief"       -> "Den heutigen Tag kann ich mit einem Gefühl von Erleichterung und wohlwollender Entspannung abschließen – der Tag ist geschafft und ich komme zur Ruhe."
                "exhaustion"   -> "Heute spüre ich vor allem eine tiefe Erschöpfung und Müdigkeit – sowohl körperlich als auch mental fühle ich mich völlig ausgelaugt."
                "overwhelmed"  -> "Der heutige Tag war geprägt von akuter Überforderung und innerer Unruhe, begleitet von vielen kreisenden Gedanken."
                "loneliness"   -> "Den heutigen Tag habe ich mit Gefühlen von Traurigkeit und Einsamkeit verbracht, begleitet von einer gewissen emotionalen Verletzlichkeit."
                "stress", "gestresst" -> "Heute stand ich fast durchgehend unter Stress und Anspannung, sodass ich jetzt erst einmal durchatmen muss."
                else -> {
                    if (e.mood.isNotBlank()) {
                        val capitalizedMood = e.mood.trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.GERMAN) else it.toString() }
                        "Als ich heute Abend in mich hineingespürt habe, war da vor allem ein vorherrschendes Gefühl: $capitalizedMood."
                    } else {
                        ""
                    }
                }
            }
            if (moodSentence.isNotBlank()) {
                append("$moodSentence ")
            }

            val energyStr = when (e.energyLevel) {
                "satisfied_tired" -> "Ich fühle mich jetzt angenehm und zufrieden erschöpft nach einem produktiven Tag."
                "wired"           -> "Körperlich bin ich müde, aber mein Geist rattert noch und steht unter Strom."
                "low"             -> "Mein Akku befindet sich im roten Bereich, der Tag war anstrengend und kräftezehrend."
                "empty"           -> "Ich bin absolut leer und ausgebrannt und sehne mich nur noch nach Schlaf, Dunkelheit und Ruhe."
                else              -> ""
            }
            if (energyStr.isNotBlank()) {
                append("$energyStr ")
            }

            val focusStr = when (e.mindfulnessFocus) {
                "present"   -> "Meine Gedanken waren überwiegend im Hier und Jetzt verankert – ich konnte den Tag bewusst erleben."
                "future"    -> "Gedanklich befand ich mich oft in der Zukunft – ich habe geplant, gegrübelt oder mir Sorgen gemacht."
                "past"      -> "Ich war viel in der Vergangenheit gefangen, habe Erlebnisse analysiert und Situationen bereut."
                "autopilot" -> "Ich lief überwiegend auf Autopilot; der Tag ist wie ein Film an mir vorbeigezogen."
                else        -> ""
            }
            val pauseStr = when (e.mindfulnessPause) {
                "yes_pure"       -> "Tagsüber habe ich mir eine echte, handyfreie und stille Auszeit gegönnt."
                "yes_distracted" -> "Es gab zwar eine Pause, aber ich war abgelenkt durch Social Media, Mails oder Podcasts."
                "no"             -> "Ich hatte keine ruhige Sekunde für mich und befand mich im Dauer-Rauschen."
                else             -> ""
            }
            if (focusStr.isNotBlank() || pauseStr.isNotBlank()) {
                if (focusStr.isNotBlank()) append(focusStr)
                if (pauseStr.isNotBlank()) {
                    if (focusStr.isNotBlank()) append(" ")
                    append(pauseStr)
                }
                append(" ")
            }

            val selfCareList = e.selfCare.mapNotNull { item ->
                when (item) {
                    "needs_met"       -> "meine Grundbedürfnisse geachtet habe (Essen, Trinken, Ausruhen)"
                    "boundaries_kept" -> "gesunde Grenzen gesetzt und auch mal 'Nein' gesagt habe"
                    "unplugged"       -> "mir eine bewusste Offline-Zeit gegönnt habe"
                    "joyful_moment"   -> "Zeit mit Dingen verbracht habe, die meiner Seele guttun"
                    "release"         -> "Druck und körperliche Anspannung bewusst abgelassen habe"
                    "forgiveness"     -> "mich so akzeptiert habe, wie ich heute war – ganz ohne Perfektionismus"
                    "neglected"       -> "mich heute selbst vernachlässigt und meine Bedürfnisse übergangen habe (was ich mir verzeihe)"
                    else              -> null
                }
            }
            if (selfCareList.isNotEmpty()) {
                append("Für mein Wohlbefinden habe ich heute gesorgt, indem ich ")
                append(joinWithAnd(selfCareList))
                append(". ")
            }

            val gratitudeList = e.gratitudeAreas.mapNotNull { area ->
                when (area) {
                    "encounter"        -> "eine wertvolle Begegnung oder ein tiefes Gespräch"
                    "micro_joys"       -> "kleine Alltagsfreuden wie ein gutes Essen oder eine gemütliche Decke"
                    "achievement"      -> "ein Erfolgserlebnis oder den Stolz auf mein eigenes Schaffen"
                    "learning"         -> "eine wichtige Erkenntnis oder das Wachstum aus einem Fehler"
                    "comfort_received" -> "erhaltenen Trost und Beistand in einem schweren Moment"
                    "connection"       -> "ein harmonisches Miteinander und tiefe Verbundenheit"
                    "none"             -> "das Gute (obwohl es mir heute extrem schwerfiel, einen Lichtblick zu sehen)"
                    else               -> null
                }
            }
            if (gratitudeList.isNotEmpty()) {
                append("Besonders dankbar war ich heute Abend für ")
                append(joinWithAnd(gratitudeList))
                append(".")
            }
        }
    } else {
        ""
    }
}

private fun joinWithAnd(list: List<String>): String {
    if (list.isEmpty()) return ""
    if (list.size == 1) return list[0]
    return list.dropLast(1).joinToString(", ") + " und " + list.last()
}

private fun parseReviewText(text: String): List<Pair<String, String>> {
    if (text.isBlank()) return emptyList()
    return text.split("\n\n").mapNotNull { block ->
        val lines = block.split("\n")
        if (lines.size >= 2) {
            lines[0] to lines.subList(1, lines.size).joinToString("\n")
        } else if (lines.isNotEmpty() && lines[0].isNotBlank()) {
            lines[0] to "—"
        } else {
            null
        }
    }
}

private fun typeLabelFull(type: String): String = when (type) {
    "morning"        -> "Morgenroutine"
    "evening"        -> "Abendroutine"
    "weekly_review"  -> "Wochenrückblick"
    "monthly_review" -> "Monatsrückblick"
    "yearly_review"  -> "Jahresrückblick"
    else             -> "Eintrag"
}

private fun formatDate(dateStr: String): String = runCatching {
    LocalDate.parse(dateStr).format(DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale("de")))
}.getOrDefault(dateStr)
