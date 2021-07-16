package actor

import model.Color.Color
import model.{Board, StandardBoard}

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

case class Game(players: Map[Color, Player], maxTurns: Int = Int.MaxValue) {

  def Play(): Try[List[Board]] = {
    var turns = 0
    val boardLog: ListBuffer[Board] = ListBuffer()
    var currentBoard: Board = StandardBoard.StartingPosition
    while (currentBoard.getNextMoves.nonEmpty && turns < maxTurns * 2) {
      turns += 1
      boardLog.append(currentBoard)
      val turnColor = currentBoard.turnColor
      val nextMove = players(turnColor).GetNextMove(currentBoard, turnColor)
      val moveAttempt = nextMove.flatMap(currentBoard.move(_))
      moveAttempt match {
        case Success(board) => currentBoard = board
        case Failure(e) => return Failure(e)
      }
    }
    Success(boardLog.toList)
  }
}
