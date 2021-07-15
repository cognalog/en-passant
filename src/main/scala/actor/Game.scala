package actor

import model.Color.Color
import model.{Board, StandardBoard}

import scala.collection.mutable.ListBuffer

class Game(val players: Map[Color, Player]) {
  var boardLog: ListBuffer[Board] = ListBuffer()

  def Play(): Unit = {
    var currentBoard: Board = StandardBoard.StartingPosition
    while (!IsGameOver(currentBoard)) {
      boardLog.append(currentBoard)
      print(currentBoard)
      val turnColor = currentBoard.turnColor
      val nextMove = players(turnColor).GetNextMove(currentBoard, turnColor)
      val moveAttempt = currentBoard.move(nextMove)
      if (moveAttempt.isLeft) throw new IllegalStateException(s"Bad move from $turnColor player: $nextMove")
      currentBoard = moveAttempt.fold(_ => StandardBoard(Map()), board => board)
    }
    print(boardLog.toList)
  }

  private def IsGameOver(board: Board): Boolean = board.getNextMoves.nonEmpty
}
