package com.devshiv.ytchannel.utils

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.devshiv.ytchannel.R
import com.devshiv.ytchannel.ui.theme.AccentColor
import com.devshiv.ytchannel.ui.theme.PrimaryColor
import com.devshiv.ytchannel.ui.theme.PrimaryLightColor
import com.devshiv.ytchannel.utils.Utils.Companion.randomColor
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextField(
    value: String,
    modifier: Modifier = Modifier,
    semanticContentDescription: String = "",
    label: @Composable () -> Unit,
    textStyle: androidx.compose.ui.text.TextStyle,
    colors: TextFieldColors,
    isError: Boolean = false,
    error: String = "",
    onValueChange: (text: String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val showPassword = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        TextField(
            modifier = Modifier
                .background(color = PrimaryLightColor)
                .fillMaxWidth()
                .semantics { contentDescription = semanticContentDescription },
            value = value,
            onValueChange = onValueChange,
            label = label,
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            supportingText = {
                if (isError) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily(Font(R.font.main_font)),
                        modifier = Modifier.padding(start = 6.dp, top = 0.dp, bottom = 2.dp)
                    )
                }
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            singleLine = true,
            isError = isError,
            visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val (icon, iconColor) = if (showPassword.value) {
                    Pair(
                        Icons.Filled.Visibility,
                        Color.White
                    )
                } else {
                    Pair(Icons.Filled.VisibilityOff, Color.White)
                }

                IconButton(onClick = { showPassword.value = !showPassword.value }) {
                    Icon(
                        icon,
                        contentDescription = "Visibility",
                        tint = iconColor
                    )
                }
            },
            colors = colors,
            textStyle = textStyle
        )
    }
}

@Composable
fun LoadingDialog(
    cornerRadius: Dp = 14.dp,
    progressIndicatorColor: Color = PrimaryLightColor,
    progressIndicatorSize: Dp = 80.dp,
    showLoad: Boolean = false,
    onDismiss: () -> Unit
) {
    var showLoading by remember {
        mutableStateOf(showLoad)
    }

    LaunchedEffect(showLoad) {
        showLoading = showLoad
    }

    if (showLoading) {
        Dialog(
            onDismissRequest = {
                showLoading = false
                onDismiss()
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Column(
                modifier = Modifier
                    .size(200.dp)
                    .background(
                        color = AccentColor,
                        shape = RoundedCornerShape(cornerRadius)
                    ), // inner padding
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ProgressIndicatorLoading(
                    progressIndicatorSize = progressIndicatorSize,
                    progressIndicatorColor = progressIndicatorColor
                )

                // Gap between progress indicator and text
                Spacer(modifier = Modifier.height(32.dp))

                // Please wait text
                Text(
                    text = "Please wait...",
                    style = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily(Font(R.font.main_font))
                    )
                )
            }
        }
    }
}

@Composable
fun ProgressIndicatorLoading(
    progressIndicatorSize: Dp,
    progressIndicatorColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition()

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 600 // animation duration
            }
        ), label = ""
    )

    CircularProgressIndicator(
        progress = 1f,
        modifier = Modifier
            .size(progressIndicatorSize)
            .rotate(angle)
            .border(
                12.dp,
                brush = Brush.sweepGradient(
                    listOf(
                        Color.White, // add background color first
                        progressIndicatorColor.copy(alpha = 0.1f),
                        progressIndicatorColor
                    )
                ),
                shape = CircleShape
            ),
        strokeWidth = 1.dp,
        color = Color.White // Set background color
    )
}

@Composable
fun Dialog(
    cornerRadius: Dp = 14.dp,
    showDialog: Boolean = false,
    title: String = "",
    description: String = "",
    onClick: () -> Unit
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = {

            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            Column(
                modifier = Modifier
                    .width(250.dp)
                    .background(
                        color = AccentColor,
                        shape = RoundedCornerShape(cornerRadius)
                    ), // inner padding
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.app_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = title,
                    style = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily(Font(R.font.main_font))
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = description,
                    style = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily(Font(R.font.main_font))
                    ),
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                        contentColor = PrimaryColor
                    ),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        text = "Okay",
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.main_font)),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }

            }
        }
    }
}

@Composable
fun randomGradient(): Brush {
    val color1 = Color(randomColor())
    val color2 = Color(randomColor())

    return Brush.verticalGradient(listOf(color1, color2))
}

@Preview(showSystemUi = true)
@Composable
fun Preview() {
//    Dialog(showDialog = true, onClick = {})
    ProgressIndicatorLoading(progressIndicatorSize = 40.dp, progressIndicatorColor = Color.White)
}

