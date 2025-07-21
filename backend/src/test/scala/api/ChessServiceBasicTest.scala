package api

import actor.{BotPlayer, Player}
import ai.evaluator.GeneralEvaluator
import ai.search.ABPruningMinimax
import model._
import org.scalatest.funsuite.AnyFunSuite

class ChessServiceBasicTest extends AnyFunSuite {

  test("ChessService can be created with a bot player") {
    val testBotPlayer: Player = BotPlayer(ABPruningMinimax(1, GeneralEvaluator))
    val chessService = new ChessService(testBotPlayer)
    
    assert(chessService != null)
  }

  test("Bot player can generate moves from starting position") {
    val testBotPlayer: Player = BotPlayer(ABPruningMinimax(1, GeneralEvaluator))
    val startingBoard = StandardBoard.StartingPosition
    
    val moveResult = testBotPlayer.GetNextMove(startingBoard.toString, Color.Black)
    
    assert(moveResult.isSuccess)
    assert(moveResult.get.toStandardNotation.nonEmpty)
  }

  test("Bot player can handle game with moves") {
    val testBotPlayer: Player = BotPlayer(ABPruningMinimax(1, GeneralEvaluator))
    val boardWithMoves = "e2e4 e7e5"
    
    val moveResult = testBotPlayer.GetNextMove(boardWithMoves, Color.Black)
    
    assert(moveResult.isSuccess)
    assert(moveResult.get.toStandardNotation.nonEmpty)
  }

  test("StandardBoard can be created from move strings") {
    val boardResult = Board.standardFromMoveStrings(Seq("e2e4", "e7e5"))
    
    assert(boardResult.isSuccess)
    assert(boardResult.get != null)
  }

  test("Colors are properly defined") {
    assert(Color.White != Color.Black)
    assert(Color.White.toString == "White")
    assert(Color.Black.toString == "Black")
  }
}