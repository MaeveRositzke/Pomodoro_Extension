package model

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.corundumstudio.socketio.listener.{ConnectListener, DataListener, DisconnectListener}
import com.corundumstudio.socketio.{AckRequest, Configuration, SocketIOClient, SocketIOServer}

class PomodoroServer() extends Actor {

  val config: Configuration = new Configuration {
    setHostname("0.0.0.0")
    setPort(8080)
  }

  val server: SocketIOServer = new SocketIOServer(config)

  var socketToActor: Map[SocketIOClient, ActorRef] = Map()
  var actorToSocket:  Map[ActorRef, SocketIOClient] = Map()

  server.addEventListener("start", classOf[Nothing], new StartListener(this))
  server.addEventListener("end", classOf[Nothing], new EndListener(this))
  server.addEventListener("working", classOf[String], new WorkingListener(this))
  server.addEventListener("shortRest", classOf[String], new ShortRestListener(this))
  server.addEventListener("longRest", classOf[String], new LongRestListener(this))

  server.start()

  override def receive: Receive = {
    case UpdateTools =>
      socketToActor.values.foreach((actor: ActorRef) => actor ! Update)
    case state: ToolState =>
      actorToSocket(sender()).sendEvent("tool_state", state.toolState)
  }

}

object PomodoroServer {

  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem()
    import actorSystem.dispatcher

    import scala.concurrent.duration._

    val server = actorSystem.actorOf(Props(classOf[PomodoroServer]))

    actorSystem.scheduler.schedule(0.milliseconds, 1000.milliseconds, server, UpdateTools)
  }
}

class StartListener(server: PomodoroServer) extends DataListener[Nothing] {
  override def onData(client: SocketIOClient, data: Nothing, ackSender: AckRequest): Unit = {
    if (!server.socketToActor.contains(client)){
      val actor = server.context.actorOf(Props(classOf[GameActor], 1500000.toLong, 300000.toLong, 1800000.toLong))
      server.socketToActor += client -> actor
      server.actorToSocket += actor -> client
      actor ! Start
    }

    else {
      server.socketToActor(client) ! Start
    }
  }
}

class EndListener(server: PomodoroServer) extends DataListener[Nothing] {
  override def onData(client: SocketIOClient, data: Nothing, ackSender: AckRequest): Unit = {
    val actor: ActorRef = server.socketToActor(client)
    server.socketToActor -= client
    server.actorToSocket -= actor
  }
}

class WorkingListener(server: PomodoroServer) extends DataListener[String] {
  override def onData(client: SocketIOClient, data: String, ackSender: AckRequest): Unit = {
    if (!server.socketToActor.contains(client)){
      val actor = server.context.actorOf(Props(classOf[GameActor], data.toLong * 60000, 300000.toLong, 1800000.toLong))
      server.socketToActor += client -> actor
      server.actorToSocket += actor -> client
    }
    else {
      val actor = server.socketToActor(client)
      actor ! UpdateWorking(data.toLong * 60000)
    }
  }
}

class ShortRestListener(server: PomodoroServer) extends DataListener[String] {
  override def onData(client: SocketIOClient, data: String, ackSender: AckRequest): Unit = {
    if (!server.socketToActor.contains(client)){
      val actor = server.context.actorOf(Props(classOf[GameActor], 1500000.toLong, data.toLong * 60000, 1800000.toLong))
      server.socketToActor += client -> actor
      server.actorToSocket += actor -> client
    }
    else {
      val actor = server.socketToActor(client)
      actor ! UpdateShortRest(data.toLong * 60000)
    }
  }
}


class LongRestListener(server: PomodoroServer) extends DataListener[String] {
  override def onData(client: SocketIOClient, data: String, ackSender: AckRequest): Unit = {
    if (!server.socketToActor.contains(client)){
      val actor = server.context.actorOf(Props(classOf[GameActor], 1500000.toLong, 300000.toLong, data.toLong * 60000))
      server.socketToActor += client -> actor
      server.actorToSocket += actor -> client
    }
    else {
      val actor = server.socketToActor(client)
      actor ! UpdateLongRest(data.toLong * 60000)
    }
  }
}
