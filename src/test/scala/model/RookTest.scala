package model

import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class RookTest extends AnyFunSuite with MockFactory {

  test("testGetLegalMoves_blockedByPiece") {
    val sameColorPiece = mock[Piece]
    val board = new Board(Map(Square(6, 3) -> sameColorPiece, Square(3, 7) -> sameColorPiece), Color.Black)
    val rook = Rook(Color.Black)
    (sameColorPiece.isColor _).expects(Color.Black).returning(true).twice()

    assertResult(Set(
      Square(1, 3), Square(2, 3), Square(4, 3), Square(5, 3), Square(3, 1), Square(3, 2), Square(3, 4),
      Square(3, 5), Square(3, 6))) {
      rook.getLegalMoves(Square(3, 3), board)
    }
  }

  test("testGetLegalMoves_capture") {
    val sameColorPiece = mock[Piece]
    val board = new Board(Map(Square(6, 3) -> sameColorPiece, Square(3, 7) -> sameColorPiece), Color.Black)
    val rook = Rook(Color.Black)
    (sameColorPiece.isColor _).expects(Color.Black).returning(false).twice()

    assertResult(Set(
      Square(1, 3), Square(2, 3), Square(4, 3), Square(5, 3), Square(6, 3), Square(3, 1), Square(3, 2),
      Square(3, 4), Square(3, 5), Square(3, 6), Square(3, 7))) {
      rook.getLegalMoves(Square(3, 3), board)
    }
  }

}
