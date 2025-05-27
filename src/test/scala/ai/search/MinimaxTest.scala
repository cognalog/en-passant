package ai.search

import ai.evaluator.MapEvaluator
import model._
import org.scalatest.funsuite.AnyFunSuite

class MinimaxTest extends AnyFunSuite {

  test("testGetBestMove_wrongColor") {
    val minimax = Minimax(0, (_, _) => 5)
    assertThrows[IllegalArgumentException] {
      minimax.GetBestMove(
        StandardBoard(
          Map(Square(1, 1) -> Pawn(Color.White)),
          turnColor = Color.Black
        ),
        Color.White
      )
    }
  }

  test("testGetBestMove_depth0") {
    val minimax = Minimax(0, (_, _) => 5)
    assertResult(null) {
      minimax.GetBestMove(
        StandardBoard(Map(Square(1, 1) -> Pawn(Color.White))),
        Color.White
      )
    }
  }

  test("testGetBestMove_gameOver") {
    val minimax = Minimax(1, (_, _) => 5)
    assertResult(null) {
      minimax.GetBestMove(StandardBoard(Map()), Color.White)
    }
  }

  test("testGetBestMove_typical") {
    val minimax = Minimax(
      10,
      new MapEvaluator(
        Map(
          "a" -> 4,
          "b" -> 1,
          "c" -> 2,
          "b1" -> 5,
          "b2" -> 6,
          "b3" -> 7,
          "c1" -> 3,
          "c2" -> 9
        )
      )
    )
    // Create test pieces
    val piece1 = Pawn(Color.White)
    val piece2 = Pawn(Color.Black)
    val board = TreeBoard(
      "root",
      Color.White,
      children = Map(
        NormalMove(Square(1, 1), Square(2, 2), piece1) -> TreeBoard("a", Color.Black),
        NormalMove(Square(1, 1), Square(3, 3), piece1) -> TreeBoard(
          "b",
          Color.Black,
          children = Map(
            NormalMove(Square(3, 3), Square(3, 4), piece2) -> TreeBoard(
              "b1",
              Color.Black
            ),
            NormalMove(Square(3, 3), Square(3, 2), piece2) -> TreeBoard(
              "b2",
              Color.Black
            ),
            NormalMove(Square(3, 3), Square(2, 2), piece2) -> TreeBoard(
              "b3",
              Color.Black
            )
          )
        ),
        NormalMove(Square(1, 1), Square(4, 4), piece1) -> TreeBoard(
          "c",
          Color.Black,
          children = Map(
            NormalMove(Square(4, 4), Square(5, 5), piece2) -> TreeBoard(
              "c1",
              Color.Black
            ),
            CastleMove(Square(5, 5)) -> TreeBoard("c2", Color.Black)
          )
        )
      )
    )
    assertResult(NormalMove(Square(1, 1), Square(3, 3), piece1) /* move "b" */ ) {
      minimax.GetBestMove(board, Color.White)
    }
  }

  test("testGetBestMove_shallowDepth") {
    val minimax = Minimax(
      1,
      new MapEvaluator(
        Map(
          "a" -> 4,
          "b" -> 1,
          "c" -> 2,
          "b1" -> 5,
          "b2" -> 6,
          "b3" -> 7,
          "c1" -> 3,
          "c2" -> 9
        )
      )
    )
    // Create test pieces
    val piece1 = Pawn(Color.White)
    val piece2 = Pawn(Color.Black)
    val board = TreeBoard(
      "root",
      Color.White,
      children = Map(
        NormalMove(Square(1, 1), Square(2, 2), piece1) -> TreeBoard("a", Color.Black),
        NormalMove(Square(1, 1), Square(3, 3), piece1) -> TreeBoard(
          "b",
          Color.Black,
          children = Map(
            NormalMove(Square(3, 3), Square(3, 4), piece2) -> TreeBoard(
              "b1",
              Color.Black
            ),
            NormalMove(Square(3, 3), Square(3, 2), piece2) -> TreeBoard(
              "b2",
              Color.Black
            ),
            NormalMove(Square(3, 3), Square(2, 2), piece2) -> TreeBoard(
              "b3",
              Color.Black
            )
          )
        ),
        NormalMove(Square(1, 1), Square(4, 4), piece1) -> TreeBoard(
          "c",
          Color.Black,
          children = Map(
            NormalMove(Square(4, 4), Square(5, 5), piece2) -> TreeBoard(
              "c1",
              Color.Black
            ),
            CastleMove(Square(5, 5)) -> TreeBoard("c2", Color.Black)
          )
        )
      )
    )
    assertResult(NormalMove(Square(1, 1), Square(2, 2), piece1) /* move "a" */ ) {
      minimax.GetBestMove(board, Color.White)
    }
  }
}
