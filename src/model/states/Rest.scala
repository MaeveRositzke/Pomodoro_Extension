package model.states

class Rest(tool: Tool) extends State(tool) {

  override def change(): Unit = {

    tool.state = new Work(tool)
    tool.message = "You should be working!"
    tool.run(tool.workTime)
  }
}
