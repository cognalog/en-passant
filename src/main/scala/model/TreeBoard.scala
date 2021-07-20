package model

import model.Color.Color

import scala.util.{Failure, Success, Try}

/**
 * A board which does not process moves except as keys to a pre-set map of
 * successors. Useful in [[ai.search.MoveSearch]] unit tests.
 *
 * @param id        identifier for the board.
 * @param turnColor color for the player whose turn it is.
 * @param children  a map of possible next boards, keyed by corresponding moves.
 *                  The move/board pairs don't need to make sense.
 */
case class TreeBoard(id: String, turnColor: Color, isCheckmate: Boolean = false,
                     isDraw: Boolean = false, children: Map[Move, TreeBoard] = Map()) extends Board {
  override def getNextMoves: Iterable[(Move, Board)] = children.toList

  override def move(move: Move): Try[Board] = children.get(move) match {
    case Some(board) => Success(board)
    case None => Failure(new IllegalArgumentException("No such move available"))
  }

  override def pieceAt(square: Square): Option[Piece] = None

  override def locatePiece(piece: Piece): Set[Square] = Set()

  override def isInBounds(square: Square): Boolean = false

  override def isEnPassantPossible(square: Square): Boolean = false

  override def kingInCheck(color: Color): Boolean = false
}
