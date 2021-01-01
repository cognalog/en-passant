package model

import model.Color.Color

case class Pawn(
    override val color: Color,
    override val hasMoved: Boolean = false
) extends Piece {

  override def getLegalMoves(
      currentSquare: Square,
      board: Board
  ): Set[Square] = {
    (Set(currentSquare.changeRank(2)).filter(_ => !hasMoved) ++ Set(
      currentSquare.changeRank(1)
    ).filter(board.pieceAt(_).isEmpty) ++
      Set(
        currentSquare.changeFile(-1).changeRank(1),
        currentSquare.changeFile(1).changeRank(1)
      ).filter(square =>
        board.enPassant.fold(false)(_ == square) || board
          .pieceAt(square)
          .fold(false)(!_.isColor(color))
      )).filter(board.isInBounds)
  }

  override def updateHasMoved(): Piece = Pawn(color, hasMoved = true)
}
