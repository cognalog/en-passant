package actor

import model.Color.Color
import model.{Board, StandardBoard}

import scala.collection.mutable.ListBuffer

class Game(val players: Map[Color, Player], val maxTurns: Int = Int.MaxValue) {

  def Play(): List[Board] = {
    var turns = 0
    val boardLog: ListBuffer[Board] = ListBuffer()
    var currentBoard: Board = StandardBoard.StartingPosition
    while (currentBoard.getNextMoves.nonEmpty && turns < maxTurns * 2) {
      turns += 1
      boardLog.append(currentBoard)
      val turnColor = currentBoard.turnColor
      val nextMove = players(turnColor).GetNextMove(currentBoard, turnColor)
      val moveAttempt = currentBoard.move(nextMove)
      if (moveAttempt.isLeft) throw new IllegalStateException(s"Bad move from $turnColor player: $nextMove")
      currentBoard = moveAttempt.fold(_ => StandardBoard(Map()), board => board)
    }
    boardLog.toList
  }
}
