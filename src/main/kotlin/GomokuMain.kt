import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.material.Surface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize
import example.imageviewer.view.*

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
                drawBoardInternal(boardModel)
            }
        }
    }
}

@Composable
private fun drawBoardInternal(
    boardModel: BoardModel,
) {
    val size = remember { mutableStateOf(Size.Zero) }
    Canvas(modifier = Modifier
        .fillMaxSize()
        .onSizeChanged {
            size.value = it.toSize()
        }
    ) {
        println("Canvas")
        val maxX = boardModel.maxX()
        val maxY = boardModel.maxY()
        val minX = boardModel.minX()
        val minY = boardModel.minY()

        if ((maxX - minX) * (maxY - minY) <= 10_000) {
            for (xd in minX..maxX) {
                for (yd in minY..maxY) {
                    drawCircle(Color.Black, 2.0f,
                        boardModel.dragHandler.translate(
                            centerTranslate(
                                boardModel.scaleHandler.translate(Offset(xd.toFloat(), yd.toFloat())), size.value
                            )
                        )
                    )
                }
            }
        }

        for (piece in boardModel.pieces) {
            drawCircle(piece.value.color, 10.0f,
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
