package model

import scala.util.{Failure, Success, Try}

object Move {
  private val normalMovePattern =
    raw"([BKNQR])?([a-h])?x?([a-h][1-8])(=[BNQR])?\+?".r

  /** Parse a move string written in Algebraic Notation. Limited legality
    * checking is performed to resolve ambiguity.
    *
    * @param move
    *   the move string.
    * @param board
    *   the board providing context for the move.
    * @return
    *   a [[Move]] which is referred to by the move string, or a [[Failure]] if
    *   the move is impossible or the string is malformed.
    * @throws IllegalArgumentException
    *   for ambiguous, malformed or impossible moves.
    */
  def fromStandardNotation(move: String, board: Board): Try[Move] = move match {
    case "O-O" =>
      Success(
        CastleMove(
          if (board.turnColor == Color.White) Square(7, 1) else Square(7, 8)
        )
      )
    case "O-O-O" =>
      Success(
        CastleMove(
          if (board.turnColor == Color.White) Square(3, 1) else Square(3, 8)
        )
      )
    case normalMovePattern(piece, startFile, destSquare, promotion) =>
      Square
        .fromStandardName(destSquare)
        .flatMap(parsedDest => {
          val pieceToFind = piece match {
            case "B"  => Bishop(board.turnColor)
            case "K"  => King(board.turnColor)
            case "N"  => Knight(board.turnColor)
            case "Q"  => Queen(board.turnColor)
            case "R"  => Rook(board.turnColor)
            case null => Pawn(board.turnColor)
          }
          val startSquares = board
            .locatePiece(pieceToFind)
            .filter(sq =>
              (startFile == null || startFile == sq.standardFileName) &&
                pieceToFind
                  .getLegalMoves(sq, board)
                  .exists(_.destination == parsedDest)
            )
          val promotionPiece = Option(promotion).map {
            case "=B" => Bishop(board.turnColor)
            case "=N" => Knight(board.turnColor)
            case "=Q" => Queen(board.turnColor)
            case "=R" => Rook(board.turnColor)
          }
          if (startSquares.size > 1)
            return Failure(
              new IllegalArgumentException(s"Ambiguous move: $move")
            )
          startSquares.headOption match {
            case Some(start) =>
              val isCapture = move.contains('x') || board
                .pieceAt(parsedDest)
                .exists(!_.isColor(board.turnColor)) ||
                (pieceToFind
                  .isInstanceOf[Pawn] && board.isEnPassantPossible(parsedDest))
              Success(
                NormalMove(
                  start,
                  parsedDest,
                  pieceToFind,
                  isCapture,
                  promotionPiece
                )
              )
            case None =>
              Failure(new IllegalArgumentException(s"Impossible move: $move"))
          }
        })
    case _ => Failure(new IllegalArgumentException(s"Malformed move: $move"))
  }
}

/** A chess move for a piece on a board.
  */
sealed trait Move {
  // The square where a piece is moved.
  def destination: Square
  // The standard notation for the move.
  def toStandardNotation: String
}

/** A castling move where the king/rook move in tandem.
  *
  * @param destination
  *   the destination square for the King.
  */
case class CastleMove(override val destination: Square) extends Move {
  override def toStandardNotation: String =
    if (destination.file == 7) "O-O" else "O-O-O"
}

/** A "normal" move for a piece from one square to another square.
  *
  * @param start
  *   the square holding the piece before the move.
  * @param destination
  *   the square holding the piece after the move.
  * @param piece
  *   the piece making the move.
  * @param promotion
  *   the replacement piece, when promoting a pawn.
  */
case class NormalMove(
    start: Square,
    override val destination: Square,
    piece: Piece,
    isCapture: Boolean = false,
    promotion: Option[Piece] = None
) extends Move {
  override def toStandardNotation: String = {
    val maybeStartSquare =
      if (piece.isInstanceOf[Pawn] && !isCapture) "" else start.standardName
    val maybeX = if (isCapture) "x" else ""
    val maybePromotion = promotion.map(p => s"=${p.standardName}").getOrElse("")
    // Note: we always include the disambiguating start square because doing otherwise requires a board state
    s"${piece.standardName}${maybeStartSquare}${maybeX}${destination.standardName}${maybePromotion}"
  }
}
