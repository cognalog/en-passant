package actor

import ai.search.MoveSearch
import model.Color.Color
import model.{Board, Move}

class BotPlayer(val moveSearch: MoveSearch) extends Player {
  override def GetNextMove(board: Board, color: Color): Move = moveSearch.GetBestMove(board, color)
}
