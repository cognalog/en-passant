package enpassant

import com.raquo.laminar.api.L._
import org.scalajs.dom
import scala.scalajs.js.annotation.JSExportTopLevel

object Frontend {

  def main(args: Array[String]): Unit = {
    println("Starting application initialization") // Debug log
    val containerNode = dom.document.querySelector("#app")
    render(containerNode, GameUI.createAppElement)

    // Initialize the chessboard after the DOM is ready
    dom.window.setTimeout(
      { () =>
        println("Initializing board and game") // Debug log
        initializeBoard()
      },
      100
    )
  }

  private def initializeBoard(): Unit = {
    ChessBoard.initialize(
      onDragStart = { (source: String, piece: String, _) =>
        MoveHandler.canDragPiece(piece)
      },
      onDrop = { (source: String, target: String, _) =>
        MoveHandler.processPlayerMove(source, target)
      },
      onSnapEnd = { () =>
        // Update the board position after the piece snap animation
        ChessBoard.updatePosition()
        GameStatusManager.updateGameStatus()
      }
    )
  }

  def appElement: Element = GameUI.createAppElement
}
