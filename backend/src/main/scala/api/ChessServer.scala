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
import scala.util.{Try, Success, Failure}

object ChessServer {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Nothing] =
      ActorSystem(Behaviors.empty, "chess-system")
    implicit val executionContext: ExecutionContextExecutor =
      system.executionContext

    // Read configuration from environment variables with defaults
    val port = sys.env.get("BACKEND_PORT")
      .flatMap(p => Try(p.toInt).toOption)
      .getOrElse(8080)
    
    val botSearchDepth = sys.env.get("BOT_SEARCH_DEPTH")
      .flatMap(d => Try(d.toInt).toOption)
      .getOrElse(4)

    println(s"Starting server on port: $port")
    println(s"Bot search depth: $botSearchDepth")

    // Create a bot player with configurable depth
    val botPlayer = BotPlayer(ABPruningMinimax(botSearchDepth, GeneralEvaluator))
    val chessService = new ChessService(botPlayer)

    val bindingFuture =
      Http().newServerAt("0.0.0.0", port).bind(chessService.routes)
    println(
      s"Server now online at http://0.0.0.0:$port/"
    )

    // Keep the server running indefinitely
    Await.result(system.whenTerminated, Duration.Inf)
  }
}
