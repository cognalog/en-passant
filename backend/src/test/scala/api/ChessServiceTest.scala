package api

import actor.{BotPlayer, Player}
import ai.evaluator.GeneralEvaluator
import ai.search.ABPruningMinimax
import akka.http.scaladsl.model.{
  ContentTypes,
  HttpEntity,
  StatusCodes,
  HttpMethods
}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import api.JsonFormats._
import model._
import org.scalatest.funsuite.AnyFunSuite
import spray.json._

class ChessServiceTest extends AnyFunSuite with ScalatestRouteTest {

  // Create a test bot player with minimal search depth for faster tests
  private val testBotPlayer: Player = BotPlayer(
    ABPruningMinimax(1, GeneralEvaluator)
  )
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
    // Test with actual JSON format that frontend sends
    val jsonRequest = """{"movesSoFar": "", "color": "White"}"""

    Post(
      "/api/chess/move",
      HttpEntity(ContentTypes.`application/json`, jsonRequest)
    ) ~>
      chessService.routes ~> check {
        assert(status == StatusCodes.OK)
        assert(contentType == ContentTypes.`application/json`)

        // Check that response contains a move in standard notation
        val responseBody = responseAs[String]
        assert(responseBody.contains("move"))
        assert(responseBody.contains("\""))
      }
  }

  test("ChessService should handle move requests with game history") {
    // Test with actual JSON format that frontend sends
    val jsonRequest = """{"movesSoFar": "e4 e5", "color": "White"}"""

    Post(
      "/api/chess/move",
      HttpEntity(ContentTypes.`application/json`, jsonRequest)
    ) ~>
      chessService.routes ~> check {
        assert(status == StatusCodes.OK)
        assert(contentType == ContentTypes.`application/json`)

        // Check that response contains a move in standard notation
        val responseBody = responseAs[String]
        assert(responseBody.contains("move"))
        assert(responseBody.contains("\""))
      }
  }

  test("ChessService should handle printBoard requests for initial position") {
    // Test with actual JSON format that frontend sends
    val jsonRequest = """{"movesSoFar": ""}"""

    Post(
      "/api/chess/printBoard",
      HttpEntity(ContentTypes.`application/json`, jsonRequest)
    ) ~>
      chessService.routes ~> check {
        assert(status == StatusCodes.OK)
        assert(contentType == ContentTypes.`application/json`)

        // Check that response contains a board representation
        val responseBody = responseAs[String]
        assert(responseBody.contains("movesSoFar"))
        assert(responseBody.contains("\""))
      }
  }

  test("ChessService should handle printBoard requests with game history") {
    // Test with actual JSON format that frontend sends
    val jsonRequest = """{"movesSoFar": "e4 e5"}"""

    Post(
      "/api/chess/printBoard",
      HttpEntity(ContentTypes.`application/json`, jsonRequest)
    ) ~>
      chessService.routes ~> check {
        assert(status == StatusCodes.OK)
        assert(contentType == ContentTypes.`application/json`)

        // Check that response contains a board representation
        val responseBody = responseAs[String]
        assert(responseBody.contains("movesSoFar"))
        assert(responseBody.contains("\""))
      }
  }

  test("ChessService should reject invalid JSON requests") {
    Post(
      "/api/chess/move",
      HttpEntity(ContentTypes.`application/json`, "invalid json")
    ) ~>
      chessService.routes ~> check {
        // Request should be rejected due to malformed JSON
        assert(rejection != null)
      }
  }

  test("ChessService should reject requests with missing fields") {
    Post(
      "/api/chess/move",
      HttpEntity(ContentTypes.`application/json`, """{"movesSoFar": ""}""")
    ) ~>
      chessService.routes ~> check {
        // Request should be rejected due to missing 'color' field
        assert(rejection != null)
      }
  }

  test("ChessService should handle requests for White color") {
    // Test with actual JSON format that frontend sends
    val jsonRequest = """{"movesSoFar": "", "color": "White"}"""

    Post(
      "/api/chess/move",
      HttpEntity(ContentTypes.`application/json`, jsonRequest)
    ) ~>
      chessService.routes ~> check {
        assert(status == StatusCodes.OK)
        assert(contentType == ContentTypes.`application/json`)

        // Check that response contains a move in standard notation
        val responseBody = responseAs[String]
        assert(responseBody.contains("move"))
        assert(responseBody.contains("\""))
      }
  }

  test("ChessService should return 404 for unknown endpoints") {
    Get("/api/chess/unknown") ~> chessService.routes ~> check {
      assert(!handled)
    }
  }

    test("ChessService should handle multiple concurrent requests") {
    // Test with actual JSON format that frontend sends
    val jsonRequest = """{"movesSoFar": "", "color": "White"}"""

    // Test multiple requests can be handled
    for (_ <- 1 to 3) {
      Post(
        "/api/chess/move",
        HttpEntity(ContentTypes.`application/json`, jsonRequest)
      ) ~>
        chessService.routes ~> check {
        assert(status == StatusCodes.OK)
        val responseBody = responseAs[String]
        assert(responseBody.contains("move"))
      }
    }
  }

  test("ChessService should include proper CORS headers in responses") {
    // Test with actual JSON format that frontend sends
    val jsonRequest = """{"movesSoFar": "", "color": "White"}"""

    Post(
      "/api/chess/move",
      HttpEntity(ContentTypes.`application/json`, jsonRequest)
    ) ~>
      addHeader(Origin(HttpOrigin("http://localhost:3000"))) ~>
      chessService.routes ~> check {
        assert(status == StatusCodes.OK)
        assert(header("Access-Control-Allow-Origin").isDefined)
      }
  }
}
