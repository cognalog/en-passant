package model

import model.Color.Color
import model.PieceType.PieceType

object PieceType extends Enumeration {
  type PieceType = Value
  val Pawn, Bishop, Knight, Rook, Queen, King = Value
}

object Color extends Enumeration {
  type Color = Value
  val White, Black = Value
}

class Piece(var pieceType: PieceType, var color: Color) {

}
