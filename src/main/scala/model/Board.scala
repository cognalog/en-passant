package model

import model.Board.{RankAndFileMax, RankAndFileMin}
import model.Color.Color

/**
 * One of 64 squares on the board. Has coordinates in terms of rank (y) and file (x).
 * Files are numerical instead of alphabetical for easier processing. A-file is 1.
 * TODO(hinderson): swap rank and file order in ctor
 *
 * @param file x coordinate, 1-indexed
 * @param rank y coordinate, 1-indexed
 */
case class Square(file: Int, rank: Int) {
  def changeFile(delta: Int): Square = {
    Square(file + delta, rank)
  }

  def changeRank(delta: Int): Square = {
    Square(file, rank + delta)
  }
}

/**
 * Companion object for board. Holds constants.
 */
object Board {
  private val RankAndFileMin = 1
  private val RankAndFileMax = 8
}

/**
 * A representation of a chessboard after some number of legal moves.
 *
 * @param pieces    the board's occupied squares mapped to the pieces sitting on them.
 * @param turnColor the color whose turn it is.
 * @param enPassant the square, if any, where a pawn may move for en passant.
 */
class Board(
    val pieces: Map[Square, Piece],
    val turnColor: Color = Color.White,
    val enPassant: Option[Square] = None
) {

  override def toString: String = {
    (RankAndFileMin to RankAndFileMax).map(
      rank => (RankAndFileMin to RankAndFileMax).map(
        file => pieceAt(Square(file, rank)) match {
          case Some(piece) => "" + Color.shortName(piece.color) + piece.shortName
          case None        => "_"
        }).mkString(" | ")).reverse.mkString("\n")
  }

  /**
   * Get the set of pieces attacking this square.
   *
   * @param square the square in question.
   * @param color  the color of attackers to find.
   * @return the set of pieces attacking this square, or an empty set if there are no attackers or the square is out
   *         of bounds.
   */
  def getAttackers(square: Square, color: Color): Set[Piece] = {
    // algo: find the attackers by checking whether each piece could capture its own kind from this square.
    val opposingColor = Color.opposite(color)
    Rook(opposingColor).getLegalMoves(square, this).flatMap(pieceAt)
                       .filter {
                         case Rook(_, _)  => true
                         case Queen(_, _) => true
                         case _           => false
                       } ++
    Bishop(opposingColor).getLegalMoves(square, this).flatMap(pieceAt)
                         .filter {
                           case Bishop(_, _) => true
                           case Queen(_, _)  => true
                           case _            => false
                         } ++
    Knight(opposingColor).getLegalMoves(square, this).flatMap(pieceAt)
                         .filter({
                           case Knight(_, _) => true
                           case _            => false
                         }) ++
    King(opposingColor).getLegalMoves(square, this).flatMap(pieceAt)
                       .filter {
                         case King(_, _) => true
                         case _          => false
                       } ++
    Pawn(opposingColor).getCaptures(square, this).flatMap(pieceAt)
                       .filter {
                         case Pawn(_, _) => true
                         case _          => false
                       }
  }

  /**
   * Generate a new board reflecting the board state after the piece at the starting square moves to the destination
   * square.
   *
   * @param start the starting square. There must be a piece here.
   * @param dest  the destination square. The piece must be able to move here.
   * @return the resulting board after a legal move, or an error string if the move is illegal.
   */
  def move(start: Square, dest: Square): Either[String, Board] = {
    checkLegalMove(start, dest)
    val piece = pieces(start).updateHasMoved()
    val nextPieces = pieces - start + (dest -> piece)
    val nextTurnColor =
      if (turnColor == Color.Black) Color.White else Color.Black
    // if it's a pawn that just moved 2 spaces, set the space behind it as en passant
    var nextEnPassant: Option[Square] = None
    val unused: Unit = piece match {
      case Pawn(Color.White, _) if dest.rank - start.rank == 2 =>
        nextEnPassant = Some(Square(start.file, start.rank + 1))
      case Pawn(Color.Black, _) if start.rank - dest.rank == 2 =>
        nextEnPassant = Some(Square(start.file, start.rank - 1))
      case _ => ()
    }
    Right(new Board(nextPieces, nextTurnColor, nextEnPassant))
    // TODO(hinderson): determine whether this turn's king is in check in new board state
  }

  /**
   * Check whether a move is legal on this board.
   * TODO(hinderson): For clarity, create an IllegalMoveException, or move away from exceptions entirely.
   *
   * @param start the starting square.
   * @param dest  the destination square.
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
      throw new IllegalArgumentException(f"${dest.toString} is not a valid space on the board. Acceptable range for " +
        f"rank and file is are ${Board.RankAndFileMin} to ${Board.RankAndFileMax}")
    }
  }

  /**
   * Determine whether a square is on the board.
   *
   * @param square the square in question.
   * @return true if the square is on the board, false otherwise.
   */
  def isInBounds(square: Square): Boolean = {
    val bounds = Board.RankAndFileMin to Board.RankAndFileMax
    bounds.contains(square.file) && bounds.contains(square.rank)
  }

  def pieceAt(square: Square): Option[Piece] = {
    pieces.get(square)
  }
}
