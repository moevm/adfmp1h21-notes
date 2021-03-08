package eltech.semoevm.notes.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import eltech.semoevm.notes.NotesApp
import eltech.semoevm.notes.data.model.CheckableItem
import eltech.semoevm.notes.data.model.CheckableNote
import eltech.semoevm.notes.data.model.TextNote

@Database(
    entities = [TextNote::class, CheckableNote::class, CheckableItem::class],
    version = 2
)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun getDao(): NoteDao
}