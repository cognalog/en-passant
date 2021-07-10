package ai.utility
import model.Board

object RandomUtility extends Utility {
  override def Evaluate(board: Board): Int = scala.util.Random.nextInt()
}
