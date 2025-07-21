package enpassant

import org.scalajs.dom

object GameStatusManager {
  
  def updateGameStatus(): Unit = {
    ChessBoard.getGame.foreach { g =>
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
}