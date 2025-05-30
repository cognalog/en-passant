package model

import org.scalatest.funsuite.AnyFunSuite

import scala.util.Success

class MoveTest extends AnyFunSuite {
  test("testFromStandardNotation_malformed") {
    assertResult("Malformed move: abcd") {
      Move
        .fromStandardNotation("abcd", StandardBoard.StartingPosition)
        .fold(ex => ex.getMessage, _ => fail())
    }
  }

  test("testFromStandardNotation_impossible") {
    assertResult("Impossible move: h8") {
      Move
        .fromStandardNotation(
          "h8",
          StandardBoard(Map(Square(1, 1) -> Pawn(Color.White)))
        )
        .fold(ex => ex.getMessage, _ => fail())
    }
  }

  test("testFromStandardNotation_whiteKingsideCastle") {
    assertResult(Success(CastleMove(Square(7, 1)))) {
      Move.fromStandardNotation(
        "O-O",
        StandardBoard(Map(), turnColor = Color.White)
      )
    }
  }

  test("testFromStandardNotation_whiteQueensideCastle") {
    assertResult(Success(CastleMove(Square(3, 1)))) {
      Move.fromStandardNotation(
        "O-O-O",
        StandardBoard(Map(), turnColor = Color.White)
      )
    }
  }

  test("testFromStandardNotation_blackKingsideCastle") {
    assertResult(Success(CastleMove(Square(7, 8)))) {
      Move.fromStandardNotation(
        "O-O",
        StandardBoard(Map(), turnColor = Color.Black)
      )
    }
  }

  test("testFromStandardNotation_blackQueensideCastle") {
    assertResult(Success(CastleMove(Square(3, 8)))) {
      Move.fromStandardNotation(
        "O-O-O",
        StandardBoard(Map(), turnColor = Color.Black)
      )
    }
  }

  test("testFromStandardNotation_standardOpeningPawnMove") {
    assertResult(Success(NormalMove(Square(4, 2), Square(4, 4), Pawn(Color.White)))) {
      Move.fromStandardNotation("d4", StandardBoard.StartingPosition)
    }
  }

  test("testFromStandardNotation_pawnCaptureVsMove") {
    assertResult(Success(NormalMove(Square(4, 4), Square(5, 5), Pawn(Color.White), true))) {
      Move.fromStandardNotation(
        "dxe5",
        StandardBoard(
          Map(
            Square(5, 4) -> Pawn(Color.White),
            Square(4, 4) -> Pawn(Color.White),
            Square(5, 5) -> Pawn(Color.Black)
          )
        )
      )
    }
  }

  test("testFromStandardNotation_pieceObstructingItsTwin") {
    assertResult(Success(NormalMove(Square(3, 3), Square(3, 8), Rook(Color.White)))) {
      Move.fromStandardNotation(
        "Rc8",
        StandardBoard(
          Map(
            Square(3, 2) -> Rook(Color.White),
            Square(3, 3) -> Rook(Color.White)
          )
        )
      )
    }
  }

  test("testFromStandardNotation_startColSpecified") {
    assertResult(Success(NormalMove(Square(4, 5), Square(2, 6), Knight(Color.White)))) {
      Move.fromStandardNotation(
        "Ndb6",
        StandardBoard(
          Map(
            Square(1, 4) -> Knight(Color.White),
            Square(4, 5) -> Knight(Color.White)
          )
        )
      )
    }
  }

  test("testFromStandardNotation_ambiguous") {
    assertResult("Ambiguous move: Nb6") {
      Move
        .fromStandardNotation(
          "Nb6",
          StandardBoard(
            Map(
              Square(1, 4) -> Knight(Color.White),
              Square(4, 5) -> Knight(Color.White)
            )
          )
        )
        .fold(t => t.getMessage, _ => fail())
    }
  }

  test("testFromStandardNotation_promotion") {
    assertResult(
      Success(NormalMove(Square(5, 7), Square(5, 8), Pawn(Color.White), promotion = Some(Queen(Color.White))))
    ) {
      Move.fromStandardNotation(
        "e8=Q+",
        StandardBoard(
          Map(
            Square(5, 7) -> Pawn(Color.White),
            Square(5, 2) -> King(Color.Black)
          )
        )
      )
    }
  }
  test("testToStandardNotation_castle_kingside") {
    assertResult("O-O") {
      CastleMove(Square(7, 8)).toStandardNotation
    }
    assertResult("O-O") {
      CastleMove(Square(7, 1)).toStandardNotation
    }
  }

  test("testToStandardNotation_castle_queenside") {
    assertResult("O-O-O") {
      CastleMove(Square(3, 1)).toStandardNotation  
    }
    assertResult("O-O-O") {
      CastleMove(Square(3, 8)).toStandardNotation  
    }
  }

  test("testToStandardNotation_normal_pawn") {
    assertResult("e4") {
      NormalMove(Square(5, 2), Square(5, 4), Pawn(Color.White)).toStandardNotation
    }
  }

  test("testToStandardNotation_normal_piece") {
    assertResult("Ne4d6") {
      NormalMove(Square(5, 4), Square(4, 6), Knight(Color.White)).toStandardNotation
    }
  }

  test("testToStandardNotation_capture") {
    assertResult("Ne4xd6") {
      NormalMove(Square(5, 4), Square(4, 6), Knight(Color.White), isCapture = true).toStandardNotation
    }
  }

  test("testToStandardNotation_promotion") {
    assertResult("e8=Q") {
      NormalMove(Square(5, 7), Square(5, 8), Pawn(Color.White), promotion = Some(Queen(Color.White))).toStandardNotation
    }
  }

  test("testToStandardNotation_capture_and_promotion") {
    assertResult("e7xf8=Q") {
      NormalMove(Square(5, 7), Square(6, 8), Pawn(Color.White), isCapture = true, promotion = Some(Queen(Color.White))).toStandardNotation
    }
  }
}
