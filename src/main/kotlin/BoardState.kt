import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import java.awt.image.BufferedImage

class BoardState {
    val mainImage = mutableStateOf(BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB))
    val hoverOffset = mutableStateOf(Offset.Zero)
    val hoverPieceLocation = mutableStateOf(PieceLocation.INVALID_PIECE_LOCATION)
    val size = mutableStateOf(Size.Zero)
}