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
    Left("Unimplemented")
  }

  def isLegalMove(start: Square, dest: Square): Either[String, Unit] = {
    if (!pieces.contains(start)) {
      return Left(f"There's no piece at ${start.toString}")
    }
    val piece = pieces(start)
    if (!piece.isColor(turnColor)) {
      return Left(
        f"The piece at ${start.toString} is ${piece.color}, and it is $turnColor's turn.'"
      )
    }
    if (!piece.getLegalMoves(start, this).contains(dest)) {
      return Left(
        f"The ${piece.getClass.getSimpleName} at $start cannot legally move to $dest"
      )
    }
    Right()
  }

  def pieceAt(square: Square): Option[Piece] = {
    pieces.get(square)
  }
}
