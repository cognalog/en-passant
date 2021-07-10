package ai.search

import ai.utility.Utility
import model.{Board, Move}

case class Minimax(depth: Int, utility: Utility) extends MoveSearch {

  override def GetBestMove(b: Board): Move = {
    MaxValue(b, depth)._1
  }

  private def MaxValue(b: Board, d: Int): (Move, Int) = {
    if (Terminate(b, d)) return (null, utility.Evaluate(b))
    b.getNextMoves.map { case (move, board) => (move, MinValue(board, d - 1)._2) }
      .max(Ordering.by[(Move, Int), Int](_._2))
  }

  private def MinValue(b: Board, d: Int): (Move, Int) = {
    if (Terminate(b, d)) return (null, utility.Evaluate(b))
    b.getNextMoves.map { case (move, board) => (move, MaxValue(board, d - 1)._2) }
      .min(Ordering.by[(Move, Int), Int](_._2))
  }

  private def Terminate(b: Board, d: Int) = d <= 0 || GameOver(b)
}
