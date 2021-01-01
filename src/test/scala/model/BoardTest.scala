package model

import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class BoardTest extends AnyFunSuite with MockFactory {

  test("testIsLegalMove_Ok") {
    val mockPiece = mock[Piece]
    val board =
      new Board(Map(Square(1, 1) -> mockPiece), Color.White)
    (mockPiece.isColor _).expects(Color.White).returning(true)
    board.checkLegalMove(Square(1, 1), Square(1, 2)) // should not throw
  }

  test("testIsLegalMove_NoPiece") {
    val mockPiece = mock[Piece]
    val board = new Board(Map(Square(1, 1) -> mockPiece), Color.White)
    (mockPiece.isColor _).expects(*).never()
    assertThrows[IllegalStateException] {
      board.checkLegalMove(Square(0, 1), Square(2, 2))
    }
  }

  test("testIsLegalMove_WrongColor") {
    val mockPiece = mock[Piece]
    val board =
      new Board(Map(Square(1, 1) -> mockPiece), Color.Black)
    (mockPiece.isColor _).expects(Color.Black).returning(false)
    (mockPiece.color _).expects().returning(Color.White)
    assertThrows[IllegalStateException] {
      board.checkLegalMove(Square(1, 1), Square(1, 2))
    }
  }

  test("testIsLegalMove_OOB") {
    val mockPiece = mock[Piece]
    val board =
      new Board(Map(Square(1, 1) -> mockPiece), Color.White)
    (mockPiece.isColor _).expects(Color.White).returning(true)
    assertThrows[IllegalArgumentException] {
      board.checkLegalMove(Square(1, 1), Square(99, 2))
    }
  }

  test("testMove_OK") {
    val mockPiece = mock[Piece]
    val board =
      new Board(Map(Square(1, 1) -> mockPiece), Color.Black)
    (mockPiece.isColor _).expects(Color.Black).returning(true)
    (mockPiece.updateHasMoved _).expects().returning(mockPiece)
    val result = board.move(Square(1, 1), Square(1, 2))
    result.fold(
      _ => fail(),
      resultBoard => {
        assertResult(None) {
          resultBoard.pieceAt(Square(1, 1))
        }
        assertResult(Some(mockPiece)) {
          resultBoard.pieceAt(Square(1, 2))
        }
        assertResult(Color.White) {
          resultBoard.turnColor
        }
        assertResult(None) {
          resultBoard.enPassant
        }
      }
    )
  }

  test("testMove_EnPassantWhite") {
    val piece = Pawn(Color.White)
    val board =
      new Board(Map(Square(1, 1) -> piece), Color.White)
    val result = board.move(Square(1, 1), Square(1, 3))
    result.fold(
      _ => fail(),
      resultBoard => {
        assertResult(None) {
          resultBoard.pieceAt(Square(1, 1))
        }
        assertResult(Some(Pawn(Color.White, hasMoved = true))) {
          resultBoard.pieceAt(Square(1, 3))
        }
        assertResult(Color.Black) {
          resultBoard.turnColor
        }
        assertResult(Some(Square(1, 2))) {
          resultBoard.enPassant
        }
      }
    )
  }

  test("testMove_EnPassantBlack") {
    val piece = Pawn(Color.Black)
    val board =
      new Board(Map(Square(8, 8) -> piece), Color.Black)
    val result = board.move(Square(8, 8), Square(8, 6))
    result.fold(
      _ => fail(),
      resultBoard => {
        assertResult(None) {
          resultBoard.pieceAt(Square(8, 8))
        }
        assertResult(Some(Pawn(Color.Black, hasMoved = true))) {
          resultBoard.pieceAt(Square(8, 6))
        }
        assertResult(Color.White) {
          resultBoard.turnColor
        }
        assertResult(Some(Square(8, 7))) {
          resultBoard.enPassant
        }
      }
    )
  }
}
