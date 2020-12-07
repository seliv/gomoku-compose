package example.imageviewer.view

import BoardModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.gesture.RawScaleObserver
import androidx.compose.ui.gesture.doubleTapGestureFilter
import androidx.compose.ui.gesture.rawScaleGestureFilter
import androidx.compose.ui.Modifier
import androidx.compose.material.Surface

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.pow

@Composable
fun Scalable (
    onScale: ScaleHandler,
    modifier: Modifier = Modifier,
    children: @Composable() () -> Unit
) {
    Surface(
        color = Color.Transparent,
        modifier = modifier.rawScaleGestureFilter(
            scaleObserver = onScale,
            canStartScaling = { true }
        ).doubleTapGestureFilter(onDoubleTap = { onScale.resetFactor() }),
    ) {
        children()
    }
}

class ScaleHandler(private val model: BoardModel, private val maxFactor: Float = 5f, private val minFactor: Float = -10f) :
    RawScaleObserver {
    val factor = mutableStateOf(1f)

    fun resetFactor() {
        if (factor.value > minFactor)
            factor.value = minFactor
    }

    override fun onScale(scaleFactor: Float): Float {
        factor.value += scaleFactor - 1f

        if (maxFactor < factor.value) factor.value = maxFactor
        if (minFactor > factor.value) factor.value = minFactor

        model.boardState.hoverPieceLocation.value = PieceLocation.INVALID_PIECE_LOCATION
        model.boardState.hoverOffset.value = Offset.Zero
        model.paintState()

        return scaleFactor
    }

    fun translate(offset: Offset): Offset {
        val scale = factor.value.toDouble().pow(1.4).toFloat()
        return Offset(offset.x * scale, offset.y * scale)
    }
}