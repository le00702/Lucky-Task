package com.example.luckytask.ui.theme.elements

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun DatePickerField(
    selectedDate: MutableState<LocalDate?>,
    label: String,
    placeholder: String = "Select a date",
    isRequired: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

    // Calendar instance for DatePickerDialog
    val calendar = Calendar.getInstance()
    selectedDate.value?.let {
        calendar.set(it.year, it.monthValue - 1, it.dayOfMonth)
    }

    // Date picker dialog
    /*** If there was a date selected, display the selected date
     *   --> otherwise display the current date ***/
    val date = selectedDate.value ?: LocalDate.now()
    val datePickerDialog =
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                selectedDate.value = LocalDate.of(year, month + 1, dayOfMonth)
            },
            date.year,
            /*** Use month -1 as the months in LocalDate are 1..12
             *   --> however in Date Picker they start from 0! ***/
            date.monthValue - 1,
            date.dayOfMonth
        )


    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 25.sp
            )
            if (isRequired) {
                Text(
                    text = " *",
                    fontSize = 25.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        OutlinedTextField(
            value = selectedDate.value?.format(dateFormatter) ?: "",
            onValueChange = { }, // Read-only
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 20.sp
                )
            },
            textStyle = TextStyle(fontSize = 20.sp),
            readOnly = true,
            trailingIcon = {
                Row {
                    if (selectedDate.value != null) {
                        IconButton(
                            onClick = { selectedDate.value = null }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear date"
                            )
                        }
                    }
                    IconButton(
                        onClick = { datePickerDialog.show() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() },
            singleLine = true
        )

        // Quick date selection chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            /*** Put space between the chips and center them ***/
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            QuickDateChip("Today") {
                selectedDate.value = LocalDate.now()
            }
            QuickDateChip("Tomorrow") {
                selectedDate.value = LocalDate.now().plusDays(1)
            }
            QuickDateChip("Next Week") {
                selectedDate.value = LocalDate.now().plusWeeks(1)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun QuickDateChip(
    label: String,
    onClick: () -> Unit,
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 12.sp
            )
        },
        modifier = Modifier.height(32.dp)
    )
}