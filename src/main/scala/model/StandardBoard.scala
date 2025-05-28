package model

import model.Color.Color
import model.StandardBoard.{RankAndFileMax, RankAndFileMin}

import scala.util.{Failure, Success, Try}

/** Companion object for board. Holds constants.
  */
object StandardBoard {
  val RankAndFileMin = 1
  val RankAndFileMax = 8

  val StartingPosition: Board = StandardBoard(
    Map(
      Square(1, 1) -> Rook(Color.White),
      Square(2, 1) -> Knight(Color.White),
      Square(3, 1) -> Bishop(Color.White),
      Square(4, 1) -> Queen(Color.White),
      Square(5, 1) -> King(Color.White),
      Square(6, 1) -> Bishop(Color.White),
      Square(7, 1) -> Knight(Color.White),
      Square(8, 1) -> Rook(Color.White),
      Square(1, 2) -> Pawn(Color.White),
      Square(2, 2) -> Pawn(Color.White),
      Square(3, 2) -> Pawn(Color.White),
      Square(4, 2) -> Pawn(Color.White),
      Square(5, 2) -> Pawn(Color.White),
      Square(6, 2) -> Pawn(Color.White),
      Square(7, 2) -> Pawn(Color.White),
      Square(8, 2) -> Pawn(Color.White),
      Square(1, 8) -> Rook(Color.Black),
      Square(2, 8) -> Knight(Color.Black),
      Square(3, 8) -> Bishop(Color.Black),
      Square(4, 8) -> Queen(Color.Black),
      Square(5, 8) -> King(Color.Black),
      Square(6, 8) -> Bishop(Color.Black),
      Square(7, 8) -> Knight(Color.Black),
      Square(8, 8) -> Rook(Color.Black),
      Square(1, 7) -> Pawn(Color.Black),
      Square(2, 7) -> Pawn(Color.Black),
      Square(3, 7) -> Pawn(Color.Black),
      Square(4, 7) -> Pawn(Color.Black),
      Square(5, 7) -> Pawn(Color.Black),
      Square(6, 7) -> Pawn(Color.Black),
      Square(7, 7) -> Pawn(Color.Black),
      Square(8, 7) -> Pawn(Color.Black)
    )
  )
}

/** A representation of a chessboard after some number of legal moves.
  *
  * @param pieces
  *   the board's occupied squares mapped to the pieces sitting on them.
  * @param turnColor
  *   the color whose turn it is.
  * @param enPassant
  *   the square, if any, where a pawn may move for en passant.
  */
