package model

import model.Color.Color

/**
 * A pawn piece
 *
 * @see [[https://en.wikipedia.org/wiki/Pawn_(chess)]]
 * @param color    the color of this Pawn.
 * @param hasMoved whether this Pawn has moved.
 */
case class Pawn(
    override val color: Color,
    override val hasMoved: Boolean = false
) extends Piece {

  override def getLegalMoves(
                              currentSquare: Square,
                              board: StandardBoard
                            ): Set[Square] = {
    (getForwardMoves(currentSquare, board)
      ++ getCaptures(currentSquare, board)).filter(board.isInBounds)
  }

  private def getForwardMoves(currentSquare: Square, board: StandardBoard) = {
    (Set(changeRankByColor(currentSquare, 2)).filter(_ =>
      board.pieceAt(changeRankByColor(currentSquare, 1)).isEmpty && !hasMoved
    ) ++ Set(
      changeRankByColor(currentSquare, 1)
    )).filter(board.pieceAt(_).isEmpty)
  }

  def getCaptures(currentSquare: Square, board: StandardBoard): Set[Square] = {
    Set(
      changeRankByColor(currentSquare, 1).changeFile(-1),
      changeRankByColor(currentSquare, 1).changeFile(1)
    ).filter(square =>
      board.enPassant.fold(false)(_ == square) || board
        .pieceAt(square)
        .fold(false)(!_.isColor(color))
    )
  }

  private def changeRankByColor(square: Square, delta: Int): Square = {
    val coefficient = if (isColor(Color.Black)) -1 else 1
    square.changeRank(coefficient * delta)
  }

  override def updateHasMoved(): Piece = Pawn(color, hasMoved = true)

  /**
   * NB: we use 'P' for printing pawns, but not for move notation per international standard.
   *
   * @return the 1-character short name for this piece.
   */
  override def shortName: Char = 'P'
}
