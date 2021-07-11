package ai.utility
import model.Board
import model.Color.Color

object RandomUtility extends Utility {
  override def Evaluate(board: Board, color: Color): Int = scala.util.Random.nextInt()
}
