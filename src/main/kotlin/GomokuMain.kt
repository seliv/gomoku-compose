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
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.material.Surface
import androidx.compose.ui.gesture.ExperimentalPointerInput
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize
import example.imageviewer.view.*
import java.awt.Cursor
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

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
    Image(
        org.jetbrains.skija.Image.makeFromEncoded(
            toByteArray(boardModel.boardState.mainImage.value)
            ).asImageBitmap(),
        modifier = Modifier
        .fillMaxSize()
        .onSizeChanged {
            boardModel.boardState.size.value = it.toSize()
            boardModel.paintState()
        }
        .pointerMoveFilter(
            onMove = {
                boardModel.boardState.hoverOffset.value = it
                boardModel.paintState()
                val cursor =
                    if (PieceLocation.INVALID_PIECE_LOCATION == boardModel.boardState.hoverPieceLocation.value)
                        Cursor.getDefaultCursor() else Cursor(Cursor.HAND_CURSOR)

                if (window.cursor.type != cursor.type) {
                    window.cursor = cursor
                }

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
    )
}

fun toByteArray(bitmap: BufferedImage) : ByteArray {
    val baos = ByteArrayOutputStream()
    ImageIO.write(bitmap, "png", baos)
    return baos.toByteArray()
}
