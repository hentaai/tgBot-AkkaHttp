package bot

import scala.util.Random


case object Hangman{
  def first:String = """  +---+
                       |  |   |
                       |      |
                       |      |
                       |      |
                       |      |
                       |=========""".stripMargin

  def second: String = """  +---+
                         |  |   |
                         |  O   |
                         |      |
                         |      |
                         |      |
                         |=========""".stripMargin

  def third: String = """  +---+
                        |  |   |
                        |  O   |
                        |  |   |
                        |      |
                        |      |
                        |=========""".stripMargin

  def fourth: String = """  +---+
                         |  |   |
                         |  O   |
                         | /|   |
                         |      |
                         |      |
                         |=========""".stripMargin

  def fifth: String = """  +---+
                        |  |   |
                        |  O   |
                        | /|\  |
                        |      |
                        |      |
                        |=========""".stripMargin

  def sixth: String = """  +---+
                        |  |   |
                        |  O   |
                        | /|\  |
                        | /    |
                        |      |
                        |=========""".stripMargin

  def seventh: String = """  +---+
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
    case 1 => second
    case 2 => third
    case 3 => fourth
    case 4 => fifth
    case 5 => sixth
    case 6 => seventh
  }
}

