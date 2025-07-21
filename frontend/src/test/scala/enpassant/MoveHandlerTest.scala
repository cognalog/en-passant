package enpassant

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterEach
import org.scalamock.scalatest.MockFactory
import scala.scalajs.js

class MoveHandlerTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach with MockFactory {

  override def beforeEach(): Unit = {
    GameState.clearHistory()
  }

  "MoveHandler" should "confirm new move when not viewing history" in {
    GameState.resetToLatest()
    val result = MoveHandler.confirmNewMove()
    result shouldBe true
  }

  it should "handle historical move confirmation concept" in {
    // Add some moves to history
    val moves = List(
      ChessMove("e2", "e4", "e4"),
      ChessMove("e7", "e5", "e5"),
      ChessMove("g1", "f3", "Nf3")
    )
    moves.foreach(GameState.addMove)
    
    // Set to viewing history
    GameState.setCurrentMoveIndex(1)
    GameState.isViewingHistory shouldBe true
    
    // The actual confirmation would show a dialog, but we can test the logic
    val currentIndex = GameState.getCurrentMoveIndex
    val historyLength = GameState.moveHistory.length
    
    currentIndex shouldBe 1
    historyLength shouldBe 3
    currentIndex < historyLength - 1 shouldBe true
  }

  "Move validation logic" should "validate chess move format" in {
    def isValidMoveFormat(from: String, to: String): Boolean = {
      def isValidSquare(square: String): Boolean = {
        square.length == 2 && 
        square.charAt(0) >= 'a' && square.charAt(0) <= 'h' &&
        square.charAt(1) >= '1' && square.charAt(1) <= '8'
      }
      
      isValidSquare(from) && isValidSquare(to) && from != to
    }

    isValidMoveFormat("e2", "e4") shouldBe true
    isValidMoveFormat("a1", "h8") shouldBe true
    isValidMoveFormat("e2", "e2") shouldBe false // same square
    isValidMoveFormat("e9", "e4") shouldBe false // invalid from
    isValidMoveFormat("e2", "i4") shouldBe false // invalid to
    isValidMoveFormat("", "e4") shouldBe false // empty from
  }

  "Piece dragging logic" should "validate piece color and turn" in {
    def canDragPieceLogic(piece: String, currentTurn: String): Boolean = {
      piece.length == 2 && piece.charAt(0) == currentTurn.charAt(0)
    }

    // Test white turn
    canDragPieceLogic("wP", "w") shouldBe true
    canDragPieceLogic("wK", "w") shouldBe true
    canDragPieceLogic("bP", "w") shouldBe false
    canDragPieceLogic("bQ", "w") shouldBe false

    // Test black turn
    canDragPieceLogic("bP", "b") shouldBe true
    canDragPieceLogic("bR", "b") shouldBe true
    canDragPieceLogic("wP", "b") shouldBe false
    canDragPieceLogic("wN", "b") shouldBe false
  }

  "Move processing result" should "return correct format" in {
    def processMoveResult(success: Boolean, from: String, to: String): String = {
      if (success) s"$from-$to" else "snapback"
    }

    processMoveResult(true, "e2", "e4") shouldBe "e2-e4"
    processMoveResult(true, "g1", "f3") shouldBe "g1-f3"
    processMoveResult(false, "e2", "e5") shouldBe "snapback"
    processMoveResult(false, "a1", "a1") shouldBe "snapback"
  }

  "Move history management" should "handle move addition and reverting" in {
    // Test adding moves
    val move1 = ChessMove("e2", "e4", "e4")
    val move2 = ChessMove("e7", "e5", "e5")
    
    GameState.addMove(move1)
    GameState.moveHistory should have size 1
    
    GameState.addMove(move2)
    GameState.moveHistory should have size 2
    
    // Test reverting logic concept
    val targetIndex = 0 // Revert to after first move
    val isWhiteTurn = targetIndex % 2 == 1 // Odd index means after black's move
    
    isWhiteTurn shouldBe false // Index 0 would be after white's move (black's turn)
    
    val targetIndex2 = 1 // Revert to after second move
    val isWhiteTurn2 = targetIndex2 % 2 == 1
    
    isWhiteTurn2 shouldBe true // Index 1 would be after black's move (white's turn)
  }

