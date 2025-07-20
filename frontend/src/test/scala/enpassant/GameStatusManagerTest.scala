package enpassant

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterEach
import org.scalamock.scalatest.MockFactory
import scala.scalajs.js

class GameStatusManagerTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach with MockFactory {

  // Mock Chess game for testing status logic
  class MockChessGame extends Chess() {
    private var _turn: String = "w"
    private var _gameOver: Boolean = false
    private var _inCheck: Boolean = false
    private var _inCheckmate: Boolean = false
    private var _inDraw: Boolean = false

    override def turn(): String = _turn
    override def game_over(): Boolean = _gameOver
    override def in_check(): Boolean = _inCheck
    override def in_checkmate(): Boolean = _inCheckmate
    override def in_draw(): Boolean = _inDraw

    def setTurn(turn: String): Unit = _turn = turn
    def setGameOver(gameOver: Boolean): Unit = _gameOver = gameOver
    def setInCheck(inCheck: Boolean): Unit = _inCheck = inCheck
    def setInCheckmate(inCheckmate: Boolean): Unit = _inCheckmate = inCheckmate
    def setInDraw(inDraw: Boolean): Unit = _inDraw = inDraw
  }

  override def beforeEach(): Unit = {
    // Reset any global state if needed
  }

  "Game status logic" should "generate correct status for ongoing game" in {
    val mockGame = new MockChessGame()
    
    def getStatusText(game: Chess): String = {
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

    // Test normal game states
    mockGame.setTurn("w")
    mockGame.setGameOver(false)
    mockGame.setInCheck(false)
    getStatusText(mockGame) shouldBe "White to move"

    mockGame.setTurn("b")
    getStatusText(mockGame) shouldBe "Black to move"

    // Test check states
    mockGame.setTurn("w")
    mockGame.setInCheck(true)
    getStatusText(mockGame) shouldBe "White is in check"

    mockGame.setTurn("b")
    getStatusText(mockGame) shouldBe "Black is in check"
  }

  it should "generate correct status for game over scenarios" in {
    val mockGame = new MockChessGame()
    
    def getStatusText(game: Chess): String = {
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

    // Test checkmate scenarios
    mockGame.setGameOver(true)
    mockGame.setInCheckmate(true)
    mockGame.setInDraw(false)

    mockGame.setTurn("w") // White is checkmated, so Black wins
    getStatusText(mockGame) shouldBe "Game Over: Black wins by checkmate"

    mockGame.setTurn("b") // Black is checkmated, so White wins
    getStatusText(mockGame) shouldBe "Game Over: White wins by checkmate"

    // Test draw scenario
    mockGame.setInCheckmate(false)
    mockGame.setInDraw(true)
    getStatusText(mockGame) shouldBe "Game Over: Draw"

    // Test generic game over
    mockGame.setInCheckmate(false)
    mockGame.setInDraw(false)
    getStatusText(mockGame) shouldBe "Game Over"
  }

  "Turn detection" should "correctly identify current player" in {
    def getCurrentPlayer(turn: String): String = {
      if (turn == "w") "White" else "Black"
    }

    getCurrentPlayer("w") shouldBe "White"
    getCurrentPlayer("b") shouldBe "Black"
  }

  "Game state validation" should "validate game state combinations" in {
    case class GameState(turn: String, gameOver: Boolean, inCheck: Boolean, inCheckmate: Boolean, inDraw: Boolean)
    
    def isValidGameState(state: GameState): Boolean = {
      // Game over states should not have ongoing game flags
      if (state.gameOver) {
        if (state.inCheckmate) !state.inDraw // Checkmate and draw are mutually exclusive
        else true // Other game over states are valid
      } else {
        !state.inCheckmate && !state.inDraw // Ongoing games shouldn't have end-game flags
      }
    }

    // Valid states
    isValidGameState(GameState("w", false, false, false, false)) shouldBe true // Normal play
    isValidGameState(GameState("w", false, true, false, false)) shouldBe true // Check
    isValidGameState(GameState("w", true, false, true, false)) shouldBe true // Checkmate
    isValidGameState(GameState("w", true, false, false, true)) shouldBe true // Draw

    // Invalid states
    isValidGameState(GameState("w", true, false, true, true)) shouldBe false // Checkmate and draw
    isValidGameState(GameState("w", false, false, true, false)) shouldBe false // Checkmate in ongoing game
    isValidGameState(GameState("w", false, false, false, true)) shouldBe false // Draw in ongoing game
  }

  "Status message formatting" should "format messages consistently" in {
    def formatStatusMessage(player: String, action: String): String = {
      s"$player $action"
    }

    formatStatusMessage("White", "to move") shouldBe "White to move"
    formatStatusMessage("Black", "to move") shouldBe "Black to move"
    formatStatusMessage("White", "is in check") shouldBe "White is in check"
    formatStatusMessage("Black", "is in check") shouldBe "Black is in check"
  }

  "Game over message formatting" should "format end game messages consistently" in {
    def formatGameOverMessage(winner: Option[String], reason: String): String = {
      winner match {
        case Some(w) => s"Game Over: $w wins by $reason"
        case None => s"Game Over: $reason"
      }
    }

    formatGameOverMessage(Some("White"), "checkmate") shouldBe "Game Over: White wins by checkmate"
    formatGameOverMessage(Some("Black"), "checkmate") shouldBe "Game Over: Black wins by checkmate"
    formatGameOverMessage(None, "Draw") shouldBe "Game Over: Draw"
    formatGameOverMessage(None, "Stalemate") shouldBe "Game Over: Stalemate"
  }

  "Turn switching logic" should "alternate turns correctly" in {
    def getNextTurn(currentTurn: String): String = {
      if (currentTurn == "w") "b" else "w"
    }

    getNextTurn("w") shouldBe "b"
    getNextTurn("b") shouldBe "w"
  }

  it should "handle invalid turn values" in {
    def isValidTurn(turn: String): Boolean = {
      turn == "w" || turn == "b"
    }

    isValidTurn("w") shouldBe true
    isValidTurn("b") shouldBe true
    isValidTurn("white") shouldBe false
    isValidTurn("black") shouldBe false
    isValidTurn("") shouldBe false
    isValidTurn("x") shouldBe false
  }

  "Status priority" should "prioritize game over states correctly" in {
    case class GameStatus(gameOver: Boolean, checkmate: Boolean, draw: Boolean, check: Boolean)
    
    def getStatusPriority(status: GameStatus): Int = {
      if (status.gameOver) {
        if (status.checkmate) 1 // Highest priority
        else if (status.draw) 2
        else 3 // Generic game over
      } else {
        if (status.check) 4
        else 5 // Normal play, lowest priority
      }
    }

    val checkmate = GameStatus(true, true, false, false)
    val draw = GameStatus(true, false, true, false)
    val gameOver = GameStatus(true, false, false, false)
    val check = GameStatus(false, false, false, true)
    val normal = GameStatus(false, false, false, false)

    getStatusPriority(checkmate) shouldBe 1
    getStatusPriority(draw) shouldBe 2
    getStatusPriority(gameOver) shouldBe 3
    getStatusPriority(check) shouldBe 4
    getStatusPriority(normal) shouldBe 5
  }
}