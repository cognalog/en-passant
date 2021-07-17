package actor

import ai.search.MoveSearch
import model.Color.Color
import model.{Board, Move}

import scala.util.{Success, Try}

case class BotPlayer(moveSearch: MoveSearch) extends Player {
  override def GetNextMove(board: Board, color: Color): Try[Move] = Success(moveSearch.GetBestMove(board, color))
}
