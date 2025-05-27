package model

import model.Color.{Color, White}

/** A pawn piece
  *
  * @see
  *   [[https://en.wikipedia.org/wiki/Pawn_(chess)]]
  * @param color
  *   the color of this Pawn.
  * @param hasMoved
  *   whether this Pawn has moved.
  */
case class Pawn(
    override val color: Color,
    override val hasMoved: Boolean = false
) extends Piece {

  override def getLegalMoves(currentSquare: Square, board: Board): Set[Move] = {
    (getForwardMoves(currentSquare, board)
      ++ getCaptures(currentSquare, board))
      .filter(move => board.isInBounds(move.destination))
      .flatMap(generatePromotions)
  }

  private def generatePromotions(move: Move): Set[Move] = {
    val maxRank =
      if (isColor(White)) StandardBoard.RankAndFileMax
      else StandardBoard.RankAndFileMin
    move match {
      case NormalMove(start, Square(destFile, destRank), piece, None)
          if destRank == maxRank =>
        Set(Knight(color), Bishop(color), Rook(color), Queen(color))
          .map(option =>
            NormalMove(start, Square(destFile, destRank), piece, Some(option))
          )
      case _ => Set(move)
    }
  }

  private def getForwardMoves(
      currentSquare: Square,
      board: Board
  ): Set[Move] = {
    (Set(changeRankByColor(currentSquare, 2))
      .filter(_ =>
        board.pieceAt(changeRankByColor(currentSquare, 1)).isEmpty && !hasMoved
      ) ++
      Set(changeRankByColor(currentSquare, 1)))
      .filter(board.pieceAt(_).isEmpty)
      .map(NormalMove(currentSquare, _, this))
  }

  override def getCaptures(currentSquare: Square, board: Board): Set[Move] = {
    Set(
      changeRankByColor(currentSquare, 1).changeFile(-1),
      changeRankByColor(currentSquare, 1).changeFile(1)
    )
      .filter(square =>
        board.isEnPassantPossible(square) || board
          .pieceAt(square)
          .fold(false)(!_.isColor(color))
      )
      .map(NormalMove(currentSquare, _, this))
  }

  private def changeRankByColor(square: Square, delta: Int): Square = {
    val coefficient = if (isColor(Color.Black)) -1 else 1
    square.changeRank(coefficient * delta)
  }

  override def updateHasMoved(): Piece = Pawn(color, hasMoved = true)

  /** NB: we use 'P' for printing pawns, but not for move notation per
    * international standard.
    *
    * @return
    *   the 1-character short name for this piece.
    */
  override def shortName: Char = 'P'

  override val canMateWithKing: Boolean = true

  override val pointValue: Int = 1
}
