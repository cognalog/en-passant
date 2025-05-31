package ai.search

import model._
import org.scalatest.funsuite.AnyFunSuite

class InterestingMovesFirstTest extends AnyFunSuite {

  test("testCompare_capture") {
    val startBoard = StandardBoard(
      Map(
        Square(1, 2) -> Queen(Color.White),
        Square(2, 3) -> Pawn(Color.Black)
      ),
      turnColor = Color.Black
    )
    val nextBoard1 = StandardBoard(
      Map(Square(1, 2) -> Pawn(Color.Black)),
      turnColor = Color.White
    )
    val nextBoard2 = StandardBoard(
      Map(
        Square(1, 2) -> Queen(Color.White),
        Square(2, 2) -> Pawn(Color.Black)
      ),
      turnColor = Color.White
    )

    assertResult(9) {
      InterestingMovesFirst(startBoard).compare(
        (NormalMove(Square(2, 3), Square(1, 2), Pawn(Color.Black)), nextBoard1),
        (NormalMove(Square(2, 3), Square(2, 2), Pawn(Color.Black)), nextBoard2)
      )
    }
  }

  test("testCompare_check") {
    val startBoard = StandardBoard(
      Map(
        Square(1, 2) -> King(Color.White),
        Square(3, 2) -> Bishop(Color.Black)
      ),
      turnColor = Color.Black
    )
    val nextBoard1 = StandardBoard(
      Map(
        Square(1, 2) -> King(Color.White),
        Square(2, 3) -> Bishop(Color.Black)
      ),
      turnColor = Color.White
    )
    val nextBoard2 = StandardBoard(
      Map(
        Square(1, 2) -> King(Color.White),
        Square(1, 4) -> Bishop(Color.Black)
      ),
      turnColor = Color.White
    )

    assertResult(5) {
      InterestingMovesFirst(startBoard).compare(
        (NormalMove(Square(3, 2), Square(2, 3), Bishop(Color.Black)), nextBoard1),
        (NormalMove(Square(3, 2), Square(1, 4), Bishop(Color.Black)), nextBoard2)
      )
    }
  }

  test("testCompare_combo") {
    val startBoard = StandardBoard(
      Map(
        Square(1, 2) -> King(Color.White),
        Square(2, 3) -> Pawn(Color.White),
        Square(3, 2) -> Bishop(Color.Black)
      ),
      turnColor = Color.Black
    )
    val nextBoard1 = StandardBoard(
      Map(
        Square(1, 2) -> King(Color.White),
        Square(2, 3) -> Bishop(Color.Black)
      ),
      turnColor = Color.White
    )
    val nextBoard2 = StandardBoard(
      Map(
        Square(1, 2) -> King(Color.White),
        Square(2, 3) -> Pawn(Color.White),
        Square(1, 4) -> Bishop(Color.Black)
      ),
      turnColor = Color.White
    )

    assertResult(6) {
      InterestingMovesFirst(startBoard).compare(
        (NormalMove(Square(3, 2), Square(2, 3), Bishop(Color.Black)), nextBoard1),
        (NormalMove(Square(3, 2), Square(1, 4), Bishop(Color.Black)), nextBoard2)
      )
    }
  }

}
