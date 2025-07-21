package enpassant

import scala.scalajs.js
import org.scalajs.dom

object ChessBoard {
  private var board: Option[Chessboard] = None
  private var game: Option[Chess] = None

  def getBoard: Option[Chessboard] = board
  def getGame: Option[Chess] = game

  def initialize(
      onDragStart: (String, String, js.Object) => Boolean,
      onDrop: (String, String, js.Object) => String,
      onSnapEnd: () => Unit
  ): Unit = {
    val config = ChessboardConfig(
      position = GameState.getStartPosition,
      draggable = js.defined(true),
      pieceTheme = js.defined(
        "https://chessboardjs.com/img/chesspieces/alpha/{piece}.png"
      ),
      onDragStart = js.defined(onDragStart),
      onDrop = js.defined(onDrop),
      onSnapEnd = js.defined(onSnapEnd)
    )

    board = Some(new Chessboard("board", config))
    game = Some(new Chess())
    game.foreach(_.load(GameState.getStartPosition))
    
    println("Board and game initialized") // Debug log
  }

  def resetToStartPosition(): Unit = {
    game.foreach { g =>
      g.load(GameState.getStartPosition)
      board.foreach(_.position(GameState.getStartPosition))
      println(s"Board reset to start position: ${g.fen()}") // Debug log
    }
  }

  def updatePosition(): Unit = {
    game.foreach { g =>
      board.foreach(_.position(g.fen()))
      println(s"Board position updated: ${g.fen()}") // Debug log
    }
  }

  def getFen: Option[String] = game.map(_.fen())

  def replayMovesToPosition(moves: List[ChessMove]): Unit = {
    game.foreach { g =>
      g.load(GameState.getStartPosition)
      moves.foreach { move =>
        println(s"Replaying move from ${move.from} to ${move.to}") // Debug log
        val result = g.move(
          js.Dynamic.literal(
            from = move.from.asInstanceOf[js.Any],
            to = move.to.asInstanceOf[js.Any],
            promotion = move.promotion
              .map(_.asInstanceOf[js.Any])
              .getOrElse("q".asInstanceOf[js.Any])
          )
        )
        if (result == null) {
          println(s"Failed to replay move: ${move.san}") // Debug log
        }
      }
      updatePosition()
    }
  }
}