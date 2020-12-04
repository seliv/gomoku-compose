package example.imageviewer.view

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.rawDragGestureFilter
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import example.imageviewer.core.EventLocker

@Composable
fun Draggable(
    onDrag: DragHandler,
    modifier: Modifier = Modifier,
    children: @Composable() () -> Unit
) {
    Surface(
        color = Color.Transparent,
        modifier = modifier.rawDragGestureFilter(
            dragObserver = onDrag,
            canStartDragging = { true }
        )
    ) {
        children()
    }
}

class DragHandler : DragObserver {

    private val amount = mutableStateOf(Point(0f, 0f))
    private val distance = mutableStateOf(Point(0f, 0f))
    private val locker: EventLocker = EventLocker()

    fun getAmount(): Point {
        return amount.value
    }

    fun getDistance(): Point {
        return distance.value
    }

    override fun onStart(downPosition: Offset) {
        distance.value = Point(Offset.Zero)
        locker.unlock()
    }

    override fun onStop(velocity: Offset) {
        distance.value = Point(Offset.Zero)
        locker.unlock()
    }

    override fun onCancel() {
        distance.value = Point(Offset.Zero)
        locker.lock()
    }

    override fun onDrag(dragDistance: Offset): Offset {
        if (locker.isLocked()) {
            val dx = dragDistance.x
            val dy = dragDistance.y

            distance.value = Point(distance.value.x + dx, distance.value.y + dy)
            amount.value = Point(amount.value.x + dx, amount.value.y + dy)

            return dragDistance
        }

        return Offset.Zero
    }

    fun translate(offset: Offset): Offset {
        return Offset(offset.x + amount.value.x, offset.y + amount.value.y)
    }
}

class Point {
    var x: Float = 0f
    var y: Float = 0f
    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
    constructor(point: Offset) {
        this.x = point.x
        this.y = point.y
    }
    fun setAttr(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
}
