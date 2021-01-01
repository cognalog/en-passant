package model

import model.Color.Color

object Color extends Enumeration {
  type Color = Value
  val White, Black = Value
}

trait Piece {
  def color: Color

  def hasMoved: Boolean

  def updateHasMoved(): Piece

  def getLegalMoves(currentSquare: Square, board: Board): Set[Square]

  /**
   * Generates available squares for this piece to move to, stopping upon encountering another piece or the edge of
   * the board. The result will include a captured piece.
   *
   * @param currentSquare the current square, which should not be included in the result.
   * @param board         the board to consider.
   * @param nextFn        the function for generating the next square.
   * @return the set of squares between currentSquare and the first piece encountered or the board's edge.
   */
  def getAvailableLinearSquares(currentSquare: Square, board: Board, nextFn: Square => Square): Set[Square] = {
    val nextSquare = nextFn(currentSquare)
    if (!board.isInBounds(nextSquare)) return Set()
    val maybeCapture = board.pieceAt(nextSquare)
    if (maybeCapture.isDefined) {
      if (maybeCapture.get.isColor(color)) return Set() else return Set(nextSquare)
    }
    getAvailableLinearSquares(nextSquare, board, nextFn) + nextSquare
  }

  def isColor(color: Color): Boolean = {
    color == this.color
  }
}
