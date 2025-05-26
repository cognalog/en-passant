package ai.search

import model.{Board, Move}

/** An [[Ordering]] for moves, used to improve the performance of [[MoveSearch]]
  * algorithms.
  */
trait MoveOrdering extends Ordering[(Move, Board)]
