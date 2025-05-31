package api

import model.Color.Color
import model._
import spray.json.{
  DefaultJsonProtocol,
  JsString,
  JsValue,
  JsonFormat,
  RootJsonFormat,
  deserializationError
}

object JsonFormats extends DefaultJsonProtocol {
  // Color format
  implicit object ColorFormat extends JsonFormat[Color] {
    def write(color: Color): JsValue = JsString(color.toString)
    def read(value: JsValue): Color = value match {
      case JsString("White") => Color.White
      case JsString("Black") => Color.Black
      case _                 => deserializationError("Color expected")
    }
  }

  // Square format
  implicit val squareFormat: RootJsonFormat[Square] = jsonFormat2(Square.apply)

  // Move format
  implicit object MoveFormat extends JsonFormat[Move] {
    def write(move: Move): JsValue = JsString(move.toStandardNotation)
    def read(value: JsValue): Move = value match {
      case JsString(str) =>
        deserializationError(
          "Can't parse move without a board (at least not castle), and thankfully we don't need to."
        )
      case _ => deserializationError("Move expected")
    }
  }

  implicit object BoardFormat extends JsonFormat[Board] {
    def write(board: Board): JsValue = JsString(board.toString)
    def read(value: JsValue): Board = value match {
      case JsString(str) =>
        Board.standardFromMoveStrings(
          str.split(" ").filter(_.nonEmpty).toSeq
        ) match {
          case scala.util.Success(board) => board
          case scala.util.Failure(ex) =>
            deserializationError(s"Failed to parse board: ${ex.getMessage}")
        }
      case _ => deserializationError("Board expected")
    }
  }

  // Request/Response formats
  case class MoveRequest(board: Board, color: Color)
  case class MoveResponse(move: Move)
  case class PrintBoardRequest(board: Board)
  case class PrintBoardResponse(board: Board)

  implicit val moveRequestFormat: RootJsonFormat[MoveRequest] = jsonFormat2(
    MoveRequest
  )
  implicit val moveResponseFormat: RootJsonFormat[MoveResponse] = jsonFormat1(
    MoveResponse
  )
  implicit val printBoardRequestFormat: RootJsonFormat[PrintBoardRequest] =
    jsonFormat1(PrintBoardRequest)
  implicit val printBoardResponseFormat: RootJsonFormat[PrintBoardResponse] =
    jsonFormat1(PrintBoardResponse)
}
