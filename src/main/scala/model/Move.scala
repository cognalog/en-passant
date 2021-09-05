package model

import scala.util.{Failure, Success, Try}

object Move {
  private val normalMove = raw"([BKNQR])?([a-h])?x?([a-h][1-8])\+?".r

  /**
   * Parse a move string written in Algebraic Notation. Limited legality
   * checking is performed to resolve ambiguity.
   *
   * @param move  the move string.
   * @param board the board providing context for the move.
   * @return a [[Move]] which is referred to by the move string, or a
   *         [[Failure]] if the move is impossible or the string is malformed.
   * @throws IllegalArgumentException for ambiguous, malformed or impossible moves.
   */
  def fromStandardNotation(move: String, board: Board): Try[Move] = move match {
    case "O-O" => Success(CastleMove(if (board.turnColor == Color.White) Square(7, 1) else Square(7, 8)))
    case "O-O-O" => Success(CastleMove(if (board.turnColor == Color.White) Square(3, 1) else Square(3, 8)))
    case normalMove(piece, startFile, destSquare) =>
      Square.fromStandardName(destSquare).flatMap(parsedDest => {
        val pieceToFind = piece match {
          case "B" => Bishop(board.turnColor)
          case "K" => King(board.turnColor)
          case "N" => Knight(board.turnColor)
          case "Q" => Queen(board.turnColor)
          case "R" => Rook(board.turnColor)
          case null => Pawn(board.turnColor)
        }
        val startSquares = board.locatePiece(pieceToFind).filter(
          sq => (startFile == null || startFile == sq.standardFileName) &&
            pieceToFind.getLegalMoves(sq, board).exists(_.destination == parsedDest))
        if (startSquares.size > 1) return Failure(new IllegalArgumentException(s"Ambiguous move: $move"))
        startSquares.headOption match {
          case Some(start) => Success(NormalMove(start, parsedDest))
          case None => Failure(new IllegalArgumentException(s"Impossible move: $move"))
        }
      })
    case _ => Failure(new IllegalArgumentException(s"Malformed move: $move"))
  }
}

/**
 * A chess move for a piece on a board.
 */
trait Move {
  // The square where a piece is moved.
  def destination: Square
}

/**
 * A castling move where the king/rook move in tandem.
 *
 * @param destination the destination square for the King.
 */
case class CastleMove(override val destination: Square) extends Move

/**
 * A "normal" move for a piece from one square to another square.
 *
 * @param start       the square holding the piece before the move.
 * @param destination the square holding the piece after the move.
 * @param promotion   the replacement piece, when promoting a pawn.
 */
case class NormalMove(start: Square, override val destination: Square, promotion: Option[Piece] = None) extends Move

