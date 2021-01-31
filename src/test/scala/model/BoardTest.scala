package model

import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class BoardTest extends AnyFunSuite with MockFactory {

  test("testGetAttackers") {
    val board = new Board(Map(
      Square(1, 1) -> Bishop(Color.Black), Square(1, 4) -> Rook(Color.Black), Square(1, 7) -> Bishop(Color.White),
      Square(2, 5) -> Knight(Color.Black), Square(4, 3) -> King(Color.Black), Square(4, 5) -> King(Color.White),
      Square(4, 6) -> Pawn(Color.Black), Square(5, 3) -> Pawn(Color.White), Square(5, 8) -> Rook(Color.White),
      Square(6, 3) -> Knight(Color.White), Square(7, 7) -> Queen(Color.White), Square(8, 5) -> Queen(Color.Black)))

    assertResult(Set(Bishop(Color.Black), Rook(Color.Black), Knight(Color.Black), King(Color.Black))) {
      board.getAttackers(Square(4, 4), Color.Black)
    }
    assertResult(
      Set(Bishop(Color.White), King(Color.White), Knight(Color.White), Pawn(Color.White), Queen(Color.White))) {
      board.getAttackers(Square(4, 4), Color.White)
    }
    assertResult(Set(Pawn(Color.Black), Queen(Color.Black), Bishop(Color.Black))) {
      board.getAttackers(Square(5, 5), Color.Black)
    }
    assertResult(Set(King(Color.White), Rook(Color.White), Knight(Color.White), Queen(Color.White))) {
      board.getAttackers(Square(5, 5), Color.White)
    }
  }

  test("testIsLegalMove_Ok") {
    val mockPiece = mock[Piece]
    val board =
      new Board(Map(Square(1, 1) -> mockPiece), Color.White)
    (mockPiece.isColor _).expects(Color.White).returning(true)
    board.checkLegalMove(Square(1, 1), Square(1, 2)) // should not throw
  }

  test("testIsLegalMove_NoPiece") {
    val mockPiece = mock[Piece]
    val board = new Board(Map(Square(1, 1) -> mockPiece), Color.White)
    (mockPiece.isColor _).expects(*).never()
    assertThrows[IllegalStateException] {
      board.checkLegalMove(Square(0, 1), Square(2, 2))
    }
  }

  test("testIsLegalMove_WrongColor") {
    val mockPiece = mock[Piece]
    val board =
      new Board(Map(Square(1, 1) -> mockPiece), Color.Black)
    (mockPiece.isColor _).expects(Color.Black).returning(false)
    (mockPiece.color _).expects().returning(Color.White)
    assertThrows[IllegalStateException] {
      board.checkLegalMove(Square(1, 1), Square(1, 2))
    }
  }

  test("testIsLegalMove_OOB") {
    val mockPiece = mock[Piece]
    val board =
      new Board(Map(Square(1, 1) -> mockPiece), Color.White)
    (mockPiece.isColor _).expects(Color.White).returning(true)
    assertThrows[IllegalArgumentException] {
      board.checkLegalMove(Square(1, 1), Square(99, 2))
    }
  }

  test("testMove_OK") {
    val mockPiece = mock[Piece]
    val board =
      new Board(Map(Square(1, 1) -> mockPiece), Color.Black)
    (mockPiece.isColor _).expects(Color.Black).returning(true)
    (mockPiece.updateHasMoved _).expects().returning(mockPiece)
    val result = board.move(Square(1, 1), Square(1, 2))
    result.fold(
      _ => fail(),
      resultBoard => {
        assertResult(None) {
          resultBoard.pieceAt(Square(1, 1))
        }
        assertResult(Some(mockPiece)) {
          resultBoard.pieceAt(Square(1, 2))
        }
        assertResult(Color.White) {
          resultBoard.turnColor
        }
        assertResult(None) {
          resultBoard.enPassant
        }
      }
      )
  }

  test("testCastle_OK") {
    val board = new Board(
      Map(
        Square(1, 1) -> Rook(Color.White), Square(5, 1) -> King(Color.White), Square(8, 1) -> Rook(Color.White),
        Square(1, 8) -> Rook(Color.White),
        Square(5, 8) -> King(Color.White), Square(8, 8) -> Rook(Color.White)), turnColor = Color.White)

    val rank1QueensideResult = board.castle(Square(3, 1))
    rank1QueensideResult.fold(
      _ => fail(), resultBoard => {
        assertResult(Some(King(Color.White, hasMoved = true))) { resultBoard.pieceAt(Square(3, 1)) }
        assertResult(Some(Rook(Color.White, hasMoved = true))) { resultBoard.pieceAt(Square(4, 1)) }
        assertResult(Color.Black) { resultBoard.turnColor }
      })
    val rank1KingsideResult = board.castle(Square(7, 1))
    rank1KingsideResult.fold(
      _ => fail(), resultBoard => {
        assertResult(Some(King(Color.White, hasMoved = true))) { resultBoard.pieceAt(Square(7, 1)) }
        assertResult(Some(Rook(Color.White, hasMoved = true))) { resultBoard.pieceAt(Square(6, 1)) }
        assertResult(Color.Black) { resultBoard.turnColor }
      })
    val rank8QueensideResult = board.castle(Square(3, 8))
    rank8QueensideResult.fold(
      _ => fail(), resultBoard => {
        assertResult(Some(King(Color.White, hasMoved = true))) { resultBoard.pieceAt(Square(3, 8)) }
        assertResult(Some(Rook(Color.White, hasMoved = true))) { resultBoard.pieceAt(Square(4, 8)) }
        assertResult(Color.Black) { resultBoard.turnColor }
      })
    val rank8KingsideResult = board.castle(Square(7, 8))
    rank8KingsideResult.fold(
      _ => fail(), resultBoard => {
        assertResult(Some(King(Color.White, hasMoved = true))) { resultBoard.pieceAt(Square(7, 8)) }
        assertResult(Some(Rook(Color.White, hasMoved = true))) { resultBoard.pieceAt(Square(6, 8)) }
        assertResult(Color.Black) { resultBoard.turnColor }
      })
  }

  test("testCastle_WrongColorKing") {
    val board = new Board(
      Map(
        Square(1, 1) -> Rook(Color.Black), Square(5, 1) -> King(Color.White)), turnColor = Color.Black)
    assertResult(Left("There is no unmoved Black king at Square(5,1).")) {
      board.castle(Square(3, 1))
    }
  }

  test("testCastle_WrongColorRook") {
    val board = new Board(
      Map(
        Square(1, 1) -> Rook(Color.White), Square(5, 1) -> King(Color.Black)), turnColor = Color.Black)
    assertResult(Left("There is no unmoved Black rook at Square(1,1).")) {
      board.castle(Square(3, 1))
    }
  }

  test("testCastle_AlreadyMovedKing") {
    val board = new Board(
      Map(
        Square(1, 1) -> Rook(Color.Black), Square(5, 1) -> King(Color.Black, hasMoved = true)), turnColor = Color.Black)
    assertResult(Left("There is no unmoved Black king at Square(5,1).")) {
      board.castle(Square(3, 1))
    }
  }

  test("testCastle_AlreadyMovedRook") {
    val board = new Board(
      Map(
        Square(1, 1) -> Rook(Color.Black, hasMoved = true), Square(5, 1) -> King(Color.Black)), turnColor = Color.Black)
    assertResult(Left("There is no unmoved Black rook at Square(1,1).")) {
      board.castle(Square(3, 1))
    }
  }

  test("testCastle_PiecesBlocking") {
    val board = new Board(
      Map(
        Square(1, 1) -> Rook(Color.Black), Square(2, 1) -> Knight(Color.Black), Square(5, 1) -> King(Color.Black)),
      turnColor = Color.Black)
    assertResult(Left("There are pieces between the king at Square(5,1) and the rook at Square(1,1).")) {
      board.castle(Square(3, 1))
    }
  }

  test("testCastle_PiecesAttacking") {
    val board = new Board(
      Map(
        Square(1, 1) -> Rook(Color.Black), Square(2, 3) -> Knight(Color.White), Square(5, 1) -> King(Color.Black)),
      turnColor = Color.Black)
    assertResult(Left("The king cannot safely move from Square(5,1) to Square(3,1).")) {
      board.castle(Square(3, 1))
    }
  }

  test("testMove_EnPassantWhite") {
    val piece = Pawn(Color.White)
    val board =
      new Board(Map(Square(1, 1) -> piece), Color.White)
    val result = board.move(Square(1, 1), Square(1, 3))
    result.fold(
      _ => fail(),
      resultBoard => {
        assertResult(None) {
          resultBoard.pieceAt(Square(1, 1))
        }
        assertResult(Some(Pawn(Color.White, hasMoved = true))) {
          resultBoard.pieceAt(Square(1, 3))
        }
        assertResult(Color.Black) {
          resultBoard.turnColor
        }
        assertResult(Some(Square(1, 2))) {
          resultBoard.enPassant
        }
      }
    )
  }

  test("testMove_EnPassantBlack") {
    val piece = Pawn(Color.Black)
    val board =
      new Board(Map(Square(8, 8) -> piece), Color.Black)
    val result = board.move(Square(8, 8), Square(8, 6))
    result.fold(
      _ => fail(),
      resultBoard => {
        assertResult(None) {
          resultBoard.pieceAt(Square(8, 8))
        }
        assertResult(Some(Pawn(Color.Black, hasMoved = true))) {
          resultBoard.pieceAt(Square(8, 6))
        }
        assertResult(Color.White) {
          resultBoard.turnColor
        }
        assertResult(Some(Square(8, 7))) {
          resultBoard.enPassant
        }
      }
    )
  }
}
