package ai.evaluator

import model.Color.Color
import model.{Board, Color, King, NormalMove}

/**
 * [[Evaluator]] of king safety, mainly whether the king has castled or can castle, but also ideally
 * the relative safety of the castle. The score is calculated relatively to the opponent.
 */
object KingSafetyEvaluator extends Evaluator {
  override def Evaluate(board: Board, color: Color): Double = {
    getSafetyScore(board, color) - getSafetyScore(board, Color.opposite(color))
  }

  private def getSafetyScore(board: Board, color: Color): Double = {
    val availableKingMoves = board.locatePiece(King(color)).headOption
      .map(sq => King(color).getLegalMoves(sq, board).count {
        case NormalMove(_, _, _, _) => true
        case _ => false
      }).getOrElse(0)
    val castleScore = board.locatePiece(King(color)).flatMap(board.pieceAt).headOption match {
      case Some(King(_, /* hasMoved */ true, /* hasCastled */ false)) => -5
      case Some(King(_, /* hasMoved */ true, /* hasCastled */ true)) => 5
      case _ => 0
    }
    castleScore - availableKingMoves
  }
}
