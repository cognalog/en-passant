package model

import org.scalatest.funsuite.AnyFunSuite


class PawnTest extends AnyFunSuite {

  test("testGetLegalMoves_OneOrTwoForwardIfFirstMoveWhite") {
    val board = new StandardBoard(Map())
    val pawn = Pawn(Color.White)
    assertResult(Set(NormalMove(Square(4, 2), Square(4, 3)), NormalMove(Square(4, 2), Square(4, 4)))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_OneForwardIfNotFirstMoveWhite") {
    val board = new StandardBoard(Map())
    val pawn = Pawn(Color.White, hasMoved = true)
    assertResult(Set(NormalMove(Square(4, 2), Square(4, 3)))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_OneForwardIfTwoBlockedWhite") {
    val board = new StandardBoard(Map(Square(4, 4) -> Pawn(Color.Black)))
    val pawn = Pawn(Color.White)
    assertResult(Set(NormalMove(Square(4, 2), Square(4, 3)))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_OneForwardOutOfBoundsWhite") {
    val board = new StandardBoard(Map())
    val pawn = Pawn(Color.White, hasMoved = true)
    assertResult(Set()) {
      pawn.getLegalMoves(Square(4, 8), board)
    }
  }

  test("testGetLegalMoves_OneForwardBlockedByPieceWhite") {
    val board =
      new StandardBoard(
        Map(Square(4, 3) -> Pawn(Color.Black))
      )
    val pawn = Pawn(Color.White)
    assertResult(Set()) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_SameColorDiagonalsWhite") {
    val board = new StandardBoard(
      Map(
        Square(3, 3) -> Pawn(Color.White),
        Square(5, 3) -> Pawn(Color.White)
      )
    )
    val pawn = Pawn(Color.White, hasMoved = true)
    assertResult(Set(NormalMove(Square(4, 2), Square(4, 3)))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_CaptureLeftWhite") {
    val board =
      new StandardBoard(
        Map(Square(3, 3) -> Pawn(Color.Black))
      )
    val pawn = Pawn(Color.White, hasMoved = true)
    assertResult(Set(NormalMove(Square(4, 2), Square(4, 3)), NormalMove(Square(4, 2), Square(3, 3)))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_CaptureRightWhite") {
    val board =
      new StandardBoard(
        Map(Square(5, 3) -> Pawn(Color.Black))
      )
    val pawn = Pawn(Color.White, hasMoved = true)
    assertResult(Set(NormalMove(Square(4, 2), Square(4, 3)), NormalMove(Square(4, 2), Square(5, 3)))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_CaptureRightBlack") {
    val board =
      new StandardBoard(
        Map(Square(5, 6) -> Pawn(Color.White))
      )
    val pawn = Pawn(Color.Black, hasMoved = true)
    assertResult(Set(NormalMove(Square(4, 7), Square(4, 6)), NormalMove(Square(4, 7), Square(5, 6)))) {
      pawn.getLegalMoves(Square(4, 7), board)
    }
  }

  test("testGetLegalMoves_EnPassant") {
    val board = new StandardBoard(Map(), enPassant = Some(Square(5, 3)))
    val pawn = Pawn(Color.White, hasMoved = true)
    assertResult(Set(NormalMove(Square(4, 2), Square(4, 3)), NormalMove(Square(4, 2), Square(5, 3)))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }
}
