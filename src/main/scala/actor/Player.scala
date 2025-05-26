package actor

import model.Color.Color
import model.{Board, Move}

import scala.util.Try

/** A trait for players who are responsible for producing a move on demand,
  * given a board state.
  */
trait Player {

  /** Produce the next move for this player on a given board.
    *
    * @param board
    *   the current state of the board.
    * @param color
    *   the piece color for this player.
    */
  def GetNextMove(board: Board, color: Color): Try[Move]
}
