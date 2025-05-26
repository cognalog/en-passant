package ai.search

import model.Color.Color
import model.{Board, Move}

/** Interface for search algorithms meant to find the best next move for a
  * player on a board.
  */
trait MoveSearch {

  /** Find the best next move for the player with the given color pieces.
    *
    * @param board
    *   the current board state.
    * @param color
    *   the color for the player making the next turn.
    * @return
    *   the best move.
    */
  def GetBestMove(board: Board, color: Color): Move
}
