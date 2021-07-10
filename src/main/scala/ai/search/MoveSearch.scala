package ai.search

import ai.utility.Utility
import model.{Board, Move}

trait MoveSearch {

  def GetBestMove(b: Board): Move

  final def GameOver(b: Board): Boolean = b.getNextMoves.isEmpty

}
