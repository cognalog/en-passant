package ai.evaluator

import model._
import org.scalatest.funsuite.AnyFunSuite

class PieceDevelopmentEvaluatorTest extends AnyFunSuite {

  test("testEvaluate_startingPosition") {
    assertResult(0) {
      PieceDevelopmentEvaluator.Evaluate(
        StandardBoard.StartingPosition,
        Color.White
      )
    }
  }

  test("testEvaluate_arbitraryBoard") {
    val board = StandardBoard(
      Map(
        Square(3, 3) -> Knight(Color.White),
        Square(3, 1) -> Bishop(Color.White),
        Square(5, 1) -> King(Color.White),
        Square(2, 8) -> Knight(Color.Black),
        Square(4, 4) -> Bishop(Color.Black),
        Square(5, 8) -> King(Color.Black)
      )
    )
    assertResult(8 + 7 - 3 - 11) {
      PieceDevelopmentEvaluator.Evaluate(board, Color.White)
    }
  }

}
