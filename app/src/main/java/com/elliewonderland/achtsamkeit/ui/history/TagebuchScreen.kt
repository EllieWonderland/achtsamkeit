package com.elliewonderland.achtsamkeit.ui.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.model.Entry
import com.elliewonderland.achtsamkeit.model.FavoriteQuote
import com.elliewonderland.achtsamkeit.ui.components.ShimmerListItem
import com.elliewonderland.achtsamkeit.ui.history.components.formatDate
import com.elliewonderland.achtsamkeit.ui.navigation.Screen
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.elliewonderland.achtsamkeit.ui.theme.HandwrittenLabelStyle
import com.elliewonderland.achtsamkeit.ui.theme.HandwrittenStyle
import com.elliewonderland.achtsamkeit.ui.theme.SerifItalic
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TagebuchScreen(
    navController: NavController,
    scrollToDate: String? = null,
    isActive: Boolean = true
) {
    val vm: HistoryViewModel = viewModel()
    val uiState by vm.uiState.collectAsState()
    val userId = Firebase.auth.currentUser?.uid ?: ""
    val colors = AppTheme.colors

    LaunchedEffect(userId, isActive) {
        if (userId.isNotBlank() && isActive) vm.load(userId)
    }

    val visibleEntries = uiState.entries

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(Modifier.height(24.dp))

        Text(
            text = "Liebes Tagebuch,...",
            style = MaterialTheme.typography.headlineSmall,
            color = AppTheme.colors.ink,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                Column(modifier = Modifier.fillMaxSize().weight(1f)) {
                    repeat(6) { ShimmerListItem() }
                }
            }
            visibleEntries.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Noch keine Einträge vorhanden.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppTheme.colors.inkSoft,
                        )
                    }
                }
            }
            else -> {
                // Das interaktive 3D-Buch zum Durchblättern
                val pagerState = rememberPagerState(pageCount = { visibleEntries.size })
                
                LaunchedEffect(scrollToDate, visibleEntries) {
                    if (!scrollToDate.isNullOrBlank() && visibleEntries.isNotEmpty()) {
                        val index = visibleEntries.indexOfFirst { it.dateStr == scrollToDate }
                        if (index >= 0) {
                            pagerState.scrollToPage(index)
                        }
                    }
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Edler Notizbuch-Lederordner-Hintergrund (Cover-Rücken)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        colors.accent.copy(alpha = 0.82f),
                                        colors.accent2.copy(alpha = 0.92f)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 16.dp)
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            pageSpacing = 8.dp
                        ) { page ->
                            val entry = visibleEntries[page]
                            
                            // 3D Buch-Blättereffekt-Berechnungen
                            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                            val rotation = (pageOffset * -35f).coerceIn(-90f, 90f)
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        // Drehung ausgehend von linker Kante (Spine)
                                        transformOrigin = TransformOrigin(0f, 0.5f)
                                        rotationY = rotation
                                        alpha = 1f - (kotlin.math.abs(pageOffset) * 0.3f).coerceIn(0f, 1f)
                                        val scale = 1f - (kotlin.math.abs(pageOffset) * 0.04f).coerceIn(0f, 1f)
                                        scaleX = scale
                                        scaleY = scale
                                    }
                            ) {
                                BookPage(
                                    entry = entry,
                                    onClick = { navController.navigate(Screen.EntryDetail.createRoute(entry.id)) }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Buch-Navigation
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Wische zum Blättern",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.inkSoft
                        )
                        Text(
                            text = "Seite ${pagerState.currentPage + 1} von ${visibleEntries.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.accent
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        // Elegantes Favoriten-Karussell (unter dem Tagebuch platziert)
        if (uiState.favorites.isNotEmpty()) {
            FavoritesCarousel(
                favorites = uiState.favorites,
                onUnfavorite = { fav -> vm.toggleFavorite(userId, fav) },
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BookPage(
    entry: Entry,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    val hairColor = colors.hair.copy(alpha = 0.08f)
    val marginColor = Color(0xFFE57373).copy(alpha = 0.35f)
    val paperColor = if (AppTheme.variant.isDark) Color(0xFF262930) else Color(0xFFFCF9F2)

    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = paperColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Liniertes Papier (Ruled Lines Canvas)
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Feine graue Schreiblinien zeichnen (mathematisch abgestimmt auf Handschrift-Höhe)
                val spacing = 26.sp.toPx()
                var y = spacing * 2.8f
                while (y < size.height) {
                    drawLine(
                        color = hairColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.5f
                    )
                    y += spacing
                }

                // Rote Margin-Linie links (38.dp Abstand)
                val marginX = 40.dp.toPx()
                drawLine(
                    color = marginColor,
                    start = Offset(marginX, 0f),
                    end = Offset(marginX, size.height),
                    strokeWidth = 2f
                )
            }

            // Binderringe-Effekt auf der linken Seite (Spiralbindung)
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(40.dp)
                    .align(Alignment.CenterStart),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(5) {
                    Box(contentAlignment = Alignment.Center) {
                        // Ring-Loch (dunkler Kreis)
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0x33000000))
                        )
                        // Der Metall-Ring selbst (nach links überstehend)
                        Box(
                            modifier = Modifier
                                .offset(x = (-6).dp)
                                .size(width = 16.dp, height = 4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF7F8C8D),
                                            Color(0xFFBDC3C7),
                                            Color(0xFF7F8C8D)
                                        )
                                    )
                                )
                        )
                    }
                }
            }

            // Content Column (Scrollable internally so we don't truncate long entries)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 52.dp, end = 20.dp, top = 20.dp, bottom = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header (Datum & Typ)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val dateFormatted = runCatching {
                        LocalDate.parse(entry.dateStr).format(
                            DateTimeFormatter.ofPattern("EEEE, d. MMMM yyyy", Locale("de"))
                        )
                    }.getOrDefault(entry.dateStr)

                    Text(
                        text = dateFormatted,
                        style = HandwrittenLabelStyle.copy(fontSize = 14.sp),
                        color = colors.accent
                    )

                    Text(
                        text = when (entry.type) {
                            "morning" -> "☀️"
                            "evening" -> "🌙"
                            "weekly_review" -> " Wbl."
                            "monthly_review" -> " Mbl."
                            "yearly_review" -> " Jbl."
                            else -> ""
                        },
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                // Stimmung-Indicator falls vorhanden
                val mood = entry.mood
                if (mood.isNotBlank() && mood != "none") {
                    val moodText = when (mood.lowercase(Locale.ROOT).trim()) {
                        "excitement" -> "Stimmung: Voller Vorfreude ✨"
                        "peace"      -> "Stimmung: Gelassen 🕊️"
                        "tiredness"  -> "Stimmung: Müde 😴"
                        "anxiety"    -> "Stimmung: Ängstlich/Unruhig 😟"
                        "melancholy" -> "Stimmung: Schwermütig 🌧️"
                        "stress", "gestresst" -> "Stimmung: Gestresst 🌩️"
                        "satisfaction" -> "Stimmung: Zufrieden 😊"
                        "relief"     -> "Stimmung: Erleichtert 😌"
                        "exhaustion" -> "Stimmung: Erschöpft 🥱"
                        "overwhelmed" -> "Stimmung: Überfordert 🤯"
                        "loneliness" -> "Stimmung: Einsam 👤"
                        "joy"        -> "Stimmung: Voller Freude ☀️"
                        "balance"    -> "Stimmung: Ausgeglichen 🌿"
                        "sadness"    -> "Stimmung: Betrübt 🌧️"
                        else -> "Stimmung: ${mood.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.GERMAN) else it.toString() }}"
                    }
                    Text(
                        text = moodText,
                        style = HandwrittenLabelStyle.copy(fontSize = 15.sp),
                        color = colors.inkSoft
                    )
                }

                HorizontalDivider(color = colors.hair.copy(alpha = 0.15f))

                // Fließender Text (Prosa für Routinen, Q&As für Reviews)
                if (entry.type.endsWith("_review")) {
                    val qas = parseReviewText(entry.freeText)
                    qas.forEach { (question, answer) ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = question,
                                style = HandwrittenLabelStyle.copy(fontSize = 16.sp),
                                color = colors.inkSoft
                            )
                            Text(
                                text = answer,
                                style = HandwrittenStyle.copy(fontSize = 19.sp, lineHeight = 26.sp),
                                color = colors.ink
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                } else {
                    val prose = buildNarrativeProse(entry)
                    if (prose.isNotBlank()) {
                        Text(
                            text = prose,
                            style = HandwrittenStyle.copy(fontSize = 19.sp, lineHeight = 26.sp),
                            color = colors.ink
                        )
                    }

                    if (entry.freeText.isNotBlank()) {
                        if (prose.isNotBlank()) {
                            HorizontalDivider(color = colors.hair.copy(alpha = 0.1f))
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Persönliche Notizen:",
                                style = HandwrittenLabelStyle.copy(fontSize = 15.sp),
                                color = colors.inkSoft
                            )
                            Text(
                                text = entry.freeText,
                                style = HandwrittenStyle.copy(fontSize = 19.sp, lineHeight = 26.sp),
                                color = colors.ink
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Hilfsmethoden für Prosa-Erstellung (Narratives Tagebuch)

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

@Composable
private fun FavoritesCarousel(
    favorites: List<FavoriteQuote>,
    onUnfavorite: (FavoriteQuote) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (favorites.isEmpty()) return

    val colors = AppTheme.colors
    val pagerState = rememberPagerState(pageCount = { favorites.size })

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "DEINE FAVORITEN",
                style = MaterialTheme.typography.labelSmall,
                color = colors.inkSoft,
            )
            Text(
                text = "${pagerState.currentPage + 1}/${favorites.size}",
                style = MaterialTheme.typography.labelSmall,
                color = colors.accent,
            )
        }
        Spacer(Modifier.height(8.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            val fav = favorites[page]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, colors.hair, RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                colors.surface,
                                colors.accent2.copy(alpha = 0.15f),
                            )
                        )
                    )
            ) {
                // Subtle Glow Orb top-right
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 30.dp, y = (-30).dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    colors.accent3.copy(alpha = 0.40f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (fav.isLifehack) "💡 LIFEHACK" else "✨ SPRUCH",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.accent,
                        )
                        IconButton(
                            onClick = { onUnfavorite(fav) },
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Aus Favoriten entfernen",
                                tint = colors.accent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = fav.text,
                        style = SerifItalic.copy(fontSize = 16.sp),
                        color = colors.ink,
                    )
                }
            }
        }

        if (favorites.size > 1) {
            Spacer(Modifier.height(8.dp))
            // Subtle dot indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(favorites.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 6.dp else 4.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) colors.accent else colors.inkSoft.copy(alpha = 0.4f))
                    )
                }
            }
        }
    }
}
