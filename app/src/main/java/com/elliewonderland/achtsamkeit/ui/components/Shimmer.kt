package com.elliewonderland.achtsamkeit.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.elliewonderland.achtsamkeit.ui.theme.AppTheme

@Composable
fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offset by transition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1000f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerOffset",
    )
    return Brush.linearGradient(
        colors = listOf(
            AppTheme.colors.surface,
            AppTheme.colors.surfaceAlt,
            AppTheme.colors.surface,
        ),
        start = Offset(x = offset - 300f, y = 0f),
        end   = Offset(x = offset,        y = 0f),
    )
}

@Composable
fun ShimmerBox(
    modifier:     Modifier = Modifier,
    height:       Dp       = 18.dp,
    cornerRadius: Dp       = 6.dp,
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(shimmerBrush()),
    )
}

/** Mimics a single entry/favorite list row while data is loading. */
@Composable
fun ShimmerListItem(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        ShimmerBox(modifier = Modifier.fillMaxWidth(0.38f), height = 12.dp)
        Spacer(Modifier.height(8.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth(), height = 18.dp)
        Spacer(Modifier.height(6.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth(0.62f), height = 12.dp)
    }
}

/** Mimics a card widget (e.g. StreakCard or StatCard) while data is loading. */
@Composable
fun ShimmerCard(
    modifier: Modifier = Modifier,
    height:   Dp       = 80.dp,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(shimmerBrush()),
    )
}
