package ai.evaluator

import model.Board
import model.Color.Color

object GeneralEvaluator extends Evaluator {
  override def Evaluate(board: Board, color: Color): Int = CheckmateEvaluator.Evaluate(board, color) +
    PieceScoreEvaluator.Evaluate(board, color)
}
