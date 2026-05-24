package com.elliewonderland.achtsamkeit.ui.heute

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme

@Composable
fun HeroHeader(
    greeting: String,
    firstName: String?,
    dateText: String,
    photoUrl: String?,
    photoScale: Float = 1.0f,
    photoOffsetX: Float = 0.0f,
    photoOffsetY: Float = 0.0f,
    onProfileClick: () -> Unit,
) {
    val colors = AppTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to colors.accent3.copy(alpha = 0.33f),
                        0.6f to colors.accent2.copy(alpha = 0.13f),
                        1.0f to Color.Transparent,
                    )
                )
            )
    ) {
        // Decorative glow orb
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-30).dp)
                .alpha(0.55f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(colors.accent3, colors.accent2, Color.Transparent)
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, end = 22.dp, top = 22.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = dateText,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.inkSoft,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text  = "$greeting,",
                    style = MaterialTheme.typography.displayMedium,
                    color = colors.ink,
                )
                if (!firstName.isNullOrBlank()) {
                    Text(
                        text  = "$firstName.",
                        style = MaterialTheme.typography.displayLarge.copy(fontStyle = FontStyle.Italic),
                        color = colors.accent,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(colors.surface)
                    .border(2.dp, colors.accent.copy(alpha = 0.4f), CircleShape)
                    .clickable(onClick = onProfileClick),
                contentAlignment = Alignment.Center,
            ) {
                if (!photoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model              = photoUrl,
                        contentDescription = "Profilbild",
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX       = photoScale,
                                scaleY       = photoScale,
                                translationX = photoOffsetX * (96f / 200f),
                                translationY = photoOffsetY * (96f / 200f)
                            ),
                    )
                } else {
                    Image(
                        painter            = painterResource(id = com.elliewonderland.achtsamkeit.R.drawable.logo_background),
                        contentDescription = null,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize()
                    )
                    Image(
                        painter            = painterResource(id = com.elliewonderland.achtsamkeit.R.drawable.logo_foreground),
                        contentDescription = "App-Logo",
                        contentScale       = ContentScale.Fit,
                        modifier           = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

