package bot

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, RequestEntity}
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.http.scaladsl.unmarshalling.Unmarshal
import bot.HangmanActor.{Letter, StartCommand}
import bot.models.{Update, UpdateList}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object TelegramActor {
  def props(token: String, hangmanActor: ActorRef) = Props(new TelegramActor(token, hangmanActor))
  case object Refresh
  case class Message(msg: String)
}

class TelegramActor(token: String, hangmanActor: ActorRef) extends Actor with ActorLogging{
  import TelegramActor._
  import akka.pattern.pipe
  import context.dispatcher
  import spray.json._
  import MyJsonProtocol._
  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  val http = Http(context.system)
  var offset: Int = 0
  var chatId: Int

  override def receive: Receive = {
    case Refresh =>
      http.singleRequest(HttpRequest(HttpMethods.GET, GetUpdates.uri(token, offset))).onComplete{
        case Success(value) =>
          val updateList = Unmarshal(value.entity).to[String].map(str => str.parseJson.convertTo[UpdateList])
          updateList.onComplete{
            case Success(UpdateList(result)) => result match {
              case Nil =>
              case _ => result.foreach( update => {
                update.text match {
                  case "/start" => hangmanActor ! StartCommand
                  case _ => hangmanActor ! Letter(update.text)
                }})
                offset = result.last.updateId + 1
                chatId = result.last.chat_id
            }
            case _ => log.error("can't unmarshal")
          }
        case _ => log.error("Wrong response")
      }
    case Message(msg) =>
        val request = Marshal(TelegramMessage(chatId, msg)).to[RequestEntity].flatMap { entity =>
          http.singleRequest(HttpRequest(HttpMethods.POST, SendMessage.uri(token), entity))
        }
        request.onComplete{
          case Success(response) =>
            response.discardEntityBytes()
          case Failure(exception) =>
            log.error(exception.toString)
        }
  }

}
