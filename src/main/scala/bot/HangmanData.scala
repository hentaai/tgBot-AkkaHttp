package bot

sealed trait Data
case object Uninitialized extends Data
case class Board(word: String, correct: String, missed: String) extends Data
