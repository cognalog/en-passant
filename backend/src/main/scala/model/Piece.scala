package model

import model.Color.Color

/** One of two piece colors in chess.
  */
object Color extends Enumeration {
  type Color = Value
  val White, Black = Value

  def opposite(color: Color): Color = if (color == White) Black else White

  def shortName(color: Color): Char = if (color == White) 'W' else 'B'
}

/** A chess piece.
  */
trait Piece {

  /** @return
    *   the color of the piece
    */
  def color: Color

  /** @return
    *   true if the piece has moved in a game, false otherwise.
    */
  def hasMoved: Boolean

  /** @return
    *   the 1-character short name for this piece.
    */
  def shortName: Char

  /** @return
    *   the standard notation for the piece. Usually the short name, except for
    *   pawns.
    */
  def standardName: String = shortName.toString

  /** @return
    *   a copy of the piece where hasMoved will return true
    */
  def updateHasMoved(): Piece

  /** Calculate the squares this piece may legally move to from the given square
    * on the given board. Does not account for leaving the king in check.
    *
    * @param currentSquare
    *   the piece's starting square on the board.
    * @param board
    *   the board to consider.
    * @return
    *   All squares to which this piece can move on the board, not considering
    *   king safety.
    */
  def getLegalMoves(currentSquare: Square, board: Board): Set[Move]

  /** Calculate the squares this piece may legally capture on. This
    * functionality is distinct from [[getLegalMoves()]] only for pawns.
    *
    * @param currentSquare
    *   the piece's starting square on the board.
    * @param board
    *   the board to consider.
    * @return
    *   All squares to which this piece can move to capture, regardless king
    *   safety or whether there is a capturable piece at the destination.
    */
  def getCaptures(currentSquare: Square, board: Board): Set[Move]

  /** Generates available squares for this piece to move to, stopping upon
    * encountering another piece or the edge of the board. The result will
    * include a captured piece.
    *
    * @param currentSquare
    *   the current square, which should not be included in the result.
    * @param board
    *   the board to consider.
    * @param nextFn
    *   the function for generating the next square.
    * @return
    *   the set of squares between currentSquare and the first piece encountered
    *   or the board's edge.
    */
  def getAvailableLinearSquares(
      currentSquare: Square,
      board: Board,
      nextFn: Square => Square
  ): Set[Square] = {
    val nextSquare = nextFn(currentSquare)
    if (!board.isInBounds(nextSquare)) return Set()
    val maybeCapture = board.pieceAt(nextSquare)
    if (maybeCapture.isDefined) {
      if (maybeCapture.get.isColor(color)) return Set()
      else return Set(nextSquare)
    }
    getAvailableLinearSquares(nextSquare, board, nextFn) + nextSquare
  }

  /** @param color
    *   the color in question.
    * @return
    *   true if this piece is the given color, false otherwise.
    */
  def isColor(color: Color): Boolean = {
    color == this.color
  }

  /** @return
    *   whether this piece, alone with the king, can mate
    */
  def canMateWithKing: Boolean

  /** @return
    *   the canonical score for this piece.
    */
  def pointValue: Int

  /** Helper method to create a NormalMove with the correct isCapture flag
    *
    * @param start
    *   the starting square
    * @param dest
    *   the destination square
    * @param board
    *   the current board state
    * @return
    *   a NormalMove with isCapture set based on whether there's a piece at dest
    */
  protected def createMove(
      start: Square,
      dest: Square,
      board: Board
  ): NormalMove = {
    val isCapture = board.pieceAt(dest).exists(!_.isColor(color)) ||
      (this.isInstanceOf[Pawn] && board.isEnPassantPossible(dest))
    NormalMove(start, dest, this, isCapture)
  }
}
