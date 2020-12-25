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
    (getForwardMoves(currentSquare, board)
      ++ getCaptures(currentSquare, board)).filter(board.isInBounds)
  }

  private def getForwardMoves(currentSquare: Square, board: Board) = {
    (Set(currentSquare.changeRank(2)).filter(_ =>
      board.pieceAt(currentSquare.changeRank(1)).isEmpty && !hasMoved
    )
      ++ Set(
        currentSquare.changeRank(1)
      )).filter(board.pieceAt(_).isEmpty)
  }

  private def getCaptures(currentSquare: Square, board: Board): Set[Square] = {
    Set(
      currentSquare.changeFile(-1).changeRank(1),
      currentSquare.changeFile(1).changeRank(1)
    ).filter(square =>
      board.enPassant.fold(false)(_ == square) || board
        .pieceAt(square)
        .fold(false)(!_.isColor(color))
    )
  }

  override def updateHasMoved(): Piece = Pawn(color, hasMoved = true)
}
