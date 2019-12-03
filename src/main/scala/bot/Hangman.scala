package bot

import scala.util.Random


case object Hangman{
  val first: String = """
                        |  +---+
                        |  |   |
                        |      |
                        |      |
                        |      |
                        |      |
                        |=========""".stripMargin('|')

  val second: String = """
                         |  +---+
                         |  |   |
                         |  O   |
                         |      |
                         |      |
                         |      |
                         |=========""".stripMargin

  val third: String = """
                        |  +---+
                        |  |   |
                        |  O   |
                        |  |   |
                        |      |
                        |      |
                        |=========""".stripMargin

  val fourth: String = """
                         |  +---+
                         |  |   |
                         |  O   |
                         | /|   |
                         |      |
                         |      |
                         |=========""".stripMargin

  val fifth: String = """
                        |  +---+
                        |  |   |
                        |  O   |
                        | /|\  |
                        |      |
                        |      |
                        |=========""".stripMargin

  val sixth: String = """
                        |  +---+
                        |  |   |
                        |  O   |
                        | /|\  |
                        | /    |
                        |      |
                        |=========""".stripMargin

  val seventh: String = """
                          |  +---+
                          |  |   |
                          |  O   |
                          | /|\  |
                          | / \  |
                          |      |
                          |=========""".stripMargin

  val words = """ant baboon badger bat bear beaver camel cat clam cobra cougar
                |       coyote crow deer dog donkey duck eagle ferret fox frog goat goose hawk
                |       lion lizard llama mole monkey moose mouse mule newt otter owl panda
                |       parrot pigeon python rabbit ram rat raven rhino salmon seal shark sheep
                |       skunk sloth snake spider stork swan tiger toad trout turkey turtle
                |       weasel whale wolf wombat zebra""".stripMargin.split(" ")

  val random = new Random()
  def getWord = {
    words(random.nextInt(words.length))
  }
  def getGallows(wrongCount: Int) = wrongCount match {
    case 1 => first
    case 2 => second
    case 3 => third
    case 4 => fourth
    case 5 => fifth
    case 6 => sixth
    case 7 => seventh
  }
}

