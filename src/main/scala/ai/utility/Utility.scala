package ai.utility

import model.Board

trait Utility {
  def Evaluate(board: Board): Int
}