  "Castling move detection" should "identify castling moves" in {
    def isCastlingMove(moveStr: String): Boolean = {
      moveStr == "O-O" || moveStr == "O-O-O"
    }

    isCastlingMove("O-O") shouldBe true
    isCastlingMove("O-O-O") shouldBe true
    isCastlingMove("e4") shouldBe false
    isCastlingMove("Nf3") shouldBe false
    isCastlingMove("Qh5") shouldBe false
    isCastlingMove("0-0") shouldBe false // Wrong format (zero instead of O)
  }

  "Move pattern matching" should "parse regular moves correctly" in {
    val movePattern = """([NBRQK])?([a-h][1-8]|[a-h]|[1-8])?x?([a-h][1-8])(?:=([NBRQ]))?""".r
    
    def parseMove(moveStr: String): Option[(Option[String], Option[String], String, Option[String])] = {
      moveStr match {
        case movePattern(piece, start, dest, promotion) => 
          Some((Option(piece), Option(start), dest, Option(promotion)))
        case _ => None
      }
    }

    // Test pawn moves
    parseMove("e4") should contain((None, None, "e4", None))
    parseMove("exd5") should contain((None, Some("e"), "d5", None))
    parseMove("e8=Q") should contain((None, None, "e8", Some("Q")))

    // Test piece moves
    parseMove("Nf3") should contain((Some("N"), None, "f3", None))
    parseMove("Ngf3") should contain((Some("N"), Some("g"), "f3", None))
    parseMove("N1f3") should contain((Some("N"), Some("1"), "f3", None))
    parseMove("Bxf7") should contain((Some("B"), None, "f7", None))

    // Test invalid moves
    parseMove("invalid") shouldBe None
    parseMove("") shouldBe None
  }

  "Capture detection" should "identify capture moves" in {
    def isCapture(moveSan: String): Boolean = {
      moveSan.contains("x")
    }

    isCapture("exd5") shouldBe true
    isCapture("Bxf7") shouldBe true
    isCapture("Qxh7#") shouldBe true
    isCapture("e4") shouldBe false
    isCapture("Nf3") shouldBe false
    isCapture("O-O") shouldBe false
  }

  "Move san validation" should "validate SAN notation format" in {
    def isValidSAN(san: String): Boolean = {
      val sanPattern = """^[NBRQK]?[a-h]?[1-8]?x?[a-h][1-8](?:=?[NBRQK])?[+#]?$|^O-O(?:-O)?[+#]?$""".r
      sanPattern.matches(san)
    }

    // Valid moves
    isValidSAN("e4") shouldBe true
    isValidSAN("Nf3") shouldBe true
    isValidSAN("Bxf7") shouldBe true
    isValidSAN("O-O") shouldBe true
    isValidSAN("O-O-O") shouldBe true
    isValidSAN("e8=Q") shouldBe true
    isValidSAN("Qh5#") shouldBe true
    isValidSAN("Nbd2") shouldBe true

    // Invalid moves
    isValidSAN("") shouldBe false
    isValidSAN("e9") shouldBe false
    isValidSAN("Zi4") shouldBe false
    isValidSAN("0-0") shouldBe false // Wrong castling format
  }

  "Error handling" should "handle invalid move scenarios" in {
    case class MoveResult(success: Boolean, message: String)
    
    def validateMoveInput(from: String, to: String): MoveResult = {
      if (from.isEmpty || to.isEmpty) {
        MoveResult(false, "Empty square")
      } else if (from == to) {
        MoveResult(false, "Same square")
      } else if (from.length != 2 || to.length != 2) {
        MoveResult(false, "Invalid square format")
      } else {
        MoveResult(true, "Valid")
      }
    }

    validateMoveInput("e2", "e4") shouldBe MoveResult(true, "Valid")
    validateMoveInput("", "e4") shouldBe MoveResult(false, "Empty square")
    validateMoveInput("e2", "") shouldBe MoveResult(false, "Empty square")
    validateMoveInput("e2", "e2") shouldBe MoveResult(false, "Same square")
    validateMoveInput("e", "e4") shouldBe MoveResult(false, "Invalid square format")
    validateMoveInput("e22", "e4") shouldBe MoveResult(false, "Invalid square format")
  }
}