package eltech.semoevm.notes.data

import eltech.semoevm.notes.data.model.Note
import eltech.semoevm.notes.data.model.TextNote

object SpStorage {
    fun updateNote(note: Note) {
        // todo
    }

    fun addNote(note: Note) {
        // todo
    }

    fun getNotes(): List<Note> {
        return List(300) {
            TextNote(title = "Note $it", "stub ".repeat(50))
        }
    }
}