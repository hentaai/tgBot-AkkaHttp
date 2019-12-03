package bot

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}
import scala.concurrent.duration._
object Boot extends App with Serializer {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val config: Config = ConfigFactory.load() // config
  val log = LoggerFactory.getLogger("Boot")

  //  val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://akka.io"))

  val token = config.getString("telegram.token") // token
  log.info(s"Token: $token")


  val updateRequest = HttpRequest(HttpMethods.GET, s"https://api.telegram.org/bot$token/getUpdates")
  Http().singleRequest(updateRequest).onComplete{
    case Success(value) =>
      log.info(s"Response: $value")
  }
  val telegramActor = system.actorOf(TelegramActor.props(token))

  val schedule = system.scheduler.schedule(0.seconds, 5.seconds, telegramActor, TelegramActor.Refresh)


  // Telegram (actor) -- only telegrams
  // JSON serializer

  // Hangman (actor) -- game logic only (does not know about Telegram), but knows about telegram actor
  // Hangman does not about HTTP

}
