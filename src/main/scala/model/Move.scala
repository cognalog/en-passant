package model

trait Move {
  // The square where a piece is moved.
  def destination: Square
}

case class CastleMove(override val destination: Square) extends Move

case class NormalMove(start: Square, override val destination: Square) extends Move

