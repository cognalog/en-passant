package ai.evaluator

import model.Color.Color
import model._

/**
 * [[Evaluator]] of the combined score of a player's remaining pieces, contrasted against the opponent's.
 */
object PieceScoreEvaluator extends Evaluator {

  override def Evaluate(board: Board, color: Color): Double = {
    PieceScoreSum(board, color) - PieceScoreSum(board, Color.opposite(color))
  }

  private def PieceScoreSum(board: Board, color: Color) = {
    val nonKingPieces = (board.locatePiece(Pawn(color)) ++ board.locatePiece(Knight(color)) ++
      board.locatePiece(Bishop(color)) ++ board.locatePiece(Rook(color)) ++ board.locatePiece(Queen(color))).toList
      .flatMap(board.pieceAt)
    nonKingPieces.map(_.pointValue).sum
  }
}
