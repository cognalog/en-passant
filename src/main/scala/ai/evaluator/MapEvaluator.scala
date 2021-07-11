package ai.evaluator

import model.Board
import model.Color.Color

/**
 * An Evaluator which reads scores that have been pre-specified per-board in a map.
 *
 * @param map map from [[Board.id]] to a numeric score.
 */
case class MapEvaluator(map: Map[String, Int]) extends Evaluator {
  override def Evaluate(board: Board, color: Color): Int = map(board.id)
}
