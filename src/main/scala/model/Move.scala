package model

import scala.util.{Failure, Success, Try}

object Move {
  private val normalMovePattern = raw"([BKNQR])?([a-h])?x?([a-h][1-8])(=[BNQR])?\+?".r

  private val kingsideCastleNotation = "O-O"
  private val queensideCastleNotation = "O-O-O"

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
    case normalMovePattern(piece, startFile, destSquare, promotion) =>
      Square.fromStandardName(destSquare).flatMap(parsedDest => {
        val pieceToFind = piece match {
          case Bishop.standardNotation => Bishop(board.turnColor)
          case King.standardNotation   => King(board.turnColor)
          case Knight.standardNotation => Knight(board.turnColor)
          case Queen.standardNotation  => Queen(board.turnColor)
          case Rook.standardNotation   => Rook(board.turnColor)
          case null                    => Pawn(board.turnColor)
        }
        val startSquares = board.locatePiece(pieceToFind).filter(
          sq => (startFile == null || startFile == sq.standardFileName) &&
            pieceToFind.getLegalMoves(sq, board).exists(_.destination == parsedDest))
        val promotionPiece = Option(promotion).map {
          case s"=${Bishop.standardNotation}" => Bishop(board.turnColor)
          case s"=${Knight.standardNotation}" => Knight(board.turnColor)
          case s"=${Queen.standardNotation}"  => Queen(board.turnColor)
          case s"=${Rook.standardNotation}"   => Rook(board.turnColor)
        }
        if (startSquares.size > 1) return Failure(new IllegalArgumentException(s"Ambiguous move: $move"))
        startSquares.headOption match {
          case Some(start) => Success(NormalMove(start, parsedDest, promotionPiece))
          case None        => Failure(new IllegalArgumentException(s"Impossible move: $move"))
        }
      })
    case _ => Failure(new IllegalArgumentException(s"Malformed move: $move"))
  }

  /**
   * Produce the standard notation for a given move on a given board.
   *
   * The result will be unambiguous. Some, but not all impossible moves will fail; ergo, do not rely upon this method
   * for move validation.
   *
   * @see https://en.wikipedia.org/wiki/Algebraic_notation_(chess)
   * @param move  the move to translate.
   * @param board the board in whose context to describe the move.
   * @return the move string in standard notation. */
  def toStandardNotation(move: Move, board: Board): Try[String] = {
    val checkIndicator = board.move(move) match {
      case Success(board) if board.kingInCheck(board.turnColor) => "+"
      case Success(_)                                           => ""
      case Failure(ex)                                          => return Failure(ex)
    }
    move match {
      case NormalMove(start, dest, maybePromo) => val capturePart =
        board.pieceAt(dest) match {
          case Some(piece: Piece) if piece.isColor(Color.opposite(board.turnColor)) => "x"
          case _                                                                    => ""
        }
        board.pieceAt(start) match {
          case Some(Pawn(_, _))   => val fileString = if (capturePart.nonEmpty) start.standardFileName else ""
            val promoPart = maybePromo match {
              case Some(piece: Piece) => s"=${piece.shortName}"
              case _                  => ""
            }
            Success(s"$fileString$capturePart${move.destination.standardName}$promoPart$checkIndicator")
          case Some(piece: Piece) => val disambiguation = disambiguatingString(move.asInstanceOf[NormalMove], board)
            Success(s"${piece.shortName}$disambiguation$capturePart${move.destination.standardName}$checkIndicator")
          case None               => Failure(new IllegalArgumentException(s"No piece at $start"))
        }
      case CastleMove(Square(file, _))         => Success(
        if (file == 7) kingsideCastleNotation else queensideCastleNotation)
      case _                                   => Failure(new IllegalArgumentException(s"Illegal move $move"))
    }
  }

  private def disambiguatingString(move: NormalMove, board: Board): String = board.pieceAt(move.start) match {
    case None               => ""
    case Some(piece: Piece) => val otherPossibleStarts = board.locatePiece(piece).filter(
      square => square != move.start &&
                piece.getLegalMoves(square, board).contains(NormalMove(square, move.destination)))
      if (otherPossibleStarts.isEmpty) return ""
      val fileAmbiguous = otherPossibleStarts.exists(square => square.file == move.start.file)
      val rankAmbiguous = otherPossibleStarts.exists(square => square.rank == move.start.rank)
      val fileStr = if (!fileAmbiguous || rankAmbiguous) move.start.standardFileName else ""
      val rankStr = if (fileAmbiguous) move.start.rank else ""
      s"$fileStr$rankStr"
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

