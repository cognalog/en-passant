package model

// Files are numerical for easier processing. A-file is 1.
case class Square(file: Int, rank: Int)

object Board {
  val RowAndColSize = 8
}

class Board {
  def isLegalMove(start: Square, dest: Square): Either[String, Unit] = {
    if (!pieces.contains(start)) {
      return Left(f"There's no piece at ${start.toString}")
    }
    val piece = pieces(start)
    if (piece.color != turn) {
      return Left(f"The piece at ${start.toString} is ${piece.color}, and it is $turn's turn.'")
    }
    //TODO(hinderson): incorporate other rules
    Right()
  }

  def swapTurns(): Unit = {
    if (turn == Color.White) {
      turn = Color.Black
    } else {
      turn = Color.White
    }
  }

  def move(start: Square, dest: Square): Unit = {
    val legalMove = isLegalMove(start, dest)
    if (!legalMove.isRight) {
      throw new IllegalArgumentException(f"${start.toString} --> ${dest.toString} is not legal: ${legalMove.left}")
    }
    swapTurns()
  }

  private var pieces = Map[Square, Piece]()
  //TODO(hinderson): revisit storing kings explicitly here.
  private var whiteKing = Square(4, 1)
  private var blackKing = Square(4, 8)
  private var turn = Color.White
}
