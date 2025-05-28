package model

import org.scalatest.funsuite.AnyFunSuite

class KnightTest extends AnyFunSuite {

  test("testGetLegalMoves") {
    val board = new StandardBoard(
      Map(Square(5, 6) -> Pawn(Color.White), Square(6, 5) -> Pawn(Color.Black)),
      Color.White
    )
    val knight = Knight(Color.White)
    assertResult(
      Set(
        NormalMove(Square(4, 4), Square(6, 5), knight, true),
        NormalMove(Square(4, 4), Square(2, 5), knight),
        NormalMove(Square(4, 4), Square(5, 2), knight),
        NormalMove(Square(4, 4), Square(3, 6), knight),
        NormalMove(Square(4, 4), Square(6, 3), knight),
        NormalMove(Square(4, 4), Square(2, 3), knight),
        NormalMove(Square(4, 4), Square(3, 2), knight)
      )
    ) {
      knight.getLegalMoves(Square(4, 4), board)
    }
  }

  test("testGetLegalMoves_SomeOOBFromRim") {
    val board = new StandardBoard(Map(), Color.White)
    val knight = Knight(Color.White)
    assertResult(
      Set(
        NormalMove(Square(1, 4), Square(2, 6), knight),
        NormalMove(Square(1, 4), Square(2, 2), knight),
        NormalMove(Square(1, 4), Square(3, 5), knight),
        NormalMove(Square(1, 4), Square(3, 3), knight)
      )
    ) {
      knight.getLegalMoves(Square(1, 4), board)
    }
  }

}
