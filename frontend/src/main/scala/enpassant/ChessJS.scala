package enpassant

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal("Chess")
class Chess(fen: js.UndefOr[String] = js.undefined) extends js.Object {
  def load(fen: String): Boolean = js.native
  def fen(): String = js.native
  def move(move: js.Dynamic): js.Dynamic = js.native
  def moves(options: js.Dynamic = js.Dynamic.literal()): js.Array[String] =
    js.native
  def in_check(): Boolean = js.native
  def in_checkmate(): Boolean = js.native
  def in_stalemate(): Boolean = js.native
  def in_draw(): Boolean = js.native
  def game_over(): Boolean = js.native
  def turn(): String = js.native
}
