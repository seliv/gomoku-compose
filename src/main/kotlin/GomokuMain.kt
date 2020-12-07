import androidx.compose.desktop.AppWindowAmbient
import androidx.compose.desktop.ComposeWindow
import androidx.compose.desktop.Window
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.ui.gesture.ExperimentalPointerInput
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import example.imageviewer.view.*
import java.awt.Cursor
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

fun main() = Window {
    var text by remember { mutableStateOf("Hello, World!") }
    val boardModel = BoardModel()

    MaterialTheme {
        Column {
            Button(onClick = {
                text = "Hello, Desktop!"
            }) {
                Text(text)
            }

            Board(boardModel)

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                Text(
                    text = boardModel.boardState.legendText.value,
                    color = boardModel.boardState.legendColor.value,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                    style = MaterialTheme.typography.body1
                )
            }
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
            .clickable(
                onClick = {
                    if (PieceLocation.INVALID_PIECE_LOCATION != boardModel.boardState.hoverPieceLocation.value) {
                        boardModel.makeTurn(boardModel.boardState.hoverPieceLocation.value)
                    }
                },
                onLongClick = {
                    boardModel.scaleHandler.onScale(1.2f)
                },
                onDoubleClick = {
                    boardModel.scaleHandler.onScale(0.8f)
                }
            )
    )
}

fun toByteArray(bitmap: BufferedImage) : ByteArray {
    val baos = ByteArrayOutputStream()
    ImageIO.write(bitmap, "png", baos)
    return baos.toByteArray()
}
