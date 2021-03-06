package model

import model.Color.Color

/**
 * A board which does not process moves except as keys to a pre-set map of
 * successors. Useful in [[ai.search.MoveSearch]] unit tests.
 *
 * @param id        identifier for the board.
 * @param turnColor color for the player whose turn it is.
 * @param children  a map of possible next boards, keyed by corresponding moves.
 *                  The move/board pairs don't need to make sense.
 */
case class TreeBoard(id: String, turnColor: Color, children: Map[Move, TreeBoard]) extends Board {
  override def getNextMoves: Iterable[(Move, Board)] = children.toList
}
