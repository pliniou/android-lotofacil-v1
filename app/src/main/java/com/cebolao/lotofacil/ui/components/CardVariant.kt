package com.cebolao.lotofacil.ui.components

sealed class CardVariant {
    data object Elevated : CardVariant()
    data object Outlined : CardVariant()
    data object Filled : CardVariant()
    
    // Compatibility aliases for BaseComponents.kt migration
    companion object {
        val Static = Elevated
        val Clickable = Elevated // Clickable is now determined by onClick != null
        val Surfaced = Filled
    }
}
