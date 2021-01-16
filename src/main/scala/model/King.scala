package model

import model.Color.Color

/**
 * The King piece.
 *
 * @see [[https://en.wikipedia.org/wiki/King_(chess)]]
 * @param color the color of this piece.
 * @param hasMoved whether the king has moved in a game.
 */
case class King(override val color: Color, override val hasMoved: Boolean = false) extends Piece {
  override def updateHasMoved(): Piece = King(color, hasMoved = true)

  override def getLegalMoves(currentSquare: Square,
                             board: Board): Set[Square] = {
    Set(
      currentSquare.changeRank(1), currentSquare.changeFile(1).changeRank(1), currentSquare.changeFile(1),
      currentSquare.changeFile(1).changeRank(-1), currentSquare.changeRank(-1),
      currentSquare.changeFile(-1).changeRank(-1), currentSquare.changeFile(-1),
      currentSquare.changeFile(-1).changeRank(1))
      .filter(board.isInBounds)
      .filter(sq => board.pieceAt(sq).forall(!_.isColor(color)))
  }
}
