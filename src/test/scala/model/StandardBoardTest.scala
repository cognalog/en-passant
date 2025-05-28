package model

import model.Color.{Black, White}
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Success

class StandardBoardTest extends AnyFunSuite with MockFactory {

  test("testGetAttackers") {
    val board = StandardBoard(
      Map(
        Square(1, 1) -> Bishop(Black),
        Square(1, 4) -> Rook(Black),
        Square(1, 7) -> Bishop(White),
        Square(2, 5) -> Knight(Black),
        Square(4, 3) -> King(Black),
        Square(4, 5) -> King(White),
        Square(4, 6) -> Pawn(Black),
        Square(5, 3) -> Pawn(White),
        Square(5, 8) -> Rook(White),
        Square(6, 3) -> Knight(White),
        Square(7, 7) -> Queen(White),
        Square(8, 5) -> Queen(Black)
      )
    )

    assertResult(Set(Bishop(Black), Rook(Black), Knight(Black), King(Black))) {
      board.getAttackers(Square(4, 4), Black)
    }
    assertResult(
      Set(Bishop(White), King(White), Knight(White), Pawn(White), Queen(White))
    ) {
      board.getAttackers(Square(4, 4), White)
    }
    assertResult(Set(Pawn(Black), Queen(Black), Bishop(Black))) {
      board.getAttackers(Square(5, 5), Black)
    }
    assertResult(Set(King(White), Rook(White), Knight(White), Queen(White))) {
      board.getAttackers(Square(5, 5), White)
    }
  }

  test("testIsLegalMove_Ok") {
    val mockPiece = mock[Piece]
    val board =
      StandardBoard(Map(Square(1, 1) -> mockPiece), White)
    (mockPiece.isColor _).expects(White).returning(true)
    board.checkLegalMove(Square(1, 1), Square(1, 2)) // should not throw
  }

  test("testIsLegalMove_NoPiece") {
    val mockPiece = mock[Piece]
    val board = StandardBoard(Map(Square(1, 1) -> mockPiece), White)
    (mockPiece.isColor _).expects(*).never()
    assertThrows[IllegalStateException] {
      board.checkLegalMove(Square(0, 1), Square(2, 2))
    }
  }

  test("testIsLegalMove_WrongColor") {
    val mockPiece = mock[Piece]
    val board =
      StandardBoard(Map(Square(1, 1) -> mockPiece), Black)
    (mockPiece.isColor _).expects(Black).returning(false)
    (mockPiece.color _).expects().returning(White)
    assertThrows[IllegalStateException] {
      board.checkLegalMove(Square(1, 1), Square(1, 2))
    }
  }

  test("testIsLegalMove_OOB") {
    val mockPiece = mock[Piece]
    val board =
      StandardBoard(Map(Square(1, 1) -> mockPiece), White)
    (mockPiece.isColor _).expects(White).returning(true)
    assertThrows[IllegalArgumentException] {
      board.checkLegalMove(Square(1, 1), Square(99, 2))
    }
  }

  test("testKingInCheck_true") {
    val board = StandardBoard(
      Map(Square(3, 3) -> King(Black), Square(7, 7) -> Bishop(White))
    )
    assertResult(true) {
      board.kingInCheck(Black)
    }
  }

  test("testKingInCheck_miss") {
    val board = StandardBoard(
      Map(Square(3, 3) -> King(Black), Square(7, 6) -> Bishop(White))
    )
    assertResult(false) {
      board.kingInCheck(Black)
    }
  }

  test("testKingInCheck_blockedByOpponent") {
    val board = StandardBoard(
      Map(
        Square(3, 3) -> King(Black),
        Square(6, 6) -> Bishop(Black),
        Square(7, 7) -> Bishop(White)
      )
    )
    assertResult(false) {
      board.kingInCheck(Black)
    }
  }

  test("testKingInCheck_blockedBySelf") {
    val board = StandardBoard(
      Map(
        Square(3, 3) -> King(Black),
        Square(3, 6) -> Pawn(White),
        Square(3, 8) -> Rook(White)
      )
    )
    assertResult(false) {
      board.kingInCheck(Black)
    }
  }

