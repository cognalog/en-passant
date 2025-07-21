package api

import actor.{BotPlayer, Player}
import ai.evaluator.GeneralEvaluator
import ai.search.ABPruningMinimax
import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model.headers._
import api.JsonFormats._
import model.Color
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json._

class ChessServiceTest extends AnyWordSpec with Matchers with ScalatestRouteTest {

  // Create a test bot player with minimal search depth for faster tests
  private val testBotPlayer: Player = BotPlayer(ABPruningMinimax(1, GeneralEvaluator))
  private val chessService = new ChessService(testBotPlayer)

  override def afterAll(): Unit = {
    ActorTestKit.shutdown(system)
  }

  "ChessService" should {
    
    "handle CORS preflight requests" in {
      Options("/api/chess/move") ~> 
        addHeader(`Access-Control-Request-Method`(HttpMethods.POST)) ~>
        addHeader(`Access-Control-Request-Headers`("Content-Type")) ~>
        chessService.routes ~> check {
        status shouldEqual StatusCodes.OK
        header("Access-Control-Allow-Origin") shouldBe defined
        header("Access-Control-Allow-Methods") shouldBe defined
        header("Access-Control-Allow-Headers") shouldBe defined
      }
    }

    "handle move requests for initial position" in {
      val moveRequest = MoveRequest(
        board = model.StandardBoard(), 
        color = Color.Black
      )
      
      Post("/api/chess/move", HttpEntity(ContentTypes.`application/json`, moveRequest.toJson.toString)) ~>
        chessService.routes ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        
        val response = responseAs[MoveResponse]
        response.move should not be null
        response.move.toStandardNotation should not be empty
      }
    }

    "handle move requests with game history" in {
      val boardWithMoves = model.Board.standardFromMoveStrings(Seq("e2e4", "e7e5")).get
      val moveRequest = MoveRequest(
        board = boardWithMoves,
        color = Color.Black
      )
      
      Post("/api/chess/move", HttpEntity(ContentTypes.`application/json`, moveRequest.toJson.toString)) ~>
        chessService.routes ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        
        val response = responseAs[MoveResponse]
        response.move should not be null
        response.move.toStandardNotation should not be empty
      }
    }

    "handle printBoard requests for initial position" in {
      val printRequest = PrintBoardRequest(
        board = model.StandardBoard()
      )
      
      Post("/api/chess/printBoard", HttpEntity(ContentTypes.`application/json`, printRequest.toJson.toString)) ~>
        chessService.routes ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        
        val response = responseAs[PrintBoardResponse]
        response.board should not be null
      }
    }

    "handle printBoard requests with game history" in {
      val boardWithMoves = model.Board.standardFromMoveStrings(Seq("e2e4", "e7e5")).get
      val printRequest = PrintBoardRequest(
        board = boardWithMoves
      )
      
      Post("/api/chess/printBoard", HttpEntity(ContentTypes.`application/json`, printRequest.toJson.toString)) ~>
        chessService.routes ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        
        val response = responseAs[PrintBoardResponse]
        response.board should not be null
      }
    }

    "reject invalid JSON requests" in {
      Post("/api/chess/move", HttpEntity(ContentTypes.`application/json`, "invalid json")) ~>
        chessService.routes ~> check {
        status should not equal StatusCodes.OK
      }
    }

    "reject requests with missing fields" in {
      Post("/api/chess/move", HttpEntity(ContentTypes.`application/json`, """{"board": ""}""")) ~>
        chessService.routes ~> check {
        status should not equal StatusCodes.OK
      }
    }

    "handle requests for White color" in {
      val moveRequest = MoveRequest(
        board = model.StandardBoard(),
        color = Color.White
      )
      
      Post("/api/chess/move", HttpEntity(ContentTypes.`application/json`, moveRequest.toJson.toString)) ~>
        chessService.routes ~> check {
        status shouldEqual StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        
        val response = responseAs[MoveResponse]
        response.move should not be null
        response.move.toStandardNotation should not be empty
      }
    }

    "return 404 for unknown endpoints" in {
      Get("/api/chess/unknown") ~> chessService.routes ~> check {
        handled shouldBe false
      }
    }

    "handle multiple concurrent requests" in {
      val moveRequest = MoveRequest(
        board = model.StandardBoard(),
        color = Color.Black
      )
      
      // Test multiple requests can be handled
      for (_ <- 1 to 3) {
        Post("/api/chess/move", HttpEntity(ContentTypes.`application/json`, moveRequest.toJson.toString)) ~>
          chessService.routes ~> check {
          status shouldEqual StatusCodes.OK
          val response = responseAs[MoveResponse]
          response.move should not be null
        }
      }
    }

    "include proper CORS headers in responses" in {
      val moveRequest = MoveRequest(
        board = model.StandardBoard(),
        color = Color.Black
      )
      
      Post("/api/chess/move", HttpEntity(ContentTypes.`application/json`, moveRequest.toJson.toString)) ~>
        addHeader(Origin(HttpOrigin("http://localhost:3000"))) ~>
        chessService.routes ~> check {
        status shouldEqual StatusCodes.OK
        header("Access-Control-Allow-Origin") shouldBe defined
      }
    }
  }
}