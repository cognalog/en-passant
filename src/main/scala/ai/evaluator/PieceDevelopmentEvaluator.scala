package ai.evaluator

import model.Color.Color
import model.{Bishop, Board, Color, Knight}

object PieceDevelopmentEvaluator extends Evaluator {
  private val developmentValue = 5

  override def Evaluate(board: Board, color: Color): Int = {
    getPieceDevelopmentScore(board, color) - getPieceDevelopmentScore(board, Color.opposite(color))
  }

  private def getPieceDevelopmentScore(board: Board, color: Color): Int = {
    (board.locatePiece(Knight(color)) ++ board.locatePiece(Bishop(color))).toList.map(board.pieceAt).map {
      case Some(piece) if piece.hasMoved => developmentValue
      case _ => 0
    }.sum
  }
}
