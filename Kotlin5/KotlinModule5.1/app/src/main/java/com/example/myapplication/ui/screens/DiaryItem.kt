package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.DiaryNote

@Composable
fun DiaryItem(
    note: DiaryNote,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),

        shape = RoundedCornerShape(16.dp)
    ) {

        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {

            Text(
                text = note.title.ifEmpty {
                    "Без заголовка"
                },

                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = note.text.take(40),

                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = note.date,

                modifier = Modifier.padding(top = 8.dp),

                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}