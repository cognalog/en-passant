package model

import org.scalatest.funsuite.AnyFunSuite

class KnightTest extends AnyFunSuite {

  test("testGetLegalMoves") {
    val board = new Board(Map(Square(5, 6) -> Pawn(Color.White), Square(6, 5) -> Pawn(Color.Black)), Color.White)
    val knight = Knight(Color.White)
    assertResult(Set(Square(6, 5), Square(2, 5), Square(5, 2), Square(3, 6), Square(6, 3), Square(2, 3), Square(3, 2))) {
      knight.getLegalMoves(Square(4, 4), board)
    }
  }

  test("testGetLegalMoves_SomeOOBFromRim") {
    val board = new Board(Map(), Color.White)
    val knight = Knight(Color.White)
    assertResult(Set(Square(2, 6), Square(2,2), Square(3, 5), Square(3, 3))) {
      knight.getLegalMoves(Square(1, 4), board)
    }
  }

}
