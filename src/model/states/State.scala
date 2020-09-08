package model.states

import scala.concurrent.duration._
import akka.actor.ActorSystem
import play.api.libs.json.{JsValue, Json}

abstract class State(tool: Tool) {


  def json(): String = {
    val jsonMessage: JsValue = Json.toJson(tool.message)
    val jsonTime: JsValue = Json.toJson(tool.time)

    val json: Map[String, JsValue] = Map(
      "message" -> jsonMessage,
      "time" -> jsonTime
    )

    Json.stringify(Json.toJson(json))
  }

  def run(time: Long): Unit = {
    val originalTime: Long = System.currentTimeMillis()
    tool.endTime = originalTime + time
    val system = ActorSystem()
    var distance: Long = 0

    import system.dispatcher
    val timer =
      system.scheduler.schedule(0.milliseconds, 1000.milliseconds) {
        val now: Long = System.currentTimeMillis()
        distance = tool.endTime - now
        val minutes: Double = Math.floor(distance / 60000)

        var seconds: Double = Math.floor(distance / 1000)
        if (minutes != 0){
          seconds = Math.floor(seconds % (minutes * 60))
        }

        tool.time = minutes.toInt + ":" + seconds.toInt
        if (distance < 0) {
          tool.change()
        }
      }
    if (distance < 0) {
      timer.cancel()
    }
  }

  def change()

}
