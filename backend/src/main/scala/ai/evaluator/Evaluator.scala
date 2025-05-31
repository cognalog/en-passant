package ai.evaluator

import model.Board
import model.Color.Color

/**
 * A trait for evaluation functions which produce a numeric score given a board
 * state.
 */
trait Evaluator {
  /**
   * Given a board state, produce a numeric score indicating the degree to which
   * the board is advantageous for the given player.
   *
   * @param board the board state.
   * @param color the color for the player in question.
   * @return a numerical score corresponding to viability.
   */
  def Evaluate(board: Board, color: Color): Double
}
