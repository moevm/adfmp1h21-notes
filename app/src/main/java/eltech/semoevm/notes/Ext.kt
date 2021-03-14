package eltech.semoevm.notes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import eltech.semoevm.notes.data.model.CheckableNoteWithItems
import eltech.semoevm.notes.data.model.TextNote
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.util.*

fun Context.getScreenWidthDp(): Int {
    val px = resources.displayMetrics.widthPixels
    return (px / resources.displayMetrics.density).toInt()
}

@SuppressLint("SimpleDateFormat")
private val ddMMyyFormat = SimpleDateFormat("dd.MM.yy")

@SuppressLint("SimpleDateFormat")
private val hhmmFormat = SimpleDateFormat("HH:mm")
fun Long.toPrettyTime(): String {
    if (DateUtils.isToday(this)) {
        return hhmmFormat.format(Date(this))
    } else {
        return ddMMyyFormat.format(Date(this))
    }
}

fun Context.sharePlainText(subject: String, text: String) {
    val intent = Intent(Intent.ACTION_SEND).also {
        it.type = "text/plain"
        it.putExtra(Intent.EXTRA_SUBJECT, subject)
        it.putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(intent, "Поделиться заметкой"))
}

fun TextNote.toShareText(): String {
    if (title.isEmpty() || text.isEmpty()) {
        throw RuntimeException("can not share empty note: $this")
    }
    return StringBuilder().apply {
        append(title + "\n\n")
        append(text)
    }.toString()
}

fun CheckableNoteWithItems.toShareText(): String {
    return StringBuilder().apply {
        append(note.title + "\n\n")
        items.forEach {
            append("${if (it.isChecked) "+" else "-"} ${it.text}\n")
        }
    }.toString()
}

fun TextNote.updateCreatedTime(): TextNote {
    val newNote = this.copy(timeEdited = System.currentTimeMillis())
    NotesApp.notesDao.updateNote(newNote)
    return newNote
}

fun CheckableNoteWithItems.updateEditedTime(): CheckableNoteWithItems {
    val newNote = this.note.copy(timeCreated = System.currentTimeMillis())
    NotesApp.notesDao.updateNote(newNote)
    return this.copy(note = newNote)
}