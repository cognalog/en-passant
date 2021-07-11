package ai.search

import ai.evaluator.Evaluator
import model.Color.Color
import model.{Board, Move}

/**
 * The minimax search algorithm, applied to a chess board.
 *
 * @param depth     the maximum depth for the DFS.
 * @param evaluator the evaluator to use.
 */
case class Minimax(depth: Int, evaluator: Evaluator) extends MoveSearch {

  override def GetBestMove(board: Board, color: Color): Move = {
    if (board.turnColor != color) throw new IllegalArgumentException(s"It isn't $color's turn'")
    MaxValue(board, color, depth)._1
  }

  private def MaxValue(b: Board, color: Color, d: Int): (Move, Int) = {
    if (d <= 0) return (null, evaluator.Evaluate(b, color))
    val movesAndScores = b.getNextMoves.map { case (move, board) => (move, MinValue(board, color, d - 1)._2) }

    if (movesAndScores.isEmpty) (null, evaluator.Evaluate(b, color)) else movesAndScores
      .max(Ordering.by[(Move, Int), Int](_._2))
  }

  private def MinValue(b: Board, color: Color, d: Int): (Move, Int) = {
    if (d <= 0) return (null, evaluator.Evaluate(b, color))
    val movesAndScores = b.getNextMoves.map { case (move, board) => (move, MaxValue(board, color, d - 1)._2) }

    if (movesAndScores.isEmpty) (null, evaluator.Evaluate(b, color)) else movesAndScores
      .min(Ordering.by[(Move, Int), Int](_._2))
  }
}
