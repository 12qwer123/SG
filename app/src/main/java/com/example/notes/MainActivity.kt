package com.example.notes
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.notes.ui.theme.NotesTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class MainActivity : ComponentActivity() {
    private lateinit var fn: FileNotebook

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        fn = FileNotebook(this)

        lifecycleScope.launch {//загрузка заметок
            fn.loadNotes()
        }

        setContent {
            NotesTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NotesApp(fn)
                }
            }
        }
    }
}

@Composable
fun NotesApp(fileNotebook: FileNotebook) {
    val navController = rememberNavController()
    val notes by fileNotebook.notes.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    //навигация
    NavHost(
        navController = navController,
        startDestination = "notesList"
    ) {
        composable("notesList") {//список заметок
            ScreenList(
                notes = notes,
                onNoteClick = { note -> navController.navigate("editNote/${note.uid}") },
                onAddNote = { navController.navigate("editNote/new") },
                onDeleteNote = { note ->
                    coroutineScope.launch {
                        fileNotebook.deleteNote(note.uid)
                    }
                }
            )
        }
        //редактирование заметки
        composable("editNote/{noteId}") { backStackEntry ->
            val log = LoggerFactory.getLogger("")
            val noteId = backStackEntry.arguments?.getString("noteId") ?: "new".also {
                log.debug("Создание новой заметки")}
            val note = if (noteId == "new") {
                Note(title = "", content = "")
            } else {
                log.debug("Загрузка существующей заметки: {}", noteId)
                val existingNote by produceState<Note?>(initialValue = null) {
                    coroutineScope.launch {
                        value = fileNotebook.getNoteById(noteId)
                    }
                }
                existingNote ?: Note(title = "", content = "")
            }

            ScreenNote(
                note = note,
                onSaveClick = { updatedNote ->
                    coroutineScope.launch {
                        try {
                            if (noteId == "new") {//+ новая заметка
                                fileNotebook.addNote(updatedNote)
                            } else {//обновление заметки
                                fileNotebook.updateNote(updatedNote)
                            }
                            navController.popBackStack()
                        } catch (e: Exception) {
                            //handleError(e)
                        }
                    }
                },
                onCancelClick = { navController.popBackStack() }
            )
        }
    }
}
