package ai.evaluator

import model._
import org.scalatest.funsuite.AnyFunSuite

class PieceScoreEvaluatorTest extends AnyFunSuite {

  test("testEvaluateStandardWhite") {
    assertResult(0) {
      PieceScoreEvaluator.Evaluate(StandardBoard.StartingPosition, Color.White)
    }
  }

  test("testEvaluateArbitraryWhite") {
    val board = StandardBoard(
      Map(
        Square(1, 1) -> Pawn(Color.White),
        Square(1, 2) -> Knight(Color.White),
        Square(1, 3) -> Bishop(Color.White),
        Square(1, 4) -> Rook(Color.White),
        Square(1, 5) -> Queen(Color.Black),
        Square(1, 6) -> Bishop(Color.Black),
        Square(1, 7) -> Knight(Color.Black)
      )
    )
    assertResult(-3) {
      PieceScoreEvaluator.Evaluate(board, Color.White)
    }
  }

  test("testEvaluateStandardBlack") {
    assertResult(0) {
      PieceScoreEvaluator.Evaluate(StandardBoard.StartingPosition, Color.Black)
    }
  }

  test("testEvaluateArbitraryBlack") {
    val board = StandardBoard(
      Map(
        Square(1, 1) -> Pawn(Color.White),
        Square(1, 2) -> Knight(Color.White),
        Square(1, 3) -> Bishop(Color.White),
        Square(1, 4) -> Rook(Color.White),
        Square(1, 5) -> Queen(Color.Black),
        Square(1, 6) -> Bishop(Color.Black),
        Square(1, 7) -> Knight(Color.Black)
      )
    )
    assertResult(3) {
      PieceScoreEvaluator.Evaluate(board, Color.Black)
    }
  }

}
