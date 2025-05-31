package enpassant

import scala.scalajs.js
import org.scalajs.dom
import scala.scalajs.js.JSON
import scala.scalajs.js.Promise

object ApiClient {
  private val baseUrl = "http://localhost:8080/api/chess"

  def getBotMove(moves: String): Promise[String] = {
    new Promise[String]((resolve, reject) => {
      val xhr = new dom.XMLHttpRequest()
      xhr.open("POST", s"$baseUrl/move")
      xhr.setRequestHeader("Content-Type", "application/json")
      xhr.onload = { _ =>
        if (xhr.status == 200) {
          val data = JSON.parse(xhr.responseText)
          val moveResponse = data.asInstanceOf[js.Dynamic]
          resolve(moveResponse.move.asInstanceOf[String])
        } else {
          reject(new js.Error(s"Failed to get bot move: ${xhr.statusText}"))
        }
      }
      xhr.onerror = { _ =>
        reject(new js.Error("Network error occurred"))
      }
      xhr.send(
        JSON.stringify(
          js.Dynamic.literal(
            board = moves,
            color = "Black" // Bot always plays as Black
          )
        )
      )
    })
  }
}
