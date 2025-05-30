package com.example.notes

import android.content.Context
import android.graphics.Color
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import org.slf4j.LoggerFactory


// класс Note
data class Note(
    val uid: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val importance: Importance = Importance.NORMAL,
    val color: Int = Color.WHITE

) {
    // важность заметки
    enum class Importance {
        NO_MATTER,
        NORMAL,
        MATTER
    }
}
class FileNotebook(context: Context) {
    private val log = LoggerFactory.getLogger(FileNotebook::class.java)
    private val remNote = RemoteNotebook()
    private val datab = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "notes-database"
    ).build()
    private val rep = RepositoryNotes(remNote, datab)

    val notes: Flow<List<Note>> = rep.getNotes()

    suspend fun addNote(note: Note) {//+ новая заметка
        log.debug("Добавление заметки: '{}'", note.title)
        try {
            rep.addNote(note)
            log.info("Заметка успешно добавлена: {}", note.uid)
        } catch (e: Exception) {
            log.error("Возникла ошибка при добавлении заметки '{}': {}", note.title, e.message)
            throw e
        }
    }

    suspend fun deleteNote(uid: String): Boolean {//удаление
        log.debug("Удаление заметки: {}", uid)
        return try {
            val u = rep.deleteNote(uid)
            if (u) {
                log.debug("Заметка успешно удалена: {}", uid)
            } else {
                log.warn("Заметка не найдена: {}", uid)
            }
            u
        } catch (e: Exception) {
            log.error("Ошибка при удалении заметки {}: {}", uid, e.message)
            false
        }
    }

    suspend fun loadNotes(): Boolean {//загрузка заметок
        log.info("Начало")
        return try {
            val rem = remNote.loadNotes()
            log.debug("Получено {}", rem.size)
            rem.forEach { note ->
                rep.addNote(note)
            }
            log.info("Успех")
            true
        } catch (e: Exception) {
            log.error("Ошибка: {}", e.message)
            false
        }
    }

    suspend fun updateNote(note: Note) {
        log.debug("Обновление заметки: {}", note.uid)
        try {
            rep.updateNote(note)
            log.info("Заметка успешно обновлена: {}", note.uid)
        } catch (e: Exception) {
            log.error("Ошибка при обновлении заметки '{}': {}", note.uid, e.message)
            throw e
        }
    }
    suspend fun getNoteById(id: String): Note? {
        log.debug("Извлечение заметки: {}", id)
        return try {
            val i = rep.getNoteId(id)
            if (i == null) {
                log.warn("Заметка не найдена: {}", id)
            } else {
                log.debug("Заметка получена: {}", id)
            }
            i
        } catch (e: Exception) {
            log.error("Ошибка при загрузке заметки {}: {}", id, e.message)
            null
        }
    }
}


