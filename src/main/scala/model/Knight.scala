package model

import model.Color.Color

case class Knight(override val color: Color, override val hasMoved: Boolean = false) extends Piece {
  override def updateHasMoved(): Piece = Knight(color, hasMoved = true)

  override def getLegalMoves(currentSquare: Square, board: Board): Set[Square] = {
    val moves = for {
      x <- Set(1, -1)
      y <- Set(2, -2)
    } yield Set(currentSquare.changeFile(x).changeRank(y), currentSquare.changeFile(y).changeRank(x))
    moves.flatten.filter(board.isInBounds)
  }
}
