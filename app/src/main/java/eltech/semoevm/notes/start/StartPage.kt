package eltech.semoevm.notes.start


import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import eltech.semoevm.notes.ui.*
import androidx.constraintlayout.compose.ConstraintLayout
import eltech.semoevm.notes.*
import eltech.semoevm.notes.R
import eltech.semoevm.notes.data.SpStorage
import eltech.semoevm.notes.data.model.CheckableNote
import eltech.semoevm.notes.data.model.Note
import eltech.semoevm.notes.data.model.TextNote

@ExperimentalFoundationApi
@Preview("StartPage")
@Composable
fun StartPage() {
    val openDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = if (isSystemInDarkTheme()) backgroundPrimaryDark else backgroundPrimaryLight),
    ) {
        val (title, notesList, addButton) = createRefs()
        val rowSize = calculateRowSize()

        val onRowClicked: ((Note) -> Unit) = {
            context.startActivity(
                MainActivity.createStateIntent(
                    context,
                    AppState.Note.setInitObj(it)
                )
            )
        }
        LazyVerticalGrid(
            cells = GridCells.Fixed(2),
            modifier = Modifier
                .constrainAs(notesList) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(title.bottom)
                    bottom.linkTo(addButton.top)
                }
                .padding(top = 50.dp, bottom = 60.dp)
                .height(Dp.Unspecified)
                .width(Dp.Unspecified)) {
            items(SpStorage.getNotes()) {
                when (it) {
                    is TextNote -> TextNoteRow(item = it, size = rowSize, onClick = onRowClicked)
                    is CheckableNote -> CheckableNoteRow(
                        item = it,
                        size = rowSize,
                        onClick = onRowClicked
                    )
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
                openDialog.value = true
            }) {
            Text(
                text = stringResource(R.string.start_page_add_note),
                fontSize = 22.sp,
                color = if (isSystemInDarkTheme()) textPrimaryDark else Color.White
            )
        }
    }

    if (openDialog.value) {
        AddNoteDialog(
            onTextNoteClick = {
                openDialog.value = false
                Toast.makeText(context, "Переход к созданию текстовой заметки", Toast.LENGTH_LONG).show()
            },
            onCheckableNoteClicked = {
                openDialog.value = false
                Toast.makeText(context, "Переход к созданию заметки-перечня", Toast.LENGTH_LONG).show()
            },
            onDismiss = {
                openDialog.value = false
            })
    }
}

@Composable
private fun AddNoteDialog(
    onTextNoteClick: () -> Unit,
    onCheckableNoteClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
        onDismissRequest = { onDismiss.invoke() }) {
        val width = (LocalContext.current.getScreenWidthDp() * 0.66).dp
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
        Column {
            Button(
                modifier = Modifier
                    .height(50.dp)
                    .width(width),
                colors = ButtonDefaults.buttonColors(backgroundColor),
                onClick = onTextNoteClick
            ) {
                Text(
                    text = "Заметка",
                    fontSize = 22.sp,
                    color = textColor
                )
            }

            Spacer(
                modifier = Modifier
                    .height(2.dp)
                    .background(
                        if (isSystemInDarkTheme()) backgroundPrimaryDark
                        else backgroundPrimaryLight
                    )
            )

            Button(
                modifier = Modifier
                    .height(50.dp)
                    .width(width)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor),
                onClick = onCheckableNoteClicked
            ) {
                Text(
                    text = "Перечень",
                    fontSize = 22.sp,
                    color = textColor
                )
            }
        }
    }
}

@Composable
private fun TextNoteRow(item: TextNote, size: Dp, onClick: ((Note) -> Unit)) {
    BaseNoteRow(item = item, size = size, onClick = onClick) {
        Text(
            text = item.title.takeIf { it.isNotBlank() } ?: item.text,
            fontSize = 16.sp,
            fontFamily = FontFamily.SansSerif,
            color = if (isSystemInDarkTheme()) textPrimaryDark else textPrimaryLight,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CheckableNoteRow(item: CheckableNote, size: Dp, onClick: (Note) -> Unit) {
    BaseNoteRow(item = item, size = size, onClick = onClick) {
        Text(
            text = item.id,
            fontSize = 16.sp,
            fontFamily = FontFamily.SansSerif,
            color = if (isSystemInDarkTheme()) textPrimaryDark else textPrimaryLight,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BaseNoteRow(
    item: Note,
    size: Dp,
    onClick: (Note) -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    val backgroundColor = if (isSystemInDarkTheme()) {
        backgroundPrimaryElevatedDark
    } else {
        backgroundPrimaryElevatedLight
    }
    Button(
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
        onClick = {
            onClick.invoke(item)
        },
        content = content
    )
}

@Composable
private fun calculateRowSize(): Dp {
    val screenWith = LocalContext.current.getScreenWidthDp()
    return (screenWith / 2).dp
}