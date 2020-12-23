package model

import model.Color.Color

class Pawn(override val color: Color) extends Piece(color) {
  override def getLegalMoves(
      currentSquare: Square,
      board: Board
  ): Set[Square] = {
    Set(currentSquare.changeRank(1)).filter(board.pieceAt(_).isEmpty) ++
      Set(
        currentSquare.changeFile(-1).changeRank(1),
        currentSquare.changeFile(1).changeRank(1)
      ).filter(board.isInBounds)
        .filter(square =>
          board.enPassant.fold(false)(_ == square) || board
            .pieceAt(square)
            .fold(false)(!_.isColor(color))
        )
  }
}
