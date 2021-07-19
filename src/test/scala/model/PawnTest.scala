package model

import org.scalatest.funsuite.AnyFunSuite


class PawnTest extends AnyFunSuite {

  test("testGetLegalMoves_OneOrTwoForwardIfFirstMoveWhite") {
    val board = StandardBoard(Map())
    val pawn = Pawn(Color.White)
    assertResult(Set(NormalMove(Square(4, 2), Square(4, 3)), NormalMove(Square(4, 2), Square(4, 4)))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_OneForwardIfNotFirstMoveWhite") {
    val board = StandardBoard(Map())
    val pawn = Pawn(Color.White, hasMoved = true)
    assertResult(Set(NormalMove(Square(4, 2), Square(4, 3)))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_OneForwardIfTwoBlockedWhite") {
    val board = StandardBoard(Map(Square(4, 4) -> Pawn(Color.Black)))
    val pawn = Pawn(Color.White)
    assertResult(Set(NormalMove(Square(4, 2), Square(4, 3)))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_OneForwardOutOfBoundsWhite") {
    val board = StandardBoard(Map())
    val pawn = Pawn(Color.White, hasMoved = true)
    assertResult(Set()) {
      pawn.getLegalMoves(Square(4, 8), board)
    }
  }

  test("testGetLegalMoves_OneForwardBlockedByPieceWhite") {
    val board = StandardBoard(Map(Square(4, 3) -> Pawn(Color.Black)))
    val pawn = Pawn(Color.White)
    assertResult(Set()) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_SameColorDiagonalsWhite") {
    val board = StandardBoard(Map(Square(3, 3) -> Pawn(Color.White), Square(5, 3) -> Pawn(Color.White))
    )
    val pawn = Pawn(Color.White, hasMoved = true)
    assertResult(Set(NormalMove(Square(4, 2), Square(4, 3)))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_CaptureLeftWhite") {
    val board = StandardBoard(Map(Square(3, 3) -> Pawn(Color.Black)))
    val pawn = Pawn(Color.White, hasMoved = true)
    assertResult(Set(NormalMove(Square(4, 2), Square(4, 3)), NormalMove(Square(4, 2), Square(3, 3)))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_CaptureRightWhite") {
    val board = StandardBoard(Map(Square(5, 3) -> Pawn(Color.Black)))
    val pawn = Pawn(Color.White, hasMoved = true)
    assertResult(Set(NormalMove(Square(4, 2), Square(4, 3)), NormalMove(Square(4, 2), Square(5, 3)))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_CaptureRightBlack") {
    val board = StandardBoard(Map(Square(5, 6) -> Pawn(Color.White)))
    val pawn = Pawn(Color.Black, hasMoved = true)
    assertResult(Set(NormalMove(Square(4, 7), Square(4, 6)), NormalMove(Square(4, 7), Square(5, 6)))) {
      pawn.getLegalMoves(Square(4, 7), board)
    }
  }

  test("testGetLegalMoves_EnPassant") {
    val board = StandardBoard(Map(), enPassant = Some(Square(5, 3)))
    val pawn = Pawn(Color.White, hasMoved = true)
    assertResult(Set(NormalMove(Square(4, 2), Square(4, 3)), NormalMove(Square(4, 2), Square(5, 3)))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_PromotionWhite") {
    val board = StandardBoard(Map(Square(4, 8) -> Pawn(Color.Black)))

    assertResult(Set(NormalMove(Square(5, 7), Square(5, 8), Some(Knight(Color.White))),
      NormalMove(Square(5, 7), Square(5, 8), Some(Bishop(Color.White))),
      NormalMove(Square(5, 7), Square(5, 8), Some(Rook(Color.White))),
      NormalMove(Square(5, 7), Square(5, 8), Some(Queen(Color.White))),
      NormalMove(Square(5, 7), Square(4, 8), Some(Knight(Color.White))),
      NormalMove(Square(5, 7), Square(4, 8), Some(Bishop(Color.White))),
      NormalMove(Square(5, 7), Square(4, 8), Some(Rook(Color.White))),
      NormalMove(Square(5, 7), Square(4, 8), Some(Queen(Color.White))))) {
      Pawn(Color.White).getLegalMoves(Square(5, 7), board)
    }
  }

  test("testGetLegalMoves_PromotionBlack") {
    val board = StandardBoard(Map(Square(4, 1) -> Pawn(Color.White)))

    assertResult(Set(NormalMove(Square(5, 2), Square(5, 1), Some(Knight(Color.Black))),
      NormalMove(Square(5, 2), Square(5, 1), Some(Bishop(Color.Black))),
      NormalMove(Square(5, 2), Square(5, 1), Some(Rook(Color.Black))),
      NormalMove(Square(5, 2), Square(5, 1), Some(Queen(Color.Black))),
      NormalMove(Square(5, 2), Square(4, 1), Some(Knight(Color.Black))),
      NormalMove(Square(5, 2), Square(4, 1), Some(Bishop(Color.Black))),
      NormalMove(Square(5, 2), Square(4, 1), Some(Rook(Color.Black))),
      NormalMove(Square(5, 2), Square(4, 1), Some(Queen(Color.Black))))) {
      Pawn(Color.Black).getLegalMoves(Square(5, 2), board)
    }
  }
}
