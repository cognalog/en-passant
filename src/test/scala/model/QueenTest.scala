package model

import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class QueenTest extends AnyFunSuite with MockFactory {

  test("testGetLegalMoves_blockedByPiece") {
    val sameColorPiece = mock[Piece]
    val board = new StandardBoard(
      Map(
        Square(6, 3) -> sameColorPiece,
        Square(3, 7) -> sameColorPiece,
        Square(6, 6) -> sameColorPiece
      ),
      Color.Black
    )
    val queen = Queen(Color.Black)
    (sameColorPiece.isColor _)
      .expects(Color.Black)
      .returning(true)
      .repeated(3)
      .times()

    assertResult(
      Set(
        NormalMove(Square(3, 3), Square(1, 3), queen),
        NormalMove(Square(3, 3), Square(2, 3), queen),
        NormalMove(Square(3, 3), Square(4, 3), queen),
        NormalMove(Square(3, 3), Square(5, 3), queen),
        NormalMove(Square(3, 3), Square(3, 1), queen),
        NormalMove(Square(3, 3), Square(3, 2), queen),
        NormalMove(Square(3, 3), Square(3, 4), queen),
        NormalMove(Square(3, 3), Square(3, 5), queen),
        NormalMove(Square(3, 3), Square(3, 6), queen),
        NormalMove(Square(3, 3), Square(4, 4), queen),
        NormalMove(Square(3, 3), Square(5, 5), queen),
        NormalMove(Square(3, 3), Square(2, 2), queen),
        NormalMove(Square(3, 3), Square(1, 1), queen),
        NormalMove(Square(3, 3), Square(2, 4), queen),
        NormalMove(Square(3, 3), Square(1, 5), queen),
        NormalMove(Square(3, 3), Square(4, 2), queen),
        NormalMove(Square(3, 3), Square(5, 1), queen)
      )
    ) {
      queen.getLegalMoves(Square(3, 3), board)
    }
  }

  test("testGetLegalMoves_capture") {
    val sameColorPiece = mock[Piece]
    val board = new StandardBoard(
      Map(
        Square(6, 3) -> sameColorPiece,
        Square(3, 7) -> sameColorPiece,
        Square(6, 6) -> sameColorPiece
      ),
      Color.Black
    )
    val queen = Queen(Color.Black)
    (sameColorPiece.isColor _)
      .expects(Color.Black)
      .returning(false)
      .repeated(3)
      .times()

    assertResult(
      Set(
        NormalMove(Square(3, 3), Square(1, 3), queen),
        NormalMove(Square(3, 3), Square(2, 3), queen),
        NormalMove(Square(3, 3), Square(4, 3), queen),
        NormalMove(Square(3, 3), Square(5, 3), queen),
        NormalMove(Square(3, 3), Square(6, 3), queen),
        NormalMove(Square(3, 3), Square(3, 1), queen),
        NormalMove(Square(3, 3), Square(3, 2), queen),
        NormalMove(Square(3, 3), Square(3, 4), queen),
        NormalMove(Square(3, 3), Square(3, 5), queen),
        NormalMove(Square(3, 3), Square(3, 6), queen),
        NormalMove(Square(3, 3), Square(3, 7), queen),
        NormalMove(Square(3, 3), Square(4, 4), queen),
        NormalMove(Square(3, 3), Square(5, 5), queen),
        NormalMove(Square(3, 3), Square(6, 6), queen),
        NormalMove(Square(3, 3), Square(2, 2), queen),
        NormalMove(Square(3, 3), Square(1, 1), queen),
        NormalMove(Square(3, 3), Square(2, 4), queen),
        NormalMove(Square(3, 3), Square(1, 5), queen),
        NormalMove(Square(3, 3), Square(4, 2), queen),
        NormalMove(Square(3, 3), Square(5, 1), queen)
      )
    ) {
      queen.getLegalMoves(Square(3, 3), board)
    }
  }

}
