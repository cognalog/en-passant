package actor

import model.Color.Color
import model.{Board, Move}

import scala.io.StdIn
import scala.util.Try

case class HumanPlayer() extends Player {
  override def GetNextMove(board: Board, color: Color): Try[Move] = {
    println(s"You're playing $color. Here is the current board:")
    println(board)
    println("What's your next move?")
    Move.fromStandardNotation(StdIn.readLine(), board)
  }
}
