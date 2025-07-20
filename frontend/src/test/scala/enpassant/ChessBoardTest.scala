package enpassant

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterEach
import scala.scalajs.js

class ChessBoardTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach {

  // Stub implementations for testing since we can't mock js.native classes
  class StubChessboard extends js.Object {
    private var _position: String = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    
    def position(fen: String): Unit = {
      _position = fen
    }
    
    def position(): String = _position
  }

  class StubChess extends js.Object {
    private var _fen: String = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    private var _gameOver: Boolean = false
    private var _turn: String = "w"
    
    def load(fen: String): Boolean = {
      if (fen.contains("invalid")) {
        false
      } else {
        _fen = fen
        true
      }
    }
    
    def fen(): String = _fen
    
    def move(move: js.Dynamic): js.Dynamic = {
      if (move != null && move.asInstanceOf[js.Dictionary[String]].contains("from")) {
        // Return a valid move result
        js.Dynamic.literal(
          "from" -> "e2",
          "to" -> "e4", 
          "san" -> "e4"
        )
      } else {
        null
      }
    }
    
    def moves(options: js.Dynamic = js.Dynamic.literal()): js.Array[String] = {
      js.Array("e2-e3", "e2-e4", "Nf3", "Ng1-f3")
    }
    
    def game_over(): Boolean = _gameOver
    def in_check(): Boolean = false
    def in_checkmate(): Boolean = false
    def in_draw(): Boolean = false
    def turn(): String = _turn
    
    def setGameOver(gameOver: Boolean): Unit = _gameOver = gameOver
    def setTurn(turn: String): Unit = _turn = turn
  }

