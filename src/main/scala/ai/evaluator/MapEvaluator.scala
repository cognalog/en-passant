package ai.evaluator

import model.Board
import model.Color.Color

case class MapEvaluator(map: Map[String, Int]) extends Evaluator {
  override def Evaluate(board: Board, color: Color): Int = map(board.id)
}
