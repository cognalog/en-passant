package ai.evaluator

import model.Board
import model.Color.Color

object GeneralEvaluator extends Evaluator {
  override def Evaluate(board: Board, color: Color): Double = CheckmateEvaluator.Evaluate(board, color) +
    PieceScoreEvaluator.Evaluate(board, color)
}
