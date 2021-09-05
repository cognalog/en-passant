package ai.evaluator

import model.Color.Color
import model._

object PieceScoreEvaluator extends Evaluator {
  private val pawnScore = 1
  private val minorPieceScore = 3
  private val rookScore = 5
  private val queenScore = 9

  override def Evaluate(board: Board, color: Color): Double = {
    PieceScoreSum(board, color) - PieceScoreSum(board, Color.opposite(color))
  }

  private def PieceScoreSum(board: Board, color: Color) = {
    board.locatePiece(Pawn(color)).size * pawnScore +
      board.locatePiece(Knight(color)).size * minorPieceScore +
      board.locatePiece(Bishop(color)).size * minorPieceScore +
      board.locatePiece(Rook(color)).size * rookScore +
      board.locatePiece(Queen(color)).size * queenScore
  }
}
