package model

import akka.actor.Actor
import model.states.Tool

class GameActor(var workTime: Long, var shortRestTime: Long, var longRestTime: Long) extends Actor {

  val tool = new Tool(workTime, shortRestTime, longRestTime)

  override def receive: Receive = {

    case Start =>
      tool.run(workTime)

    case workTime: UpdateWorking =>
      if (tool.message == "You should be working!") {
        tool.endTime += workTime.time - tool.workTime
        tool.run(workTime.time)
      }
      tool.workTime = workTime.time

    case shortRestTime: UpdateShortRest =>
      if (tool.message == "Take a short break") {
        tool.endTime += shortRestTime.time - tool.shortRestTime
        tool.run(shortRestTime.time)
      }
      tool.shortRestTime = shortRestTime.time

    case longRestTime: UpdateLongRest =>
      if (tool.message == "Take a long break") {
        tool.endTime += longRestTime.time - tool.longRestTime
        tool.run(longRestTime.time)
      }
      tool.longRestTime = longRestTime.time

    case Update =>
      val json = tool.json()
      sender() ! ToolState(json)
  }
}
