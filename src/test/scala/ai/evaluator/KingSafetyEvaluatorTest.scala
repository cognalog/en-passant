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
      Map(
        Square(7, 1) -> King(Color.White, hasMoved = true, hasCastled = true),
        Square(6, 1) -> Rook(Color.White),
        Square(6, 2) -> Pawn(Color.White),
        Square(7, 2) -> Pawn(Color.White),
        Square(8, 2) -> Pawn(Color.White),
        Square(4, 4) -> King(Color.Black, hasMoved = true)
      )
    )
    assertResult(4 - -13) {
      KingSafetyEvaluator.Evaluate(board, Color.White)
    }
  }
}
