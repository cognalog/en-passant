package ai.utility

import model.Board
import model.Color.Color

case class MapUtility(map: Map[String, Int]) extends Utility {
  override def Evaluate(board: Board, color: Color): Int = map(board.id)
}