  test("testKingInCheck_ColorSpecific") {
    val board = StandardBoard(
      Map(
        Square(5, 8) -> King(Black),
        Square(5, 1) -> King(White),
        Square(4, 8) -> Queen(Black)
      )
    )
    assertResult(false) {
      board.kingInCheck(White)
    }
  }

  test("testCastle_WhiteQueensideOK") {
    val board = StandardBoard(
      Map(
        Square(1, 1) -> Rook(White),
        Square(5, 1) -> King(White),
        Square(8, 1) -> Rook(White)
      ),
      turnColor = White
    )

    assertResult(
      Success(
        StandardBoard(
          Map(
            Square(3, 1) -> King(White, hasMoved = true, hasCastled = true),
            Square(4, 1) -> Rook(White, hasMoved = true),
            Square(8, 1) -> Rook(White)
          ),
          Black
        )
      )
    ) {
      board.move(CastleMove(Square(3, 1)))
    }
  }

  test("testCastle_WhiteKingsideOK") {
    val board = StandardBoard(
      Map(
        Square(1, 1) -> Rook(White),
        Square(5, 1) -> King(White),
        Square(8, 1) -> Rook(White)
      ),
      turnColor = White
    )

    assertResult(
      Success(
        StandardBoard(
          Map(
            Square(7, 1) -> King(White, hasMoved = true, hasCastled = true),
            Square(6, 1) -> Rook(White, hasMoved = true),
            Square(1, 1) -> Rook(White)
          ),
          Black
        )
      )
    ) {
      board.move(CastleMove(Square(7, 1)))
    }
  }

  test("testCastle_BlackKingsideOK") {
    val board = StandardBoard(
      Map(
        Square(1, 8) -> Rook(Black),
        Square(5, 8) -> King(Black),
        Square(8, 8) -> Rook(Black)
      ),
      turnColor = Black
    )

    assertResult(
      Success(
        StandardBoard(
          Map(
            Square(7, 8) -> King(Black, hasMoved = true, hasCastled = true),
            Square(6, 8) -> Rook(Black, hasMoved = true),
            Square(1, 8) -> Rook(Black)
          ),
          White
        )
      )
    ) {
      board.move(CastleMove(Square(7, 8)))
    }
  }

  test("testCastle_BlackQueensideOK") {
    val board = StandardBoard(
      Map(
        Square(1, 8) -> Rook(Black),
        Square(5, 8) -> King(Black),
        Square(8, 8) -> Rook(Black)
      ),
      turnColor = Black
    )

    assertResult(
      Success(
        StandardBoard(
          Map(
            Square(3, 8) -> King(Black, hasMoved = true, hasCastled = true),
            Square(4, 8) -> Rook(Black, hasMoved = true),
            Square(8, 8) -> Rook(Black)
          ),
          White
        )
      )
    ) {
      board.move(CastleMove(Square(3, 8)))
    }
  }

  test("testCastle_WrongColorKing") {
    val board = StandardBoard(
      Map(Square(1, 1) -> Rook(Black), Square(5, 1) -> King(White)),
      turnColor = Black
    )
    assertResult("There is no unmoved Black king at Square(5,1).") {
      board
        .move(CastleMove(Square(3, 1)))
        .fold(ex => ex.getMessage, _ => fail())
    }
  }

  test("testCastle_WrongColorRook") {
    val board = StandardBoard(
      Map(Square(1, 1) -> Rook(White), Square(5, 1) -> King(Black)),
      turnColor = Black
    )
    assertResult("There is no unmoved Black rook at Square(1,1).") {
      board
        .move(CastleMove(Square(3, 1)))
        .fold(ex => ex.getMessage, _ => fail())
    }
  }

  test("testCastle_AlreadyMovedKing") {
    val board = StandardBoard(
      Map(
        Square(1, 1) -> Rook(Black),
        Square(5, 1) -> King(Black, hasMoved = true)
      ),
      turnColor = Black
    )
    assertResult("There is no unmoved Black king at Square(5,1).") {
      board
        .move(CastleMove(Square(3, 1)))
        .fold(ex => ex.getMessage, _ => fail())
    }
  }

