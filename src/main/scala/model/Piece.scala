package model

import model.Color.Color

object PieceType extends Enumeration {
  type PieceType = Value
  val Pawn, Bishop, Knight, Rook, Queen, King = Value
}

object Color extends Enumeration {
  type Color = Value
  val White, Black = Value
}

abstract class Piece(val color: Color, val hasMoved: Boolean = false) {
  def getLegalMoves(currentSquare: Square, board: Board): Set[Square]
  def isColor(color: Color): Boolean = {
    color == this.color
  }
}
