package ai.evaluator

import model.Board
import model.Color.Color

/**
 * An Evaluator which reads scores that have been pre-specified per-board in a map.
 *
 * @param map map from [[Board.id]] to a numeric score.
 */
class MapEvaluator(map: Map[String, Int]) extends Evaluator {
  private var accessCounts: Map[String, Int] = Map()

  override def Evaluate(board: Board, color: Color): Double = {
    accessCounts += ((board.id, 1 + accessCounts.getOrElse(board.id, 0)))
    map(board.id)
  }

  /**
   * Report the number of times a board was evaluated
   *
   * @param boardId the id of the board in question.
   * @return the number of times [[Evaluate()]] was called with the given board.
   */
  def getAccessCount(boardId: String): Int = accessCounts.getOrElse(boardId, 0)
}
