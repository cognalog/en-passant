package enpassant

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import org.scalajs.dom.HTMLElement

@js.native
@JSGlobal("Chessboard")
class Chessboard(element: String, config: ChessboardConfig) extends js.Object {
  def position(fen: String): Unit = js.native
  def position(): String = js.native
  def clear(): Unit = js.native
  def destroy(): Unit = js.native
  def move(move: String): Unit = js.native
}

trait ChessboardConfig extends js.Object {
  var position: js.UndefOr[String] = js.undefined
  var draggable: js.UndefOr[Boolean] = js.undefined
  var pieceTheme: js.UndefOr[String] = js.undefined
  var onDragStart
      : js.UndefOr[js.Function3[String, String, js.Object, Boolean]] =
    js.undefined
  var onDrop: js.UndefOr[js.Function3[String, String, js.Object, String]] =
    js.undefined
  var onSnapEnd: js.UndefOr[js.Function0[Unit]] = js.undefined
}

object ChessboardConfig {
  def apply(
      position: js.UndefOr[String] = js.undefined,
      draggable: js.UndefOr[Boolean] = js.undefined,
      pieceTheme: js.UndefOr[String] = js.undefined,
      onDragStart: js.UndefOr[
        js.Function3[String, String, js.Object, Boolean]
      ] = js.undefined,
      onDrop: js.UndefOr[js.Function3[String, String, js.Object, String]] =
        js.undefined,
      onSnapEnd: js.UndefOr[js.Function0[Unit]] = js.undefined
  ): ChessboardConfig = {
    val config = js.Object().asInstanceOf[ChessboardConfig]
    config.position = position
    config.draggable = draggable
    config.pieceTheme = pieceTheme
    config.onDragStart = onDragStart
    config.onDrop = onDrop
    config.onSnapEnd = onSnapEnd
    config
  }
}
