package com.elliewonderland.achtsamkeit.ui.heute

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elliewonderland.achtsamkeit.model.Lifehack
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme
import com.elliewonderland.achtsamkeit.ui.theme.SerifItalic

@Composable
fun LifehackCard(
    lifehack: Lifehack?,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
) {
    if (lifehack == null) return
    val colors = AppTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, colors.hair, RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        colors.surface,
                        colors.accent2.copy(alpha = 0.18f),
                    )
                )
            )
    ) {
        // Glow orb top-right with secondary accent color (Salbei/Nebel)
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            colors.accent3.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(modifier = Modifier.padding(22.dp)) {
            Text(
                "💡 LIFEHACK FÜR DICH",
                style = MaterialTheme.typography.labelSmall,
                color = colors.inkSoft,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                lifehack.text,
                style = SerifItalic.copy(fontSize = 18.sp, lineHeight = 24.sp),
                color = colors.ink,
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    lifehack.tags.take(3).forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.surface)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "#$tag",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = colors.inkSoft,
                            )
                        }
                    }
                }

                AnimatedContent(
                    targetState = isFavorite,
                    transitionSpec = {
                        (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut())
                    },
                    label = "heart",
                ) { fav ->
                    IconButton(onClick = onFavoriteToggle) {
                        Icon(
                            imageVector     = if (fav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (fav) "Aus Favoriten entfernen" else "Zu Favoriten hinzufügen",
                            tint     = colors.accent,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}
