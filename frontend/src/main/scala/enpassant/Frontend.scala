package enpassant

import com.raquo.laminar.api.L._
import org.scalajs.dom
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportTopLevel, JSImport}

object Frontend {
  private var board: Option[Chessboard] = None
  private var game: Option[Chess] = None
  private var moveHistory: List[String] = List.empty
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
      pieceTheme = js.defined(
        "https://chessboardjs.com/img/chesspieces/alpha/{piece}.png"
      ),
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
              // Add move to history in SAN format
              moveHistory = moveHistory :+ move.san.asInstanceOf[String]
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
        ApiClient
          .getBotMove(moveHistory.mkString(" "))
          .`then`[Unit]({ moveStr =>
            println(s"Received move from backend: $moveStr") // Debug log
            println(s"Current position FEN: ${g.fen()}") // Debug log
            println(s"Current turn: ${g.turn()}") // Debug log

            // If direct move failed, try to parse it
            val movePattern =
              """([NBRQK])?([a-h][1-8])?x?([a-h][1-8])(?:=([NBRQ]))?""".r
            moveStr match {
              case movePattern(piece, start, dest, promotion) =>
                println(
                  s"Parsed move: piece=$piece, start=$start, dest=$dest, promotion=$promotion"
                ) // Debug log

                // For pawn moves, try all possible source squares
                val attempts = if (piece == null) {
                  // Get all legal moves
                  val legalMoves = g
                    .moves(js.Dynamic.literal(verbose = true))
                    .asInstanceOf[js.Array[js.Dynamic]]
                  println(
                    s"Legal moves: ${js.Dynamic.global.JSON.stringify(legalMoves)}"
                  ) // Debug log

                  // Find moves that match our destination
                  val matchingMoves =
                    legalMoves.filter(m => m.to.asInstanceOf[String] == dest)
                  println(
                    s"Matching moves: ${js.Dynamic.global.JSON.stringify(matchingMoves)}"
                  ) // Debug log

                  matchingMoves
                    .map(m =>
                      js.Dynamic.literal(
                        from = m.from.asInstanceOf[String],
                        to = m.to.asInstanceOf[String],
                        promotion = Option(promotion)
                          .map(_.tail)
                          .getOrElse("q")
                          .asInstanceOf[js.Any]
                      )
                    )
                    .toSeq
                } else {
                  // For pieces, try multiple formats in order of preference
                  Seq(
                    // Try with explicit from-to if start square is provided (most reliable)
                    Option(start).map(s =>
                      js.Dynamic.literal(
                        from = s.asInstanceOf[js.Any],
                        to = dest.asInstanceOf[js.Any]
                      )
                    ),
                    // Try SAN format without start square
                    Some(
                      js.Dynamic.literal(
                        san =
                          s"${piece}${dest}${Option(promotion).getOrElse("")}"
                            .asInstanceOf[js.Any]
                      )
                    ),
                    // Try SAN format with start square if provided
                    Option(start).map(s =>
                      js.Dynamic.literal(
                        san =
                          s"${piece}${s}${dest}${Option(promotion).getOrElse("")}"
                            .asInstanceOf[js.Any]
                      )
                    )
                  ).flatten
                }

                // Try each move format until one succeeds
                val moveResult = attempts.foldLeft[Option[js.Dynamic]](None) { (acc, moveAttempt) =>
                  acc.orElse {
                    println(
                      s"Attempting move with: ${js.Dynamic.global.JSON.stringify(moveAttempt)}"
                    ) // Debug log
                    val move = g.move(moveAttempt)
                    println(
                      s"Move result: ${if (move != null) "success" else "failed"}"
                    ) // Debug log
                    if (move != null) Some(move.asInstanceOf[js.Dynamic])
                    else None
                  }
                }

                moveResult match {
                  case Some(move) =>
                    moveHistory = moveHistory :+ move.san.asInstanceOf[String]
                    board.foreach(_.position(g.fen()))
                    updateGameStatus()
                  case None =>
                    println(
                      s"All move attempts failed for: $moveStr"
                    ) // Debug log
                }

              case _ =>
                println(s"Move didn't match pattern: $moveStr") // Debug log
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
              moveHistory = List.empty
              updateGameStatus()
            }
          }
        )
      )
    )
  }
}
