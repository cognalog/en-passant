package ai.utility

import model.Board
import model.Color.Color

trait Utility {
  def Evaluate(board: Board, color: Color): Int
}
