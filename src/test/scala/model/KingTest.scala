package model

import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite

class KingTest extends AnyFunSuite with MockFactory {

  test("testGetLegalMoves_blockedByPiece") {
    val sameColorPiece = mock[Piece]
    val board = new StandardBoard(Map(Square(3, 4) -> sameColorPiece))
    val king = King(Color.White)
    (sameColorPiece.isColor _).expects(Color.White).returning(true)
    assertResult(
      Set(
        NormalMove(Square(3, 3), Square(2, 4), king),
        NormalMove(Square(3, 3), Square(4, 4), king),
        NormalMove(Square(3, 3), Square(4, 3), king),
        NormalMove(Square(3, 3), Square(4, 2), king),
        NormalMove(Square(3, 3), Square(3, 2), king),
        NormalMove(Square(3, 3), Square(2, 2), king),
        NormalMove(Square(3, 3), Square(2, 3), king),
        CastleMove(Square(1, 3)),
        CastleMove(Square(5, 3))
      )
    ) {
      king.getLegalMoves(Square(3, 3), board)
    }
  }

  test("testGetLegalMoves_capture") {
    val board = new StandardBoard(
      Map(Square(1, 1) -> Pawn(Color.White)),
      turnColor = Color.Black
    )
    assertResult(
      Set(
        NormalMove(Square(2, 1), Square(1, 1), King(Color.Black)),
        NormalMove(Square(2, 1), Square(3, 1), King(Color.Black)),
        NormalMove(Square(2, 1), Square(3, 2), King(Color.Black)),
        NormalMove(Square(2, 1), Square(1, 2), King(Color.Black)),
        NormalMove(Square(2, 1), Square(2, 2), King(Color.Black)),
        CastleMove(Square(4, 1))
      )
    ) {
      King(Color.Black).getLegalMoves(Square(2, 1), board)
    }
  }

  test("testGetLegalMoves_someOOB") {
    val board = new StandardBoard(Map())
    val king = King(Color.White)
    assertResult(
      Set(
        NormalMove(Square(3, 1), Square(2, 2), king),
        NormalMove(Square(3, 1), Square(3, 2), king),
        NormalMove(Square(3, 1), Square(4, 2), king),
        NormalMove(Square(3, 1), Square(4, 1), king),
        NormalMove(Square(3, 1), Square(2, 1), king),
        CastleMove(Square(1, 1)),
        CastleMove(Square(5, 1))
      )
    ) {
      king.getLegalMoves(Square(3, 1), board)
    }
  }

  test("testGetCaptures_castleAvailable") {
    val board = new StandardBoard(
      Map(Square(1, 1) -> Pawn(Color.White)),
      turnColor = Color.Black
    )
    assertResult(
      Set(
        NormalMove(Square(2, 1), Square(1, 1), King(Color.Black)),
        NormalMove(Square(2, 1), Square(3, 1), King(Color.Black)),
        NormalMove(Square(2, 1), Square(3, 2), King(Color.Black)),
        NormalMove(Square(2, 1), Square(1, 2), King(Color.Black)),
        NormalMove(Square(2, 1), Square(2, 2), King(Color.Black))
      )
    ) {
      King(Color.Black).getCaptures(Square(2, 1), board)
    }
  }
}
