package bot

sealed trait Data
case object Uninitialized extends Data
case class Gallows(str: String) extends Data
