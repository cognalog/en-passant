package enpassant

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterEach

class GameStateTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    // Reset state before each test
    GameState.clearHistory()
  }

  "GameState" should "start with empty move history" in {
    GameState.moveHistory shouldBe empty
    GameState.moveHistorySignal.now() shouldBe empty
    GameState.getCurrentMoveIndex shouldBe -1
  }

  it should "add moves to history correctly" in {
    val move1 = ChessMove("e2", "e4", "e4")
    val move2 = ChessMove("e7", "e5", "e5")

    GameState.addMove(move1)
    GameState.moveHistory should have size 1
    GameState.moveHistory.head shouldBe move1
    GameState.moveHistorySignal.now() should have size 1

    GameState.addMove(move2)
    GameState.moveHistory should have size 2
    GameState.moveHistory(1) shouldBe move2
    GameState.moveHistorySignal.now() should have size 2
  }

  it should "clear history correctly" in {
    val move = ChessMove("e2", "e4", "e4")
    GameState.addMove(move)
    GameState.moveHistory should not be empty

    GameState.clearHistory()
    GameState.moveHistory shouldBe empty
    GameState.moveHistorySignal.now() shouldBe empty
    GameState.getCurrentMoveIndex shouldBe -1
  }

  it should "truncate history correctly" in {
    val moves = List(
      ChessMove("e2", "e4", "e4"),
      ChessMove("e7", "e5", "e5"),
      ChessMove("g1", "f3", "Nf3"),
      ChessMove("b8", "c6", "Nc6")
    )
    
    moves.foreach(GameState.addMove)
    GameState.moveHistory should have size 4

    GameState.truncateHistoryTo(1) // Keep first 2 moves
    GameState.moveHistory should have size 2
    GameState.moveHistory shouldBe moves.take(2)
    GameState.moveHistorySignal.now() should have size 2
  }

  it should "manage current move index correctly" in {
    GameState.getCurrentMoveIndex shouldBe -1

    GameState.setCurrentMoveIndex(5)
    GameState.getCurrentMoveIndex shouldBe 5

    GameState.resetToLatest()
    GameState.getCurrentMoveIndex shouldBe -1
  }

  it should "detect viewing history correctly" in {
    val moves = List(
      ChessMove("e2", "e4", "e4"),
      ChessMove("e7", "e5", "e5"),
      ChessMove("g1", "f3", "Nf3")
    )
    
    moves.foreach(GameState.addMove)
    
    // At latest position (index -1)
    GameState.resetToLatest()
    GameState.isViewingHistory shouldBe false

    // At historical position
    GameState.setCurrentMoveIndex(1)
    GameState.isViewingHistory shouldBe true

    // At the last move
    GameState.setCurrentMoveIndex(2)
    GameState.isViewingHistory shouldBe false
  }

  it should "format move pairs correctly" in {
    val moves = List(
      ChessMove("e2", "e4", "e4"),
      ChessMove("e7", "e5", "e5"),
      ChessMove("g1", "f3", "Nf3"),
      ChessMove("b8", "c6", "Nc6"),
      ChessMove("f1", "c4", "Bc4")
    )

    val formatted = GameState.formatMovePairs(moves)
    formatted should have size 3

    formatted(0) shouldBe (1, "e4", "e5")
    formatted(1) shouldBe (2, "Nf3", "Nc6")
    formatted(2) shouldBe (3, "Bc4", "")
  }

  it should "format empty move list correctly" in {
    val formatted = GameState.formatMovePairs(List.empty)
    formatted shouldBe empty
  }

  it should "format single move correctly" in {
    val moves = List(ChessMove("e2", "e4", "e4"))
    val formatted = GameState.formatMovePairs(moves)
    
    formatted should have size 1
    formatted(0) shouldBe (1, "e4", "")
  }

  it should "return correct start position" in {
    val startPos = GameState.getStartPosition
    startPos shouldBe "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
  }

  "ChessMove case class" should "be created with all parameters" in {
    val move = ChessMove("e2", "e4", "e4", Some("q"))
    
    move.from shouldBe "e2"
    move.to shouldBe "e4"
    move.san shouldBe "e4"
    move.promotion shouldBe Some("q")
  }

  it should "be created with default promotion" in {
    val move = ChessMove("e2", "e4", "e4")
    
    move.from shouldBe "e2"
    move.to shouldBe "e4"
    move.san shouldBe "e4"
    move.promotion shouldBe None
  }

  it should "support equality comparison" in {
    val move1 = ChessMove("e2", "e4", "e4", Some("q"))
    val move2 = ChessMove("e2", "e4", "e4", Some("q"))
    val move3 = ChessMove("e2", "e4", "e4", None)

    move1 shouldBe move2
    move1 should not be move3
  }
}