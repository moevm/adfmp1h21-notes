package eltech.semoevm.notes

import android.app.Application
import android.content.Context
import androidx.room.Room
import eltech.semoevm.notes.data.database.NoteDao
import eltech.semoevm.notes.data.database.NoteDatabase

class NotesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            baseContext,
            NoteDatabase::class.java,
            "NoteDatabase"
        ).allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
        notesDao = db.getDao()
    }

    companion object {
        lateinit var db: NoteDatabase
        lateinit var notesDao: NoteDao
    }
}