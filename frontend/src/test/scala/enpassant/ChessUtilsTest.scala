package enpassant

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterEach

class ChessUtilsTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach {

  "ChessUtils.getPieceAt" should "return correct piece from starting position" in {
    val startFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    
    ChessUtils.getPieceAt("e1", startFen) shouldBe Some("wk")
    ChessUtils.getPieceAt("e8", startFen) shouldBe Some("bk")
    ChessUtils.getPieceAt("a1", startFen) shouldBe Some("wr")
    ChessUtils.getPieceAt("h8", startFen) shouldBe Some("br")
    ChessUtils.getPieceAt("d1", startFen) shouldBe Some("wq")
    ChessUtils.getPieceAt("d8", startFen) shouldBe Some("bq")
  }

  it should "return correct piece for pawn positions" in {
    val startFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    
    ChessUtils.getPieceAt("e2", startFen) shouldBe Some("wp")
    ChessUtils.getPieceAt("e7", startFen) shouldBe Some("bp")
    ChessUtils.getPieceAt("a2", startFen) shouldBe Some("wp")
    ChessUtils.getPieceAt("h7", startFen) shouldBe Some("bp")
  }

  it should "return None for empty squares" in {
    val startFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    
    ChessUtils.getPieceAt("e4", startFen) shouldBe None
    ChessUtils.getPieceAt("e5", startFen) shouldBe None
    ChessUtils.getPieceAt("d4", startFen) shouldBe None
    ChessUtils.getPieceAt("f3", startFen) shouldBe None
  }

  it should "handle position after moves" in {
    val fenAfterE4 = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"
    
    ChessUtils.getPieceAt("e4", fenAfterE4) shouldBe Some("wp")
    ChessUtils.getPieceAt("e2", fenAfterE4) shouldBe None
  }

  it should "return None for invalid square format" in {
    val startFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    
    ChessUtils.getPieceAt("", startFen) shouldBe None
    ChessUtils.getPieceAt("e", startFen) shouldBe None
    ChessUtils.getPieceAt("e9", startFen) shouldBe None
    ChessUtils.getPieceAt("z1", startFen) shouldBe None
  }

  it should "return None for empty FEN" in {
    ChessUtils.getPieceAt("e1", "") shouldBe None
  }

  it should "return None for malformed FEN" in {
    ChessUtils.getPieceAt("e1", "invalid-fen") shouldBe None
    ChessUtils.getPieceAt("e1", "rnbqkbnr") shouldBe None // No spaces
  }

  "ChessUtils.getSquareCoordinates" should "return correct coordinates for valid squares" in {
    ChessUtils.getSquareCoordinates("a1") shouldBe (0, 7)
    ChessUtils.getSquareCoordinates("h1") shouldBe (7, 7)
    ChessUtils.getSquareCoordinates("a8") shouldBe (0, 0)
    ChessUtils.getSquareCoordinates("h8") shouldBe (7, 0)
    ChessUtils.getSquareCoordinates("e4") shouldBe (4, 4)
    ChessUtils.getSquareCoordinates("d5") shouldBe (3, 3)
  }

  it should "handle all files correctly" in {
    ChessUtils.getSquareCoordinates("a4") shouldBe (0, 4)
    ChessUtils.getSquareCoordinates("b4") shouldBe (1, 4)
    ChessUtils.getSquareCoordinates("c4") shouldBe (2, 4)
    ChessUtils.getSquareCoordinates("d4") shouldBe (3, 4)
    ChessUtils.getSquareCoordinates("e4") shouldBe (4, 4)
    ChessUtils.getSquareCoordinates("f4") shouldBe (5, 4)
    ChessUtils.getSquareCoordinates("g4") shouldBe (6, 4)
    ChessUtils.getSquareCoordinates("h4") shouldBe (7, 4)
  }

  it should "handle all ranks correctly" in {
    ChessUtils.getSquareCoordinates("e1") shouldBe (4, 7)
    ChessUtils.getSquareCoordinates("e2") shouldBe (4, 6)
    ChessUtils.getSquareCoordinates("e3") shouldBe (4, 5)
    ChessUtils.getSquareCoordinates("e4") shouldBe (4, 4)
    ChessUtils.getSquareCoordinates("e5") shouldBe (4, 3)
    ChessUtils.getSquareCoordinates("e6") shouldBe (4, 2)
    ChessUtils.getSquareCoordinates("e7") shouldBe (4, 1)
    ChessUtils.getSquareCoordinates("e8") shouldBe (4, 0)
  }

