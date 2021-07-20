package ai.evaluator

import model.Board
import model.Color.Color

/**
 * [[Evaluator]] composing several, more focused [[Evaluator]]s
 */
object GeneralEvaluator extends Evaluator {
  val pieceDevelopmentCoef = 0.5

  override def Evaluate(board: Board, color: Color): Double = CheckmateEvaluator.Evaluate(board, color) +
    PieceScoreEvaluator.Evaluate(board, color) + pieceDevelopmentCoef * PieceDevelopmentEvaluator.Evaluate(board, color)
}