@Composable
fun Timer(
    totalTime: Long,
    handleColor: Color,
    inactiveBarColor: Color,
    activeBarColor: Color,
    modifier: Modifier = Modifier,
    startTimer: Boolean = false,
    initialValue: Float = 1f,
    strokeWidth: Dp = 5.dp,
    textSize: Int = 14,
    curPage: Int = 0,
    onTimerCompleted: () -> Unit
) {
    // create variable for
    // size of the composable
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    // create variable for value
    var value by remember {
        mutableStateOf(initialValue)
    }
    // create variable for current time
    var currentTime by remember {
        mutableStateOf(totalTime)
    }

    LaunchedEffect(key1 = curPage, block = {
        currentTime = totalTime
    })

    // create variable for isTimerRunning
    var isTimerRunning by remember {
        mutableStateOf(startTimer)
    }

    LaunchedEffect(key1 = currentTime, key2 = isTimerRunning) {
        if (currentTime > 0 && isTimerRunning) {
            delay(100L)
            currentTime -= 100L
            value = currentTime / totalTime.toFloat()
        } else {
            onTimerCompleted()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .onSizeChanged {
                size = it
            }
    ) {
        // draw the timer
        Canvas(modifier = modifier) {
            // draw the inactive arc with following parameters
            drawArc(
                color = inactiveBarColor, // assign the color
                startAngle = -215f, // assign the start angle
                sweepAngle = 250f, // arc angles
                useCenter = false, // prevents our arc to connect at te ends
                size = Size(size.width.toFloat(), size.height.toFloat()),

                // to make ends of arc round
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            // draw the active arc with following parameters
            drawArc(
                color = activeBarColor, // assign the color
                startAngle = -215f,  // assign the start angle
                sweepAngle = 250f * value, // reduce the sweep angle
                // with the current value
                useCenter = false, // prevents our arc to connect at te ends
                size = Size(size.width.toFloat(), size.height.toFloat()),

                // to make ends of arc round
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            // calculate the value from arc pointer position
            val center = Offset(size.width / 2f, size.height / 2f)
            val beta = (250f * value + 145f) * (PI / 180f).toFloat()
            val r = size.width / 2f
            val a = cos(beta) * r
            val b = sin(beta) * r
            // draw the circular pointer/ cap
            drawPoints(
                listOf(Offset(center.x + a, center.y + b)),
                pointMode = PointMode.Points,
                color = handleColor,
                strokeWidth = (strokeWidth * 3f).toPx(),
                cap = StrokeCap.Round  // make the pointer round
            )
        }
        // add value of the timer
        Text(
            text = (currentTime / 1000L).toString(),
            fontSize = textSize.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun AutoResizeText(
    text: String,
    fontSizeRange: FontSizeRange,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current,
) {
    var fontSizeValue by remember { mutableStateOf(fontSizeRange.max.value) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        maxLines = maxLines,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        style = style,
        fontSize = fontSizeValue.sp,
        onTextLayout = { it ->
            if (it.didOverflowHeight && !readyToDraw) {
                val nextFontSizeValue = fontSizeValue - fontSizeRange.step.value
                if (nextFontSizeValue <= fontSizeRange.min.value) {
                    // Reached minimum, set minimum font size and it's readToDraw
                    fontSizeValue = fontSizeRange.min.value
                    readyToDraw = true
                } else {
                    // Text doesn't fit yet and haven't reached minimum text range, keep decreasing
                    fontSizeValue = nextFontSizeValue
                }
            } else {
                // Text fits before reaching the minimum, it's readyToDraw
                readyToDraw = true
            }
        },
        modifier = modifier.drawWithContent { if (readyToDraw) drawContent() }
    )
}

data class FontSizeRange(
    val min: TextUnit,
    val max: TextUnit,
    val step: TextUnit = DEFAULT_TEXT_STEP,
) {
    init {
        require(min < max) { "min should be less than max, $this" }
        require(step.value > 0) { "step should be greater than 0, $this" }
    }

    companion object {
        private val DEFAULT_TEXT_STEP = 1.sp
    }
}

@Composable
fun MyDropDownMenu(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
                tint = Color.White
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            content()
        }
    }
}

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun NoInternetScreen(openFullDialogCustom: MutableState<Boolean>) {

    if (openFullDialogCustom.value) {

        // Dialog function
        Dialog(
            onDismissRequest = {
//                openFullDialogCustom.value = false
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false // experimental
            )
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    Image(
                        painter = painterResource(id = R.drawable.no_intrenet),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth(),

                        )

                    Spacer(modifier = Modifier.height(20.dp))
                    //.........................Text: title
                    Text(
                        text = "Whoops!!",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth(),
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.main_font)),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    //.........................Text : description
                    Text(
                        text = "No Internet connection was found. Check your connection or try again.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                            .fillMaxWidth(),
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily(Font(R.font.main_font)),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                    )
                    //.........................Spacer
                    Spacer(modifier = Modifier.height(24.dp))

                    val cornerRadius = 16.dp
                    val gradientColor = listOf(Color(0xFFff669f), Color(0xFFff8961))
//                    GradientButton(
//                        gradientColors = gradientColor,
//                        cornerRadius = cornerRadius,
//                        nameButton = "Try again",
//                        roundedCornerShape = RoundedCornerShape(topStart = 30.dp,bottomEnd = 30.dp),
//                        openFullDialogCustom
//                    )

                }

            }
        }

    }
}

@Composable
fun GradientButton(
    gradientColors: List<Color>,
    cornerRadius: Dp,
    nameButton: String,
    roundedCornerShape: RoundedCornerShape,
    openFullDialogCustom: MutableState<Boolean>
) {

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, end = 32.dp),
        onClick = {
            openFullDialogCustom.value = false
        },

        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(cornerRadius)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(colors = gradientColors),
                    shape = roundedCornerShape
                )
                .clip(roundedCornerShape)
                /*.background(
                    brush = Brush.linearGradient(colors = gradientColors),
                    shape = RoundedCornerShape(cornerRadius)
                )*/
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nameButton,
                fontFamily = FontFamily(Font(R.font.main_font)),
                fontSize = 20.sp,
                color = Color.White
            )
        }
    }
}