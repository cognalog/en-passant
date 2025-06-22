package enpassant

object ChessUtils {
  
  def getPieceAt(square: String, fen: String): Option[String] = {
    if (fen.isEmpty) return None
    
    val (file, rank) = getSquareCoordinates(square)
    
    val fenParts = fen.split(" ")
    if (fenParts.isEmpty) return None
    
    val rows = fenParts(0).split("/")
    if (rank < 0 || rank >= rows.length) return None
    
    var currentFile = 0
    for (char <- rows(rank)) {
      if (char.isDigit) {
        currentFile += char.asDigit
      } else {
        if (currentFile == file) {
          val color = if (char.isUpper) "w" else "b"
          val piece = char.toLower
          return Some(s"$color$piece")
        }
        currentFile += 1
      }
    }
    None
  }
  
  def getSquareCoordinates(square: String): (Int, Int) = {
    val file = square.charAt(0) - 'a'
    val rank = 8 - square.charAt(1).asDigit
    (file, rank)
  }
}