  test("testCastle_AlreadyMovedRook") {
    val board = StandardBoard(
      Map(
        Square(1, 1) -> Rook(Black, hasMoved = true),
        Square(5, 1) -> King(Black)
      ),
      turnColor = Black
    )
    assertResult("There is no unmoved Black rook at Square(1,1).") {
      board
        .move(CastleMove(Square(3, 1)))
        .fold(ex => ex.getMessage, _ => fail())
    }
  }

  test("testCastle_PiecesBlocking") {
    val board = StandardBoard(
      Map(
        Square(1, 1) -> Rook(Black),
        Square(2, 1) -> Knight(Black),
        Square(5, 1) -> King(Black)
      ),
      turnColor = Black
    )
    assertResult(
      "There are pieces between the king at Square(5,1) and the rook at Square(1,1)."
    ) {
      board
        .move(CastleMove(Square(3, 1)))
        .fold(ex => ex.getMessage, _ => fail())
    }
  }

  test("testCastle_PiecesAttacking") {
    val board = StandardBoard(
      Map(
        Square(1, 1) -> Rook(Black),
        Square(2, 3) -> Knight(White),
        Square(5, 1) -> King(Black)
      ),
      turnColor = Black
    )
    assertResult(
      "The king cannot safely move from Square(5,1) to Square(3,1)."
    ) {
      board
        .move(CastleMove(Square(3, 1)))
        .fold(ex => ex.getMessage, _ => fail())
    }
  }

  test("testMove_OK") {
    val pawn = Pawn(Black)
    val board =
      StandardBoard(Map(Square(1, 1) -> pawn), Black)

    assertResult(
      Success(
        StandardBoard(Map(Square(1, 2) -> Pawn(Black, hasMoved = true)), White)
      )
    ) {
      board.move(NormalMove(Square(1, 1), Square(1, 2), pawn))
    }
  }

  test("testMove_kingMovingIntoCheck") {
    val king = King(Black)
    val board =
      StandardBoard(
        Map(Square(1, 1) -> king, Square(2, 2) -> Rook(White)),
        Black
      )
    assertResult("Move Square(1,1) -> Square(2,1) leaves the king in check.") {
      board
        .move(NormalMove(Square(1, 1), Square(2, 1), king))
        .fold(ex => ex.getMessage, _ => fail())
    }
  }

  test("testMove_pinnedPiece") {
    val rook = Rook(Black)
    val board =
      StandardBoard(
        Map(
          Square(1, 1) -> King(Black),
          Square(2, 2) -> rook,
          Square(5, 5) -> Bishop(White)
        ),
        Black
      )
    assertResult("Move Square(2,2) -> Square(3,2) leaves the king in check.") {
      board
        .move(NormalMove(Square(2, 2), Square(3, 2), rook))
        .fold(ex => ex.getMessage, _ => fail())
    }
  }

  test("testMove_signalEnPassantWhite") {
    val pawn = Pawn(White)
    val board =
      StandardBoard(Map(Square(1, 1) -> pawn), White)
    assertResult(
      Success(
        StandardBoard(
          Map(Square(1, 3) -> Pawn(White, hasMoved = true)),
          turnColor = Black,
          enPassant = Some(Square(1, 2))
        )
      )
    ) {
      board.move(NormalMove(Square(1, 1), Square(1, 3), pawn))
    }
  }

  test("testMove_signalEnPassantBlack") {
    val pawn = Pawn(Black)
    val board =
      StandardBoard(Map(Square(8, 8) -> pawn), Black)

    assertResult(
      Success(
        StandardBoard(
          Map(Square(8, 6) -> Pawn(Black, hasMoved = true)),
          turnColor = White,
          enPassant = Some(Square(8, 7))
        )
      )
    ) {
      board.move(NormalMove(Square(8, 8), Square(8, 6), pawn))
    }
  }

