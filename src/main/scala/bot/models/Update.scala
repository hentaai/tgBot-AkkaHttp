package bot.models

case class Update(updateId: Int, chat_id: Int, text: String)
case class UpdateList(result: Seq[Update])