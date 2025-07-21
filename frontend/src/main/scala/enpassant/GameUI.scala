package enpassant

import com.raquo.laminar.api.L._

object GameUI {

  private def moveLogElement: Element = {
    val moveLogStyles = Seq(
      "display" -> "flex",
      "overflow-x" -> "auto",
      "padding" -> "10px",
      "margin-top" -> "10px",
      "background-color" -> "#f5f5f5",
      "border-radius" -> "4px",
      "font-family" -> "monospace"
    )

    val moveItemStyles = Seq(
      "margin-right" -> "20px",
      "white-space" -> "nowrap"
    )

    val clickableStyle = moveItemStyles ++ Seq("cursor" -> "pointer")
    val nonClickableStyle = moveItemStyles

    div(
      h3("Move Log"),
      p(
        styleAttr := "font-size: 0.8em; color: #666; margin: 5px 0;",
        "Click on a black move to view the board position after that move."
      ),
      div(
        cls := "move-log",
        styleAttr := moveLogStyles
          .map { case (k, v) => s"$k: $v" }
          .mkString(";"),
        children <-- GameState.moveHistorySignal.signal
          .combineWith(GameState.currentMoveIndexSignal.signal)
          .map { case (moves, currentIndex) =>
            GameState.formatMovePairs(moves).zipWithIndex.map {
              case ((moveNumber, white, black), pairIndex) =>
                val whiteIndex = pairIndex * 2
                val blackIndex = whiteIndex + 1

                val isFuture = currentIndex >= 0 && whiteIndex > currentIndex

                // We can click black's moves (odd indices) since they lead to white's turn
                val wouldBeWhiteTurn = blackIndex % 2 == 1
                val isClickable = black.nonEmpty && wouldBeWhiteTurn

                val moveItemStyle = (if (isFuture) Seq("color" -> "#999")
                                     else Seq()) ++
                  (if (isClickable) clickableStyle else nonClickableStyle)

                div(
                  cls := "move-pair",
                  styleAttr := moveItemStyle
                    .map { case (k, v) => s"$k: $v" }
                    .mkString(";"),
                  span(s"$moveNumber."),
                  span(
                    cls := "white-move",
                    styleAttr := s"margin: 0 5px; ${if (whiteIndex == currentIndex) "background-color: #e0e0e0;"
                      else ""}",
                    s" $white"
                  ),
                  if (black.nonEmpty) {
                    span(
                      cls := "black-move",
                      styleAttr := s"margin-left: 5px; ${if (blackIndex == currentIndex) "background-color: #e0e0e0;"
                        else ""}",
                      onClick --> { _ =>
                        if (isClickable) MoveHandler.revertToMove(blackIndex)
                      },
                      s" $black"
                    )
                  } else emptyNode
                )
            }
          }
      )
    )
  }

  private def gameInfoSection: Element = {
    div(
      cls := "game-info",
      p(
        idAttr := "status",
        "Game in progress..."
      )
    )
  }

  private def controlsSection: Element = {
    div(
      cls := "controls",
      button(
        "New Game",
        onClick --> { _ =>
          ChessBoard.resetToStartPosition()
          GameState.clearHistory()
          GameStatusManager.updateGameStatus()
        }
      )
    )
  }

  private def chessboardSection: Element = {
    div(
      cls := "chessboard",
      idAttr := "board"
    )
  }

  def createAppElement: Element = {
    div(
      cls := "chess-app",
      h1("En Passant Chess Bot"),
      chessboardSection,
      gameInfoSection,
      controlsSection,
      moveLogElement
    )
  }
}