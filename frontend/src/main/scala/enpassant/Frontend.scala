package enpassant

import com.raquo.laminar.api.L._
import org.scalajs.dom
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportTopLevel, JSImport}

object Frontend {
  private var board: Option[Chessboard] = None
  private var game: Option[Chess] = None
  private val startPosition =
    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

  def main(args: Array[String]): Unit = {
    val containerNode = dom.document.querySelector("#app")
    render(containerNode, appElement)

    // Initialize the chessboard after the DOM is ready
    dom.window.setTimeout(
      { () =>
        initializeBoard()
        game = Some(new Chess())
        game.foreach(_.load(startPosition))
      },
      100
    )
  }

  private def initializeBoard(): Unit = {
    val config = ChessboardConfig(
      position = startPosition,
      draggable = js.defined(true),
      onDragStart =
        js.defined({ (source: String, piece: String, _: js.Object) =>
          game match {
            case Some(g) =>
              // Only allow white pieces to be moved and only on white's turn
              piece.charAt(0) == 'w' && g.turn() == "w"
            case None => false
          }
        }),
      onDrop = js.defined({ (source: String, target: String, _: js.Object) =>
        game match {
          case Some(g) =>
            // Try to make the move
            val move = g.move(
              js.Dynamic.literal(
                from = source,
                to = target,
                promotion = "q" // Always promote to queen for simplicity
              )
            )

            if (move != null) {
              // If move was legal, make bot move
              makeBotMove()
              s"$source-$target"
            } else {
              "snapback"
            }
          case None => "snapback"
        }
      }),
      onSnapEnd = js.defined({ () =>
        // Update the board position after the piece snap animation
        game.foreach(g => board.foreach(_.position(g.fen())))
        updateGameStatus()
      })
    )

    board = Some(new Chessboard("board", config))
  }

  private def makeBotMove(): Unit = {
    game.foreach { g =>
      if (!g.game_over()) {
        val fen = g.fen()
        ApiClient
          .getBotMove(fen)
          .`then`[Unit]({ moveStr =>
            // Parse the move string (e.g., "e7-e5") into from/to
            val parts = moveStr.split("-")
            if (parts.length == 2) {
              val move = g.move(
                js.Dynamic.literal(
                  from = parts(0),
                  to = parts(1),
                  promotion = "q" // Always promote to queen for simplicity
                )
              )
              if (move != null) {
                board.foreach(_.position(g.fen()))
                updateGameStatus()
              }
            }
          })
      }
    }
  }

  private def updateGameStatus(): Unit = {
    game.foreach { g =>
      val status = dom.document.getElementById("status")
      if (status != null) {
        val statusText = if (g.game_over()) {
          if (g.in_checkmate()) {
            if (g.turn() == "w") "Game Over: Black wins by checkmate"
            else "Game Over: White wins by checkmate"
          } else if (g.in_draw()) {
            "Game Over: Draw"
          } else {
            "Game Over"
          }
        } else {
          if (g.in_check()) {
            if (g.turn() == "w") "White is in check" else "Black is in check"
          } else {
            if (g.turn() == "w") "White to move" else "Black to move"
          }
        }
        status.textContent = statusText
      }
    }
  }

  def appElement: Element = {
    div(
      cls := "chess-app",
      h1("En Passant Chess Bot"),
      div(
        cls := "chessboard",
        idAttr := "board"
      ),
      div(
        cls := "game-info",
        p(
          idAttr := "status",
          "Game in progress..."
        )
      ),
      div(
        cls := "controls",
        button(
          "New Game",
          onClick --> { _ =>
            game.foreach { g =>
              g.load(startPosition)
              board.foreach(_.position(startPosition))
              updateGameStatus()
            }
          }
        )
      )
    )
  }
}
