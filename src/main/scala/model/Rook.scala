package model

import model.Color.Color

/** A Rook Piece.
  *
  * @see
  *   [[https://en.wikipedia.org/wiki/Rook_(chess)]]
  * @param color
  *   the color of this Rook.
  * @param hasMoved
  *   whether this Rook has moved.
  */
case class Rook(
    override val color: Color,
    override val hasMoved: Boolean = false
) extends Piece {
  override def updateHasMoved(): Piece = Rook(color, hasMoved = true)

  override def getLegalMoves(currentSquare: Square, board: Board): Set[Move] = {
    (getAvailableLinearSquares(currentSquare, board, _.changeFile(1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeFile(-1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeRank(1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeRank(-1)))
      .map(NormalMove(currentSquare, _, this))
  }

  /** @return
    *   the 1-character short name for this piece.
    */
  override def shortName: Char = 'R'

  override val canMateWithKing: Boolean = true

  override val pointValue: Int = 5

  override def getCaptures(currentSquare: Square, board: Board): Set[Move] =
    getLegalMoves(currentSquare, board)
}
