import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import example.imageviewer.view.DragHandler
import example.imageviewer.view.ScaleHandler
import java.awt.RenderingHints
import java.awt.image.BufferedImage

class BoardModel {
    val boardState: BoardState = BoardState()

    internal val pieces: MutableMap<PieceLocation, PieceColor> = mutableMapOf()
    val scaleHandler: ScaleHandler = ScaleHandler(this)
    val dragHandler: DragHandler = DragHandler(this)

    init {
        pieces[PieceLocation(1, 1)] = PieceColor.WHITE
        pieces[PieceLocation(-1, -1)] = PieceColor.BLACK

        pieces[PieceLocation(3, 2)] = PieceColor.WHITE
        pieces[PieceLocation(7, -4)] = PieceColor.WHITE
        pieces[PieceLocation(5, 4)] = PieceColor.WHITE
        pieces[PieceLocation(-2, 3)] = PieceColor.WHITE
        pieces[PieceLocation(-3, 5)] = PieceColor.WHITE
        pieces[PieceLocation(-7, -2)] = PieceColor.WHITE

        pieces[PieceLocation(-3, -3)] = PieceColor.BLACK
        pieces[PieceLocation(-4, 1)] = PieceColor.BLACK
        pieces[PieceLocation(-1, 2)] = PieceColor.BLACK
        pieces[PieceLocation(5, 6)] = PieceColor.BLACK
        pieces[PieceLocation(6, -7)] = PieceColor.BLACK
        pieces[PieceLocation(2, -1)] = PieceColor.BLACK
    }

    fun paintState() {
        boardState.mainImage.value = paintState(
            boardState.size.value.width.toInt(),
            boardState.size.value.height.toInt()
        )
        boardState.mainImage.value
    }

    private fun paintState(width: Int, height: Int): BufferedImage {
        if ((width <= 0) || (height <= 0)) {
            return BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        }
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.createGraphics()
        graphics.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )
        graphics.setRenderingHint(
            RenderingHints.KEY_STROKE_CONTROL,
            RenderingHints.VALUE_STROKE_PURE
        )

        val maxX = 1L + width / DEFAULT_GRID_SIZE / 2
        val maxY = 1L + height / DEFAULT_GRID_SIZE / 2
        val minX = -maxX
        val minY = -maxY

        val size = Size(width.toFloat(), height.toFloat())
        val offset0 = centerTranslate(scaleHandler.translate(Offset(0.0f, 0.0f)), size)
        val offset1 = centerTranslate(scaleHandler.translate(Offset(1.0f, 0.0f)), size)

        boardState.hoverPieceLocation.value = PieceLocation.INVALID_PIECE_LOCATION
        // Skipping grid display if we can't click a point precisely enough
        if (offset1.x - offset0.x > SNAP_OFFSET + SNAP_OFFSET) {
            for (xd in minX..maxX) {
                for (yd in minY..maxY) {
                    val offset = dragHandler.translate(
                        centerTranslate(
                            scaleHandler.translate(Offset(xd.toFloat(), yd.toFloat())), size
                        )
                    )
                    if (
                        (offset.x - boardState.hoverOffset.value.x) * (offset.x - boardState.hoverOffset.value.x) +
                        (offset.y - boardState.hoverOffset.value.y) * (offset.y - boardState.hoverOffset.value.y) <=
                        (SNAP_OFFSET * SNAP_OFFSET)
                    ) {
                        val pieceLocation = PieceLocation(xd, yd)
                        if (!pieces.containsKey(pieceLocation)) {
                            graphics.color = java.awt.Color.LIGHT_GRAY
                            graphics.paint = graphics.color
                            graphics.fillOval(offset.x.toInt() - 10, offset.y.toInt() - 10, 20, 20)
                            boardState.hoverPieceLocation.value = pieceLocation
                        }
                    }
                    graphics.color = java.awt.Color.BLACK
                    graphics.paint = graphics.color
                    graphics.fillRect(offset.x.toInt() - 1, offset.y.toInt() - 1, 2, 2)
                }
            }
        }

        for (piece in pieces) {
            val offset = dragHandler.translate(
                centerTranslate(
                    scaleHandler.translate(Offset(piece.key.x.toFloat(), piece.key.y.toFloat())), size
                )
            )
            graphics.color = java.awt.Color(piece.value.color.toArgb())
            graphics.paint = graphics.color
            graphics.fillOval(offset.x.toInt() - 15, offset.y.toInt() - 15, 30, 30)
        }

        return image
    }

    fun minX(): Long {
        return pieces.keys.stream().mapToLong { pl -> pl.x }.min().orElse(0)
    }

    fun maxX(): Long {
        return pieces.keys.stream().mapToLong { pl -> pl.x }.max().orElse(0)
    }

    fun minY(): Long {
        return pieces.keys.stream().mapToLong { pl -> pl.y }.min().orElse(0)
    }

    fun maxY(): Long {
        return pieces.keys.stream().mapToLong { pl -> pl.y }.max().orElse(0)
    }
}

enum class PieceColor(val color: Color) {
    BLACK(Color.Blue), WHITE(Color.Red)
}

data class PieceLocation(val x: Long, val y: Long) {
    companion object {
        @Stable
        val INVALID_PIECE_LOCATION = PieceLocation(Long.MAX_VALUE, Long.MAX_VALUE)
    }
}

const val DEFAULT_GRID_SIZE = 50

private fun centerTranslate(offset: Offset, size: Size): Offset {
    return Offset(
        (size.width / 2.0 + offset.x * DEFAULT_GRID_SIZE).toFloat(),
        (size.height / 2.0 + offset.y * DEFAULT_GRID_SIZE).toFloat()
    )
}
