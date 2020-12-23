package model

import org.scalatest.FunSuite

class BoardTest extends FunSuite {

  test("testIsLegalMove_NoPiece") {
    val board  = new Board(Map(Square(1, 1) -> new Pawn(Color.White)))
    val result = board.isLegalMove(Square(0, 1), Square(2, 2))
    assert(result.isLeft)
    assert(result.swap.getOrElse("wrong").contains("no piece"))
  }

  test("testIsLegalMove_WrongColor") {
    val board =
      new Board(Map(Square(1, 1) -> new Pawn(Color.White)), Color.Black)
    val result = board.isLegalMove(Square(1, 1), Square(1, 2))
    assert(result.isLeft)
    assert(result.swap.getOrElse("wrong").contains("Black's turn"))
  }
}
