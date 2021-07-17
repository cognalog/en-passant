package model

import model.Color.Color

/**
 * The Bishop piece.
 *
 * @see [[https://en.wikipedia.org/wiki/Bishop_(chess)]]
 * @param color    the color of the Bishop.
 * @param hasMoved whether the Bishop has moved (likely unused).
 */
case class Bishop(override val color: Color, override val hasMoved: Boolean = false) extends Piece {
  override def updateHasMoved(): Piece = Bishop(color, hasMoved = true)

  override def getLegalMoves(currentSquare: Square, board: Board): Set[Square] = {
    getAvailableLinearSquares(currentSquare, board, _.changeFile(1).changeRank(1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeFile(-1).changeRank(1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeFile(1).changeRank(-1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeFile(-1).changeRank(-1))
  }

  /**
   * @return the 1-character short name for this piece.
   */
  override def shortName: Char = 'B'
}
