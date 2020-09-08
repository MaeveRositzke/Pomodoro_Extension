package model.states

class Tool(var workTime: Long, var shortRestTime: Long, var longRestTime: Long) {

  var state: State = new Work(this)

  var shortRest: Int = 0

  var message: String = "You should be working!"

  var time: String = ""

  var endTime: Long = 0

  def change(): Unit = {
    this.state.change()
  }

  def json(): String = {
    this.state.json()
  }

  def run(time: Long): Unit = {
    this.state.run(time)
  }
}