case class StandardBoard(
    pieces: Map[Square, Piece],
    override val turnColor: Color = Color.White,
    enPassant: Option[Square] = None
) extends Board {

  override def toString: String = {
    (RankAndFileMin to RankAndFileMax)
      .map(rank =>
        (RankAndFileMin to RankAndFileMax)
          .map(file =>
            pieceAt(Square(file, rank)) match {
              case Some(piece) =>
                "" + Color
                  .shortName(piece.color) + piece.shortName + (if (
                                                                 piece.hasMoved
                                                               ) "+"
                                                               else "-")
              case None if enPassant.contains(Square(file, rank)) => "e/p"
              case _                                              => "___"
            }
          )
          .mkString(" | ")
      )
      .reverse
      .mkString("\n") + "\n"
  }

  override def kingInCheck(color: Color): Boolean = {
    val kingSquare = locatePiece(King(color)).headOption
    kingSquare.exists(square =>
      pieces.exists(piece_square =>
        piece_square._2.isColor(Color.opposite(color)) &&
          piece_square._2
            .getCaptures(piece_square._1, this)
            .exists {
              case NormalMove(_, dest, _, _, _) => dest == square
              case _                                    => false
            }
      )
    )
  }

  /** Get the set of pieces attacking this square.
    *
    * @param square
    *   the square in question.
    * @param color
    *   the color of attackers to find.
    * @return
    *   the set of pieces attacking this square, or an empty set if there are no
    *   attackers or the square is out of bounds.
    */
  def getAttackers(square: Square, color: Color): Set[Piece] = {
    // algo: find the attackers by checking whether each piece could capture its own kind from this square.
    val opposingColor = Color.opposite(color)
    Rook(opposingColor)
      .getLegalMoves(square, this)
      .flatMap(move => pieceAt(move.destination))
      .filter {
        case Rook(_, _)  => true
        case Queen(_, _) => true
        case _           => false
      } ++
      Bishop(opposingColor)
        .getLegalMoves(square, this)
        .flatMap(move => pieceAt(move.destination))
        .filter {
          case Bishop(_, _) => true
          case Queen(_, _)  => true
          case _            => false
        } ++
      Knight(opposingColor)
        .getLegalMoves(square, this)
        .flatMap(move => pieceAt(move.destination))
        .filter({
          case Knight(_, _) => true
          case _            => false
        }) ++
      King(opposingColor)
        .getLegalMoves(square, this)
        .filter {
          case NormalMove(_, _, _, _, _) => true
          case _                   => false
        }
        .flatMap(move => pieceAt(move.destination))
        .filter {
          case King(_, _, _) => true
          case _             => false
        } ++
      Pawn(opposingColor)
        .getCaptures(square, this)
        .flatMap(move => pieceAt(move.destination))
        .filter {
          case Pawn(_, _) => true
          case _          => false
        }
  }

  override def move(move: Move): Try[StandardBoard] = {
    move match {
      case NormalMove(start, dest, _, _, promotion) =>
        normalMove(start, dest, promotion)
      case CastleMove(dest) => castle(dest)
      case _ => Failure(new IllegalArgumentException(s"Malformed move: $move"))
    }
  }

  /** Generate a new board reflecting the board state after the piece at the
    * starting square moves to the destination square.
    *
    * @param start
    *   the starting square. There must be a piece here.
    * @param dest
    *   the destination square. The piece must be able to move here.
    * @param promotion
    *   the replacement piece, when promoting a pawn.
    * @return
    *   the resulting board after a legal move, or an error string if the move
    *   is illegal.
    */
  private def normalMove(
      start: Square,
      dest: Square,
      promotion: Option[Piece]
  ): Try[StandardBoard] = {
    Try(checkLegalMove(start, dest)) match {
      case Failure(ex) => return Failure(ex)
      case _           => ()
    }
    val piece = pieces(start).updateHasMoved()
    val nextPieces =
      pieces - start - getEnPassantVictim(dest).orNull + (promotion match {
        case Some(newPiece) => dest -> newPiece
        case _              => dest -> piece
      })

    val nextTurnColor = Color.opposite(turnColor)
    // if it's a pawn that just moved 2 spaces, set the space behind it as en passant
    var nextEnPassant: Option[Square] = None
    piece match {
      case Pawn(Color.White, _) if dest.rank - start.rank == 2 =>
        nextEnPassant = Some(Square(start.file, start.rank + 1))
      case Pawn(Color.Black, _) if start.rank - dest.rank == 2 =>
        nextEnPassant = Some(Square(start.file, start.rank - 1))
      case _ => ()
    }
    val newBoard = StandardBoard(nextPieces, nextTurnColor, nextEnPassant)
    if (newBoard.kingInCheck(turnColor))
      return Failure(
        new IllegalArgumentException(
          s"Move $start -> $dest leaves the king in check."
        )
      )
    Success(newBoard)
  }

  private def getEnPassantVictim(dest: Square): Option[Square] = {
    if (!isEnPassantPossible(dest)) return None
    Some(Square(dest.file, if (turnColor == Color.White) dest.rank - 1 else dest.rank + 1))
  }

  /** Castle the king to the destination square, moving the rook as well
    * according to the rules.
    *
    * @see
    *   [[https://en.wikipedia.org/wiki/Castling]]
    * @param kingDest
    *   the square to which the king will move.
    * @return
    *   the new board after the king has castled accordingly, or a failure
    *   message if the move is not legal.
    */
  private def castle(kingDest: Square): Try[StandardBoard] = {
    // make sure neither king, destination, or in-between square has opposing attackers
    // make sure there are no pieces between king and rook
    if (
      !Set(3, 7).contains(kingDest.file) || !Set(1, 8).contains(kingDest.rank)
    ) {
      return Failure(
        new IllegalArgumentException(
          s"$kingDest is not a legal castling destination."
        )
      )
    }

    val kingStart = Square(5, kingDest.rank)
    val rookStart =
      if (kingDest.file == 3) Square(1, kingDest.rank)
      else Square(8, kingDest.rank)
    val validKing: Boolean = pieceAt(kingStart) match {
      case Some(King(this.turnColor, false, false)) => true
      case _                                        => false
    }
    if (!validKing)
      return Failure(
        new IllegalArgumentException(
          s"There is no unmoved $turnColor king at $kingStart."
        )
      )
    val validRook: Boolean = pieceAt(rookStart) match {
      case Some(Rook(this.turnColor, false)) => true
      case _                                 => false
    }
    if (!validRook)
      return Failure(
        new IllegalArgumentException(
          s"There is no unmoved $turnColor rook at $rookStart."
        )
      )
    val rookDest =
      if (kingDest.file == 3) Square(4, kingDest.rank)
      else Square(6, kingDest.rank)

    val piecesInBetween: Boolean =
      (math.min(kingStart.file, rookStart.file) + 1 until
        math.max(kingStart.file, rookStart.file))
        .exists(file => pieceAt(Square(file, kingStart.rank)).nonEmpty)
    if (piecesInBetween)
      return Failure(
        new IllegalArgumentException(
          s"There are pieces between the king at $kingStart and the rook at $rookStart."
        )
      )

    val kingMovesSafe: Boolean = (math.min(kingStart.file, kingDest.file) to
      math.max(kingStart.file, kingDest.file))
      .forall(file =>
        getAttackers(
          Square(file, kingDest.rank),
          Color.opposite(turnColor)
        ).isEmpty
      )
    if (!kingMovesSafe)
      return Failure(
        new IllegalArgumentException(
          s"The king cannot safely move from $kingStart to $kingDest."
        )
      )

    val newPieces = pieces - kingStart - rookStart + (rookDest -> Rook(
      turnColor,
      hasMoved = true
    )) +
      (kingDest -> King(turnColor, hasMoved = true, hasCastled = true))
    Success(new StandardBoard(pieces = newPieces, Color.opposite(turnColor)))
  }

  /** Check whether a move is legal on this board. TODO(hinderson): For clarity,
    * create an IllegalMoveException, or move away from exceptions entirely.
    *
    * @param start
    *   the starting square.
    * @param dest
    *   the destination square.
    */
  def checkLegalMove(start: Square, dest: Square): Unit = {
    if (!pieces.contains(start)) {
      throw new IllegalStateException(f"There's no piece at ${start.toString}")
    }
    val piece = pieces(start)
    if (!piece.isColor(turnColor)) {
      throw new IllegalStateException(
        f"The piece at ${start.toString} is ${piece.color}, and it is $turnColor's turn.'"
      )
    }
    if (!isInBounds(dest)) {
      throw new IllegalArgumentException(
        f"${dest.toString} is not a valid space on the board. Acceptable range for " +
          f"rank and file is are ${StandardBoard.RankAndFileMin} to ${StandardBoard.RankAndFileMax}"
      )
    }
  }

  override def getNextMoves: Iterable[(Move, StandardBoard)] = {
    pieces
      .filter(_._2.isColor(turnColor))
      .flatMap(sq_piece => sq_piece._2.getLegalMoves(sq_piece._1, this))
      .map(new_move => (new_move, move(new_move)))
      .flatMap {
        case (_, Failure(e)) => /*println(e);*/
          None // TODO make this optional a la VLOG
        case (move, Success(board)) => Some((move, board))
      }
  }

  override def isInBounds(square: Square): Boolean = {
    val bounds = StandardBoard.RankAndFileMin to StandardBoard.RankAndFileMax
    bounds.contains(square.file) && bounds.contains(square.rank)
  }

  override def pieceAt(square: Square): Option[Piece] = {
    pieces.get(square)
  }

  override def id: String = s"$hashCode"

  override def locatePiece(piece: Piece): Set[Square] = {
    pieces
      .filter(_._2.shortName == piece.shortName)
      .filter(_._2.color == piece.color)
      .keySet
  }

  override def isEnPassantPossible(square: Square): Boolean = {
    enPassant.contains(square)
  }

  override def isCheckmate: Boolean = {
    kingInCheck(turnColor) && getNextMoves.isEmpty
  }

  override def isDraw: Boolean = isMaterialInsufficient || isStalemate

  private def isStalemate: Boolean =
    !kingInCheck(turnColor) && getNextMoves.isEmpty

  private def isMaterialInsufficient: Boolean = {
    val (whitePieces, blackPieces) = pieces.partition(_._2.isColor(Color.White))
    whitePieces.size < 3 && blackPieces.size < 3 &&
    !whitePieces.exists(_._2.canMateWithKing) && !blackPieces.exists(
      _._2.canMateWithKing
    )
  }
}
