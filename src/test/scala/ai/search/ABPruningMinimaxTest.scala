package ai.search

import ai.evaluator.MapEvaluator
import model._
import org.scalatest.funsuite.AnyFunSuite

class ABPruningMinimaxTest extends AnyFunSuite {

  test("testGetBestMove_wrongColor") {
    val search = ABPruningMinimax(0, (_, _) => 5)
    assertThrows[IllegalArgumentException] {
      search.GetBestMove(
        StandardBoard(
          Map(Square(1, 1) -> Pawn(Color.White)),
          turnColor = Color.Black
        ),
        Color.White
      )
    }
  }

  test("testGetBestMove_depth0") {
    val search = ABPruningMinimax(0, (_, _) => 5)
    assertResult(null) {
      search.GetBestMove(
        StandardBoard(Map(Square(1, 1) -> Pawn(Color.White))),
        Color.White
      )
    }
  }

  test("testGetBestMove_gameOver") {
    val search = ABPruningMinimax(1, (_, _) => 5)
    assertResult(null) {
      search.GetBestMove(StandardBoard(Map()), Color.White)
    }
  }

  test("testGetBestMove_pruning") {
    val evaluator = new MapEvaluator(
      Map(
        "a1" -> 3,
        "a2" -> 12,
        "a3" -> 8,
        "b1" -> 2,
        "b2" -> 6,
        "b3" -> 7,
        "c1" -> 14,
        "c2" -> 5,
        "c3" -> 2
      )
    )
    val minimax = ABPruningMinimax(10, evaluator)
    // Create test pieces
    val piece1 = Pawn(Color.White)
    val piece2 = Pawn(Color.Black)
    val board = TreeBoard(
      "root",
      Color.White,
      children = Map(
        NormalMove(Square(1, 1), Square(2, 2), piece1) ->
          TreeBoard(
            "a",
            Color.Black,
            children = Map(
              NormalMove(Square(2, 2), Square(3, 4), piece2) -> TreeBoard(
                "a1",
                Color.White
              ),
              NormalMove(Square(2, 2), Square(3, 2), piece2) -> TreeBoard(
                "a2",
                Color.White
              ),
              NormalMove(Square(2, 2), Square(2, 2), piece2) -> TreeBoard(
                "a3",
                Color.White
              )
            )
          ),
        NormalMove(Square(1, 1), Square(3, 3), piece1) ->
          TreeBoard(
            "b",
            Color.Black,
            children = Map(
              NormalMove(Square(3, 3), Square(3, 4), piece2) -> TreeBoard(
                "b1",
                Color.White
              ),
              NormalMove(Square(3, 3), Square(3, 2), piece2) -> TreeBoard(
                "b2",
                Color.White
              ),
              NormalMove(Square(3, 3), Square(2, 2), piece2) -> TreeBoard(
                "b3",
                Color.White
              )
            )
          ),
        NormalMove(Square(1, 1), Square(4, 4), piece1) ->
          TreeBoard(
            "c",
            Color.Black,
            children = Map(
              NormalMove(Square(4, 4), Square(5, 5), piece2) -> TreeBoard(
                "c1",
                Color.White
              ),
              CastleMove(Square(5, 5)) -> TreeBoard("c2", Color.White),
              CastleMove(Square(5, 5)) -> TreeBoard("c3", Color.White)
            )
          )
      )
    )

    assertResult(NormalMove(Square(1, 1), Square(2, 2), piece1) /* move "b" */ ) {
      minimax.GetBestMove(board, Color.White)
    }
    assertResult(1) {
      evaluator.getAccessCount("b1")
    }
    assertResult(0) {
      evaluator.getAccessCount("b2")
    }
    assertResult(0) {
      evaluator.getAccessCount("b3")
    }
  }

  test("testGetBestMove_shallowDepth") {
    val minimax = ABPruningMinimax(
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
