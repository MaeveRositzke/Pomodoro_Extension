package model

// received by GameActors
case object Start
case object Update
case class UpdateWorking(time: Long)
case class UpdateShortRest(time: Long)
case class UpdateLongRest(time: Long)

// received by PomodoroServer
case object UpdateTools
case class ToolState(toolState: String)
case object End
