package model

import org.scalatest.FunSuite

class BoardTest extends FunSuite {

  test("testIsLegalMove_Ok") {
    val board =
      new Board(Map(Square(1, 1) -> new Pawn(Color.White)), Color.White)
    board.checkLegalMove(Square(1, 1), Square(1, 2)) // should not throw
  }

  test("testIsLegalMove_NoPiece") {
    val board = new Board(Map(Square(1, 1) -> new Pawn(Color.White)))
    assertThrows[IllegalStateException] {
      board.checkLegalMove(Square(0, 1), Square(2, 2))
    }
  }

  test("testIsLegalMove_WrongColor") {
    val board =
      new Board(Map(Square(1, 1) -> new Pawn(Color.White)), Color.Black)
    assertThrows[IllegalStateException] {
      board.checkLegalMove(Square(1, 1), Square(1, 2))
    }
  }

  test("testIsLegalMove_PieceIncapable") {
    val board =
      new Board(Map(Square(1, 1) -> new Pawn(Color.White)), Color.White)
    assertThrows[IllegalArgumentException] {
      board.checkLegalMove(Square(1, 1), Square(1, 8))
    }
  }
}
