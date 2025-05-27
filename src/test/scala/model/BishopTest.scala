package model

import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class BishopTest extends AnyFunSuite with MockFactory {

  test("testGetLegalMoves_blockedByPiece") {
    val sameColorPiece = mock[Piece]
    val board = new StandardBoard(Map(Square(6, 6) -> sameColorPiece))
    val bishop = Bishop(Color.White)
    (sameColorPiece.isColor _).expects(Color.White).returning(true)
    assertResult(
      Set(
        NormalMove(Square(3, 3), Square(4, 4), bishop),
        NormalMove(Square(3, 3), Square(5, 5), bishop),
        NormalMove(Square(3, 3), Square(2, 2), bishop),
        NormalMove(Square(3, 3), Square(1, 1), bishop),
        NormalMove(Square(3, 3), Square(2, 4), bishop),
        NormalMove(Square(3, 3), Square(1, 5), bishop),
        NormalMove(Square(3, 3), Square(4, 2), bishop),
        NormalMove(Square(3, 3), Square(5, 1), bishop)
      )
    ) {
      bishop.getLegalMoves(Square(3, 3), board)
    }
  }

  test("testGetLegalMoves_capture") {
    val sameColorPiece = mock[Piece]
    val board = new StandardBoard(Map(Square(6, 6) -> sameColorPiece))
    val bishop = Bishop(Color.White)
    (sameColorPiece.isColor _).expects(Color.White).returning(false)
    assertResult(
      Set(
        NormalMove(Square(3, 3), Square(4, 4), bishop),
        NormalMove(Square(3, 3), Square(5, 5), bishop),
        NormalMove(Square(3, 3), Square(2, 2), bishop),
        NormalMove(Square(3, 3), Square(1, 1), bishop),
        NormalMove(Square(3, 3), Square(2, 4), bishop),
        NormalMove(Square(3, 3), Square(1, 5), bishop),
        NormalMove(Square(3, 3), Square(4, 2), bishop),
        NormalMove(Square(3, 3), Square(5, 1), bishop),
        NormalMove(Square(3, 3), Square(6, 6), bishop)
      )
    ) {
      bishop.getLegalMoves(Square(3, 3), board)
    }
  }


}
