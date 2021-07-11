package ai.evaluator

import model.Board
import model.Color.Color

trait Evaluator {
  def Evaluate(board: Board, color: Color): Int
}
