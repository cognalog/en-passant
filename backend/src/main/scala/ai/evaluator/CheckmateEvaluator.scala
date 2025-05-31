package ai.evaluator

import model.Board
import model.Color.Color

/**
 * [[Evaluator]] to reward checkmating and penalize being checkmated (heavily, in either case).
 */
object CheckmateEvaluator extends Evaluator {
  private val checkmateValue = 9999999

  override def Evaluate(board: Board, color: Color): Double = {
    val coefficient = if (color == board.turnColor) -1 else 1
    val value = if (board.isCheckmate) checkmateValue else 0
    coefficient * value
  }
}
