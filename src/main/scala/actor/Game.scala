package actor

import ai.evaluator.RandomEvaluator
import ai.search.Minimax
import model.Color.Color
import model.{Board, Color, StandardBoard}

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Random, Success, Try}

object Game {
  private val minimax_depth = 3
  private val botGameMaxTurns = 25

  def withRandomColors(player1: Player, player2: Player, maxTurns: Int = Int.MaxValue): Game = {
    val color1 = if (Random.nextBoolean()) Color.White else Color.Black
    Game(Map(color1 -> player1, Color.opposite(color1) -> player2), maxTurns)
  }

  def humanVsBot: Game = withRandomColors(HumanPlayer(), BotPlayer(Minimax(minimax_depth, RandomEvaluator)))

  def botVsBot: Game = withRandomColors(BotPlayer(Minimax(minimax_depth, RandomEvaluator)),
    BotPlayer(Minimax(minimax_depth, RandomEvaluator)), botGameMaxTurns)
}

case class Game(players: Map[Color, Player], maxTurns: Int = Int.MaxValue) {

  def Play(): Try[List[Board]] = {
    var turns = 0
    val boardLog: ListBuffer[Board] = ListBuffer()
    var currentBoard: Board = StandardBoard.StartingPosition
    while (currentBoard.getNextMoves.nonEmpty && turns / 2 < maxTurns) {
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
