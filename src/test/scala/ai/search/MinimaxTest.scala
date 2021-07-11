package ai.search

import ai.utility.MapUtility
import model._
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class MinimaxTest extends AnyFunSuite with MockFactory {

  test("testGetBestMove_wrongColor") {
    val minimax = Minimax(0, Color.White, (_, _) => 5)
    assertThrows[IllegalArgumentException] {
      minimax.GetBestMove(StandardBoard(Map(Square(1, 1) -> Pawn(Color.White)), turnColor = Color.Black))
    }
  }

  test("testGetBestMove_depth0") {
    val minimax = Minimax(0, Color.White, (_, _) => 5)
    assertResult(null) {
      minimax.GetBestMove(StandardBoard(Map(Square(1, 1) -> Pawn(Color.White))))
    }
  }

  test("testGetBestMove_gameOver") {
    val minimax = Minimax(1, Color.White, (_, _) => 5)
    assertResult(null) {
      minimax.GetBestMove(StandardBoard(Map()))
    }
  }

  test("testGetBestMove_typical") {
    val minimax = Minimax(10, Color.White,
      MapUtility(Map("a" -> 4, "b" -> 1, "c" -> 2, "b1" -> 5, "b2" -> 6, "b3" -> 7, "c1" -> 3, "c2" -> 9)))
    val board = TreeBoard("root", Color.White,
      Map(NormalMove(Square(1, 1), Square(2, 2)) -> TreeBoard("a", Color.Black, Map()),
        NormalMove(Square(1, 1), Square(3, 3)) -> TreeBoard("b", Color.Black,
          Map(NormalMove(Square(3, 3), Square(3, 4)) -> TreeBoard("b1", Color.Black, Map()),
            NormalMove(Square(3, 3), Square(3, 2)) -> TreeBoard("b2", Color.Black, Map()),
            NormalMove(Square(3, 3), Square(2, 2)) -> TreeBoard("b3", Color.Black, Map()))),
        NormalMove(Square(1, 1), Square(4, 4)) -> TreeBoard("c", Color.Black,
          Map(NormalMove(Square(4, 4), Square(5, 5)) -> TreeBoard("c1", Color.Black, Map()),
            CastleMove(Square(5, 5)) -> TreeBoard("c2", Color.Black, Map())))))
    assertResult(NormalMove(Square(1, 1), Square(3, 3)) /* move "b" */) {
      minimax.GetBestMove(board)
    }
  }

  test("testGetBestMove_shallowDepth") {
    val minimax = Minimax(1, Color.White,
      MapUtility(Map("a" -> 4, "b" -> 1, "c" -> 2, "b1" -> 5, "b2" -> 6, "b3" -> 7, "c1" -> 3, "c2" -> 9)))
    val board = TreeBoard("root", Color.White,
      Map(NormalMove(Square(1, 1), Square(2, 2)) -> TreeBoard("a", Color.Black, Map()),
        NormalMove(Square(1, 1), Square(3, 3)) -> TreeBoard("b", Color.Black,
          Map(NormalMove(Square(3, 3), Square(3, 4)) -> TreeBoard("b1", Color.Black, Map()),
            NormalMove(Square(3, 3), Square(3, 2)) -> TreeBoard("b2", Color.Black, Map()),
            NormalMove(Square(3, 3), Square(2, 2)) -> TreeBoard("b3", Color.Black, Map()))),
        NormalMove(Square(1, 1), Square(4, 4)) -> TreeBoard("c", Color.Black,
          Map(NormalMove(Square(4, 4), Square(5, 5)) -> TreeBoard("c1", Color.Black, Map()),
            CastleMove(Square(5, 5)) -> TreeBoard("c2", Color.Black, Map())))))
    assertResult(NormalMove(Square(1, 1), Square(2, 2)) /* move "a" */) {
      minimax.GetBestMove(board)
    }
  }

  test("testMaxValue_depth0") {
    val minimax = Minimax(0, Color.White, (_, _) => 5)
    assertResult((null, 5)) {
      minimax.MaxValue(StandardBoard(Map(Square(1, 1) -> Pawn(Color.White))), 0)
    }
  }

  test("testMaxValue_noMoves") {
    val minimax = Minimax(1, Color.White, (_, _) => 5)
    assertResult((null, 5)) {
      minimax.MaxValue(StandardBoard(Map()), 1)
    }
  }
}
