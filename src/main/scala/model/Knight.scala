package model

import model.Color.Color

/** A Knight piece
  *
  * @see
  *   [[https://en.wikipedia.org/wiki/Knight_(chess)]]
  * @param color
  *   the color of the Knight.
  * @param hasMoved
  *   whether the Knight has moved (likely unused).
  */
case class Knight(
    override val color: Color,
    override val hasMoved: Boolean = false
) extends Piece {
  override def updateHasMoved(): Piece = Knight(color, hasMoved = true)

  override def getLegalMoves(currentSquare: Square, board: Board): Set[Move] = {
    val moves = for {
      x <- Set(1, -1)
      y <- Set(2, -2)
    } yield Set(
      currentSquare.changeFile(x).changeRank(y),
      currentSquare.changeFile(y).changeRank(x)
    )
    moves.flatten
      .filter(board.isInBounds)
      .filter(sq => board.pieceAt(sq).forall(!_.isColor(color)))
      .map(NormalMove(currentSquare, _))
  }

  /** @return
    *   the 1-character short name for this piece.
    */
  override def shortName: Char = 'N'

  override val canMateWithKing: Boolean = false

  override val pointValue: Int = 3

  override def getCaptures(currentSquare: Square, board: Board): Set[Move] =
    getLegalMoves(currentSquare, board)
}
