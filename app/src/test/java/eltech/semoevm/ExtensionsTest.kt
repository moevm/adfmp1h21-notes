package eltech.semoevm

import eltech.semoevm.notes.data.model.CheckableItem
import eltech.semoevm.notes.data.model.CheckableNote
import eltech.semoevm.notes.data.model.CheckableNoteWithItems
import eltech.semoevm.notes.data.model.TextNote
import eltech.semoevm.notes.toPrettyTime
import eltech.semoevm.notes.toShareText
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class ExtensionsTest {

    @Test
    fun `check share text TextNote`() {
        val shareText = TEXT_NOTE.toShareText()
        assert(shareText == "${TEXT_NOTE.title}\n\n${TEXT_NOTE.text}")
    }

    @Test
    fun `check share text contains title CheckableNote`() {
        val shareText = CHECKABLE_NOTE.toShareText()
        assert(shareText.startsWith(CHECKABLE_NOTE.note.title))
    }

    @Test
    fun `check share text contains each item CheckableNote`() {
        val shareText = CHECKABLE_NOTE.toShareText()
        for (item in CHECKABLE_NOTE.items) {
            assert(shareText.contains(item.text))
        }
    }

    @Test
    fun `check ddMMyy formatting not today`() {
        val time = System.currentTimeMillis()
        assert(time.toPrettyTime() == SimpleDateFormat("dd.MM.yy").format(Date(time)))
    }

    @Test(expected = RuntimeException::class)
    fun `check share TextNote with empty title exception`() {
        val note = TextNote(title = "", text = "stub")
        note.toShareText()
    }

    @Test(expected = RuntimeException::class)
    fun `check share TextNote with empty text  exception`() {
        val note = TextNote(title = "stub", text = "")
        note.toShareText()
    }

    @Test(expected = RuntimeException::class)
    fun `check share TextNote with empty text and title exception`() {
        val note = TextNote(title = "", text = "")
        note.toShareText()
    }

    private companion object {
        const val DEFAULT_TIME = 1000L
        val TEXT_NOTE = TextNote(text = "Text", title = "Title")
        val CHECKABLE_NOTE = CheckableNoteWithItems(
            note = CheckableNote(title = "Title", timeCreated = DEFAULT_TIME),
            items = List(5){
                CheckableItem(text = "Item $it")
            }
        )
    }
}