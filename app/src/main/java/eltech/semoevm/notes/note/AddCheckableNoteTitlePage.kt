package eltech.semoevm.notes.note

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eltech.semoevm.notes.AppState
import eltech.semoevm.notes.MainActivity
import eltech.semoevm.notes.NotesApp
import eltech.semoevm.notes.data.model.CheckableNote
import eltech.semoevm.notes.data.model.CheckableNoteWithItems
import eltech.semoevm.notes.getScreenWidthDp
import eltech.semoevm.notes.ui.*
import kotlin.concurrent.thread

@Composable
fun AddCheckableNoteTitlePage() {
    val context = LocalContext.current
    AddCheckableNoteTitleDialog(onSaveClicked = {
        thread {
            var note = CheckableNote(title = it)
            note = note.copy(id = NotesApp.notesDao.insertNote(note))
            val checkableNoteWithItems = CheckableNoteWithItems(note = note, items = emptyList())
            context.startActivity(MainActivity.createStateIntent(context, AppState.Note.setInitObj(checkableNoteWithItems)))
        }
    }, onDismiss = {
        context.startActivity(MainActivity.createStateIntent(context, AppState.StartPage))
    })
}

@Composable
private fun AddCheckableNoteTitleDialog(onSaveClicked: (String) -> Unit, onDismiss: () -> Unit) {
    val backgroundColor = if (isSystemInDarkTheme()) {
        backgroundPrimaryElevatedDark
    } else {
        backgroundPrimaryElevatedLight
    }
    val textColor = if (isSystemInDarkTheme()) {
        textPrimaryDark
    } else {
        textPrimaryLight
    }
    val textValue = remember { mutableStateOf(TextFieldValue("")) }

    Row(
        modifier = Modifier.fillMaxSize().background(Color(0x20000000)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.constraintlayout.compose.ConstraintLayout(
            modifier = Modifier
                .width((LocalContext.current.getScreenWidthDp() * 0.8).dp)
                .background(color = backgroundColor)
                .wrapContentHeight()
        ) {
            val (title, titleDiv, bottomSpace, button) = createRefs()
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
                        text = "Название",
                        modifier = Modifier
                            .wrapContentHeight()
                            .alpha(0.5f),
                        color = textColor,
                        fontSize = 23.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                },
                value = textValue.value,
                activeColor = textColor,
                onValueChange = {
                    textValue.value = it
                }, modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 20.dp, end = 20.dp)
                    .constrainAs(title) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(anchor = parent.top)
                        bottom.linkTo(bottomSpace.top)
                    })

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(backgroundColor)
                    .constrainAs(titleDiv) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(bottomSpace.top)
                    })

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(backgroundColor)
                    .constrainAs(bottomSpace) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(button.top)
                    })

            Button(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .constrainAs(button) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                colors = ButtonDefaults.buttonColors(backgroundColor = if (isSystemInDarkTheme()) greenDark else greenLight),
                onClick = {
                    onSaveClicked.invoke(textValue.value.text)
                }) {
                Text(
                    text = "Сохранить",
                    fontSize = 22.sp,
                    color = if (isSystemInDarkTheme()) textPrimaryDark else Color.White
                )
            }
        }
    }
}