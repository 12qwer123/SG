package com.example.notes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val uid: String,
    val title: String,
    val content: String,
    val importance: String,
    val color: Int
) {
    fun toNote(): Note {
        return Note(
            uid = uid,
            title = title,
            content = content,
            importance = when (importance) {
                "NO_MATTER" -> Note.Importance.NO_MATTER
                "MATTER" -> Note.Importance.MATTER
                else -> Note.Importance.NORMAL
            }, color = color
        )
    }

    companion object {
        fun fromNote(note: Note): NoteEntity {
            return NoteEntity(
                uid = note.uid,
                title = note.title,
                content = note.content,
                importance = note.importance.name,
                color = note.color
            )
        }
    }
}