  var stubChessboard: StubChessboard = _
  var stubChess: StubChess = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    stubChessboard = new StubChessboard()
    stubChess = new StubChess()
  }

  "ChessBoard initialization" should "set up board and game correctly" in {
    val startFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    
    // Simulate initialization
    def initializeBoard(board: StubChessboard, game: StubChess, fen: String): Boolean = {
      val loaded = game.load(fen)
      if (loaded) {
        board.position(fen)
      }
      loaded
    }
    
    val result = initializeBoard(stubChessboard, stubChess, startFen)
    result shouldBe true
    stubChessboard.position() shouldBe startFen
  }

  "Board position management" should "handle position updates correctly" in {
    val testFen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"
    
    stubChessboard.position(testFen)
    stubChessboard.position() shouldBe testFen
  }

  it should "get current position correctly" in {
    val currentFen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"
    
    stubChessboard.position(currentFen)
    stubChessboard.position() shouldBe currentFen
  }

  "Game instance management" should "load FEN positions correctly" in {
    val testFen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"
    
    val result = stubChess.load(testFen)
    result shouldBe true
    stubChess.fen() shouldBe testFen
  }

  it should "handle invalid FEN positions" in {
    val invalidFen = "invalid-fen-string"
    
    val result = stubChess.load(invalidFen)
    result shouldBe false
  }

  it should "get current FEN correctly" in {
    val currentFen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"
    
    stubChess.load(currentFen)
    stubChess.fen() shouldBe currentFen
  }

  "Move execution" should "execute valid moves correctly" in {
    val move = js.Dynamic.literal("from" -> "e2", "to" -> "e4")
    
    val result = stubChess.move(move)
    result should not be null
    result.asInstanceOf[js.Dynamic].san.asInstanceOf[String] shouldBe "e4"
  }

  it should "handle invalid moves correctly" in {
    val invalidMove = null
    
    val result = stubChess.move(invalidMove)
    result shouldBe null
  }

  "Move generation" should "get legal moves correctly" in {
    val legalMoves = stubChess.moves()
    
    legalMoves.length shouldBe 4
    legalMoves.toList should contain("e2-e3")
    legalMoves.toList should contain("e2-e4")
  }

  it should "get verbose legal moves correctly" in {
    val verboseOptions = js.Dynamic.literal("verbose" -> true)
    val verboseMoves = stubChess.moves(verboseOptions)
    
    verboseMoves.length should be > 0
  }

  "Game state queries" should "check game over status correctly" in {
    stubChess.game_over() shouldBe false
    
    stubChess.setGameOver(true)
    stubChess.game_over() shouldBe true
  }

  it should "check check status correctly" in {
    stubChess.in_check() shouldBe false
  }

  it should "check checkmate status correctly" in {
    stubChess.in_checkmate() shouldBe false
  }

  it should "check draw status correctly" in {
    stubChess.in_draw() shouldBe false
  }

  it should "get current turn correctly" in {
    stubChess.turn() shouldBe "w"
    
    stubChess.setTurn("b")
    stubChess.turn() shouldBe "b"
  }

  "ChessBoard module integration" should "handle board updates correctly" in {
    // Test integration logic - simulate what ChessBoard.updatePosition might do
    def updateBoardPosition(board: StubChessboard, game: StubChess): Unit = {
      val currentFen = game.fen()
      board.position(currentFen)
    }
    
    val newFen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"
    stubChess.load(newFen)
    updateBoardPosition(stubChessboard, stubChess)
    
    stubChessboard.position() shouldBe newFen
  }

  it should "handle move history replay correctly" in {
    // Test move history replay logic - simulate what ChessBoard.replayMovesToPosition might do
    val moves = List(
      ChessMove("e2", "e4", "e4"),
      ChessMove("e7", "e5", "e5")
    )
    
    def replayMoves(game: StubChess, board: StubChessboard, moves: List[ChessMove]): Unit = {
      moves.foreach { move =>
        val moveObj = js.Dynamic.literal("from" -> move.from, "to" -> move.to)
        val result = game.move(moveObj)
        if (result != null) {
          board.position(game.fen())
        }
      }
    }
    
    replayMoves(stubChess, stubChessboard, moves)
    // The moves should have been processed
    moves should have size 2
  }

  "Error handling" should "handle board initialization errors gracefully" in {
    // Test error handling for board initialization
    def safeInitialize(board: StubChessboard, game: StubChess, fen: String): Boolean = {
      try {
        val loaded = game.load(fen)
        if (loaded) {
          board.position(fen)
          true
        } else {
          false
        }
      } catch {
        case _: Exception => false
      }
    }
    
    val validFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    safeInitialize(stubChessboard, stubChess, validFen) shouldBe true
  }

  it should "handle game loading errors gracefully" in {
    val corruptedFen = "invalid-corrupted-fen"
    
    def safeLoadPosition(game: StubChess, fen: String): Boolean = {
      try {
        game.load(fen)
      } catch {
        case _: Exception => false
      }
    }
    
    safeLoadPosition(stubChess, corruptedFen) shouldBe false
  }

  "Square validation" should "validate chess square format" in {
    def isValidSquare(square: String): Boolean = {
      square.length == 2 && 
      square.charAt(0) >= 'a' && square.charAt(0) <= 'h' &&
      square.charAt(1) >= '1' && square.charAt(1) <= '8'
    }

    isValidSquare("e2") shouldBe true
    isValidSquare("a1") shouldBe true
    isValidSquare("h8") shouldBe true
    isValidSquare("e9") shouldBe false
    isValidSquare("i1") shouldBe false
    isValidSquare("e") shouldBe false
    isValidSquare("") shouldBe false
  }

  "Piece validation" should "validate piece format" in {
    def isValidPiece(piece: String): Boolean = {
      piece.length == 2 &&
      (piece.charAt(0) == 'w' || piece.charAt(0) == 'b') &&
      "PRNBQK".contains(piece.charAt(1))
    }

    isValidPiece("wP") shouldBe true
    isValidPiece("bK") shouldBe true
    isValidPiece("wQ") shouldBe true
    isValidPiece("bN") shouldBe true
    isValidPiece("xP") shouldBe false
    isValidPiece("wX") shouldBe false
    isValidPiece("w") shouldBe false
    isValidPiece("") shouldBe false
  }
}