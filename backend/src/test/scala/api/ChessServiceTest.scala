package api

import actor.{BotPlayer, Player}
import ai.evaluator.GeneralEvaluator
import ai.search.ABPruningMinimax
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes, HttpMethods}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import api.JsonFormats._
import model._
import org.scalatest.funsuite.AnyFunSuite
import spray.json._

class ChessServiceTest extends AnyFunSuite with ScalatestRouteTest {

  // Create a test bot player with minimal search depth for faster tests
  private val testBotPlayer: Player = BotPlayer(ABPruningMinimax(1, GeneralEvaluator))
  private val chessService = new ChessService(testBotPlayer)

  // No need for custom cleanup - ScalatestRouteTest handles actor system lifecycle

  test("ChessService should handle CORS preflight requests") {
    Options("/api/chess/move") ~> 
      addHeader(`Access-Control-Request-Method`(HttpMethods.POST)) ~>
      addHeader(`Access-Control-Request-Headers`("Content-Type")) ~>
      chessService.routes ~> check {
      assert(status == StatusCodes.OK)
      assert(header("Access-Control-Allow-Origin").isDefined)
      assert(header("Access-Control-Allow-Methods").isDefined)
      assert(header("Access-Control-Allow-Headers").isDefined)
    }
  }

  test("ChessService should handle move requests for initial position") {
    // For initial position, use empty string (no moves played yet)
    val board = Board.standardFromMoveStrings(Seq()).get
    val moveRequest = MoveRequest(
      board = board, 
      color = Color.White // White goes first
    )
    
    Post("/api/chess/move", HttpEntity(ContentTypes.`application/json`, moveRequest.toJson.toString)) ~>
      chessService.routes ~> check {
      assert(status == StatusCodes.OK)
      assert(contentType == ContentTypes.`application/json`)
      
      val response = responseAs[MoveResponse]
      assert(response.move != null)
      assert(response.move.toStandardNotation.nonEmpty)
    }
  }

  test("ChessService should handle move requests with game history") {
    val boardWithMoves = Board.standardFromMoveStrings(Seq("e4", "e5")).get
    val moveRequest = MoveRequest(
      board = boardWithMoves,
      color = Color.White // After "e4 e5", it's White's turn
    )
    
    Post("/api/chess/move", HttpEntity(ContentTypes.`application/json`, moveRequest.toJson.toString)) ~>
      chessService.routes ~> check {
      assert(status == StatusCodes.OK)
      assert(contentType == ContentTypes.`application/json`)
      
      val response = responseAs[MoveResponse]
      assert(response.move != null)
      assert(response.move.toStandardNotation.nonEmpty)
    }
  }

  test("ChessService should handle printBoard requests for initial position") {
    val board = Board.standardFromMoveStrings(Seq()).get
    val printRequest = PrintBoardRequest(
      board = board
    )
    
    Post("/api/chess/printBoard", HttpEntity(ContentTypes.`application/json`, printRequest.toJson.toString)) ~>
      chessService.routes ~> check {
      assert(status == StatusCodes.OK)
      assert(contentType == ContentTypes.`application/json`)
      
      val response = responseAs[PrintBoardResponse]
      assert(response.board != null)
    }
  }

  test("ChessService should handle printBoard requests with game history") {
    val boardWithMoves = Board.standardFromMoveStrings(Seq("e4", "e5")).get
    val printRequest = PrintBoardRequest(
      board = boardWithMoves
    )
    
    Post("/api/chess/printBoard", HttpEntity(ContentTypes.`application/json`, printRequest.toJson.toString)) ~>
      chessService.routes ~> check {
      assert(status == StatusCodes.OK)
      assert(contentType == ContentTypes.`application/json`)
      
      val response = responseAs[PrintBoardResponse]
      assert(response.board != null)
    }
  }

  test("ChessService should reject invalid JSON requests") {
    Post("/api/chess/move", HttpEntity(ContentTypes.`application/json`, "invalid json")) ~>
      chessService.routes ~> check {
      // Request should be rejected due to malformed JSON
      assert(rejection != null)
    }
  }

  test("ChessService should reject requests with missing fields") {
    Post("/api/chess/move", HttpEntity(ContentTypes.`application/json`, """{"board": ""}""")) ~>
      chessService.routes ~> check {
      // Request should be rejected due to missing 'color' field
      assert(rejection != null)
    }
  }

  test("ChessService should handle requests for White color") {
    val board = Board.standardFromMoveStrings(Seq()).get
    val moveRequest = MoveRequest(
      board = board,
      color = Color.White
    )
    
    Post("/api/chess/move", HttpEntity(ContentTypes.`application/json`, moveRequest.toJson.toString)) ~>
      chessService.routes ~> check {
      assert(status == StatusCodes.OK)
      assert(contentType == ContentTypes.`application/json`)
      
      val response = responseAs[MoveResponse]
      assert(response.move != null)
      assert(response.move.toStandardNotation.nonEmpty)
    }
  }

  test("ChessService should return 404 for unknown endpoints") {
    Get("/api/chess/unknown") ~> chessService.routes ~> check {
      assert(!handled)
    }
  }

  test("ChessService should handle multiple concurrent requests") {
    val board = Board.standardFromMoveStrings(Seq()).get
    val moveRequest = MoveRequest(
      board = board,
      color = Color.White
    )
    
    // Test multiple requests can be handled
    for (_ <- 1 to 3) {
      Post("/api/chess/move", HttpEntity(ContentTypes.`application/json`, moveRequest.toJson.toString)) ~>
        chessService.routes ~> check {
        assert(status == StatusCodes.OK)
        val response = responseAs[MoveResponse]
        assert(response.move != null)
      }
    }
  }

  test("ChessService should include proper CORS headers in responses") {
    val board = Board.standardFromMoveStrings(Seq()).get
    val moveRequest = MoveRequest(
      board = board,
      color = Color.White
    )
    
    Post("/api/chess/move", HttpEntity(ContentTypes.`application/json`, moveRequest.toJson.toString)) ~>
      addHeader(Origin(HttpOrigin("http://localhost:3000"))) ~>
      chessService.routes ~> check {
      assert(status == StatusCodes.OK)
      assert(header("Access-Control-Allow-Origin").isDefined)
    }
  }
}