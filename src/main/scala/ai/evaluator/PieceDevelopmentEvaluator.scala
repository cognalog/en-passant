package ai.evaluator

import model.Color.Color
import model.{Bishop, Board, Color, Knight}

object PieceDevelopmentEvaluator extends Evaluator {

  override def Evaluate(board: Board, color: Color): Double = {
    getPieceDevelopmentScore(board, color) - getPieceDevelopmentScore(board, Color.opposite(color))
  }

  private def getPieceDevelopmentScore(board: Board, color: Color): Int = {
    val knightMoves = board.locatePiece(Knight(color)).flatMap(sq => Knight(color).getLegalMoves(sq, board))
    val bishopMoves = board.locatePiece(Bishop(color)).flatMap(sq => Bishop(color).getLegalMoves(sq, board))
    knightMoves.size + bishopMoves.size
  }
}
