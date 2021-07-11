package model

import model.Color.Color

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
}
