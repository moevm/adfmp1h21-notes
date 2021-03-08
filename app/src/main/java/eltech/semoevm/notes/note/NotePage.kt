package eltech.semoevm.notes.note

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension.Companion.fillToConstraints
import eltech.semoevm.notes.*
import eltech.semoevm.notes.R
import eltech.semoevm.notes.data.model.*
import eltech.semoevm.notes.ui.*
import kotlin.concurrent.thread

private val showRemoveItemDialog = mutableStateOf(false)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NotePage(note: Note) {
    when (note) {
        is TextNote -> TextNotePage(textNote = note)
        is CheckableNoteWithItems -> CheckableNotePage(checkableNote = note)
    }
}

@ExperimentalComposeUiApi
@Composable
private fun TextNotePage(textNote: TextNote) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (toolbar, scroll) = createRefs()
        val backgroundColor =
                if (isSystemInDarkTheme()) backgroundPrimaryDark else backgroundPrimaryLight
        ConstraintLayout(
                modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(backgroundColor)
                        .constrainAs(toolbar) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                        }
        ) {
            val (backArrow, editIcon) = createRefs()
            val context = LocalContext.current

            IconButton(onClick = {
                context.startActivity(MainActivity.createStateIntent(context, AppState.StartPage))
            }, modifier = Modifier.constrainAs(backArrow) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }) {
                Image(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = null,
                        modifier = Modifier
                                .width(26.dp)
                                .height(26.dp)
                )
            }

            IconButton(onClick = {
                context.sharePlainText("Посмотри мой прогресс...", textNote.toShareText())
            }, modifier = Modifier.constrainAs(editIcon) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }) {
                Image(
                        painter = painterResource(id = R.drawable.ic_share),
                        contentDescription = null,
                        modifier = Modifier
                                .width(26.dp)
                                .height(26.dp)
                )
            }
        }

        Column(
                modifier = Modifier
                        .verticalScroll(
                                state = rememberScrollState(),
                                enabled = true,
                                reverseScrolling = false
                        )
                        .background(if (isSystemInDarkTheme()) backgroundPrimaryDark else backgroundPrimaryLight)
                        .constrainAs(scroll) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(toolbar.bottom)
                            bottom.linkTo(parent.bottom)
                            height = fillToConstraints
                            width = fillToConstraints
                        }
        ) {
            Text(
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                    text = textNote.title,
                    fontSize = 30.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = if (isSystemInDarkTheme()) textPrimaryDark else textPrimaryLight,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                            .background(if (isSystemInDarkTheme()) backgroundPrimaryDark else backgroundPrimaryLight)
                            .fillMaxWidth()
                            .padding(20.dp)
            )

            Text(
                    text = textNote.text,
                    fontSize = 22.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = if (isSystemInDarkTheme()) textSecondaryDark else textSecondaryLight,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier
                            .fillMaxSize()
                            .padding(PaddingValues(start = 20.dp, end = 20.dp))
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalComposeUiApi
@Composable
private fun CheckableNotePage(checkableNote: CheckableNoteWithItems) {
    val checkableNoteItems: MutableState<List<CheckableItem>> =
            remember { mutableStateOf(checkableNote.items) }

    var onRemoveItemAccepted: (() -> Unit)? = null

    AcceptDialog(
            showState = showRemoveItemDialog,
            textStr = "Удалить элемент?",
            acceptButtonText = "Удалить",
            dismissButtonText = "Отмена",
            onDismiss = {
                showRemoveItemDialog.value = false
            },
            onAccept = {
                showRemoveItemDialog.value = false
                onRemoveItemAccepted?.invoke()
            })

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val backgroundColor =
                if (isSystemInDarkTheme()) backgroundPrimaryDark else backgroundPrimaryLight

        val (header, title, itemsList, newItemFiled) = createRefs()

        ConstraintLayout(
                modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(backgroundColor)
                        .constrainAs(header) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }
        ) {
            val (backArrow, editIcon) = createRefs()
            val context = LocalContext.current

            IconButton(onClick = {
                context.startActivity(MainActivity.createStateIntent(context, AppState.StartPage))
            }, modifier = Modifier.constrainAs(backArrow) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }) {
                Image(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = null,
                        modifier = Modifier
                                .width(26.dp)
                                .height(26.dp)
                )
            }

            IconButton(onClick = {
                context.sharePlainText("Посмотри мой прогресс...", checkableNote.copy(items = checkableNoteItems.value).toShareText())
            }, modifier = Modifier.constrainAs(editIcon) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }) {
                Image(
                        painter = painterResource(id = R.drawable.ic_share),
                        contentDescription = null,
                        modifier = Modifier
                                .width(26.dp)
                                .height(26.dp)
                )
            }
        }

        Text(
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
                text = checkableNote.note.title,
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif,
                color = if (isSystemInDarkTheme()) textPrimaryDark else textPrimaryLight,
                textAlign = TextAlign.Start,
                modifier = Modifier
                        .background(if (isSystemInDarkTheme()) backgroundPrimaryDark else backgroundPrimaryLight)
                        .fillMaxWidth()
                        .padding(10.dp)
                        .constrainAs(title) {
                            top.linkTo(header.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
        )

        LazyVerticalGrid(
                cells = GridCells.Fixed(1),
                modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(itemsList) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(title.bottom)
                            bottom.linkTo(newItemFiled.top)
                            height = fillToConstraints
                        }
        ) {

            itemsIndexed(checkableNoteItems.value) { index, it ->
                when (it) {
                    is CheckableItem -> CheckableItemCell(
                            item = it,
                            onCheckedChanged = { checkedItem, isChecked ->
                                checkableNoteItems.value.toMutableList().let { list ->
                                    list.forEachIndexed { index, checkableItem ->
                                        if (checkableItem.id == checkedItem.id) {
                                            thread {
                                                list[index] = checkableItem.copy(isChecked = isChecked)
                                                NotesApp.notesDao.updateCheckableNoteItem(list[index])
                                                checkableNote.updateEditedTime()
                                                checkableNoteItems.value = list
                                            }
                                            return@forEachIndexed
                                        }
                                    }
                                }
                            },
                            onItemClick = {
                                onRemoveItemAccepted = {
                                    thread {
                                        val removedItem = checkableNoteItems.value[index]
                                        NotesApp.notesDao.deleteItem(removedItem)
                                        checkableNoteItems.value = checkableNoteItems.value.filter { it != removedItem }
                                        checkableNote.updateEditedTime()
                                    }
                                }
                                showRemoveItemDialog.value = true
                            }
                    )
                }
            }
        }

        val textColor = if (isSystemInDarkTheme()) {
            textPrimaryDark
        } else {
            textPrimaryLight
        }
        val focusManager = LocalFocusManager.current
        val textValue = remember { mutableStateOf(TextFieldValue("")) }
        TextField(
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    focusManager.clearFocus(true)
                    if (textValue.value.text.isEmpty()) {
                        return@KeyboardActions
                    }
                    thread {
                        var newItem = CheckableItem(noteId = checkableNote.note.id!!, text = textValue.value.text)
                        newItem = newItem.copy(id = NotesApp.notesDao.insertCheckableNoteItem(item = newItem))
                        val newItems = checkableNoteItems.value + listOf(newItem)
                        textValue.value = TextFieldValue("")
                        checkableNoteItems.value = newItems
                        checkableNote.updateEditedTime()
                    }
                }),
                textStyle = TextStyle(
                        color = textColor,
                        fontSize = 23.sp,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Start
                ),
                backgroundColor = Color.Transparent,
                placeholder = {
                    Text(
                            text = "Вввод нового элемента",
                            modifier = Modifier
                                    .wrapContentHeight()
                                    .alpha(0.5f),
                            color = textColor,
                            fontSize = 20.sp,
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
                .padding(start = 12.dp, top = 8.dp, end = 8.dp)
                .constrainAs(newItemFiled) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
        )
    }
}

