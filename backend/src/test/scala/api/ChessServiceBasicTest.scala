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
    
    // White goes first in starting position
    val moveResult = testBotPlayer.GetNextMove(startingBoard, Color.White)
    
    assert(moveResult.isSuccess)
    assert(moveResult.get.toStandardNotation.nonEmpty)
  }

  test("Bot player can handle game with moves") {
    val testBotPlayer: Player = BotPlayer(ABPruningMinimax(1, GeneralEvaluator))
    val boardWithMoves = Board.standardFromMoveStrings(Seq("e4", "e5")).get
    
    // After "e4 e5", it's White's turn again
    val moveResult = testBotPlayer.GetNextMove(boardWithMoves, Color.White)
    
    assert(moveResult.isSuccess)
    assert(moveResult.get.toStandardNotation.nonEmpty)
  }

  test("StandardBoard can be created from move strings") {
    val boardResult = Board.standardFromMoveStrings(Seq("e4", "e5"))
    
    assert(boardResult.isSuccess)
    assert(boardResult.get != null)
  }

  test("Colors are properly defined") {
    assert(Color.White != Color.Black)
    assert(Color.White.toString == "White")
    assert(Color.Black.toString == "Black")
  }
}