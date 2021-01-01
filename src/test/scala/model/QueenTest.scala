package model

import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class QueenTest extends AnyFunSuite with MockFactory {

  test("testGetLegalMoves_blockedByPiece") {
    val sameColorPiece = mock[Piece]
    val board = new Board(
      Map(Square(6, 3) -> sameColorPiece, Square(3, 7) -> sameColorPiece, Square(6, 6) -> sameColorPiece), Color.Black)
    val queen = Queen(Color.Black)
    (sameColorPiece.isColor _).expects(Color.Black).returning(true).repeated(3).times()

    assertResult(Set(
      Square(1, 3), Square(2, 3), Square(4, 3), Square(5, 3), Square(3, 1), Square(3, 2), Square(3, 4),
      Square(3, 5), Square(3, 6), Square(4, 4), Square(5, 5), Square(2, 2), Square(1, 1), Square(2, 4), Square(1, 5),
      Square(4, 2), Square(5, 1))) {
      queen.getLegalMoves(Square(3, 3), board)
    }
  }

  test("testGetLegalMoves_capture") {
    val sameColorPiece = mock[Piece]
    val board = new Board(
      Map(Square(6, 3) -> sameColorPiece, Square(3, 7) -> sameColorPiece, Square(6, 6) -> sameColorPiece), Color.Black)
    val queen = Queen(Color.Black)
    (sameColorPiece.isColor _).expects(Color.Black).returning(false).repeated(3).times()

    assertResult(Set(
      Square(1, 3), Square(2, 3), Square(4, 3), Square(5, 3), Square(6, 3), Square(3, 1), Square(3, 2), Square(3, 4),
      Square(3, 5), Square(3, 6), Square(3, 7), Square(4, 4), Square(5, 5), Square(6, 6), Square(2, 2), Square(1, 1),
      Square(2, 4), Square(1, 5), Square(4, 2), Square(5, 1))) {
      queen.getLegalMoves(Square(3, 3), board)
    }
  }

}
