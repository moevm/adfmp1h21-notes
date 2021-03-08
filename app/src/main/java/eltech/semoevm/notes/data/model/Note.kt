package eltech.semoevm.notes.data.model

import androidx.room.*
import java.io.Serializable
import java.util.*

interface Note: Serializable

@Entity(tableName = "TextNote")
data class TextNote(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    val title: String,
    val text: String,
    val timeEdited: Long = System.currentTimeMillis()
) : Note, Serializable

@Entity(tableName = "CheckableNote")
data class CheckableNote(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    val title: String,
    val timeCreated: Long = System.currentTimeMillis()
) : Note, Serializable


data class CheckableNoteWithItems(
    @Embedded
    val note: CheckableNote,
    @Relation(parentColumn = "id", entityColumn = "noteId")
    val items: List<CheckableItem>,
) : Note, Serializable

@Entity(tableName = "CheckableItem")
data class CheckableItem(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    val noteId: Long = -1,
    val text: String,
    val isChecked: Boolean = false
): Serializable