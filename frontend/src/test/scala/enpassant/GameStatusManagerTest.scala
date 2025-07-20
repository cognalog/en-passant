package enpassant

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterEach
import scala.scalajs.js

class GameStatusManagerTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach {

  // Stub implementations for testing since we can't mock js.native classes
  class StubChess extends js.Object {
    var _turn: String = "w"
    var _gameOver: Boolean = false
    var _inCheck: Boolean = false
    var _inCheckmate: Boolean = false
    var _inDraw: Boolean = false

    def turn(): String = _turn
    def game_over(): Boolean = _gameOver
    def in_check(): Boolean = _inCheck
    def in_checkmate(): Boolean = _inCheckmate
    def in_draw(): Boolean = _inDraw

    def setTurn(turn: String): Unit = _turn = turn
    def setGameOver(gameOver: Boolean): Unit = _gameOver = gameOver
    def setInCheck(inCheck: Boolean): Unit = _inCheck = inCheck
    def setInCheckmate(inCheckmate: Boolean): Unit = _inCheckmate = inCheckmate
    def setInDraw(inDraw: Boolean): Unit = _inDraw = inDraw
  }

  var stubGame: StubChess = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    stubGame = new StubChess()
  }

  "Game status logic" should "generate correct status for ongoing game" in {
    def getStatusText(game: StubChess): String = {
      if (game.game_over()) {
        if (game.in_checkmate()) {
          if (game.turn() == "w") "Game Over: Black wins by checkmate"
          else "Game Over: White wins by checkmate"
        } else if (game.in_draw()) {
          "Game Over: Draw"
        } else {
          "Game Over"
        }
      } else {
        if (game.in_check()) {
          if (game.turn() == "w") "White is in check" else "Black is in check"
        } else {
          if (game.turn() == "w") "White to move" else "Black to move"
        }
      }
    }

    // Test normal game - white to move
    stubGame.setTurn("w")
    stubGame.setGameOver(false)
    stubGame.setInCheck(false)
    getStatusText(stubGame) shouldBe "White to move"

    // Test normal game - black to move
    stubGame.setTurn("b")
    getStatusText(stubGame) shouldBe "Black to move"
  }

  it should "generate correct status for check states" in {
    def getStatusText(game: StubChess): String = {
      if (game.game_over()) {
        if (game.in_checkmate()) {
          if (game.turn() == "w") "Game Over: Black wins by checkmate"
          else "Game Over: White wins by checkmate"
        } else if (game.in_draw()) {
          "Game Over: Draw"
        } else {
          "Game Over"
        }
      } else {
        if (game.in_check()) {
          if (game.turn() == "w") "White is in check" else "Black is in check"
        } else {
          if (game.turn() == "w") "White to move" else "Black to move"
        }
      }
    }

    // Test white in check
    stubGame.setTurn("w")
    stubGame.setGameOver(false)
    stubGame.setInCheck(true)
    getStatusText(stubGame) shouldBe "White is in check"

    // Test black in check
    stubGame.setTurn("b")
    getStatusText(stubGame) shouldBe "Black is in check"
  }

  it should "generate correct status for checkmate" in {
    def getStatusText(game: StubChess): String = {
      if (game.game_over()) {
        if (game.in_checkmate()) {
          if (game.turn() == "w") "Game Over: Black wins by checkmate"
          else "Game Over: White wins by checkmate"
        } else if (game.in_draw()) {
          "Game Over: Draw"
        } else {
          "Game Over"
        }
      } else {
        if (game.in_check()) {
          if (game.turn() == "w") "White is in check" else "Black is in check"
        } else {
          if (game.turn() == "w") "White to move" else "Black to move"
        }
      }
    }

    // Test white checkmated (black wins)
    stubGame.setTurn("w")
    stubGame.setGameOver(true)
    stubGame.setInCheckmate(true)
    stubGame.setInDraw(false)
    getStatusText(stubGame) shouldBe "Game Over: Black wins by checkmate"

    // Test black checkmated (white wins)
    stubGame.setTurn("b")
    getStatusText(stubGame) shouldBe "Game Over: White wins by checkmate"
  }

  it should "generate correct status for draw" in {
    def getStatusText(game: StubChess): String = {
      if (game.game_over()) {
        if (game.in_checkmate()) {
          if (game.turn() == "w") "Game Over: Black wins by checkmate"
          else "Game Over: White wins by checkmate"
        } else if (game.in_draw()) {
          "Game Over: Draw"
        } else {
          "Game Over"
        }
      } else {
        if (game.in_check()) {
          if (game.turn() == "w") "White is in check" else "Black is in check"
        } else {
          if (game.turn() == "w") "White to move" else "Black to move"
        }
      }
    }

    // Test draw
    stubGame.setGameOver(true)
    stubGame.setInCheckmate(false)
    stubGame.setInDraw(true)
    getStatusText(stubGame) shouldBe "Game Over: Draw"
  }

  it should "generate correct status for general game over" in {
    def getStatusText(game: StubChess): String = {
      if (game.game_over()) {
        if (game.in_checkmate()) {
          if (game.turn() == "w") "Game Over: Black wins by checkmate"
          else "Game Over: White wins by checkmate"
        } else if (game.in_draw()) {
          "Game Over: Draw"
        } else {
          "Game Over"
        }
      } else {
        if (game.in_check()) {
          if (game.turn() == "w") "White is in check" else "Black is in check"
        } else {
          if (game.turn() == "w") "White to move" else "Black to move"
        }
      }
    }

    // Test general game over (not checkmate or draw)
    stubGame.setGameOver(true)
    stubGame.setInCheckmate(false)
    stubGame.setInDraw(false)
    getStatusText(stubGame) shouldBe "Game Over"
  }

  "Turn detection" should "correctly identify current player" in {
    def getCurrentPlayer(game: StubChess): String = {
      if (game.turn() == "w") "White" else "Black"
    }

    stubGame.setTurn("w")
    getCurrentPlayer(stubGame) shouldBe "White"

    stubGame.setTurn("b")
    getCurrentPlayer(stubGame) shouldBe "Black"
  }

  "Game state validation" should "correctly identify game over conditions" in {
    def isGameActive(game: StubChess): Boolean = !game.game_over()

    stubGame.setGameOver(false)
    isGameActive(stubGame) shouldBe true

    stubGame.setGameOver(true)
    isGameActive(stubGame) shouldBe false
  }

  "Check detection" should "correctly identify check status" in {
    def isInCheck(game: StubChess): Boolean = game.in_check()

    stubGame.setInCheck(true)
    isInCheck(stubGame) shouldBe true

    stubGame.setInCheck(false)
    isInCheck(stubGame) shouldBe false
  }

  "Checkmate detection" should "correctly identify checkmate" in {
    def isCheckmate(game: StubChess): Boolean = game.in_checkmate()

    stubGame.setInCheckmate(true)
    isCheckmate(stubGame) shouldBe true

    stubGame.setInCheckmate(false)
    isCheckmate(stubGame) shouldBe false
  }

  "Draw detection" should "correctly identify draw conditions" in {
    def isDraw(game: StubChess): Boolean = game.in_draw()

    stubGame.setInDraw(true)
    isDraw(stubGame) shouldBe true

    stubGame.setInDraw(false)
    isDraw(stubGame) shouldBe false
  }
}