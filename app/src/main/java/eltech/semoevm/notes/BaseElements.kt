package eltech.semoevm.notes

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import eltech.semoevm.notes.ui.*

@Composable
fun AcceptDialog(
    showState: State<Boolean>,
    textStr: String,
    acceptButtonText: String,
    dismissButtonText: String,
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    if (showState.value) {
        val dialogWidth = (LocalContext.current.getScreenWidthDp() * 0.8).dp
        val backgroundColor = if (isSystemInDarkTheme()) backgroundPrimaryElevatedDark else backgroundPrimaryElevatedLight
        val textColor = if (isSystemInDarkTheme()) textPrimaryDark else textPrimaryLight

        androidx.compose.ui.window.Dialog(
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false),
            onDismissRequest = { onDismiss.invoke() }) {
            ConstraintLayout(
                modifier = Modifier
                    .background(backgroundColor)
                    .width(dialogWidth)
            ) {
                val (text, dismissButton, acceptButton) = createRefs()

                Text(
                    text = textStr, modifier = Modifier.constrainAs(text) {
                        start.linkTo(parent.start, margin = 20.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                        top.linkTo(parent.top, margin = 20.dp)
                        width = Dimension.fillToConstraints
                    }, fontSize = 20.sp, fontFamily = FontFamily.SansSerif, color = textColor
                )

                ButtonWithCustomClickHandlers(
                    elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
                    modifier = Modifier.constrainAs(dismissButton) {
                        top.linkTo(acceptButton.top)
                        end.linkTo(acceptButton.start)
                        bottom.linkTo(acceptButton.bottom)
                        width = Dimension.preferredWrapContent
                        height = Dimension.preferredWrapContent
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor),
                    onClick = {
                        onDismiss.invoke()
                    }
                ) {
                    Text(
                        text = dismissButtonText,
                        fontSize = 14.sp,
                        color = textColor
                    )
                }

                ButtonWithCustomClickHandlers(
                    elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
                    modifier = Modifier.constrainAs(acceptButton) {
                        top.linkTo(text.bottom, 20.dp)
                        end.linkTo(parent.end, 10.dp)
                        bottom.linkTo(parent.bottom, 10.dp)
                        width = Dimension.preferredWrapContent
                        height = Dimension.preferredWrapContent
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor),
                    onClick = {
                        onAccept.invoke()
                    }
                ) {
                    androidx.compose.material.Text(
                        text = acceptButtonText,
                        fontSize = 14.sp,
                        color = greenLight
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
fun ButtonWithCustomClickHandlers(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionState: InteractionState = remember { InteractionState() },
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val contentColor by colors.contentColor(enabled)
    Surface(
        shape = shape,
        color = colors.backgroundColor(enabled).value,
        contentColor = contentColor.copy(alpha = 1f),
        border = border,
        elevation = elevation?.elevation(enabled, interactionState)?.value ?: 0.dp,
        modifier = modifier.combinedClickable(
            onClick = onClick,
            onDoubleClick = onDoubleClick,
            onLongClick = onLongClick,
            enabled = enabled,
            role = Role.Button,
            interactionState = interactionState,
            indication = null
        )
    ) {
        Providers(LocalContentAlpha provides contentColor.alpha) {
            ProvideTextStyle(
                value = MaterialTheme.typography.button
            ) {
                Row(
                    Modifier
                        .defaultMinSizeConstraints(
                            minWidth = ButtonDefaults.MinWidth,
                            minHeight = ButtonDefaults.MinHeight
                        )
                        .indication(interactionState, rememberRipple())
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
        }
    }
}

@Composable
fun DialogWithTwoButtons(
    firstText: String,
    secondText: String,
    onFirstClicked: () -> Unit,
    onSecondClicked: () -> Unit,
    onDismiss: () -> Unit,
    isFirstButtonVisible: Boolean = true
) {
    Dialog(
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
        onDismissRequest = { onDismiss.invoke() }) {
        val width = (LocalContext.current.getScreenWidthDp() * 0.8).dp
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
            if (isFirstButtonVisible) {
                Button(
                    modifier = Modifier
                        .height(50.dp)
                        .width(width),
                    colors = ButtonDefaults.buttonColors(backgroundColor),
                    onClick = onFirstClicked
                ) {
                    Text(
                        text = firstText,
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
            }

            Button(
                modifier = Modifier
                    .height(50.dp)
                    .width(width)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor),
                onClick = onSecondClicked
            ) {
                Text(
                    text = secondText,
                    fontSize = 22.sp,
                    color = textColor
                )
            }
        }
    }
}