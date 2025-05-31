package ai.evaluator

import model._
import org.scalatest.funsuite.AnyFunSuite

class CheckmateEvaluatorTest extends AnyFunSuite {

  test("testEvaluate_forMatedPlayer") {
    val board = StandardBoard(
      Map(
        Square(5, 8) -> King(Color.Black),
        Square(5, 7) -> Queen(Color.White),
        Square(3, 5) -> Bishop(Color.White)
      ),
      turnColor = Color.Black
    )

    assertResult(-9999999) {
      CheckmateEvaluator.Evaluate(board, Color.Black)
    }
  }

  test("testEvaluate_forMatingPlayer") {
    val board = StandardBoard(
      Map(
        Square(5, 8) -> King(Color.Black),
        Square(5, 7) -> Queen(Color.White),
        Square(3, 5) -> Bishop(Color.White)
      ),
      turnColor = Color.Black
    )

    assertResult(9999999) {
      CheckmateEvaluator.Evaluate(board, Color.White)
    }
  }

  test("testEvaluate_noMate") {
    assertResult(0) {
      CheckmateEvaluator.Evaluate(StandardBoard.StartingPosition, Color.White)
    }
  }
}
