package ai.evaluator

import model.Board
import model.Color.Color

object RandomEvaluator$ extends Evaluator {
  override def Evaluate(board: Board, color: Color): Int = scala.util.Random.nextInt()
}
