package enpassant

import scala.scalajs.js
import org.scalajs.dom

object MoveHandler {

  private def handleCaptureAnimation(
      moveSan: String,
      fromSquare: String,
      toSquare: String,
      boardPositionBeforeMove: String
  ): Unit = {
    if (moveSan.contains("x") && fromSquare.nonEmpty && toSquare.nonEmpty) {
      ChessUtils.getPieceAt(toSquare, boardPositionBeforeMove).foreach { piece =>
        CaptureAnimation.animateCapture(piece, fromSquare, toSquare)
      }
    }
  }

  def confirmNewMove(): Boolean = {
    if (GameState.isViewingHistory) {
      val confirmed = dom.window.confirm(
        "Making a move from this historical position will discard all future moves. Do you want to continue?"
      )
      if (confirmed) {
        // Truncate move history to current position
        GameState.truncateHistoryTo(GameState.getCurrentMoveIndex)
        GameState.resetToLatest()
      }
      confirmed
    } else true
  }

  def processPlayerMove(source: String, target: String): String = {
    println(s"Processing player move from $source to $target") // Debug log

    if (!confirmNewMove()) {
      return "snapback"
    }

    ChessBoard.getGame match {
      case Some(g) =>
        // Store board position before move for capture detection
        val boardPosition = g.fen()
        
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
          
          // Handle capture animation
          handleCaptureAnimation(
            move.san.asInstanceOf[String],
            source,
            target,
            boardPosition
          )
          
          // Store full move object
          val chessMove = ChessMove(
            from = source,
            to = target,
            san = move.san.asInstanceOf[String],
            promotion = Some("q")
          )
          GameState.addMove(chessMove)
          GameState.resetToLatest()
          
          // If move was legal, make bot move
          makeBotMove()
          s"$source-$target"
        } else {
          println(s"Move invalid: $source-$target") // Debug log
          "snapback"
        }
      case None =>
        println("Game not initialized") // Debug log
        "snapback"
    }
  }

  def canDragPiece(piece: String): Boolean = {
    ChessBoard.getGame match {
      case Some(g) =>
        val canMove = piece.charAt(0) == 'w' && g.turn() == "w"
        println(s"Can drag piece: $piece, turn: ${g.turn()}, canMove: $canMove") // Debug log
        canMove
      case None =>
        println("Game not initialized for drag check") // Debug log
        false
    }
  }

  def makeBotMove(): Unit = {
    ChessBoard.getGame.foreach { g =>
      if (!g.game_over()) {
        ApiClient
          .getBotMove(GameState.moveHistory.map(_.san).mkString(" "))
          .`then`[Unit]({ moveStr =>
            processBotMove(moveStr, g)
          })
      }
    }
  }

  private def processBotMove(moveStr: String, game: Chess): Unit = {
    println(s"Received move from backend: $moveStr") // Debug log
    println(s"Current position FEN: ${game.fen()}") // Debug log
    println(s"Current turn: ${game.turn()}") // Debug log
    
    // Store current board position to detect captures
    val positionBeforeMove = game.fen()

    // Try to make the move, handling both castling and regular moves
    val moveResult = if (moveStr == "O-O" || moveStr == "O-O-O") {
      processCastlingMove(moveStr, game)
    } else {
      processRegularMove(moveStr, game)
    }

    // Apply the successful move or log failure
    moveResult match {
      case Some(move) =>
        val moveFrom = Option(move.from).map(_.asInstanceOf[String]).getOrElse("")
        val moveTo = Option(move.to).map(_.asInstanceOf[String]).getOrElse("")
        val moveSan = Option(move.san).map(_.asInstanceOf[String]).getOrElse("")

        // Handle capture animation
        handleCaptureAnimation(moveSan, moveFrom, moveTo, positionBeforeMove)

        val chessMove = ChessMove(
          from = moveFrom,
          to = moveTo,
          san = moveSan,
          promotion = Some("q")
        )
        println(s"Created ChessMove: from=$moveFrom, to=$moveTo, san=$moveSan") // Debug log
        GameState.addMove(chessMove)
        GameState.resetToLatest()
        ChessBoard.updatePosition()
        GameStatusManager.updateGameStatus()
      case None =>
        println(s"Move failed: $moveStr") // Debug log
    }
  }

  private def processCastlingMove(moveStr: String, game: Chess): Option[js.Dynamic] = {
    println(s"Attempting castling move: $moveStr") // Debug log
    val legalMoves = game.moves(js.Dynamic.literal(verbose = true))
    println(s"Legal moves: ${js.Dynamic.global.JSON.stringify(legalMoves)}") // Debug log

    // Find the castling move in legal moves
    val castlingMove = legalMoves.asInstanceOf[js.Array[js.Dynamic]].find { move =>
      (moveStr == "O-O" && move.san.asInstanceOf[String] == "O-O") ||
      (moveStr == "O-O-O" && move.san.asInstanceOf[String] == "O-O-O")
    }

    castlingMove match {
      case Some(move) =>
        // Use the from/to squares from the legal move
        val result = game.move(
          js.Dynamic.literal(
            from = move.from.asInstanceOf[String],
            to = move.to.asInstanceOf[String]
          )
        )
        println(s"Move result: ${if (result != null) "success" else "failed - castling not allowed"}") // Debug log
        if (result != null) Some(result) else None
      case None =>
        println("Castling move not found in legal moves") // Debug log
        None
    }
  }

  private def processRegularMove(moveStr: String, game: Chess): Option[js.Dynamic] = {
    val movePattern = """([NBRQK])?([a-h][1-8])?x?([a-h][1-8])(?:=([NBRQ]))?""".r
    moveStr match {
      case movePattern(piece, start, dest, promotion) =>
        println(s"Parsed move: piece=$piece, start=$start, dest=$dest, promotion=$promotion") // Debug log

        // For pawn moves, try all possible source squares
        val attempts = if (piece == null) {
          // Get all legal moves
          val legalMoves = game
            .moves(js.Dynamic.literal(verbose = true))
            .asInstanceOf[js.Array[js.Dynamic]]
          println(s"Legal moves: ${js.Dynamic.global.JSON.stringify(legalMoves)}") // Debug log

          // Find moves that match our destination
          val matchingMoves = legalMoves.filter(m => m.to.asInstanceOf[String] == dest)
          println(s"Matching moves: ${js.Dynamic.global.JSON.stringify(matchingMoves)}") // Debug log

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
                san = s"${piece}${dest}${Option(promotion).getOrElse("")}".asInstanceOf[js.Any]
              )
            ),
            // Try SAN format with start square if provided
            Option(start).map(s =>
              js.Dynamic.literal(
                san = s"${piece}${s}${dest}${Option(promotion).getOrElse("")}".asInstanceOf[js.Any]
              )
            )
          ).flatten
        }

        // Try each move format until one succeeds
        attempts.foldLeft[Option[js.Dynamic]](None) { (acc, moveAttempt) =>
          acc.orElse {
            println(s"Attempting move with: ${js.Dynamic.global.JSON.stringify(moveAttempt)}") // Debug log
            val move = game.move(moveAttempt)
            println(s"Move result: ${if (move != null) "success" else "failed"}") // Debug log
            if (move != null) Some(move.asInstanceOf[js.Dynamic]) else None
          }
        }

      case _ =>
        println(s"Move didn't match pattern: $moveStr") // Debug log
        None
    }
  }

  def revertToMove(index: Int): Unit = {
    println(s"Reverting to move index: $index") // Debug log

    // Calculate if it would be white's turn after reverting
    val isWhiteTurn = index % 2 == 1 // Odd index means we're reverting to after black's move

    if (isWhiteTurn) {
      GameState.setCurrentMoveIndex(index)
      
      // Reset game and replay moves up to the selected index
      val movesToReplay = GameState.moveHistory.take(index + 1)
      ChessBoard.replayMovesToPosition(movesToReplay)
      GameStatusManager.updateGameStatus()
    } else {
      println(s"Cannot revert to index $index - it would be black's turn") // Debug log
    }
  }
}