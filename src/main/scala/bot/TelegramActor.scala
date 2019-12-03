package bot

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, RequestEntity}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import bot.HangmanActor.{Letter, StartCommand}
import bot.models.UpdateList

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object TelegramActor {
  def props(token: String): Props = Props(new TelegramActor(token))
  case object Refresh
  case class Message(msg: String)
}

class TelegramActor(token: String) extends Actor with ActorLogging{
  import TelegramActor._
  import spray.json._
  import MyJsonProtocol._

  final implicit val system = context.system
  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  final implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val http = Http(context.system)
  var offset: Int = 0
  var chatIdToActor = Map.empty[Int, ActorRef]
  var actorToChatId = Map.empty[ActorRef, Int]

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
                  case "/start" =>
                    chatIdToActor.get(update.chat_id) match {
                      case Some(actor) =>
                        actor ! StartCommand
                      case None =>
                        val actor = context.actorOf(HangmanActor.props(self))
                        chatIdToActor += (update.chat_id -> actor)
                        actorToChatId += (actor -> update.chat_id)
                        actor ! StartCommand
                    }
                  case letter: String =>
                    chatIdToActor.get(update.chat_id) match {
                      case Some(actor) =>
                        actor ! Letter(letter.toLowerCase)
                      case None =>
                        sendMessage(update.chat_id, "If you want to play, type /start first")
                    }
                  case _ =>
                    sendMessage(update.chat_id, "I can process only text messages!")
                }})
                offset = result.last.updateId + 1
            }
            case _ => log.error("can't unmarshal")
          }
        case _ => log.error("Wrong response")
      }
    case Message(msg) =>
      actorToChatId.get(sender()) match {
        case Some(chatId) =>
          sendMessage(chatId, msg)
        case None =>
      }
  }

  def sendMessage(chatId: Int, msg: String): Unit = {
    val request = Marshal(TelegramMessage(chatId, msg)).to[RequestEntity].flatMap { entity =>
      http.singleRequest(HttpRequest(HttpMethods.POST, SendMessage.uri(token), Nil, entity))
    }
    request.onComplete{
      case Success(response) =>
        response.discardEntityBytes()
      case Failure(exception) =>
        log.error(exception.toString)
    }
  }
}
