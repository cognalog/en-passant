package ai.search

import ai.utility.Utility
import model.Color.Color
import model.{Board, Color, Pawn, Square}
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class MinimaxTest extends AnyFunSuite with MockFactory {

  test("testGetBestMove_depth0") {
    val minimax = Minimax(0, Color.White, (b: Board, color: Color) => 5)
    assertResult(null){
      minimax.GetBestMove(Board(Map(Square(1, 1) -> Pawn(Color.White))))
    }
  }

  test("testGetBestMove_gameOver") {
    val minimax = Minimax(1, Color.White, (b: Board, color: Color) => 5)
    assertResult(null){
      minimax.GetBestMove(Board(Map()))
    }
  }

  test("testMaxValue_depth0") {
    val minimax = Minimax(0, Color.White, (b: Board, color: Color) => 5)
    assertResult((null, 5)){
      minimax.MaxValue(Board(Map(Square(1, 1) -> Pawn(Color.White))), 0)
    }
  }

  test("testMaxValue_noMoves") {
    val minimax = Minimax(1, Color.White, (b: Board, color: Color) => 5)
    assertResult((null, 5)){
      minimax.MaxValue(Board(Map()), 1)
    }
  }
}
