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
  val hangmanActor = system.actorOf(HangmanActor.props)
  val telegramActor = system.actorOf(TelegramActor.props(token, hangmanActor))

  val schedule = system.scheduler.schedule(0.seconds, 10.seconds, telegramActor, TelegramActor.Refresh)


  // Telegram (actor) -- only telegrams
  // JSON serializer

  // Hangman (actor) -- game logic only (does not know about Telegram), but knows about telegram actor
  // Hangman does not about HTTP


//  val message: TelegramMessage = TelegramMessage(400757313, "Hello world from movie service")
//  log.info(Marshal(message).to[RequestEntity].toString)
//
//  val httpReq = Marshal(message).to[RequestEntity].flatMap { entity =>
//    val request = HttpRequest(HttpMethods.POST, s"https://api.telegram.org/bot$token/sendMessage", Nil, entity)
//    log.debug("Request: {}", request)
//    Http().singleRequest(request)
//  }
//
//
//  httpReq.onComplete {
//    case Success(value) =>
//      log.info(s"Response: $value")
//      value.discardEntityBytes()
//
//    case Failure(exception) =>
//      log.error("error")
//  }
}
