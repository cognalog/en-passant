package ai.search

import ai.evaluator.Evaluator
import model.Color.Color
import model.{Board, Move}

/** The minimax search algorithm with alpha/beta pruning, applied to a chess
  * board.
  *
  * @param depth
  *   the maximum depth for the DFS.
  * @param evaluator
  *   the evaluator to use.
  */
case class ABPruningMinimax(depth: Int, evaluator: Evaluator)
    extends MoveSearch {

  override def GetBestMove(board: Board, color: Color): Move = {
    if (board.turnColor != color)
      throw new IllegalArgumentException(s"It isn't $color's turn'")
    MaxValue(board, color, Double.MinValue, Double.MaxValue, depth)._1
  }

  private def MaxValue(
      board: Board,
      color: Color,
      alpha: Double,
      beta: Double,
      depth: Int
  ): (Move, Double) = {
    if (depth <= 0) return (null, evaluator.Evaluate(board, color))
    var a = alpha
    var finalMax: (Move, Double) = (null, Double.MinValue)
    val successors = board.getNextMoves
    if (successors.isEmpty) return (null, evaluator.Evaluate(board, color))
    successors.foreach { case (move, board) =>
      val minValue = MinValue(board, color, a, beta, depth - 1)
      if (minValue._2 > finalMax._2) finalMax = (move, minValue._2)
      if (finalMax._2 >= beta) return finalMax
      a = math.max(a, finalMax._2)
    }
    finalMax
  }

  private def MinValue(
      board: Board,
      color: Color,
      alpha: Double,
      beta: Double,
      depth: Int
  ): (Move, Double) = {
    if (depth <= 0) return (null, evaluator.Evaluate(board, color))
    var b = beta
    var finalMin: (Move, Double) = (null, Double.MaxValue)
    val successors = board.getNextMoves
    if (successors.isEmpty) return (null, evaluator.Evaluate(board, color))
    successors.foreach { case (move, board) =>
      val maxValue = MaxValue(board, color, alpha, b, depth - 1)
      if (maxValue._2 < finalMin._2) finalMin = (move, maxValue._2)
      if (finalMin._2 <= alpha) return finalMin
      b = math.min(b, finalMin._2)
    }
    finalMin
  }
}
