package com.cava.libruhry.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IsbnFormView(
    onSubmit: (String) -> Unit
) {
    // @TODO skip this and insert all the info
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var isbn by remember { mutableStateOf(TextFieldValue("")) }
        Column(
            modifier = Modifier
                .border(2.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(20.dp))
                .align(Alignment.CenterHorizontally)
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 30.dp,
                    bottom = 30.dp
                ),
        ) {
            Text(
                text = "Enter the book's ISBN",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
                )

            Spacer(modifier = Modifier.size(40.dp))
            TextField(
                value = isbn,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { input ->
                    isbn = input
                },
                placeholder = {
                    Text(
                        text = "Insert here your isbn"
                    )
                },
                shape = RoundedCornerShape(30.dp),
                trailingIcon = {
                    Button(
                        modifier = Modifier
                            .height(56.dp),
                        onClick = { onSubmit(isbn.text) }
                    ) {
                        Text(
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(start = 10.dp, end = 10.dp),
                            text = "Find"
                        )
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                isError = !isValidText(isbn.text)
            )
            if (!isValidText(isbn.text)) {
                Text(text = "Please enter valid text", color = Color.Red)
            }
        }

    }
}

fun isValidText(text: String): Boolean {
    return text.matches(Regex("^\\d{13}\$"))
}