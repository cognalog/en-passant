package api

import actor.BotPlayer
import ai.evaluator.GeneralEvaluator
import ai.search.ABPruningMinimax
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.Duration
import scala.concurrent.Await

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
      Http().newServerAt("0.0.0.0", 8080).bind(chessService.routes)
    println(
      s"Server now online at http://0.0.0.0:8080/"
    )

    // Keep the server running indefinitely
    Await.result(system.whenTerminated, Duration.Inf)
  }
}
