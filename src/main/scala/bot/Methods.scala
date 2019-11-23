package bot

sealed trait APIMethod{
  def name: String
  def param: Option[String]
  def uri(token: String) = s"https://api.telegram.org/bot$token/$name"
  def uri(token: String, value: Any) = s"https://api.telegram.org/bot$token/$name?${param.get}=$value"
}

case object GetUpdates extends APIMethod{
  override def name: String = "getUpdates"
  override def param = Some("offset")
}

case object SendMessage extends APIMethod {
  override def name: String = "sendMessage"
  override def param: Option[String] = None
}