  test("testMove_enPassantMoveBlack") {
    val pawn = Pawn(Black)
    val board =
      StandardBoard(
        Map(Square(1, 3) -> Pawn(White), Square(2, 3) -> pawn),
        turnColor = Black,
        enPassant = Some(Square(1, 2))
      )
    assertResult(
      Success(
        StandardBoard(
          Map(Square(1, 2) -> Pawn(Black, hasMoved = true)),
          turnColor = White
        )
      )
    ) {
      board.move(NormalMove(Square(2, 3), Square(1, 2), pawn))
    }
  }

  test("testMove_enPassantMoveWhite") {
    val pawn = Pawn(White)
    val board =
      StandardBoard(
        Map(Square(5, 5) -> Pawn(Black), Square(4, 5) -> pawn),
        turnColor = White,
        enPassant = Some(Square(5, 6))
      )
    assertResult(
      Success(
        StandardBoard(
          Map(Square(5, 6) -> Pawn(White, hasMoved = true)),
          turnColor = Black
        )
      )
    ) {
      board.move(NormalMove(Square(4, 5), Square(5, 6), pawn))
    }
  }

  test("testMove_Promotion") {
    val pawn = Pawn(Black)
    val promotionPiece = Queen(Black)
    val board =
      StandardBoard(Map(Square(1, 7) -> pawn), Black)

    assertResult(
      Success(StandardBoard(Map(Square(1, 8) -> Queen(Black)), White))
    ) {
      board.move(NormalMove(Square(1, 7), Square(1, 8), pawn, promotion = Some(promotionPiece)))
    }
  }

  test("testGetNextMoves_NoCurrentCheck") {
    val king = King(White)
    val queen = Queen(White)
    val pawn = Pawn(White, hasMoved = true)
    val board =
      StandardBoard(
        Map(
          Square(7, 1) -> king,
          Square(8, 1) -> Knight(Black),
          Square(5, 2) -> Bishop(Black),
          Square(7, 2) -> queen,
          Square(8, 3) -> pawn,
          Square(7, 4) -> Rook(Black)
        ),
        turnColor = White
      )

    val expectedMoves: Set[(Move, StandardBoard)] = Set(
      (
        NormalMove(Square(7, 1), Square(8, 2), king),
        StandardBoard(
          Map(
            Square(8, 2) -> King(White, hasMoved = true),
            Square(8, 1) -> Knight(Black),
            Square(5, 2) -> Bishop(Black),
            Square(7, 2) -> queen,
            Square(8, 3) -> pawn,
            Square(7, 4) -> Rook(Black)
          ),
          turnColor = Black
        )
      ),
      (
        NormalMove(Square(7, 1), Square(8, 1), king, true),
        StandardBoard(
          Map(
            Square(8, 1) -> King(White, hasMoved = true),
            Square(5, 2) -> Bishop(Black),
            Square(7, 2) -> queen,
            Square(8, 3) -> pawn,
            Square(7, 4) -> Rook(Black)
          ),
          turnColor = Black
        )
      ),
      (
        NormalMove(Square(7, 2), Square(7, 3), queen),
        StandardBoard(
          Map(
            Square(7, 1) -> king,
            Square(8, 1) -> Knight(Black),
            Square(5, 2) -> Bishop(Black),
            Square(7, 3) -> Queen(White, hasMoved = true),
            Square(8, 3) -> pawn,
            Square(7, 4) -> Rook(Black)
          ),
          turnColor = Black
        )
      ),
      (
        NormalMove(Square(7, 2), Square(7, 4), queen, true),
        StandardBoard(
          Map(
            Square(7, 1) -> king,
            Square(8, 1) -> Knight(Black),
            Square(5, 2) -> Bishop(Black),
            Square(7, 4) -> Queen(White, hasMoved = true),
            Square(8, 3) -> pawn
          ),
          turnColor = Black
        )
      ),
      (
        NormalMove(Square(8, 3), Square(8, 4), pawn),
        StandardBoard(
          Map(
            Square(7, 1) -> king,
            Square(8, 1) -> Knight(Black),
            Square(5, 2) -> Bishop(Black),
            Square(7, 2) -> queen,
            Square(8, 4) -> Pawn(White, hasMoved = true),
            Square(7, 4) -> Rook(Black)
          ),
          turnColor = Black
        )
      ),
      (
        NormalMove(Square(8, 3), Square(7, 4), pawn, true),
        StandardBoard(
          Map(
            Square(7, 1) -> king,
            Square(8, 1) -> Knight(Black),
            Square(5, 2) -> Bishop(Black),
            Square(7, 2) -> queen,
            Square(7, 4) -> Pawn(White, hasMoved = true)
          ),
          turnColor = Black
        )
      )
    )
    assertResult(expectedMoves) {
      board.getNextMoves.toSet
    }
  }

