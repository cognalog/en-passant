package ai.evaluator

import model.Color.Color
import model.{Board, Color, King, NormalMove}

/**
 * [[Evaluator]] of king safety, mainly whether the king has castled or not.
 * The score is calculated relative to the opponent's king safety.
 */
object KingSafetyEvaluator extends Evaluator {
  override def Evaluate(board: Board, color: Color): Double = {
    getSafetyScore(board, color) - getSafetyScore(board, Color.opposite(color))
  }

  private def getSafetyScore(board: Board, color: Color): Double = {
    val checkScore = if (board.kingInCheck(color)) -10 else 0
    val availableKingMoves = board.locatePiece(King(color)).headOption
      .map(sq => King(color).getLegalMoves(sq, board).count {
        case NormalMove(_, _, _) => true
        case _ => false
      }).getOrElse(0)
    checkScore - availableKingMoves
  }
}
