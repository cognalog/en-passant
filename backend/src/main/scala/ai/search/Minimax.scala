package ai.search

import ai.evaluator.Evaluator
import model.Color.Color
import model.{Board, Move}

/** The minimax search algorithm, applied to a chess board.
  *
  * @param depth
  *   the maximum depth for the DFS.
  * @param evaluator
  *   the evaluator to use.
  */
case class Minimax(depth: Int, evaluator: Evaluator) extends MoveSearch {

  override def GetBestMove(board: Board, color: Color): Move = {
    if (board.turnColor != color)
      throw new IllegalArgumentException(s"It isn't $color's turn'")
    MaxValue(board, color, depth)._1
  }

  private def MaxValue(
      board: Board,
      color: Color,
      depth: Int
  ): (Move, Double) = {
    if (depth <= 0) return (null, evaluator.Evaluate(board, color))
    val movesAndScores = board.getNextMoves.map { case (move, board) =>
      (move, MinValue(board, color, depth - 1)._2)
    }

    if (movesAndScores.isEmpty) (null, evaluator.Evaluate(board, color))
    else
      movesAndScores
        .max(Ordering.by[(Move, Double), Double](_._2))
  }

  private def MinValue(
      board: Board,
      color: Color,
      depth: Int
  ): (Move, Double) = {
    if (depth <= 0) return (null, evaluator.Evaluate(board, color))
    val movesAndScores = board.getNextMoves.map { case (move, board) =>
      (move, MaxValue(board, color, depth - 1)._2)
    }

    if (movesAndScores.isEmpty) (null, evaluator.Evaluate(board, color))
    else
      movesAndScores
        .min(Ordering.by[(Move, Double), Double](_._2))
  }
}
