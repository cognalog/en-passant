package ai.search

import model.{Board, Move}

/** A [[MoveOrdering]] that prefers "interesting" moves, meaning moves resulting
  * in captures or checks.
  *
  * @param startBoard
  *   the board preceding the move and resultant board.
  */
case class InterestingMovesFirst(startBoard: Board) extends MoveOrdering() {
  override def compare(x: (Move, Board), y: (Move, Board)): Int =
    totalScore(x._1, x._2) - totalScore(y._1, y._2)

  private def totalScore(move: Move, board: Board): Int =
    captureScore(move) + checkScore(board)

  private def captureScore(move: Move) =
    startBoard.pieceAt(move.destination).map(_.pointValue).getOrElse(0)

  /* TODO: kill this expensive-ish check if the ordering doesn't seem to help with performance */
  private def checkScore(board: Board) = if (
    board
      .kingInCheck(board.turnColor)
  ) 5
  else 0
}
