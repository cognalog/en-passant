package ai.evaluator

import model.Board
import model.Color.Color

/**
 * [[Evaluator]] composing several, more focused [[Evaluator]]s
 */
object GeneralEvaluator extends Evaluator {
  val pieceScoreCoef = 3

  override def Evaluate(board: Board, color: Color): Double = CheckmateEvaluator.Evaluate(board, color) +
    pieceScoreCoef * PieceScoreEvaluator.Evaluate(board, color) +
    PieceDevelopmentEvaluator.Evaluate(board, color) +
    KingSafetyEvaluator.Evaluate(board, color)
}
