package ai.search

import model.{Board, Move}

trait MoveSearch {

  def GetBestMove(b: Board): Move

}
