package com.example.notes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory

class RepositoryNotes(
    private val remNotebook: RemoteNotebook,
    private val database: AppDatabase
) {
    private val log = LoggerFactory.getLogger(RepositoryNotes::class.java)
    private val daoN = database.noteDao()



    fun getNotes(): Flow<List<Note>> {
        log.debug("Получение заметок")
        return daoN.getNotes().map { entities ->
            log.info("Заметки {}", entities.size)
            entities.map { it.toNote() }
        }
    }

    suspend fun getNoteId(id: String): Note? {
        log.debug("Получение заметки: {}", id)
        return try {
            val entity = daoN.getId(id)
            entity?.toNote().also {
                if (it == null) log.error("Не найдена {}", id)
            }
        } catch (e: Exception) {
            log.error("Ошибка получения заметки {}: {}", id, e.message)
            null
        }
    }

    suspend fun addNote(note: Note) {
        log.debug("Добавление заметки: {}", note.uid)
        try {
            remNotebook.saveNote(note)
            log.info("Заметка успешно сохранена на сервере")
            daoN.insertNote(NoteEntity.fromNote(note))
            log.info("Добавлено локально {}", note.uid)
        } catch (e: Exception) {
            log.error("Не удалось добавить заметку {}: {}", note.uid, e.message)
            throw e
        }
    }

    suspend fun updateNote(note: Note) {
        log.debug("Обновление заметки: {}", note.uid)
        try {
            remNotebook.saveNote(note)
            log.info("Заметка упешно обновлена удаленно")
            daoN.updateNote(NoteEntity.fromNote(note))
            log.info("Заметка упешно обновлена локально {}", note.uid)
        } catch (e: Exception) {
            log.error("Не удалось обновить заметку {}: {}", note.uid, e.message)
            throw e
        }
    }


    suspend fun deleteNote(uid: String): Boolean {
        log.debug("Удаление заметки: {}", uid)
        return try {
            remNotebook.deleteNote(uid)
            log.info("Заметка успешно удалена на сервере")
            val note = daoN.getId(uid)
            if (note != null) {
                daoN.deleteNote(note)
                log.info("Заметка успешно удалена локально {}", uid)
                true
            } else {
                log.error("Не найдено {}", uid)
                false
            }
        } catch (e: Exception) {
            log.error("Не удалось удалить заметку {}: {}", uid, e.message)
            false
        }
    }

}