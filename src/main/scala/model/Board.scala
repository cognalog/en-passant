package model

import model.Color.Color

trait Board {
  def turnColor: Color

  def getNextMoves: Iterable[(Move, StandardBoard)]

  def toString: String
}
