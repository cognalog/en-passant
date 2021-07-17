package model

import model.Color.Color

/**
 * A Queen piece.
 *
 * @see [[https://en.wikipedia.org/wiki/Queen_(chess)]]
 * @param color    the color of this Queen.
 * @param hasMoved whether this Queen has moved (likely unused).
 */
case class Queen(override val color: Color, override val hasMoved: Boolean = false) extends Piece {
  override def updateHasMoved(): Piece = Queen(color, hasMoved = true)

  override def getLegalMoves(currentSquare: Square, board: Board): Set[Square] = {
    getAvailableLinearSquares(currentSquare, board, _.changeFile(1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeFile(-1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeRank(1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeRank(-1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeFile(1).changeRank(1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeFile(-1).changeRank(1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeFile(1).changeRank(-1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeFile(-1).changeRank(-1))
  }

  /**
   * @return the 1-character short name for this piece.
   */
  override def shortName: Char = 'Q'
}
