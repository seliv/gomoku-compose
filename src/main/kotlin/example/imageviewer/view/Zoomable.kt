package example.imageviewer.view

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.material.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.focus
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(
    ExperimentalKeyInput::class,
    ExperimentalFocus::class
)
@Composable
fun Zoomable(
    onScale: ScaleHandler,
    modifier: Modifier = Modifier,
    children: @Composable() () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    Surface(
        color = Color.Transparent,
        modifier = modifier.shortcuts {
            on(Key.I) {
                onScale.onScale(1.2f)
            }
            on(Key.O) {
                onScale.onScale(0.8f)
            }
            on(Key.R) {
                onScale.resetFactor()
            }
        }
        .focusRequester(focusRequester)
        .focus()
        .clickable(indication = null) { focusRequester.requestFocus() }
    ) {
        children()
    }
}