  test("testGetNextMoves_InCheckAlready") {
    val king = King(Black)
    val queen = Queen(Black)
    val board =
      StandardBoard(
        Map(
          Square(7, 1) -> king,
          Square(4, 3) -> queen,
          Square(7, 3) -> Rook(White)
        ),
        turnColor = Black
      )

    val expectedMoves: Set[(Move, StandardBoard)] = Set(
      (
        NormalMove(Square(7, 1), Square(6, 2), king),
        StandardBoard(
          Map(
            Square(6, 2) -> King(Black, hasMoved = true),
            Square(4, 3) -> queen,
            Square(7, 3) -> Rook(White)
          ),
          turnColor = White
        )
      ),
      (
        NormalMove(Square(7, 1), Square(8, 2), king),
        StandardBoard(
          Map(
            Square(8, 2) -> King(Black, hasMoved = true),
            Square(4, 3) -> queen,
            Square(7, 3) -> Rook(White)
          ),
          turnColor = White
        )
      ),
      (
        NormalMove(Square(7, 1), Square(8, 1), king),
        StandardBoard(
          Map(
            Square(8, 1) -> King(Black, hasMoved = true),
            Square(4, 3) -> queen,
            Square(7, 3) -> Rook(White)
          ),
          turnColor = White
        )
      ),
      (
        NormalMove(Square(7, 1), Square(6, 1), king),
        StandardBoard(
          Map(
            Square(6, 1) -> King(Black, hasMoved = true),
            Square(4, 3) -> queen,
            Square(7, 3) -> Rook(White)
          ),
          turnColor = White
        )
      ),
      (
        NormalMove(Square(4, 3), Square(7, 3), queen, true),
        StandardBoard(
          Map(
            Square(7, 1) -> king,
            Square(7, 3) -> Queen(Black, hasMoved = true)
          ),
          turnColor = White
        )
      )
    )
    assertResult(expectedMoves) {
      board.getNextMoves.toSet
    }
  }

  test("testGetNextMoves_WithCastles") {
    val king = King(White)
    val rook = Rook(White)
    val board =
      StandardBoard(
        Map(
          Square(5, 1) -> king,
          Square(8, 1) -> rook,
          Square(8, 2) -> Rook(Black),
          Square(4, 8) -> Rook(Black)
        ),
        turnColor = White
      )

    val expectedMoves: Set[(Move, StandardBoard)] = Set(
      (
        CastleMove(Square(7, 1)),
        StandardBoard(
          Map(
            Square(7, 1) -> King(White, hasMoved = true, hasCastled = true),
            Square(6, 1) -> Rook(White, hasMoved = true),
            Square(8, 2) -> Rook(Black),
            Square(4, 8) -> Rook(Black)
          ),
          turnColor = Black
        )
      ),
      (
        NormalMove(Square(5, 1), Square(6, 1), king),
        StandardBoard(
          Map(
            Square(6, 1) -> King(White, hasMoved = true),
            Square(8, 1) -> rook,
            Square(8, 2) -> Rook(Black),
            Square(4, 8) -> Rook(Black)
          ),
          turnColor = Black
        )
      ),
      (
        NormalMove(Square(8, 1), Square(6, 1), rook),
        StandardBoard(
          Map(
            Square(5, 1) -> king,
            Square(6, 1) -> Rook(White, hasMoved = true),
            Square(8, 2) -> Rook(Black),
            Square(4, 8) -> Rook(Black)
          ),
          turnColor = Black
        )
      ),
      (
        NormalMove(Square(8, 1), Square(7, 1), rook),
        StandardBoard(
          Map(
            Square(5, 1) -> king,
            Square(7, 1) -> Rook(White, hasMoved = true),
            Square(8, 2) -> Rook(Black),
            Square(4, 8) -> Rook(Black)
          ),
          turnColor = Black
        )
      ),
      (
        NormalMove(Square(8, 1), Square(8, 2), rook, true),
        StandardBoard(
          Map(
            Square(5, 1) -> king,
            Square(8, 2) -> Rook(White, hasMoved = true),
            Square(4, 8) -> Rook(Black)
          ),
          turnColor = Black
        )
      )
    )
    assertResult(expectedMoves) {
      board.getNextMoves.toSet
    }
  }

