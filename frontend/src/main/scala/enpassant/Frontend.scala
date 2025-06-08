package enpassant

import com.raquo.laminar.api.L._
import org.scalajs.dom
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportTopLevel, JSImport}

object Frontend {
  private var board: Option[Chessboard] = None
  private var game: Option[Chess] = None

  // Store full move objects instead of just SAN notation
  case class ChessMove(
      from: String,
      to: String,
      san: String,
      promotion: Option[String] = None
  )
  private var moveHistory: List[ChessMove] = List.empty
  private val startPosition =
    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

  // Initialize signals
  private val moveHistorySignal: Var[List[ChessMove]] = Var(
    List.empty[ChessMove]
  )
  private val currentMoveIndexSignal: Var[Int] = Var(-1) // -1 means latest move
  private val showWarningSignal: Var[Boolean] = Var(false)

  // Track if we're viewing a historical position
  private def isViewingHistory: Boolean = {
    val currentIndex = currentMoveIndexSignal.now()
    currentIndex >= 0 && currentIndex < moveHistory.length - 1
  }

  private def revertToMove(index: Int): Unit = {
    println(s"Reverting to move index: $index") // Debug log

    // Calculate if it would be white's turn after reverting
    val isWhiteTurn =
      index % 2 == 1 // Odd index means we're reverting to after black's move

    if (isWhiteTurn) {
      currentMoveIndexSignal.set(index)

      // Reset game to starting position
      game.foreach { g =>
        g.load(startPosition)

        // Replay moves up to the selected index using full move objects
        moveHistory.take(index + 1).foreach { move =>
          println(
            s"Replaying move from ${move.from} to ${move.to}"
          ) // Debug log
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

        // Update board position and game status
        board.foreach { b =>
          println(s"Setting board position to FEN: ${g.fen()}") // Debug log
          b.position(g.fen())
        }
        updateGameStatus()
      }
    } else {
      println(
        s"Cannot revert to index $index - it would be black's turn"
      ) // Debug log
    }
  }

  private def confirmNewMove(): Boolean = {
    if (isViewingHistory) {
      val confirmed = dom.window.confirm(
        "Making a move from this historical position will discard all future moves. Do you want to continue?"
      )
      if (confirmed) {
        // Truncate move history to current position
        moveHistory = moveHistory.take(currentMoveIndexSignal.now() + 1)
        moveHistorySignal.set(moveHistory)
        currentMoveIndexSignal.set(-1)
      }
      confirmed
    } else true
  }

  // Helper method to format moves in pairs
  private def formatMovePairs(
      moves: List[ChessMove]
  ): List[(Int, String, String)] = {
    moves.map(_.san).grouped(2).toList.zipWithIndex.map { case (pair, idx) =>
      val moveNumber = idx + 1
      val whitePart = pair.headOption.getOrElse("")
      val blackPart = pair.lift(1).getOrElse("")
      (moveNumber, whitePart, blackPart)
    }
  }

  private def moveLogElement: Element = {
    val moveLogStyles = Seq(
      "display" -> "flex",
      "overflow-x" -> "auto",
      "padding" -> "10px",
      "margin-top" -> "20px",
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
      cls := "move-log",
      styleAttr := moveLogStyles.map { case (k, v) => s"$k: $v" }.mkString(";"),
      children <-- moveHistorySignal.signal
        .combineWith(currentMoveIndexSignal.signal)
        .map { case (moves, currentIndex) =>
          formatMovePairs(moves).zipWithIndex.map {
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
                      if (isClickable) revertToMove(blackIndex)
                    },
                    s" $black"
                  )
                } else emptyNode
              )
          }
        }
    )
  }

  def main(args: Array[String]): Unit = {
    println("Starting application initialization") // Debug log
    val containerNode = dom.document.querySelector("#app")
    render(containerNode, appElement)

    // Initialize the chessboard after the DOM is ready
    dom.window.setTimeout(
      { () =>
        println("Initializing board and game") // Debug log
        initializeBoard()
        game = Some(new Chess())
        game.foreach { g =>
          g.load(startPosition)
          println(s"Game initialized with FEN: ${g.fen()}") // Debug log
        }
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
              val canMove = piece.charAt(0) == 'w' && g.turn() == "w"
              println(
                s"onDragStart - piece: $piece, turn: ${g.turn()}, canMove: $canMove"
              ) // Debug log
              canMove
            case None =>
              println("onDragStart - game not initialized") // Debug log
              false
          }
        }),
      onDrop = js.defined({ (source: String, target: String, _: js.Object) =>
        println(
          s"onDrop - attempting move from $source to $target"
        ) // Debug log

        if (!confirmNewMove()) {
          "snapback"
        } else {
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
                println(s"Move successful: ${move.san}") // Debug log
                // Store full move object
                val chessMove = ChessMove(
                  from = source,
                  to = target,
                  san = move.san.asInstanceOf[String],
                  promotion = Some("q")
                )
                moveHistory = moveHistory :+ chessMove
                moveHistorySignal.set(moveHistory)
                // Reset current move index since we're at the latest move
                currentMoveIndexSignal.set(-1)
                // If move was legal, make bot move
                makeBotMove()
                s"$source-$target"
              } else {
                println(s"Move invalid: $source-$target") // Debug log
                "snapback"
              }
            case None =>
              println("onDrop - game not initialized") // Debug log
              "snapback"
          }
        }
      }),
      onSnapEnd = js.defined({ () =>
        // Update the board position after the piece snap animation
        game.foreach { g =>
          board.foreach(_.position(g.fen()))
          println(s"onSnapEnd - updated position: ${g.fen()}") // Debug log
        }
        updateGameStatus()
      })
    )

    board = Some(new Chessboard("board", config))
    println("Board initialized") // Debug log
  }

  private def makeBotMove(): Unit = {
    game.foreach { g =>
      if (!g.game_over()) {
        ApiClient
          .getBotMove(moveHistory.map(_.san).mkString(" "))
          .`then`[Unit]({ moveStr =>
            println(s"Received move from backend: $moveStr") // Debug log
            println(s"Current position FEN: ${g.fen()}") // Debug log
            println(s"Current turn: ${g.turn()}") // Debug log

            // Try to make the move, handling both castling and regular moves
            val moveResult = if (moveStr == "O-O" || moveStr == "O-O-O") {
              // Handle castling moves
              println(s"Attempting castling move: $moveStr") // Debug log
              val legalMoves = g.moves(js.Dynamic.literal(verbose = true))
              println(
                s"Legal moves: ${js.Dynamic.global.JSON.stringify(legalMoves)}"
              ) // Debug log

              // Find the castling move in legal moves
              val castlingMove =
                legalMoves.asInstanceOf[js.Array[js.Dynamic]].find { move =>
                  (moveStr == "O-O" && move.san
                    .asInstanceOf[String] == "O-O") ||
                  (moveStr == "O-O-O" && move.san
                    .asInstanceOf[String] == "O-O-O")
                }

              castlingMove match {
                case Some(move) =>
                  // Use the from/to squares from the legal move
                  val result = g.move(
                    js.Dynamic.literal(
                      from = move.from.asInstanceOf[String],
                      to = move.to.asInstanceOf[String]
                    )
                  )
                  println(
                    s"Move result: ${if (result != null) "success"
                      else "failed - castling not allowed (check console for details)"}"
                  ) // Debug log
                  if (result != null) Some(result) else None
                case None =>
                  println("Castling move not found in legal moves") // Debug log
                  None
              }
            } else {
              // Handle regular moves
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
                      // Try with explicit from-to if start square is provided
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
                  attempts.foldLeft[Option[js.Dynamic]](None) {
                    (acc, moveAttempt) =>
                      acc.orElse {
                        println(
                          s"Attempting move with: ${js.Dynamic.global.JSON.stringify(moveAttempt)}"
                        ) // Debug log
                        val move = g.move(moveAttempt)
                        println(s"Move result: ${if (move != null) "success"
                          else "failed"}") // Debug log
                        if (move != null) Some(move.asInstanceOf[js.Dynamic])
                        else None
                      }
                  }

                case _ =>
                  println(s"Move didn't match pattern: $moveStr") // Debug log
                  None
              }
            }

            // Apply the successful move or log failure
            moveResult match {
              case Some(move) =>
                val moveFrom =
                  Option(move.from).map(_.asInstanceOf[String]).getOrElse("")
                val moveTo =
                  Option(move.to).map(_.asInstanceOf[String]).getOrElse("")
                val moveSan =
                  Option(move.san).map(_.asInstanceOf[String]).getOrElse("")

                val chessMove = ChessMove(
                  from = moveFrom,
                  to = moveTo,
                  san = moveSan,
                  promotion = Some("q")
                )
                println(
                  s"Created ChessMove: from=$moveFrom, to=$moveTo, san=$moveSan"
                ) // Debug log
                moveHistory = moveHistory :+ chessMove
                moveHistorySignal.set(moveHistory)
                currentMoveIndexSignal.set(-1)
                board.foreach(_.position(g.fen()))
                updateGameStatus()
              case None =>
                println(s"Move failed: $moveStr") // Debug log
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
              moveHistorySignal.set(List.empty)
              currentMoveIndexSignal.set(-1) // Reset to latest move
              updateGameStatus()
            }
          }
        )
      ),
      moveLogElement
    )
  }
}
