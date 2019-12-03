package bot

import akka.actor.{Actor, ActorLogging, ActorRef, FSM, Props}

case object HangmanActor{
  def props(telegramActor: ActorRef) = Props(new HangmanActor(telegramActor))

  case object StartCommand
  case class Letter(msg: String)
  def board(missedLetters: String): String = s"${Hangman.getGallows(missedLetters.length)}\nMissed: ${missedLetters.mkString(", ")}"
  def spaces(word: String, correctLetters: String) = word.map{ letter =>
    if (correctLetters.contains(letter)) letter else "_"
  }.mkString(" ")
}
class HangmanActor(telegramActor: ActorRef) extends FSM[State, Data]{
  import TelegramActor._
  import HangmanActor._

  startWith(Idle, Uninitialized)

  when(Idle) {
    case Event(StartCommand, Uninitialized) =>
      val word = Hangman.getWord
      telegramActor ! Message(s"GAME STARTED, GOOD LUCK :)\n${spaces(word, "")}")
      goto(Playing).using(Board(word, "", ""))
  }

  when(Playing) {
    case Event(Letter(l), Board(word, correct, missed)) =>
      if (correct.contains(l)){
        telegramActor ! Message(spaces(word, correct))
        stay()
      }
      else if (word.contains(l)){
        val correctLetters = correct + l
        correctLetters.length match {
          case length if length == word.length =>
            telegramActor ! Message(s"${spaces(word, correctLetters)}\nYOU WON!")
            goto(Idle).using(Uninitialized)
          case _ =>
            telegramActor ! Message(spaces(word, correctLetters))
            stay.using(Board(word, correctLetters, missed))
        }
      }
      else{
        val missedLetters = missed + l
        missedLetters.length match {
          case 7 =>
            telegramActor ! Message(s"${Hangman.seventh}\nTHE MAN IS DEAD, YOU LOST :C\nTHE WORD WAS: $word")
            goto(Idle).using(Uninitialized)
          case _ =>
            telegramActor ! Message(board(missedLetters))
            stay.using(Board(word, correct, missedLetters))
        }
      }
  }

  initialize()
}
