package com.cava.libruhry.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    @SuppressLint(
        "ModifierParameter"
    ) modifier: Modifier = Modifier,
    placeholder: String = ""
) {
    var openDialog by remember { mutableStateOf(false) }
    val state = rememberDatePickerState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { openDialog = true }
            .clip(RoundedCornerShape(12.dp))
            .then(modifier)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(16.dp),
            text = if (state.selectedDateMillis != null) millisToDate(state.selectedDateMillis!!) else placeholder,
            fontSize = 16.sp,
            color = if (state.selectedDateMillis != null) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        )

        IconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            onClick = { openDialog = true }
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
            )
        }
    }

    if (openDialog) {
        DatePickerDialog(
            onDismissRequest = {
                openDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onValueChange(value)
                        openDialog = false
                    },
                    enabled = true
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                    }
                ) {
                    Text("CANCEL")
                }
            }
        ) {
            DatePicker(
                state = state,
                showModeToggle = false
            )
        }
    }
}