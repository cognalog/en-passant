package model

import model.Color.Color

/** A Queen piece.
  *
  * @see
  *   [[https://en.wikipedia.org/wiki/Queen_(chess)]]
  * @param color
  *   the color of this Queen.
  * @param hasMoved
  *   whether this Queen has moved (likely unused).
  */
case class Queen(
    override val color: Color,
    override val hasMoved: Boolean = false
) extends Piece {
  override def updateHasMoved(): Piece = Queen(color, hasMoved = true)

  override def getLegalMoves(currentSquare: Square, board: Board): Set[Move] = {
    (getAvailableLinearSquares(currentSquare, board, _.changeFile(1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeFile(-1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeRank(1)) ++
      getAvailableLinearSquares(currentSquare, board, _.changeRank(-1)) ++
      getAvailableLinearSquares(
        currentSquare,
        board,
        _.changeFile(1).changeRank(1)
      ) ++
      getAvailableLinearSquares(
        currentSquare,
        board,
        _.changeFile(-1).changeRank(1)
      ) ++
      getAvailableLinearSquares(
        currentSquare,
        board,
        _.changeFile(1).changeRank(-1)
      ) ++
      getAvailableLinearSquares(
        currentSquare,
        board,
        _.changeFile(-1).changeRank(-1)
      ))
      .map(NormalMove(currentSquare, _, this))
  }

  /** @return
    *   the 1-character short name for this piece.
    */
  override def shortName: Char = 'Q'

  override val canMateWithKing: Boolean = true

  override val pointValue: Int = 9

  override def getCaptures(currentSquare: Square, board: Board): Set[Move] =
    getLegalMoves(currentSquare, board)
}
