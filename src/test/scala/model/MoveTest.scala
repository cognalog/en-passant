package model

import org.scalatest.funsuite.AnyFunSuite

import scala.util.Success

class MoveTest extends AnyFunSuite {
  test("testFromStandardNotation_malformed") {
    assertResult("Malformed move: abcd") {
      Move.fromStandardNotation("abcd", StandardBoard.StartingPosition).fold(ex => ex.getMessage, _ => fail())
    }
  }

  test("testFromStandardNotation_impossible") {
    assertResult("Impossible move: h8") {
      Move.fromStandardNotation("h8", StandardBoard(Map(Square(1, 1) -> Pawn(Color.White))))
        .fold(ex => ex.getMessage, _ => fail())
    }
  }

  test("testFromStandardNotation_whiteKingsideCastle") {
    assertResult(Success(CastleMove(Square(7, 1)))) {
      Move.fromStandardNotation("O-O", StandardBoard(Map(), turnColor = Color.White))
    }
  }

  test("testFromStandardNotation_whiteQueensideCastle") {
    assertResult(Success(CastleMove(Square(3, 1)))) {
      Move.fromStandardNotation("O-O-O", StandardBoard(Map(), turnColor = Color.White))
    }
  }

  test("testFromStandardNotation_blackKingsideCastle") {
    assertResult(Success(CastleMove(Square(7, 8)))) {
      Move.fromStandardNotation("O-O", StandardBoard(Map(), turnColor = Color.Black))
    }
  }

  test("testFromStandardNotation_blackQueensideCastle") {
    assertResult(Success(CastleMove(Square(3, 8)))) {
      Move.fromStandardNotation("O-O-O", StandardBoard(Map(), turnColor = Color.Black))
    }
  }

  test("testFromStandardNotation_standardOpeningPawnMove") {
    assertResult(Success(NormalMove(Square(4, 2), Square(4, 4)))) {
      Move.fromStandardNotation("d4", StandardBoard.StartingPosition)
    }
  }

  test("testFromStandardNotation_pawnCaptureVsMove") {
    assertResult(Success(NormalMove(Square(4, 4), Square(5, 5)))) {
      Move.fromStandardNotation("dxe5", StandardBoard(
        Map(Square(5, 4) -> Pawn(Color.White), Square(4, 4) -> Pawn(Color.White), Square(5, 5) -> Pawn(Color.Black))))
    }
  }

  test("testFromStandardNotation_pieceObstructingItsTwin") {
    assertResult(Success(NormalMove(Square(3, 3), Square(3, 8)))) {
      Move.fromStandardNotation("Rc8", StandardBoard(
        Map(Square(3, 2) -> Rook(Color.White), Square(3, 3) -> Rook(Color.White))))
    }
  }

  test("testFromStandardNotation_startColSpecified") {
    assertResult(Success(NormalMove(Square(4, 5), Square(2, 6)))) {
      Move.fromStandardNotation("Ndb6", StandardBoard(
        Map(Square(1, 4) -> Knight(Color.White), Square(4, 5) -> Knight(Color.White))))
    }
  }

  test("testFromStandardNotation_ambiguous") {
    assertResult("Ambiguous move: Nb6") {
      Move.fromStandardNotation("Nb6", StandardBoard(
        Map(Square(1, 4) -> Knight(Color.White), Square(4, 5) -> Knight(Color.White))))
        .fold(t => t.getMessage, _ => fail())
    }
  }
}
