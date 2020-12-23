package model

import org.scalatest.FunSuite

class PawnTest extends FunSuite {

  test("testGetLegalMoves_FirstMove") {
    val board = new Board(Map())
    val pawn = new Pawn(Color.White, false)
    assertResult(Set(Square(4, 3), Square(4, 4))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_OneForward") {
    val board = new Board(Map())
    val pawn = new Pawn(Color.White, true)
    assertResult(Set(Square(4, 3))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_OneForwardOutOfBounds") {
    val board = new Board(Map())
    val pawn = new Pawn(Color.White, true)
    assertResult(Set()) {
      pawn.getLegalMoves(Square(4, 8), board)
    }
  }

  test("testGetLegalMoves_OneForwardBlockedByPiece") {
    val board =
      new Board(
        Map(Square(4, 3) -> new Pawn(Color.Black))
      )
    val pawn = new Pawn(Color.White, true)
    assertResult(Set()) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_SameColorDiagonals") {
    val board = new Board(
      Map(
        Square(3, 3) -> new Pawn(Color.White),
        Square(5, 3) -> new Pawn(Color.White)
      )
    )
    val pawn = new Pawn(Color.White, true)
    assertResult(Set(Square(4, 3))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_CaptureLeft") {
    val board =
      new Board(
        Map(Square(3, 3) -> new Pawn(Color.Black))
      )
    val pawn = new Pawn(Color.White, true)
    assertResult(Set(Square(4, 3), Square(3, 3))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_CaptureRight") {
    val board =
      new Board(
        Map(Square(5, 3) -> new Pawn(Color.Black))
      )
    val pawn = new Pawn(Color.White, true)
    assertResult(Set(Square(4, 3), Square(5, 3))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_EnPassant") {
    val board = new Board(Map(), enPassant = Some(Square(5, 3)))
    val pawn = new Pawn(Color.White, true)
    assertResult(Set(Square(4, 3), Square(5, 3))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

}
