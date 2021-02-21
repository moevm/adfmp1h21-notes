package eltech.semoevm.notes.data.model

import java.io.Serializable
import java.util.*

sealed class Note(val id: String = UUID.randomUUID().toString()): Serializable

data class TextNote(val title: String, val text: String): Note(), Serializable

data class CheckableNote(val title: String, val items: CheckableItem): Note(), Serializable

data class CheckableItem(val text: String, val isChecked: Boolean = false): Serializable