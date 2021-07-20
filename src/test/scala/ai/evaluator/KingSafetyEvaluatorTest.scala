package ai.evaluator

import model._
import org.scalatest.funsuite.AnyFunSuite

class KingSafetyEvaluatorTest extends AnyFunSuite {

  test("testEvaluate_standardBoard") {
    assertResult(0) {
      KingSafetyEvaluator.Evaluate(StandardBoard.StartingPosition, Color.White)
    }
  }

  test("testEvaluate_arbitrary") {
    val board = StandardBoard(
      Map(Square(7, 1) -> King(Color.White), Square(6, 1) -> Rook(Color.White), Square(6, 2) -> Pawn(Color.White),
        Square(7, 2) -> Pawn(Color.White), Square(8, 2) -> Pawn(Color.White), Square(4, 4) -> King(Color.Black)))
    assertResult(-1 - -8) {
      KingSafetyEvaluator.Evaluate(board, Color.White)
    }
  }

  test("testEvaluate_check") {
    val board = StandardBoard(
      Map(Square(4, 4) -> King(Color.White), Square(2, 2) -> Bishop(Color.Black), Square(5, 1) -> King(Color.Black)))
    assertResult(-5 - -18) {
      KingSafetyEvaluator.Evaluate(board, Color.Black)
    }
  }

}