  it should "return (-1, -1) for invalid square format" in {
    ChessUtils.getSquareCoordinates("") shouldBe (-1, -1)
    ChessUtils.getSquareCoordinates("e") shouldBe (-1, -1)
    ChessUtils.getSquareCoordinates("e9") shouldBe (-1, -1)
    ChessUtils.getSquareCoordinates("z1") shouldBe (-1, -1)
    ChessUtils.getSquareCoordinates("a0") shouldBe (-1, -1)
    ChessUtils.getSquareCoordinates("i4") shouldBe (-1, -1)
  }

  "FEN parsing edge cases" should "handle complex positions" in {
    // Position with some pieces moved
    val complexFen = "r1bqkb1r/pppp1ppp/2n2n2/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4"
    
    ChessUtils.getPieceAt("e1", complexFen) shouldBe Some("wk")
    ChessUtils.getPieceAt("c4", complexFen) shouldBe Some("wb")
    ChessUtils.getPieceAt("c6", complexFen) shouldBe Some("bn")
    ChessUtils.getPieceAt("f6", complexFen) shouldBe Some("bn")
    ChessUtils.getPieceAt("e4", complexFen) shouldBe Some("wp")
    ChessUtils.getPieceAt("e5", complexFen) shouldBe Some("bp")
  }

  it should "handle positions with many empty squares" in {
    // Position with few pieces
    val sparseFen = "8/8/8/3k4/3K4/8/8/8 w - - 0 1"
    
    ChessUtils.getPieceAt("d5", sparseFen) shouldBe Some("bk")
    ChessUtils.getPieceAt("d4", sparseFen) shouldBe Some("wk")
    ChessUtils.getPieceAt("e4", sparseFen) shouldBe None
    ChessUtils.getPieceAt("a1", sparseFen) shouldBe None
    ChessUtils.getPieceAt("h8", sparseFen) shouldBe None
  }

  "Square validation" should "identify valid chess squares" in {
    def isValidSquare(square: String): Boolean = {
      square.length == 2 && 
      square.charAt(0) >= 'a' && square.charAt(0) <= 'h' &&
      square.charAt(1) >= '1' && square.charAt(1) <= '8'
    }

    // Valid squares
    isValidSquare("a1") shouldBe true
    isValidSquare("h8") shouldBe true
    isValidSquare("e4") shouldBe true
    isValidSquare("d5") shouldBe true

    // Invalid squares
    isValidSquare("") shouldBe false
    isValidSquare("a") shouldBe false
    isValidSquare("1") shouldBe false
    isValidSquare("a9") shouldBe false
    isValidSquare("i1") shouldBe false
    isValidSquare("a0") shouldBe false
    isValidSquare("z5") shouldBe false
  }

  "Piece type detection" should "identify piece types correctly" in {
    def getPieceType(piece: String): Option[Char] = {
      if (piece.length == 2) Some(piece.charAt(1)) else None
    }

    getPieceType("wp") shouldBe Some('p')
    getPieceType("bk") shouldBe Some('k')
    getPieceType("wq") shouldBe Some('q')
    getPieceType("br") shouldBe Some('r')
    getPieceType("wb") shouldBe Some('b')
    getPieceType("bn") shouldBe Some('n')
    getPieceType("") shouldBe None
    getPieceType("x") shouldBe None
  }

  "Piece color detection" should "identify piece colors correctly" in {
    def getPieceColor(piece: String): Option[Char] = {
      if (piece.length == 2) Some(piece.charAt(0)) else None
    }

    getPieceColor("wp") shouldBe Some('w')
    getPieceColor("bp") shouldBe Some('b')
    getPieceColor("wk") shouldBe Some('w')
    getPieceColor("bq") shouldBe Some('b')
    getPieceColor("") shouldBe None
    getPieceColor("x") shouldBe None
  }

  "File and rank conversion" should "convert between different representations" in {
    def fileToIndex(file: Char): Int = file - 'a'
    def rankToIndex(rank: Char): Int = 8 - rank.asDigit
    def indexToFile(index: Int): Char = ('a' + index).toChar
    def indexToRank(index: Int): Char = ('1' + (7 - index)).toChar

    // File conversions
    fileToIndex('a') shouldBe 0
    fileToIndex('h') shouldBe 7
    fileToIndex('e') shouldBe 4

    // Rank conversions
    rankToIndex('1') shouldBe 7
    rankToIndex('8') shouldBe 0
    rankToIndex('4') shouldBe 4

    // Reverse conversions
    indexToFile(0) shouldBe 'a'
    indexToFile(7) shouldBe 'h'
    indexToFile(4) shouldBe 'e'

    indexToRank(7) shouldBe '1'
    indexToRank(0) shouldBe '8'
    indexToRank(4) shouldBe '4'
  }
}