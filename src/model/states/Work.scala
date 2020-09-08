package model.states

class Work(tool: Tool) extends State(tool) {


  override def change(): Unit = {

    if (tool.shortRest < 4) {
      tool.state = new Rest(tool)
      tool.message = "Take a short break"
      tool.shortRest += 1
      tool.run(tool.shortRestTime)
    }

    else {
      tool.state = new Rest(tool)
      tool.message = "Take a long break"
      tool.shortRest = 0
      tool.run(tool.longRestTime)
    }
  }
}
