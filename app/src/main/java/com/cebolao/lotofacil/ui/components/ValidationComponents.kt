package com.cebolao.lotofacil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Real-time validation feedback component.
 * Shows validation state with visual feedback and error messages.
 */
data class ValidationState(
    val isValid: Boolean = true,
    val errorMessage: String? = null
) {
    companion object {
        val Valid = ValidationState(isValid = true)
        fun Error(message: String) = ValidationState(isValid = false, errorMessage = message)
    }
}

/**
 * Validated input box with red border and error message on invalid state.
 */
@Composable
fun ValidatedInputBox(
    validationState: ValidationState,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor, shape)
                .border(
                    width = if (validationState.isValid) 1.dp else 2.dp,
                    color = if (validationState.isValid) {
                        MaterialTheme.colorScheme.outline
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    shape = shape
                )
                .padding(12.dp)
        ) {
            content()
        }

        // Show error message if validation fails
        if (!validationState.isValid && validationState.errorMessage != null) {
            Text(
                text = validationState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 12.dp)
            )
        }
    }
}

/**
 * Validate filter selection.
 * Ensures that filter ranges are logically possible.
 */
fun validateFilterSelection(
    minValue: Int?,
    maxValue: Int?,
    minAllowed: Int,
    maxAllowed: Int
): ValidationState {
    if (minValue == null || maxValue == null) {
        return ValidationState.Error("Selecione ambos os valores")
    }

    if (minValue > maxValue) {
        return ValidationState.Error("Valor mínimo não pode ser maior que máximo")
    }

    if (minValue < minAllowed || maxValue > maxAllowed) {
        return ValidationState.Error(
            "Valores devem estar entre $minAllowed e $maxAllowed"
        )
    }

    return ValidationState.Valid
}

/**
 * Validate game numbers selection.
 */
fun validateGameSelection(
    selectedNumbers: Set<Int>,
    requiredCount: Int
): ValidationState {
    return when {
        selectedNumbers.isEmpty() -> ValidationState.Error("Selecione ao menos um número")
        selectedNumbers.size < requiredCount -> ValidationState.Error(
            "Você precisa de $requiredCount números (${selectedNumbers.size}/$requiredCount)"
        )
        selectedNumbers.size > requiredCount -> ValidationState.Error(
            "Máximo de $requiredCount números (${selectedNumbers.size}/$requiredCount)"
        )
        else -> ValidationState.Valid
    }
}
