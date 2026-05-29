package com.elliewonderland.achtsamkeit.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.elliewonderland.achtsamkeit.data.local.CardConfig
import com.elliewonderland.achtsamkeit.data.local.CardPreferences
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardCustomizationScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val heuteCardsFlow = remember(context) { CardPreferences.getHeuteCards(context) }
    val statsCardsFlow = remember(context) { CardPreferences.getStatsCards(context) }

    val heuteCards by heuteCardsFlow.collectAsState(initial = CardPreferences.defaultHeuteCards)
    val statsCards by statsCardsFlow.collectAsState(initial = CardPreferences.defaultStatsCards)

    var selectedTab by remember { mutableStateOf(0) } // 0 = Heute, 1 = Statistik
    val colors = AppTheme.colors

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Bereiche personalisieren",
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.ink,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Zurück",
                            tint = colors.ink,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                "Passe die Reihenfolge und Sichtbarkeit der einzelnen Bereiche in deiner App an. Änderungen werden sofort gespeichert.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.inkSoft,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Custom Rounded Pill Tabs (Premium Aesthetic)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.surfaceAlt)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tab0Selected = selectedTab == 0
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (tab0Selected) colors.surface else colors.surfaceAlt)
                        .border(
                            1.dp,
                            if (tab0Selected) colors.hair else colors.surfaceAlt,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedTab = 0 }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Heute-Tab",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = if (tab0Selected) colors.ink else colors.inkSoft
                    )
                }

                val tab1Selected = selectedTab == 1
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (tab1Selected) colors.surface else colors.surfaceAlt)
                        .border(
                            1.dp,
                            if (tab1Selected) colors.hair else colors.surfaceAlt,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedTab = 1 }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Statistik",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = if (tab1Selected) colors.ink else colors.inkSoft
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (selectedTab == 0) {
                    heuteCards.forEachIndexed { index, card ->
                        val cardLabel = getHeuteCardLabel(card.id)
                        val cardDesc = getHeuteCardDesc(card.id)
                        CustomizableCardItem(
                            title = cardLabel,
                            description = cardDesc,
                            visible = card.visible,
                            onVisibilityChange = { isVisible ->
                                val updated = heuteCards.map {
                                    if (it.id == card.id) it.copy(visible = isVisible) else it
                                }
                                scope.launch { CardPreferences.saveHeuteCards(context, updated) }
                            },
                            canMoveUp = index > 0,
                            canMoveDown = index < heuteCards.size - 1,
                            onMoveUp = {
                                if (index > 0) {
                                    val updated = heuteCards.toMutableList()
                                    val temp = updated[index]
                                    updated[index] = updated[index - 1]
                                    updated[index - 1] = temp
                                    scope.launch { CardPreferences.saveHeuteCards(context, updated) }
                                }
                            },
                            onMoveDown = {
                                if (index < heuteCards.size - 1) {
                                    val updated = heuteCards.toMutableList()
                                    val temp = updated[index]
                                    updated[index] = updated[index + 1]
                                    updated[index + 1] = temp
                                    scope.launch { CardPreferences.saveHeuteCards(context, updated) }
                                }
                            }
                        )
                    }
                } else {
                    statsCards.forEachIndexed { index, card ->
                        val cardLabel = getStatsCardLabel(card.id)
                        val cardDesc = getStatsCardDesc(card.id)
                        CustomizableCardItem(
                            title = cardLabel,
                            description = cardDesc,
                            visible = card.visible,
                            onVisibilityChange = { isVisible ->
                                val updated = statsCards.map {
                                    if (it.id == card.id) it.copy(visible = isVisible) else it
                                }
                                scope.launch { CardPreferences.saveStatsCards(context, updated) }
                            },
                            canMoveUp = index > 0,
                            canMoveDown = index < statsCards.size - 1,
                            onMoveUp = {
                                if (index > 0) {
                                    val updated = statsCards.toMutableList()
                                    val temp = updated[index]
                                    updated[index] = updated[index - 1]
                                    updated[index - 1] = temp
                                    scope.launch { CardPreferences.saveStatsCards(context, updated) }
                                }
                            },
                            onMoveDown = {
                                if (index < statsCards.size - 1) {
                                    val updated = statsCards.toMutableList()
                                    val temp = updated[index]
                                    updated[index] = updated[index + 1]
                                    updated[index + 1] = temp
                                    scope.launch { CardPreferences.saveStatsCards(context, updated) }
                                }
                            }
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun CustomizableCardItem(
    title: String,
    description: String,
    visible: Boolean,
    onVisibilityChange: (Boolean) -> Unit,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    val colors = AppTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, colors.hair, RoundedCornerShape(16.dp))
            .background(if (visible) colors.surface else colors.surfaceAlt)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reordering Arrows Panel
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = 12.dp)
        ) {
            IconButton(
                onClick = onMoveUp,
                enabled = canMoveUp,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowUpward,
                    contentDescription = "Nach oben verschieben",
                    tint = if (canMoveUp) colors.accent else colors.hair,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.height(4.dp))
            IconButton(
                onClick = onMoveDown,
                enabled = canMoveDown,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowDownward,
                    contentDescription = "Nach unten verschieben",
                    tint = if (canMoveDown) colors.accent else colors.hair,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Info Column
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = if (visible) colors.ink else colors.inkSoft,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.inkSoft,
                lineHeight = 16.sp
            )
        }

        Spacer(Modifier.width(8.dp))

        // Switch to toggle visibility
        Switch(
            checked = visible,
            onCheckedChange = onVisibilityChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.onAccent,
                checkedTrackColor = colors.accent,
                uncheckedThumbColor = colors.inkSoft,
                uncheckedTrackColor = colors.surfaceAlt,
                uncheckedBorderColor = colors.hair
            )
        )
    }
}

