package ai.search

import ai.evaluator.Evaluator
import model.Color.Color
import model.{Board, Move}

case class Minimax(depth: Int, color: Color, utility: Evaluator) extends MoveSearch {

  override def GetBestMove(b: Board): Move = {
    if (b.turnColor != color) throw new IllegalArgumentException(s"It isn't $color's turn'")
    MaxValue(b, depth)._1
  }

  private[search] def MaxValue(b: Board, d: Int): (Move, Int) = {
    if (Terminate(b, d)) return (null, utility.Evaluate(b, color))
    val movesAndScores = b.getNextMoves.map { case (move, board) => (move, MinValue(board, d - 1)._2) }

    if (movesAndScores.isEmpty) (null, utility.Evaluate(b, color)) else movesAndScores
      .max(Ordering.by[(Move, Int), Int](_._2))
  }

  private[search] def MinValue(b: Board, d: Int): (Move, Int) = {
    if (Terminate(b, d)) return (null, utility.Evaluate(b, color))
    val movesAndScores = b.getNextMoves.map { case (move, board) => (move, MaxValue(board, d - 1)._2) }

    if (movesAndScores.isEmpty) (null, utility.Evaluate(b, color)) else movesAndScores
      .min(Ordering.by[(Move, Int), Int](_._2))
  }

  private def Terminate(b: Board, d: Int) = d <= 0
}
