package model

import model.Color.Color

/**
 * A Rook Piece.
 *
 * @see [[https://en.wikipedia.org/wiki/Rook_(chess)]]
 * @param color    the color of this Rook.
 * @param hasMoved whether this Rook has moved.
 */
case class Rook(override val color: Color, override val hasMoved: Boolean = false) extends Piece {
  override def updateHasMoved(): Piece = Rook(color, hasMoved = true)

  override def getLegalMoves(currentSquare: Square, board: StandardBoard): Set[Square] = {
    getAvailableLinearSquares(currentSquare, board, _.changeFile(1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeFile(-1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeRank(1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeRank(-1))
  }

  /**
   * @return the 1-character short name for this piece.
   */
  override def shortName: Char = 'R'
}
