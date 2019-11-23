package bot

import akka.actor.{Actor, ActorLogging, FSM, Props}

case object HangmanActor{
  def props = Props(new HangmanActor)

  case object StartCommand
  case class Letter(msg: String)
  def board(missedLetters: String): String = s"${Hangman.getGallows(missedLetters.length)}\nMissed: $missedLetters"
  def spaces(word: String, correctLetters: String) = word.map{ letter =>
    if (correctLetters.contains(letter)) letter else "_"
  }.mkString(" ")
}
class HangmanActor extends Actor with ActorLogging{
  import TelegramActor._
  import HangmanActor._

  var missedLetters: String = ""
  val secretWord = Hangman.getWord
  var correctLetters: String = ""

  override def receive: Receive = {
    case StartCommand =>
      sender() ! Message(Hangman.first)
      sender() ! Message(spaces(secretWord, correctLetters))
    case Letter(l) =>
      if (secretWord.contains(l)) {
        correctLetters += l
        sender() ! Message(spaces(secretWord, correctLetters))
      }
      else{
        missedLetters += l
        sender() ! Message(board(missedLetters))
      }
  }


  //  startWith(Idle, Uninitialized)
//
//  when(Idle) {
//    case Event(StartCommand, Uninitialized) =>
//      goto(Playing).using(Gallows(Hangman.first))
//  }
//
//  onTransition{
//    Idle -> Playing(0)
//  }
//
//  when(Playing(0)) {
//    case Event(HangmanMessage(msg), Gallows(word)) =>
//
//  }
//    case Hangman.first
}