@Composable
fun CheckableItemCell(item: CheckableItem, onCheckedChanged: (CheckableItem, Boolean) -> Unit, onItemClick: () -> Unit) {
    ConstraintLayout(
            modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
    ) {
        val textColor = if (isSystemInDarkTheme()) textSecondaryDark else textSecondaryLight
        val checkmarkColor = if (isSystemInDarkTheme()) backgroundPrimaryDark else backgroundPrimaryLight
        val (icon, text) = createRefs()

        Checkbox(
                colors = CheckboxDefaults.colors(
                        checkedColor = textColor,
                        uncheckedColor = textColor,
                        checkmarkColor = checkmarkColor
                ),
                checked = item.isChecked,
                onCheckedChange = { v ->
                    onCheckedChanged.invoke(item, v)
                },
                modifier = Modifier
                        .constrainAs(icon) {
                            start.linkTo(parent.start, margin = 14.dp)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        })

        ClickableText(
                text = buildAnnotatedString {
                    withStyle(
                            style = SpanStyle(fontSize = 22.sp, fontFamily = FontFamily.SansSerif, color = textColor)
                    ) {
                        if (item.isChecked) {
                            withStyle(style = SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                                append(item.text)
                            }
                        } else {
                            append(item.text)
                        }
                    }
                },
                modifier = Modifier
                        .constrainAs(text) {
                            start.linkTo(icon.end, margin = 14.dp)
                            top.linkTo(parent.top, margin = 8.dp)
                            bottom.linkTo(parent.bottom, margin = 8.dp)
                            end.linkTo(parent.end)
                            width = fillToConstraints
                        },
                onClick = {
                    onItemClick.invoke()
                }
        )
    }
}