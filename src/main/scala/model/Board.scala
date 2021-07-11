package model

import model.Color.Color

trait Board {
  def id: String

  def turnColor: Color

  def getNextMoves: Iterable[(Move, Board)]
}
