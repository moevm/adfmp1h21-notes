package eltech.semoevm.notes.start


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eltech.semoevm.notes.ui.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import eltech.semoevm.notes.*
import eltech.semoevm.notes.R
import eltech.semoevm.notes.data.model.*
import eltech.semoevm.notes.note.AddCheckableNoteTitlePage
import java.util.*
import kotlin.concurrent.thread

lateinit var isDialogOpened: () -> Boolean
private val openNoteLongClickDialog = mutableStateOf(false)
private val openDeleteNoteDialog = mutableStateOf(false)

@Volatile
private var longClickedNoteIndex: Int? = null

@ExperimentalFoundationApi
@Preview
@Composable
fun StartPage() {
    val notes = mutableStateOf(NotesApp.notesDao.getAllNotes())
    val openCreateNoteDialog = remember { mutableStateOf(false) }
    val openAddCheckableNoteDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    isDialogOpened = {
        if (openAddCheckableNoteDialog.value) {
            openAddCheckableNoteDialog.value = false
            true
        } else {
            false
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = if (isSystemInDarkTheme()) backgroundPrimaryDark else backgroundPrimaryLight),
    ) {
        val (title, notesList, addButton) = createRefs()
        val rowSize = calculateRowSize()

        val onNoteClicked: ((Int) -> Unit) = {
            context.startActivity(MainActivity.createStateIntent(context, AppState.Note.setInitObj(notes.value[it])))
        }

        LazyVerticalGrid(
            cells = GridCells.Fixed(2),
            modifier = Modifier
                .constrainAs(notesList) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(title.bottom)
                    bottom.linkTo(addButton.top)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .padding(top = 50.dp, bottom = 60.dp)) {

            itemsIndexed(notes.value) { index, it ->
                val onCLick = { onNoteClicked.invoke(index) }
                val onLongClick = {
                    longClickedNoteIndex = index
                    openNoteLongClickDialog.value = true
                }
                when (it) {
                    is TextNote -> TextNoteCell(item = it, size = rowSize, onClick = onCLick, onLongClick)
                    is CheckableNoteWithItems -> CheckableNoteCell(item = it, size = rowSize, onClick = onCLick, onLongClick)
                }
            }
        }

        Text(
            text = stringResource(R.string.start_page_notes),
            fontSize = 30.sp,
            fontFamily = FontFamily.SansSerif,
            color = if (isSystemInDarkTheme()) textPrimaryDark else textPrimaryLight,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(if (isSystemInDarkTheme()) backgroundPrimaryDark else backgroundPrimaryLight)
                .fillMaxWidth()
                .padding(10.dp)
                .constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Button(
            modifier = Modifier
                .height(60.dp)
                .constrainAs(addButton) {
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = if (isSystemInDarkTheme()) greenDark else greenLight),
            onClick = {
                openCreateNoteDialog.value = true
            }) {
            Text(
                text = stringResource(R.string.start_page_add_note),
                fontSize = 22.sp,
                color = if (isSystemInDarkTheme()) textPrimaryDark else Color.White
            )
        }

        if (openAddCheckableNoteDialog.value) {
            AddCheckableNoteTitlePage()
        }
    }

    if (openCreateNoteDialog.value) {
        DialogWithTwoButtons(
            firstText = "Заметка",
            secondText = "Перечень",
            onFirstClicked = { context.startActivity(MainActivity.createStateIntent(context, AppState.AddTextNote)) },
            onSecondClicked = {
                openCreateNoteDialog.value = false
                openAddCheckableNoteDialog.value = true
            },
            onDismiss = {
                openCreateNoteDialog.value = false
            }
        )
    }

    if (openNoteLongClickDialog.value) {
        DialogWithTwoButtons(
            firstText = "Редактировать",
            secondText = "Удалить",
            onFirstClicked = {
                openNoteLongClickDialog.value = false
                longClickedNoteIndex?.let {
                    val note = notes.value.getOrNull(it) as? TextNote? ?: return@let
                    context.startActivity(MainActivity.createStateIntent(context, AppState.AddTextNote.setInitObj(note)))
                }
            },
            onSecondClicked = {
                openNoteLongClickDialog.value = false
                openDeleteNoteDialog.value = true
            },
            onDismiss = {
                openNoteLongClickDialog.value = false
            }
        )
    }

    AcceptDialog(
        showState = openDeleteNoteDialog,
        textStr = "Удалить заметку?",
        acceptButtonText = "Удалить",
        dismissButtonText = "Оставить",
        onAccept = {
            openDeleteNoteDialog.value = false
            longClickedNoteIndex?.let { noteIndex ->
                val note = notes.value.getOrNull(noteIndex) ?: return@let
                thread {
                    when (note) {
                        is TextNote -> {
                            NotesApp.notesDao.deleteNote(note)
                        }
                        is CheckableNoteWithItems -> {
                            NotesApp.notesDao.deleteNote(note)
                        }
                    }
                    notes.value = notes.value.filterIndexed { index, _ -> index != noteIndex }
                    notes.value
                }
            }
        },
        onDismiss = {
            openDeleteNoteDialog.value = false
        }
    )
}

@Composable
private fun TextNoteCell(item: TextNote, size: Dp, onClick: () -> Unit, onLongClick: () -> Unit) {
    BaseNoteRow(item = item, size = size, onClick = onClick, onLongClick = onLongClick) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val textRef = createRef()
            Text(
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(textRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        top.linkTo(parent.top)
                    },
                text = item.title.takeIf { it.isNotBlank() } ?: item.text,
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif,
                color = if (isSystemInDarkTheme()) textPrimaryDark else textPrimaryLight,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CheckableNoteCell(item: CheckableNoteWithItems, size: Dp, onClick: () -> Unit, onLongClick: () -> Unit) {
    BaseNoteRow(item = item, size = size, onClick = onClick, onLongClick = onLongClick) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (text, icon) = createRefs()
            Text(
                text = item.note.title,
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif,
                color = if (isSystemInDarkTheme()) textPrimaryDark else textPrimaryLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.constrainAs(text) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
            )

            Image(
                painter = painterResource(id = R.drawable.ic_checkable_logo),
                contentDescription = null,
                modifier = Modifier
                    .width(20.dp)
                    .height(20.dp)
                    .constrainAs(icon) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            )
        }
    }
}

@Composable
private fun BaseNoteRow(
    item: Note,
    size: Dp,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    val backgroundColor = if (isSystemInDarkTheme()) {
        backgroundPrimaryElevatedDark
    } else {
        backgroundPrimaryElevatedLight
    }
    val timeEdited = if (item is TextNote) {
        item.timeEdited
    } else {
        (item as CheckableNoteWithItems).note.timeCreated
    }

    ButtonWithCustomClickHandlers(
        onLongClick = onLongClick,
        colors = ButtonDefaults.buttonColors(backgroundColor),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 1.dp,
            pressedElevation = 3.dp
        ),
        modifier = Modifier
            .width(size)
            .height(size)
            .padding(14.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(24.dp)
            ),
        onClick = onClick,
        content = {
            Box(contentAlignment = Alignment.BottomCenter) {
                content.invoke(this@ButtonWithCustomClickHandlers)
                Text(text = timeEdited.toPrettyTime(), color = if (isSystemInDarkTheme()) textSecondaryDark else textSecondaryLight)
            }
        }
    )
}

@Composable
private fun calculateRowSize(): Dp {
    val screenWith = LocalContext.current.getScreenWidthDp()
    return (screenWith / 2).dp
}