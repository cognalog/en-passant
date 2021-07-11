package ai.evaluator

import model.Board
import model.Color.Color

/**
 * An Evaluator which chooses a random value for every board state. Useful as a
 * baseline evaluator for demo purposes.
 */
object RandomEvaluator extends Evaluator {
  override def Evaluate(board: Board, color: Color): Int = scala.util.Random.nextInt()
}
