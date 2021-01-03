package model

import model.Color.Color

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