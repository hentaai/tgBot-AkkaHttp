package bot

sealed trait State
case object Idle extends State
case object Playing extends State
case object Victory extends State
case object Death extends State