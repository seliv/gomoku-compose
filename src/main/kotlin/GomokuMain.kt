import androidx.compose.desktop.AppWindowAmbient
import androidx.compose.desktop.ComposeWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.material.Surface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.gesture.ExperimentalPointerInput
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize
import example.imageviewer.view.*
import java.awt.Cursor

fun main() = Window {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Column {
            Button(onClick = {
                text = "Hello, Desktop!"
            }) {
                Text(text)
            }

            Board(BoardModel())
        }
    }
}

@Composable
private fun Board(
    boardModel: BoardModel
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Draggable(
            onDrag = boardModel.dragHandler,
            modifier = Modifier.fillMaxSize()
        ) {
            Zoomable(
                onScale = boardModel.scaleHandler,
                modifier = Modifier.fillMaxSize()
            ) {
                drawBoardInternal(boardModel, AppWindowAmbient.current!!.window)
            }
        }
    }
}

const val SNAP_OFFSET = 13f

@OptIn(ExperimentalPointerInput::class)
@Composable
private fun drawBoardInternal(
    boardModel: BoardModel,
    window: ComposeWindow
) {
    val hoverOffset = remember { mutableStateOf(Offset.Zero) }
    val hoverPieceLocation = remember { mutableStateOf(PieceLocation.INVALID_PIECE_LOCATION) }
    val size = remember { mutableStateOf(Size.Zero) }
    Canvas(modifier = Modifier
        .fillMaxSize()
        .onSizeChanged {
            size.value = it.toSize()
        }
        .pointerMoveFilter(
            onMove = {
                hoverOffset.value = it
                false
            }
        )
        .pointerInput {
            forEachGesture {
                handlePointerInput {
                    val down = awaitFirstDown()
                    val x = down.current.position.x
                    val y = down.current.position.y
                    println("touch x = ${x}")
                    println("touch.y = ${y}")
                }
            }
        }
    ) {
        val maxX = 1L + (size.value.width / DEFAULT_GRID_SIZE / 2).toInt()
        val maxY = 1L + (size.value.height / DEFAULT_GRID_SIZE / 2).toInt()
        val minX = -maxX
        val minY = -maxY

        var cursor = Cursor.getDefaultCursor()

        val offset0 = centerTranslate(boardModel.scaleHandler.translate(Offset(0.0f, 0.0f)), size.value)
        val offset1 = centerTranslate(boardModel.scaleHandler.translate(Offset(1.0f, 0.0f)), size.value)

        hoverPieceLocation.value = PieceLocation.INVALID_PIECE_LOCATION
        // Skipping grid display if we can't click a point precisely enough
        if (offset1.x - offset0.x > SNAP_OFFSET + SNAP_OFFSET) {
            for (xd in minX..maxX) {
                for (yd in minY..maxY) {
                    val offset = boardModel.dragHandler.translate(
                        centerTranslate(
                            boardModel.scaleHandler.translate(Offset(xd.toFloat(), yd.toFloat())), size.value
                        )
                    )
                    if (
                        (offset.x - hoverOffset.value.x) * (offset.x - hoverOffset.value.x) +
                        (offset.y - hoverOffset.value.y) * (offset.y - hoverOffset.value.y) <=
                        (SNAP_OFFSET * SNAP_OFFSET)
                    ) {
                        val pieceLocation = PieceLocation(xd, yd)
                        if (!boardModel.pieces.containsKey(pieceLocation)) {
                            cursor = Cursor(Cursor.HAND_CURSOR)
                            drawCircle(Color.LightGray, 10f, offset)
                            hoverPieceLocation.value = pieceLocation
                        }
                    }
                    drawCircle(Color.Black, 2.0f, offset)
                }
            }
        }
        if (window.cursor.type != cursor.type) {
            window.cursor = cursor
        }

        for (piece in boardModel.pieces) {
            drawCircle(piece.value.color, 15.0f,
                boardModel.dragHandler.translate(
                    centerTranslate(
                        boardModel.scaleHandler.translate(Offset(piece.key.x.toFloat(), piece.key.y.toFloat())), size.value
                    )
                )
            )
        }
    }
}

const val DEFAULT_GRID_SIZE = 50

private fun centerTranslate(offset: Offset, size: Size): Offset {
    return Offset((size.width / 2.0 + offset.x * DEFAULT_GRID_SIZE).toFloat(), (size.height / 2.0 + offset.y * DEFAULT_GRID_SIZE).toFloat())
}
