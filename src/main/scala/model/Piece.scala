package model

import model.Color.Color

object Color extends Enumeration {
  type Color = Value
  val White, Black = Value
}

trait Piece {
  def color: Color
  def hasMoved: Boolean
  def updateHasMoved(): Piece
  def getLegalMoves(currentSquare: Square, board: Board): Set[Square]
  def isColor(color: Color): Boolean = {
    color == this.color
  }
}
