package model

import model.Color.Color

class Pawn(
    override val color: Color,
    var enPassant: Option[Square] = None
) extends Piece(color) {

  override def getLegalMoves(
      currentSquare: Square,
      board: Board
  ): Set[Square] = {
    //TODO(hinderson): include 2-step first move
    (Set(currentSquare.changeRank(1)).filter(board.pieceAt(_).isEmpty) ++
      Set(
        currentSquare.changeFile(-1).changeRank(1),
        currentSquare.changeFile(1).changeRank(1)
      ).filter(square =>
        enPassant.fold(false)(_ == square) || board
          .pieceAt(square)
          .fold(false)(!_.isColor(color))
      )).filter(board.isInBounds)
  }
}
