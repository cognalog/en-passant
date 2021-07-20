package model

import model.Color.Color

import scala.util.Try

/**
 * The representation of a chess board.
 */
trait Board {
  /**
   * A string identifier for the board. Useful for debugging or testing.
   *
   * @return a suitable string identifier.
   */
  def id: String

  /**
   * The color of the player whose turn it is.
   *
   * @return the aforementioned color.
   */
  def turnColor: Color

  /**
   * Generate all legal next moves and respective updated boards for this board.
   *
   * @return a collection of the legal moves and successors of this board.
   */
  def getNextMoves: Iterable[(Move, Board)]

  /**
   * Generate a new board by applying a move to this board.
   *
   * @param move the move to apply.
   * @return Right(the new board) if the move is legal, otherwise Left(error message)
   */
  def move(move: Move): Try[Board]

  /**
   * Get the piece at a particular square on a board, if it exists.
   *
   * @param square the square in question.
   * @return Some(piece) if there is a piece on the given square, [[None]] otherwise.
   */
  def pieceAt(square: Square): Option[Piece]

  /**
   * Get all Squares where a matching piece can be found.
   * A piece matches if it has the same type and color as the given piece.
   *
   * @param piece the piece to find.
   * @return all squares holding a matching piece.
   */
  def locatePiece(piece: Piece): Set[Square]

  /**
   * Determine whether a square is on the board.
   *
   * @param square the square in question.
   * @return true if the square is on the board, false otherwise.
   */
  def isInBounds(square: Square): Boolean

  /**
   * Determine whether a square can be entered via the en passant rule.
   *
   * @param square the square in question.
   * @return true iff a Pawn can enter this square via en passant.
   */
  def isEnPassantPossible(square: Square): Boolean

  /**
   * @return whether the player whose turn it is has been checkmated.
   */
  def isCheckmate: Boolean

  /**
   * @return whether the game is drawn at this state.
   */
  def isDraw: Boolean
}
