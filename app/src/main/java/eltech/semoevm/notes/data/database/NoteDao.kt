package eltech.semoevm.notes.data.database

import androidx.room.*
import eltech.semoevm.notes.data.model.*
import java.lang.IllegalStateException

@Dao
abstract class NoteDao {
    @Transaction
    open fun getAllNotes(): List<Note> {
        val textNotes = getAllTextNotes()
        val checkableNotes = getAllCheckableNotes()
        val notes = textNotes + checkableNotes
        return notes.sortedByDescending {
            when (it) {
                is TextNote -> it.timeEdited
                is CheckableNoteWithItems -> it.note.timeCreated
                else -> throw IllegalStateException()
            }
        }
    }

    @Transaction
    open fun updateNote(note: Note) {
        when(note) {
            is CheckableNote -> updateCheckableNote(note)
            is TextNote -> updateTextNote(note)
            is CheckableNoteWithItems -> {
                note.items.forEach {
                    updateCheckableNoteItem(it)
                }
                updateCheckableNote(note.note)
            }
        }
    }

    @Transaction
    open fun insertCheckableNoteWithItems(note: CheckableNoteWithItems) {
        val nId = insertNote(note.note)
        insertCheckableNoteItems(note.items.map { it.copy(noteId = nId) })
    }

    @Query("SELECT * FROM textnote")
    abstract fun getAllTextNotes(): List<TextNote>

    @Query("SELECT * FROM checkablenote")
    abstract fun getAllCheckableNotes(): List<CheckableNoteWithItems>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateTextNote(note: TextNote)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateCheckableNote(note: CheckableNote)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateCheckableNoteItem(note: CheckableItem)

    @Insert
    abstract fun insertNote(note: TextNote): Long

    @Insert
    abstract fun insertNotes(notes: List<TextNote>): List<Long>

    @Insert
    abstract fun insertNote(note: CheckableNote): Long

    @Insert
    abstract fun insertCheckableNotes(notes: List<CheckableNote>): List<Long>

    @Insert
    abstract fun insertNoteItems(note: CheckableItem): Long

    @Insert
    abstract fun insertCheckableNoteItems(notes: List<CheckableItem>): List<Long>

    @Insert
    abstract fun insertCheckableNoteItem(item: CheckableItem): Long

    @Delete
    abstract fun deleteItem(item: CheckableItem)

    @Delete
    abstract fun deleteItems(items: List<CheckableItem>)

    @Delete
    abstract fun deleteNote(note: TextNote)

    @Delete
    abstract fun deleteNote(note: CheckableNote)

    @Transaction
    open fun deleteNote(note: CheckableNoteWithItems) {
        deleteNote(note.note)
        deleteItems(note.items)
    }
}