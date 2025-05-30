package com.example.notes

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.notes.Note.Importance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenNote(
    modifier: Modifier = Modifier,
    note: Note = Note(title = "", content = ""),
    onSaveClick: (Note) -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }
    var selectedColor by remember { mutableStateOf(note.color) }
    var selectedImportance by remember { mutableStateOf(note.importance) }
    //цвет для заметки
    val col = listOf(
        Color.WHITE,
        Color.GREEN,
        Color.MAGENTA,
        Color.LTGRAY,
        Color.YELLOW,
        Color.CYAN
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (note.title.isEmpty()) "Новая заметка" else note.title) },
                //кнопка назад
                navigationIcon = {
                    IconButton(onClick = onCancelClick) {
                        Icon(Icons.Default.ArrowBack, "назад")
                    }
                },
                //кнопка сохранить
                actions = {
                    IconButton(onClick = {
                        val updatedNote = note.copy(
                            title = title,
                            content = content,
                            color = selectedColor,
                            importance = selectedImportance
                        )
                        onSaveClick(updatedNote)
                    }) {
                        Icon(Icons.Default.Check, "сохранить")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            // Название заметки
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название заметки") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Create, contentDescription = "") }
            )

            Spacer(modifier = Modifier.height(25.dp))

            // Содержимое заметки
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Содержимое заметки") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                singleLine = false
            )

            Spacer(modifier = Modifier.height(25.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(25.dp))

            // Цвет заметки
            Text(
                text = "Цвет заметки",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                col.forEach { color ->
                    ColorSelectionItem(
                        color = color,
                        isSelected = color == selectedColor,
                        onSelect = { selectedColor = color }
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(25.dp))

            // Важност
            Text(
                text = "Важность заметки",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            // кнопки для выбора важности
            Importance.entries.forEach { importance ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedImportance = importance }
                        .padding(8.dp)
                ) {
                    RadioButton(
                        selected = selectedImportance == importance,
                        onClick = { selectedImportance = importance }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (importance) {
                            Importance.NO_MATTER -> "Не важно"
                            Importance.NORMAL -> "Обычная"
                            Importance.MATTER -> "Важно"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun ColorSelectionItem(
    color: Int,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(androidx.compose.ui.graphics.Color(color))
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onSelect),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "",
                tint = if (color == Color.WHITE) MaterialTheme.colorScheme.onSurface
                else androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}