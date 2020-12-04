import androidx.compose.ui.graphics.Color
import example.imageviewer.view.DragHandler
import example.imageviewer.view.ScaleHandler

class BoardModel {
    internal val pieces: MutableMap<PieceLocation, PieceColor> = mutableMapOf()
    val scaleHandler: ScaleHandler = ScaleHandler()
    val dragHandler: DragHandler = DragHandler()

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
}
