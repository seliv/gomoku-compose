import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

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
    drawBoardInternal(boardModel)
}

@Composable
private fun drawBoardInternal(boardModel: BoardModel) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        var minX = boardModel.minX()
        var minY = boardModel.minY()
        var modelWidth = boardModel.maxX() - minX
        var modelHeight = boardModel.maxY() - minY
        if (modelWidth < 10) {
            modelWidth = 10
        }
        if (modelHeight < 10) {
            modelHeight = 10
        }
        modelWidth = modelWidth * 1200 / 1000
        modelHeight = modelHeight * 1200 / 1000
        minX -= modelWidth * 1000 / 10000
        minY -= modelHeight * 1000 / 10000

        val cellWidth = size.width / modelWidth
        val cellHeight = size.height / modelHeight

        if (modelWidth * modelHeight <= 10_000) {
            for (xd in boardModel.minX()..boardModel.maxX()) {
                for (yd in boardModel.minY()..boardModel.maxY()) {
                    val x = (xd - minX) * cellWidth
                    val y = (yd - minY) * cellHeight
                    drawCircle(Color.Black, 2.0f, Offset(x, y))
                }
            }
        }

        for (piece in boardModel.pieces) {
            val x = (piece.key.x - minX) * cellWidth
            val y = (piece.key.y - minY) * cellHeight
            drawCircle(piece.value.color, 10.0f, Offset(x, y))
        }
    }
}