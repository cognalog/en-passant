package model

import model.Color.Color

object King {
  val standardNotation = "K"
}

/**
 * The King piece.
 *
 * @see [[https://en.wikipedia.org/wiki/King_(chess)]]
 * @param color    the color of this piece.
 * @param hasMoved whether the king has moved in a game.
 */
case class King(override val color: Color, override val hasMoved: Boolean = false, hasCastled: Boolean = false)
  extends Piece {
  override def updateHasMoved(): Piece = King(color, hasMoved = true)

  override def getLegalMoves(currentSquare: Square, board: Board): Set[Move] = {
    getNormalMoves(currentSquare, board) ++ getCastleMoves(currentSquare, board)
  }

  private def getCastleMoves(currentSquare: Square, board: Board): Set[Move] = {
    Set(Square(currentSquare.file - 2, currentSquare.rank), Square(currentSquare.file + 2, currentSquare.rank))
      .filter(board.isInBounds).map(CastleMove)
  }

  private def getNormalMoves(currentSquare: Square, board: Board): Set[Move] = {
    Set(currentSquare.changeRank(1), currentSquare.changeFile(1).changeRank(1), currentSquare.changeFile(1),
      currentSquare.changeFile(1).changeRank(-1), currentSquare.changeRank(-1),
      currentSquare.changeFile(-1).changeRank(-1), currentSquare.changeFile(-1),
      currentSquare.changeFile(-1).changeRank(1)).filter(board.isInBounds)
                                                 .filter(sq => board.pieceAt(sq).forall(!_.isColor(color)))
                                                 .map(NormalMove(currentSquare, _))
  }

  /**
   * @return the 1-character short name for this piece. */
  override def shortName: Char = 'K'

  override val canMateWithKing: Boolean = false

  override val pointValue: Int = Int.MaxValue

  override def getCaptures(currentSquare: Square, board: Board): Set[Move] = getLegalMoves(currentSquare, board)
    .filter {
      case NormalMove(_, _, _) => true
      case _ => false
    }
}
