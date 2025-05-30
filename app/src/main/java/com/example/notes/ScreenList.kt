package com.example.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenList(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    onAddNote: () -> Unit,
    onDeleteNote: (Note) -> Unit
) {
    //экран
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои заметки") }
            )
        },
        //кнопка +
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNote,
                containerColor = Color.Transparent,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(245,176,56), Color(100,83,234)),
                        ),
                        shape = RoundedCornerShape(5.dp)
                    )

            ) {
                Icon(Icons.Default.Add, contentDescription = "")
            }
        }
    ) { innerPadding ->
        if (notes.isEmpty()) {
            //пустой экран без заметок
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Нет заметок.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(25.dp),
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                items(
                    items = notes,
                    key = { note -> note.uid }
                ) { note ->
                    DeleteN(
                        note = note,
                        onNoteClick = onNoteClick,
                        onDeleteNote = onDeleteNote
                    )
                }
            }
        }
    }
}
//удаление заметки
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteN(note: Note, onNoteClick: (Note) -> Unit, onDeleteNote: (Note) -> Unit) {
    var del by remember { mutableStateOf(false) }
    if (del) {
        LaunchedEffect(Unit) { onDeleteNote(note) }
        return
    }

    SwipeToDismissBox(
        state = rememberSwipeToDismissBoxState(confirmValueChange = {
            if (it == SwipeToDismissBoxValue.Settled) {
                del = true
                true
            } else { false }
        }),
        //фон при удалении
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1f)
                    .wrapContentSize(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(255, 158, 0))
                    .padding(35.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "",
                    tint = Color.White
                )
            }
        },
        content = { ElementN(note = note, onNoteClick = onNoteClick)}
    )
}

//отображение заметок
@Composable
fun ElementN(note: Note, onNoteClick: (Note) -> Unit) {
    Card(
        onClick = { onNoteClick(note) },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(note.color)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(//заголовок
                text = note.title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(//содержимое первые 70 символов
                text = note.content.take(70) + if (note.content.length > 70) "..." else "",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(//важность
                text = when (note.importance) {
                    Note.Importance.NO_MATTER -> "Не важно"
                    Note.Importance.NORMAL -> "Обычная"
                    Note.Importance.MATTER -> "Важно"
                },
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}