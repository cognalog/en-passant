package enpassant

object ChessUtils {
  
  def getPieceAt(square: String, fen: String): Option[String] = {
    if (fen.isEmpty) return None
    if (square.length != 2) return None
    
    val (file, rank) = getSquareCoordinates(square)
    if (file < 0 || file > 7 || rank < 0 || rank > 7) return None
    
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
    if (square.length < 2) return (-1, -1)
    
    val fileChar = square.charAt(0)
    val rankChar = square.charAt(1)
    
    if (fileChar < 'a' || fileChar > 'h') return (-1, -1)
    if (!rankChar.isDigit || rankChar < '1' || rankChar > '8') return (-1, -1)
    
    val file = fileChar - 'a'
    val rank = 8 - rankChar.asDigit
    (file, rank)
  }
}