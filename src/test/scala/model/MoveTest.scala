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

  test("testToStandardNotation_impossible") {
    assertResult("There's no piece at Square(8,7)") {
      Move.toStandardNotation(
        NormalMove(Square(8, 7), Square(8, 8)), StandardBoard(Map(Square(1, 1) -> Pawn(Color.White))))
          .fold(ex => ex.getMessage, _ => fail())
    }
  }

  test("testFromStandardNotation_whiteKingsideCastle") {
    assertResult(Success(CastleMove(Square(7, 1)))) {
      Move.fromStandardNotation("O-O", StandardBoard(Map(), turnColor = Color.White))
    }
  }

  test("testToStandardNotation_whiteKingsideCastle") {
    assertResult(Success("O-O")) {
      Move.toStandardNotation(
        CastleMove(Square(7, 1)), StandardBoard(
          Map(Square(5, 1) -> King(Color.White), Square(8, 1) -> Rook(Color.White)), turnColor = Color.White))
    }
  }

  test("testFromStandardNotation_whiteQueensideCastle") {
    assertResult(Success(CastleMove(Square(3, 1)))) {
      Move.fromStandardNotation("O-O-O", StandardBoard(Map(), turnColor = Color.White))
    }
  }

  test("testToStandardNotation_whiteQueensideCastle") {
    assertResult(Success("O-O-O")) {
      Move.toStandardNotation(
        CastleMove(Square(3, 1)), StandardBoard(
          Map(Square(5, 1) -> King(Color.White), Square(1, 1) -> Rook(Color.White)), turnColor = Color.White))
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

  test("testToStandardNotation_blackQueensideCastle") {
    assertResult(Success("O-O-O")) {
      Move.toStandardNotation(
        CastleMove(Square(3, 8)), StandardBoard(
          Map(Square(5, 8) -> King(Color.Black), Square(1, 8) -> Rook(Color.Black)), turnColor = Color.Black))
    }
  }


  test("testFromStandardNotation_standardOpeningPawnMove") {
    assertResult(Success(NormalMove(Square(4, 2), Square(4, 4)))) {
      Move.fromStandardNotation("d4", StandardBoard.StartingPosition)
    }
  }

  test("testToStandardNotation_standardOpeningPawnMove") {
    assertResult(Success("d4")) {
      Move.toStandardNotation(NormalMove(Square(4, 2), Square(4, 4)), StandardBoard.StartingPosition)
    }
  }

  test("testFromStandardNotation_pawnCaptureVsMove") {
    assertResult(Success(NormalMove(Square(4, 4), Square(5, 5)))) {
      Move.fromStandardNotation(
        "dxe5", StandardBoard(
          Map(Square(5, 4) -> Pawn(Color.White), Square(4, 4) -> Pawn(Color.White), Square(5, 5) -> Pawn(Color.Black))))
    }
  }

  test("testToStandardNotation_pawnCaptureVsMove") {
    assertResult(Success("dxe5")) {
      Move.toStandardNotation(
        NormalMove(Square(4, 4), Square(5, 5)), StandardBoard(
          Map(Square(5, 4) -> Pawn(Color.White), Square(4, 4) -> Pawn(Color.White), Square(5, 5) -> Pawn(Color.Black))))
    }
  }

  test("testFromStandardNotation_pieceObstructingItsTwin") {
    assertResult(Success(NormalMove(Square(3, 3), Square(3, 8)))) {
      Move.fromStandardNotation(
        "Rc8", StandardBoard(Map(Square(3, 2) -> Rook(Color.White), Square(3, 3) -> Rook(Color.White))))
    }
  }

  test("testToStandardNotation_pieceObstructingItsTwin") {
    assertResult(Success("Rc8")) {
      Move.toStandardNotation(
        NormalMove(Square(3, 3), Square(3, 8)),
        StandardBoard(Map(Square(3, 2) -> Rook(Color.White), Square(3, 3) -> Rook(Color.White))))
    }
  }

  test("testFromStandardNotation_startFileSpecified") {
    assertResult(Success(NormalMove(Square(4, 5), Square(2, 6)))) {
      Move.fromStandardNotation(
        "Ndb6", StandardBoard(Map(Square(1, 4) -> Knight(Color.White), Square(4, 5) -> Knight(Color.White))))
    }
  }

  test("testToStandardNotation_startFileSpecified") {
    assertResult(Success("Ndb6")) {
      Move.toStandardNotation(
        NormalMove(Square(4, 5), Square(2, 6)),
        StandardBoard(Map(Square(1, 4) -> Knight(Color.White), Square(4, 5) -> Knight(Color.White))))
    }
  }

  test("testToStandardNotation_rankSpecified") {
    assertResult(Success("Q4e1")) {
      Move.toStandardNotation(
        NormalMove(Square(8, 4), Square(5, 1)),
        StandardBoard(Map(Square(8, 4) -> Queen(Color.White), Square(8, 1) -> Queen(Color.White))))
    }
  }

  test("testToStandardNotation_fileAndRankSpecified") {
    assertResult(Success("Qh4e1")) {
      Move.toStandardNotation(
        NormalMove(Square(8, 4), Square(5, 1)),
        StandardBoard(Map(
          Square(8, 4) -> Queen(Color.White), Square(5, 4) -> Queen(Color.White), Square(8, 1) -> Queen(Color.White))))
    }
  }

  test("testFromStandardNotation_ambiguous") {
    assertResult("Ambiguous move: Nb6") {
      Move.fromStandardNotation(
        "Nb6", StandardBoard(Map(Square(1, 4) -> Knight(Color.White), Square(4, 5) -> Knight(Color.White))))
          .fold(t => t.getMessage, _ => fail())
    }
  }

  test("testFromStandardNotation_promotion") {
    assertResult(Success(NormalMove(Square(5, 7), Square(5, 8), Some(Queen(Color.White))))) {
      Move.fromStandardNotation(
        "e8=Q+", StandardBoard(Map(Square(5, 7) -> Pawn(Color.White), Square(5, 2) -> King(Color.Black))))
    }
  }

  test("testToStandardNotation_promotionWithCheck") {
    assertResult(Success("e8=Q+")) {
      Move.toStandardNotation(
        NormalMove(Square(5, 7), Square(5, 8), Some(Queen(Color.White))),
        StandardBoard(Map(Square(5, 7) -> Pawn(Color.White), Square(5, 2) -> King(Color.Black))))
    }
  }
}
