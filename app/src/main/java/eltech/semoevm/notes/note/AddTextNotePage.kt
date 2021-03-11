package eltech.semoevm.notes.note

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eltech.semoevm.notes.AppState
import eltech.semoevm.notes.MainActivity
import eltech.semoevm.notes.R
import eltech.semoevm.notes.ui.*
import androidx.constraintlayout.compose.ConstraintLayout
import eltech.semoevm.notes.NotesApp
import eltech.semoevm.notes.data.model.TextNote
import kotlin.concurrent.thread


@Preview
@Composable
fun AddTextNotePage(textNote: TextNote?) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = if (isSystemInDarkTheme()) backgroundPrimaryDark else backgroundPrimaryLight)
    ) {
        val (backArrow, titleContainer, divider, text, bottomButton) = createRefs()
        val context = LocalContext.current
        val titleValue = remember { mutableStateOf(TextFieldValue(textNote?.title ?: "")) }
        val textValue = remember { mutableStateOf(TextFieldValue(textNote?.text ?: "")) }

        IconButton(onClick = {
            context.startActivity(MainActivity.createStateIntent(context, AppState.StartPage))
        }, modifier = Modifier.constrainAs(backArrow) {
            start.linkTo(parent.start)
            top.linkTo(titleContainer.top)
            bottom.linkTo(titleContainer.bottom)
        }) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = null,
                modifier = Modifier
                    .width(26.dp)
                    .height(26.dp)
            )
        }

        ConstraintLayout(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .constrainAs(titleContainer) {
                    start.linkTo(backArrow.end)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                }) {
            val (title, titleDiv) = createRefs()
            TextField(
                textStyle = TextStyle(
                    color = if (isSystemInDarkTheme()) textPrimaryDark else textPrimaryLight,
                    fontSize = 27.sp,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Start
                ),
                backgroundColor = Color.Transparent,
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Название",
                        modifier = Modifier
                            .wrapContentHeight()
                            .alpha(0.5f),
                        color = if (isSystemInDarkTheme()) textSecondaryDark else textSecondaryLight,
                        fontSize = 27.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                },
                value = titleValue.value,
                activeColor = if (isSystemInDarkTheme()) textPrimaryDark else textPrimaryLight,
                onValueChange = {
                    titleValue.value = it
                }, modifier = Modifier
                    .padding(0.dp)
                    .wrapContentHeight()
                    .constrainAs(title) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(anchor = parent.top)
                        bottom.linkTo(parent.bottom)
                        width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                    })

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(if (isSystemInDarkTheme()) backgroundPrimaryDark else backgroundPrimaryLight)
                    .constrainAs(titleDiv) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    })
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(if (isSystemInDarkTheme()) backgroundPrimaryElevatedLight else backgroundPrimaryElevatedDark)
                .constrainAs(divider) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(titleContainer.bottom)
                })

        ScrollableColumn(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(text) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(anchor = divider.bottom)
                    bottom.linkTo(bottomButton.top)
                    height = androidx.constraintlayout.compose.Dimension.fillToConstraints
                }
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth()
            ) {
                val (title, titleDiv) = createRefs()
                val textColor = if (isSystemInDarkTheme()) textSecondaryDark else textSecondaryLight
                TextField(
                    textStyle = TextStyle(
                        color = textColor,
                        fontSize = 23.sp,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Start
                    ),
                    backgroundColor = Color.Transparent,
                    placeholder = {
                        Text(
                            text = "Текст заметки",
                            modifier = Modifier
                                .wrapContentHeight()
                                .alpha(0.33f),
                            color = textColor,
                            fontSize = 25.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    },
                    value = textValue.value,
                    activeColor = textColor,
                    onValueChange = {
                        textValue.value = it
                    }, modifier = Modifier
                        .padding(0.dp)
                        .wrapContentHeight()
                        .constrainAs(title) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(anchor = parent.top)
                            bottom.linkTo(parent.bottom)
                            width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                        })

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(if (isSystemInDarkTheme()) backgroundPrimaryDark else backgroundPrimaryLight)
                        .constrainAs(titleDiv) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        })
            }
        }

        Button(
            modifier = Modifier
                .height(60.dp)
                .constrainAs(bottomButton) {
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = if (isSystemInDarkTheme()) greenDark else greenLight),
            onClick = {
                if (textNote != null) {
                    thread {
                        val newNote = textNote.copy(title = titleValue.value.text, text = textValue.value.text, timeEdited = System.currentTimeMillis())
                        NotesApp.notesDao.updateTextNote(newNote)
                        context.startActivity(MainActivity.createStateIntent(context, AppState.Note.setInitObj(newNote)))
                    }
                } else {
                    saveNote(context, titleValue.value.text, textValue.value.text)
                }
            }) {
            Text(
                text = "Сохранить заметку",
                fontSize = 22.sp,
                color = if (isSystemInDarkTheme()) textPrimaryDark else Color.White
            )
        }
    }
}

private fun saveNote(context: Context, title: String, text: String) {
    if (title.isBlank() || text.isBlank()) return
    thread(start = true) {
        var note = TextNote(title = title, text = text)
        val id = NotesApp.notesDao.insertNote(note)
        note = note.copy(id = id)
        context.startActivity(MainActivity.createStateIntent(context, AppState.Note.setInitObj(note)))
    }
}
