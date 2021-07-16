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
}