private fun getHeuteCardLabel(id: String): String = when (id) {
    "mood_trend" -> "Stimmungstrend"
    "quote"      -> "Spruch des Tages"
    "lifehack"   -> "Lifehack"
    "routines"   -> "Tägliche Routinen"
    "week_strip" -> "Wochenübersicht"
    "reviews"    -> "Rückblicke"
    else         -> id
}

private fun getHeuteCardDesc(id: String): String = when (id) {
    "mood_trend" -> "Deine durchschnittliche Stimmung im Monatsverlauf."
    "quote"      -> "Dein täglicher inspirierender Spruch."
    "lifehack"   -> "Ein praktischer und achtsamer Lifehack für deinen Alltag."
    "routines"   -> "Morgen- & Abendroutine für deine Achtsamkeit."
    "week_strip" -> "Deine wöchentliche Aktivitätsübersicht im Streifen."
    "reviews"    -> "Wochen-, Monats- und Jahresrückblicke verfassen."
    else         -> ""
}

private fun getStatsCardLabel(id: String): String = when (id) {
    "kompass"   -> "Achtsamkeits-Kompass"
    "mood_dist" -> "Stimmungsverteilung"
    "energy"    -> "Energielevel"
    "focus"     -> "Achtsamkeits-Fokus"
    "gratitude" -> "Dankbarkeits-Momente"
    "self_care" -> "Selbstfürsorge-Säulen"
    "impulse"   -> "Mitfühlender Impuls"
    else        -> id
}

private fun getStatsCardDesc(id: String): String = when (id) {
    "kompass"   -> "Gesamtbewertung, Pausen und Tagebucheinträge."
    "mood_dist" -> "Die Verteilung deiner Stimmungen."
    "energy"    -> "Die Verteilung deiner Energielevel."
    "focus"     -> "Worauf du deinen Fokus gelegt hast."
    "gratitude" -> "Verteilung deiner Dankbarkeits-Bereiche."
    "self_care" -> "Verteilung deiner ausgeführten Selbstfürsorge-Aktivitäten."
    "impulse"   -> "Ein individueller, mitfühlender Impuls basierend auf deinen Bewertungen."
    else        -> ""
}
