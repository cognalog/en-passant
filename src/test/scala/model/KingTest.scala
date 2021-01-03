package model

import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class KingTest extends AnyFunSuite with MockFactory {

  test("testGetLegalMoves_blockedByPiece") {
    val sameColorPiece = mock[Piece]
    val board = new Board(Map(Square(3, 4) -> sameColorPiece))
    val king = King(Color.White)
    (sameColorPiece.isColor _).expects(Color.White).returning(true)
    assertResult(Set(
      Square(2, 4), Square(4, 4), Square(4, 3), Square(4, 2), Square(3, 2), Square(2, 2), Square(2, 3))) {
      king.getLegalMoves(Square(3, 3), board)
    }
  }

  test("testGetLegalMoves_capture") {

  }

  test("testGetLegalMoves_someOOB") {
    val board = new Board(Map())
    val king = King(Color.White)
    assertResult(Set(
      Square(2, 2), Square(3, 2), Square(4, 2), Square(4, 1), Square(2, 1))) {
      king.getLegalMoves(Square(3, 1), board)
    }
  }
}
