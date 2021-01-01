package model

import model.Color.Color

case class Rook(override val color: Color, override val hasMoved: Boolean = false) extends Piece {
  override def updateHasMoved(): Piece = Rook(color, hasMoved = true)

  override def getLegalMoves(currentSquare: Square, board: Board): Set[Square] = {
    getAvailableLinearSquares(currentSquare, board, _.changeFile(1)) ++
    getAvailableLinearSquares(currentSquare, board, _.changeFile(-1)) ++
    getAvailableLinearSquares(currentSquare, board, _.changeRank(1)) ++
    getAvailableLinearSquares(currentSquare, board, _.changeRank(-1))
  }
}