  test("testIsCheckmate_true") {
    val board = StandardBoard(
      Map(
        Square(5, 8) -> King(Color.Black),
        Square(5, 7) -> Queen(Color.White),
        Square(3, 5) -> Bishop(Color.White)
      ),
      turnColor = Black
    )

    assertResult(true) {
      board.isCheckmate
    }
  }

  test("testIsCheckmate_saveableByCapture") {
    val board = StandardBoard(
      Map(
        Square(5, 8) -> King(Color.Black),
        Square(5, 7) -> Queen(Color.White)
      ),
      turnColor = Black
    )

    assertResult(false) {
      board.isCheckmate
    }
  }

  test("testIsDraw_stalemate") {
    val board = StandardBoard(
      Map(
        Square(1, 8) -> King(White),
        Square(2, 6) -> Queen(Black),
        Square(8, 8) -> King(Black)
      ),
      turnColor = White
    )

    assertResult(true) {
      board.isDraw
    }
  }

  test("testIsDraw_KingVsKing") {
    val board = StandardBoard(
      Map(Square(5, 1) -> King(White), Square(5, 8) -> King(Black))
    )

    assertResult(true) {
      board.isDraw
    }
  }

  test("testIsDraw_BishopVsKnight") {
    val board = StandardBoard(
      Map(
        Square(5, 1) -> King(White),
        Square(6, 1) -> Bishop(White),
        Square(5, 8) -> King(Black),
        Square(2, 8) -> Knight(Black)
      )
    )

    assertResult(true) {
      board.isDraw
    }
  }

  test("testIsDraw_RookVsKing") {
    val board = StandardBoard(
      Map(
        Square(5, 1) -> King(White),
        Square(5, 8) -> King(Black),
        Square(1, 8) -> Rook(Black)
      )
    )

    assertResult(false) {
      board.isDraw
    }
  }

  test("testIsDraw_PawnVsKing") {
    val board = StandardBoard(
      Map(
        Square(5, 1) -> King(White),
        Square(5, 8) -> King(Black),
        Square(1, 7) -> Pawn(White)
      )
    )

    assertResult(false) {
      board.isDraw
    }
  }

  test("testIsDraw_TwoBishopsVsKing") {
    val board = StandardBoard(
      Map(
        Square(5, 1) -> King(White),
        Square(5, 8) -> King(Black),
        Square(1, 8) -> Bishop(White),
        Square(1, 7) -> Bishop(White)
      )
    )

    assertResult(false) {
      board.isDraw
    }
  }

  test("testIsDraw_TwoKnightsVsKing") {
    val board = StandardBoard(
      Map(
        Square(5, 1) -> King(White),
        Square(5, 8) -> King(Black),
        Square(1, 8) -> Knight(White),
        Square(1, 7) -> Knight(White)
      )
    )

    assertResult(false) {
      board.isDraw
    }
  }

  test("testIsDraw_QueenVsKing") {
    val board = StandardBoard(
      Map(
        Square(5, 1) -> King(White),
        Square(5, 8) -> King(Black),
        Square(1, 7) -> Queen(Black)
      )
    )

    assertResult(false) {
      board.isDraw
    }
  }
}
