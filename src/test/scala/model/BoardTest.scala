package model

import org.scalatest.funsuite.AnyFunSuite

import scala.util.Success

class BoardTest extends AnyFunSuite {

  test("testStandardFromMoves_noMoves") {
    assertResult(Success(StandardBoard.StartingPosition)) {
      Board.standardFromMoves(Seq())
    }
  }

  test("testStandardFromMoves_invalidMoves") {
    assertResult("There's no piece at Square(5,5)") {
      Board
        .standardFromMoves(
          Seq(
            NormalMove(Square(5, 2), Square(5, 4), Pawn(Color.White)),
            NormalMove(Square(5, 5), Square(5, 6), Pawn(Color.Black))
          )
        )
        .fold(ex => ex.getMessage, _ => fail())
    }
  }

  test("testStandardFromMoves_validMoves") {
    val move1 = NormalMove(Square(5, 2), Square(5, 4), Pawn(Color.White))
    val move2 = NormalMove(Square(5, 7), Square(5, 5), Pawn(Color.Black))
    assertResult(
      Success(StandardBoard.StartingPosition.move(move1).get.move(move2).get)
    ) {
      Board.standardFromMoves(Seq(move1, move2))
    }
  }

  test("testStandardFromMoveStrings_noMoves") {
    assertResult(Success(StandardBoard.StartingPosition)) {
      Board.standardFromMoveStrings(Seq())
    }
  }

  test("testStandardFromMoveStrings_invalidMoves") {
    assertResult("Impossible move: Bc4") {
      Board
        .standardFromMoveStrings(Seq("e4", "Bc4"))
        .fold(ex => ex.getMessage, _ => fail())
    }
  }

  test("testStandardFromMoveStrings_validMoves") {
    val move1 = NormalMove(Square(5, 2), Square(5, 4), Pawn(Color.White))
    val move2 = NormalMove(Square(5, 7), Square(5, 5), Pawn(Color.Black))
    assertResult(
      Success(StandardBoard.StartingPosition.move(move1).get.move(move2).get)
    ) {
      Board.standardFromMoveStrings(Seq("e4", "e5"))
    }
  }

}
