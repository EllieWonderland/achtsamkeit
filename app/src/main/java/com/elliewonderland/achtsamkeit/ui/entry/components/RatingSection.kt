package com.elliewonderland.achtsamkeit.ui.entry.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.elliewonderland.achtsamkeit.ui.theme.HandwrittenStyle
import com.elliewonderland.achtsamkeit.ui.theme.SerifItalic

@Composable
fun RatingSection(selected: Int, onSelect: (Int) -> Unit) {
    SectionCard(title = "Wie bewerte ich den heutigen Tag insgesamt?") {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Interaktive Sterne-Reihe
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..5) {
                    val isStarred = i <= selected
                    val icon = if (isStarred) Icons.Filled.Star else Icons.Outlined.Star

                    val tint by animateColorAsState(
                        targetValue = if (isStarred) AppTheme.colors.accent else AppTheme.colors.inkSoft.copy(alpha = 0.3f),
                        animationSpec = tween(durationMillis = 250),
                        label = "StarTint"
                    )

                    val scale by animateFloatAsState(
                        targetValue = if (selected == i) 1.25f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "StarScale"
                    )

                    Icon(
                        imageVector = icon,
                        contentDescription = "$i Sterne",
                        tint = tint,
                        modifier = Modifier
                            .size(48.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .clickable {
                                onSelect(i)
                            }
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamisches Reflexions-Kärtchen
            val reflectionTitle = when (selected) {
                1 -> "Sehr herausfordernd"
                2 -> "Eher unruhig"
                3 -> "In meiner Mitte"
                4 -> "Schön & friedvoll"
                5 -> "Zutiefst erfüllt"
                else -> "Innehalten..."
            }

            val reflectionText = when (selected) {
                1 -> "Ein sehr schwerer, fordernder Tag. Ich darf heute einfach nur sein, atmen und ganz weich und mitfühlend mit mir umgehen."
                2 -> "Ein unruhiger, kraftraubender Tag. Ich habe mein Bestes gegeben und darf die Anspannung nun loslassen."
                3 -> "Ein ganz passabler, ruhiger Tag. Ich ruhe in meiner Mitte und bin dankbar für das Alltägliche."
                4 -> "Ein schöner Tag mit feinen Lichtblicken. Ich durfte heute viele kleine Momente der Freude wahrnehmen."
                5 -> "Ein wunderschöner, freudvoller Tag. Mein Herz ist heute voller Wärme, Fülle und tiefer Dankbarkeit."
                else -> "Nimm dir einen kurzen Moment, um in dich hineinzuspüren. Wie fühlt sich dein Tag heute rückblickend an?"
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (selected > 0) {
                        AppTheme.colors.accent.copy(alpha = 0.05f).compositeOver(AppTheme.colors.surface)
                    } else {
                        AppTheme.colors.surfaceAlt.copy(alpha = 0.5f)
                    }
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (selected > 0) AppTheme.colors.accent.copy(alpha = 0.2f) else AppTheme.colors.hair
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = reflectionTitle,
                        style = SerifItalic.copy(fontSize = 18.sp),
                        color = if (selected > 0) AppTheme.colors.accent else AppTheme.colors.inkSoft,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = reflectionText,
                        style = HandwrittenStyle.copy(fontSize = 20.sp, lineHeight = 26.sp),
                        color = AppTheme.colors.ink,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

private fun Color.compositeOver(bg: Color): Color {
    val a = alpha
    return Color(
        red   = red   * a + bg.red   * (1 - a),
        green = green * a + bg.green * (1 - a),
        blue  = blue  * a + bg.blue  * (1 - a),
        alpha = 1f,
    )
}
