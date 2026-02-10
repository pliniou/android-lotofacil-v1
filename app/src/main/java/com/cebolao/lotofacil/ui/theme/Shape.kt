package com.cebolao.lotofacil.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Shapes do Material 3 alinhados com os tokens de AppShapes
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // AppShapes.xs
    small = RoundedCornerShape(6.dp),        // AppShapes.sm
    medium = RoundedCornerShape(8.dp),       // AppShapes.md (card)
    large = RoundedCornerShape(16.dp),       // AppShapes.xl (dialog)
    extraLarge = RoundedCornerShape(24.dp)   // AppShapes.xxxl
)
