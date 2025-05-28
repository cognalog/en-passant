package api

import actor.BotPlayer
import ai.evaluator.GeneralEvaluator
import ai.search.ABPruningMinimax
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object ChessServer {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Nothing] =
      ActorSystem(Behaviors.empty, "chess-system")
    implicit val executionContext: ExecutionContextExecutor =
      system.executionContext

    // Create a bot player with default settings
    // TODO: make this configurable
    val botPlayer = BotPlayer(ABPruningMinimax(3, GeneralEvaluator))
    val chessService = new ChessService(botPlayer)

    val bindingFuture =
      Http().newServerAt("localhost", 8080).bind(chessService.routes)
    println(
      s"Server now online at http://localhost:8080/\nPress RETURN to stop..."
    )
    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
