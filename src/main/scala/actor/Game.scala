package actor

import actor.Game.maxRetries
import ai.evaluator.{GeneralEvaluator, RandomEvaluator}
import ai.search.ABPruningMinimax
import model.Color.Color
import model.{Board, Color, StandardBoard}

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Random, Success}

object Game {
  private val defaultMinimaxDepth = 3
  private val randomMinimaxDepth = 3
  private val botGameMaxTurns = 25
  private val maxRetries = 2

  def withRandomColors(player1: Player, player2: Player, maxTurns: Int = Int.MaxValue): Game = {
    val color1 = if (Random.nextBoolean()) Color.White else Color.Black
    Game(Map(color1 -> player1, Color.opposite(color1) -> player2), maxTurns)
  }

  def humanVsBot(depth: Int = defaultMinimaxDepth): Game = withRandomColors(HumanPlayer(),
    BotPlayer(ABPruningMinimax(depth, GeneralEvaluator)))

  def botVsBotEven(depth: Int = defaultMinimaxDepth): Game = withRandomColors(
    BotPlayer(ABPruningMinimax(depth, GeneralEvaluator)),
    BotPlayer(ABPruningMinimax(depth, GeneralEvaluator)), botGameMaxTurns)

  def botVsBotUneven(depth: Int = defaultMinimaxDepth): Game = withRandomColors(
    BotPlayer(ABPruningMinimax(depth, GeneralEvaluator)),
    BotPlayer(ABPruningMinimax(randomMinimaxDepth, RandomEvaluator)), botGameMaxTurns)
}

case class Game(players: Map[Color, Player], maxTurns: Int = Int.MaxValue) {

  def play(): List[Board] = {
    var turns = 0
    val boardLog: ListBuffer[Board] = ListBuffer()
    var currentBoard: Board = StandardBoard.StartingPosition
    while (currentBoard.getNextMoves.nonEmpty && turns / 2 < maxTurns) {
      turns += 1
      boardLog.append(currentBoard)
      val turnColor = currentBoard.turnColor
      var nextMove = players(turnColor).GetNextMove(currentBoard, turnColor)
      // move retry loop
      var retries = 0
      while (nextMove.isFailure && retries < maxRetries) {
        nextMove match {
          case Failure(e) => println(e);
        }
        retries += 1
        nextMove = players(turnColor).GetNextMove(currentBoard, turnColor)
      }

      val moveAttempt = nextMove.flatMap(currentBoard.move(_))
      moveAttempt match {
        case Success(board) => currentBoard = board
        case Failure(e) =>
          print(e)
          return boardLog.toList
      }
    }
    boardLog.append(currentBoard)
    boardLog.toList
  }

}
