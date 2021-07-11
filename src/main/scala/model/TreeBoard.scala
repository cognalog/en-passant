package model

import model.Color.Color

case class TreeBoard(id: String, turnColor: Color, children: Map[Move, TreeBoard]) extends Board {
  override def getNextMoves: Iterable[(Move, Board)] = children.toList
}
