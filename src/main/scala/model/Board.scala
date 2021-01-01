package model

import model.Color.Color

// Files are numerical for easier processing. A-file is 1.
case class Square(file: Int, rank: Int) {
  def changeFile(delta: Int): Square = {
    Square(file + delta, rank)
  }

  def changeRank(delta: Int): Square = {
    Square(file, rank + delta)
  }
}

object Board {
  private val RankAndFileMin = 1
  private val RankAndFileMax = 8
}

class Board(
    val pieces: Map[Square, Piece],
    val turnColor: Color = Color.White,
    val enPassant: Option[Square] = None
) {

  def isInBounds(square: Square): Boolean = {
    val bounds = Board.RankAndFileMin to Board.RankAndFileMax
    bounds.contains(square.file) && bounds.contains(square.rank)
  }

  def move(start: Square, dest: Square): Either[String, Board] = {
    checkLegalMove(start, dest)
    val piece = pieces(start).updateHasMoved()
    val nextPieces = pieces - start + (dest -> piece)
    val nextTurnColor =
      if (turnColor == Color.Black) Color.White else Color.Black
    // if it's a pawn that just moved 2 spaces, set the space behind it as en passant
    var nextEnPassant: Option[Square] = None
    val unused: Unit = piece match {
      case Pawn(Color.White, _) if dest.rank - start.rank == 2 =>
        nextEnPassant = Some(Square(start.file, start.rank + 1))
      case Pawn(Color.Black, _) if start.rank - dest.rank == 2 =>
        nextEnPassant = Some(Square(start.file, start.rank - 1))
      case _ => ()
    }
    Right(new Board(nextPieces, nextTurnColor, nextEnPassant))
    // TODO(hinderson): determine whether this turn's king is in check in new board state
  }

  def checkLegalMove(start: Square, dest: Square): Unit = {
    if (!pieces.contains(start)) {
      throw new IllegalStateException(f"There's no piece at ${start.toString}")
    }
    val piece = pieces(start)
    if (!piece.isColor(turnColor)) {
      throw new IllegalStateException(
        f"The piece at ${start.toString} is ${piece.color}, and it is $turnColor's turn.'"
      )
    }
    if (!isInBounds(dest)) {
      throw new IllegalArgumentException(f"${dest.toString} is not a valid space on the board. Acceptable range for " +
        f"rank and file is are ${Board.RankAndFileMin} to ${Board.RankAndFileMax}")
    }
  }

  def pieceAt(square: Square): Option[Piece] = {
    pieces.get(square)
  }
}
