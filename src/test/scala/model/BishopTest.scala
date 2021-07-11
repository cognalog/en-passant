package model

import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class BishopTest extends AnyFunSuite with MockFactory {

  test("testGetLegalMoves_blockedByPiece") {
    val sameColorPiece = mock[Piece]
    val board = new StandardBoard(Map(Square(6, 6) -> sameColorPiece))
    val bishop = Bishop(Color.White)
    (sameColorPiece.isColor _).expects(Color.White).returning(true)
    assertResult(Set(Square(4, 4), Square(5, 5), Square(2, 2), Square(1, 1), Square(2, 4), Square(1, 5), Square(4, 2)
      , Square(5, 1))) {
      bishop.getLegalMoves(Square(3, 3), board)
    }
  }

  test("testGetLegalMoves_capture") {
    val sameColorPiece = mock[Piece]
    val board = new StandardBoard(Map(Square(6, 6) -> sameColorPiece))
    val bishop = Bishop(Color.White)
    (sameColorPiece.isColor _).expects(Color.White).returning(false)
    assertResult(Set(Square(4, 4), Square(5, 5), Square(2, 2), Square(1, 1), Square(2, 4), Square(1, 5), Square(4, 2)
      , Square(5, 1), Square(6,6))) {
      bishop.getLegalMoves(Square(3, 3), board)
    }
  }

}
