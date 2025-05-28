package model

import org.scalatest.funsuite.AnyFunSuite

class PawnTest extends AnyFunSuite {

  test("testGetLegalMoves_OneOrTwoForwardIfFirstMoveWhite") {
    val board = StandardBoard(Map())
    val pawn = Pawn(Color.White)
    assertResult(
      Set(
        NormalMove(Square(4, 2), Square(4, 3), pawn),
        NormalMove(Square(4, 2), Square(4, 4), pawn)
      )
    ) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_OneForwardIfNotFirstMoveWhite") {
    val board = StandardBoard(Map())
    val pawn = Pawn(Color.White, hasMoved = true)
    assertResult(Set(NormalMove(Square(4, 2), Square(4, 3), pawn))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_OneForwardIfTwoBlockedWhite") {
    val board = StandardBoard(Map(Square(4, 4) -> Pawn(Color.Black)))
    val pawn = Pawn(Color.White)
    assertResult(Set(NormalMove(Square(4, 2), Square(4, 3), pawn))) {
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
    val board = StandardBoard(
      Map(Square(3, 3) -> Pawn(Color.White), Square(5, 3) -> Pawn(Color.White))
    )
    val pawn = Pawn(Color.White, hasMoved = true)
    assertResult(Set(NormalMove(Square(4, 2), Square(4, 3), pawn))) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_CaptureLeftWhite") {
    val board = StandardBoard(Map(Square(3, 3) -> Pawn(Color.Black)))
    val pawn = Pawn(Color.White, hasMoved = true)
    assertResult(
      Set(
        NormalMove(Square(4, 2), Square(4, 3), pawn),
        NormalMove(Square(4, 2), Square(3, 3), pawn, true)
      )
    ) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_CaptureRightWhite") {
    val board = StandardBoard(Map(Square(5, 3) -> Pawn(Color.Black)))
    val pawn = Pawn(Color.White, hasMoved = true)
    assertResult(
      Set(
        NormalMove(Square(4, 2), Square(4, 3), pawn),
        NormalMove(Square(4, 2), Square(5, 3), pawn, true)
      )
    ) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_CaptureRightBlack") {
    val board = StandardBoard(Map(Square(5, 6) -> Pawn(Color.White)))
    val pawn = Pawn(Color.Black, hasMoved = true)
    assertResult(
      Set(
        NormalMove(Square(4, 7), Square(4, 6), pawn),
        NormalMove(Square(4, 7), Square(5, 6), pawn, true)
      )
    ) {
      pawn.getLegalMoves(Square(4, 7), board)
    }
  }

  test("testGetLegalMoves_EnPassant") {
    val board = StandardBoard(Map(), enPassant = Some(Square(5, 3)))
    val pawn = Pawn(Color.White, hasMoved = true)
    assertResult(
      Set(
        NormalMove(Square(4, 2), Square(4, 3), pawn),
        NormalMove(Square(4, 2), Square(5, 3), pawn, true)
      )
    ) {
      pawn.getLegalMoves(Square(4, 2), board)
    }
  }

  test("testGetLegalMoves_PromotionWhite") {
    val board = StandardBoard(Map(Square(4, 8) -> Pawn(Color.Black)))
    val pawn = Pawn(Color.White)

    assertResult(
      Set(
        NormalMove(Square(5, 7), Square(5, 8), pawn, promotion = Some(Knight(Color.White))),
        NormalMove(Square(5, 7), Square(5, 8), pawn, promotion = Some(Bishop(Color.White))),
        NormalMove(Square(5, 7), Square(5, 8), pawn, promotion = Some(Rook(Color.White))),
        NormalMove(Square(5, 7), Square(5, 8), pawn, promotion = Some(Queen(Color.White))),
        NormalMove(Square(5, 7), Square(4, 8), pawn, true, Some(Knight(Color.White))),
        NormalMove(Square(5, 7), Square(4, 8), pawn, true, Some(Bishop(Color.White))),
        NormalMove(Square(5, 7), Square(4, 8), pawn, true, Some(Rook(Color.White))),
        NormalMove(Square(5, 7), Square(4, 8), pawn, true, Some(Queen(Color.White)))
      )
    ) {
      pawn.getLegalMoves(Square(5, 7), board)
    }
  }

  test("testGetLegalMoves_PromotionBlack") {
    val board = StandardBoard(Map(Square(4, 1) -> Pawn(Color.White)))
    val pawn = Pawn(Color.Black)
    assertResult(
      Set(
        NormalMove(Square(5, 2), Square(5, 1), pawn, promotion = Some(Knight(Color.Black))),
        NormalMove(Square(5, 2), Square(5, 1), pawn, promotion = Some(Bishop(Color.Black))),
        NormalMove(Square(5, 2), Square(5, 1), pawn, promotion = Some(Rook(Color.Black))),
        NormalMove(Square(5, 2), Square(5, 1), pawn, promotion = Some(Queen(Color.Black))),
        NormalMove(Square(5, 2), Square(4, 1), pawn, true, Some(Knight(Color.Black))),
        NormalMove(Square(5, 2), Square(4, 1), pawn, true, Some(Bishop(Color.Black))),
        NormalMove(Square(5, 2), Square(4, 1), pawn, true, Some(Rook(Color.Black))),
        NormalMove(Square(5, 2), Square(4, 1), pawn, true, Some(Queen(Color.Black)))
      )
    ) {
      pawn.getLegalMoves(Square(5, 2), board)
    }
  }
}
