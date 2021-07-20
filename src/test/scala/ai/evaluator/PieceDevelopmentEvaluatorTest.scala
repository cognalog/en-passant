package ai.evaluator

import model._
import org.scalatest.funsuite.AnyFunSuite

class PieceDevelopmentEvaluatorTest extends AnyFunSuite {

  test("testEvaluate_startingPosition") {
    assertResult(0) {
      PieceDevelopmentEvaluator.Evaluate(StandardBoard.StartingPosition, Color.White)
    }
  }

  test("testEvaluate_arbitraryBoard") {
    val board = StandardBoard(
      Map(Square(2, 1) -> Knight(Color.White, hasMoved = true), Square(3, 1) -> Bishop(Color.White),
        Square(5, 1) -> King(Color.White, hasMoved = true), Square(2, 8) -> Knight(Color.Black, hasMoved = true),
        Square(3, 8) -> Bishop(Color.Black, hasMoved = true),
        Square(5, 8) -> King(Color.Black)))
    assertResult(-5) {
      PieceDevelopmentEvaluator.Evaluate(board, Color.White)
    }
  }

}
