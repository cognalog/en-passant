package api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.{StatusCodes, HttpMethods}
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import actor.Player
import api.JsonFormats._
import model.{Board, StandardBoard}
import spray.json._

class ChessService(player: Player) {
  import JsonFormats._

  private val corsAllowedOrigin =
    sys.env.getOrElse("CORS_ALLOWED_ORIGIN", "http://localhost:3000")

  private def corsHandler(route: Route) = {
    respondWithHeaders(
      `Access-Control-Allow-Origin`(HttpOrigin(corsAllowedOrigin)),
      `Access-Control-Allow-Methods`(HttpMethods.POST, HttpMethods.OPTIONS),
      `Access-Control-Allow-Headers`("Content-Type")
    ) {
      route ~ options {
        complete(StatusCodes.OK)
      }
    }
  }

  val routes: Route = {
    pathPrefix("api" / "chess") {
      corsHandler {
        concat(
          path("move") {
            post {
              entity(as[MoveRequest]) { request =>
                complete {
                  player.GetNextMove(request.board, request.color) match {
                    case scala.util.Success(move) => MoveResponse(move)
                    case scala.util.Failure(ex) =>
                      throw new RuntimeException(
                        s"Failed to get next move: ${ex.getMessage}"
                      )
                  }
                }
              }
            }
          },
          path("printBoard") {
            post {
              entity(as[PrintBoardRequest]) { request =>
                complete {
                  PrintBoardResponse(request.board)
                }
              }
            }
          }
        )
      }
    }
  }
}
