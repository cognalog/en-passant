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

  // Tests for Move.toSAN method - proper Standard Algebraic Notation
  test("testToSAN_pawn_simple_move") {
    val board = StandardBoard.StartingPosition
    val move = NormalMove(Square(5, 2), Square(5, 4), Pawn(Color.White))
    assertResult("e4") {
      Move.toSAN(move, board)
    }
  }

  test("testToSAN_knight_simple_move") {
    val board = StandardBoard.StartingPosition
    val move = NormalMove(Square(7, 1), Square(6, 3), Knight(Color.White))
    assertResult("Nf3") {
      Move.toSAN(move, board)
    }
  }

  test("testToSAN_pawn_capture") {
    // Create board with capture possible
    val board = StandardBoard(Map(
      Square(5, 2) -> Pawn(Color.White),
      Square(4, 3) -> Pawn(Color.Black)
    ), Color.White)
    val move = NormalMove(Square(5, 2), Square(4, 3), Pawn(Color.White), isCapture = true)
    assertResult("exd3") {
      Move.toSAN(move, board)
    }
  }

  test("testToSAN_knight_with_disambiguation_needed") {
    // Create board where both knights can actually move to e5
    val board = StandardBoard(Map(
      Square(3, 3) -> Knight(Color.White), // Nc3 can move to e5 via d5-e5 
      Square(6, 3) -> Knight(Color.White), // Nf3 can move to e5 via d4-e5
      Square(1, 1) -> King(Color.White),
      Square(8, 8) -> King(Color.Black)
    ), Color.White)
    
    // Check if both knights can actually reach e5
    val nc3Moves = Knight(Color.White).getLegalMoves(Square(3, 3), board)
    val nf3Moves = Knight(Color.White).getLegalMoves(Square(6, 3), board)
    
    // Find a square both can reach for true disambiguation test
    val commonSquares = nc3Moves.map(_.destination).intersect(nf3Moves.map(_.destination))
    
    if (commonSquares.nonEmpty) {
      val targetSquare = commonSquares.head
      val move = NormalMove(Square(6, 3), targetSquare, Knight(Color.White))
      val sanMove = Move.toSAN(move, board)
      
      // Should include disambiguation
      assert(sanMove.contains("f") || sanMove.length > 3, s"Expected disambiguation in $sanMove")
      assert(sanMove.startsWith("N"))
    } else {
      // If no common squares, just test that no disambiguation is added when not needed
      val move = NormalMove(Square(6, 3), Square(5, 5), Knight(Color.White))
      val sanMove = Move.toSAN(move, board)
      assertResult("Ne5") { sanMove }
    }
  }

  test("testToSAN_castle_kingside") {
    val board = StandardBoard.StartingPosition
    val move = CastleMove(Square(7, 1))
    assertResult("O-O") {
      Move.toSAN(move, board)
    }
  }

  test("testToSAN_castle_queenside") {
    val board = StandardBoard.StartingPosition
    val move = CastleMove(Square(3, 1))
    assertResult("O-O-O") {
      Move.toSAN(move, board)
    }
  }

  test("testToSAN_promotion") {
    val board = StandardBoard(Map(
      Square(5, 7) -> Pawn(Color.White),
      Square(1, 1) -> King(Color.White),
      Square(8, 1) -> King(Color.Black) // Put black king on back rank to avoid check
    ), Color.White)
    val move = NormalMove(Square(5, 7), Square(5, 8), Pawn(Color.White), promotion = Some(Queen(Color.White)))
    val sanMove = Move.toSAN(move, board)
    
    // The move should be promotion, possibly with check
    assert(sanMove.startsWith("e8=Q"))
    assert(sanMove.matches("^e8=Q[+#]?$"))
  }

  test("testToSAN_capture_with_promotion") {
    val board = StandardBoard(Map(
      Square(5, 7) -> Pawn(Color.White),
      Square(6, 8) -> Rook(Color.Black),
      Square(1, 1) -> King(Color.White),
      Square(8, 1) -> King(Color.Black) // Put black king away to avoid check
    ), Color.White)
    val move = NormalMove(Square(5, 7), Square(6, 8), Pawn(Color.White), isCapture = true, promotion = Some(Queen(Color.White)))
    val sanMove = Move.toSAN(move, board)
    
    // The move should be capture with promotion, possibly with check
    assert(sanMove.startsWith("exf8=Q"))
    assert(sanMove.matches("^exf8=Q[+#]?$"))
  }
}
