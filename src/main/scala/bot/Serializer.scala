package bot

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import bot.models.{Update, UpdateList}
import spray.json._

trait Serializer extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val messageFormat: RootJsonFormat[TelegramMessage] = jsonFormat2(TelegramMessage)
}


object MyJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit object updateListFormat extends RootJsonFormat[UpdateList] {
    def write(upd: UpdateList): JsValue = JsArray(JsString("just an useless method"))

    def read(json: JsValue): UpdateList = {
      json.asJsObject.fields.get("result") match {
        case Some(updateList) =>
          UpdateList(updateList.asInstanceOf[JsArray].elements.map({ update =>
            val updateId: Int = update.asJsObject.fields.get("update_id") match {
              case Some(JsNumber(updateId)) => updateId.intValue()
              case _ => -1
            }
            val message = update.asJsObject.fields.get("message")
            val chatId: Int = message.flatMap(message => {
              message.asJsObject.fields.get("chat").flatMap( chat => chat.asJsObject.fields.get("id"))
            }) match {
              case Some(JsNumber(chatId)) => chatId.intValue()
              case _=> -1
            }
            val text = message.flatMap( message => message.asJsObject.fields.get("text")) match {
              case Some(JsString(text)) => text
              case _ => ""
            }
            Update(updateId, chatId, text)
          }))
      }
    }
  }
  implicit val messageFormat: RootJsonFormat[TelegramMessage] = jsonFormat2(TelegramMessage)

}