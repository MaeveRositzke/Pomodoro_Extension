const socket = io.connect("http://localhost:8080", {transports: ['websocket']});

socket.on("tool_state", display);

document.getElementById("start").onclick = function start() {
    socket.emit("start");
    document.getElementById("content").innerHTML = "<h1 id='message'>You should be working!</h1> <p id='time'></p>";
};


document.getElementById("end").onclick = function end() {
    socket.emit("end");
    document.getElementById("content").innerHTML = "";

    document.getElementById("working").value = "25";
    document.getElementById("workValue").innerHTML = "25";

    document.getElementById("shortRest").value = "5";
    document.getElementById("shortRestValue").innerHTML = "5";

    document.getElementById("longRest").value = "30";
    document.getElementById("longRestValue").innerHTML = "30";
};


let working = document.getElementById("working");
working.oninput = function() {
    socket.emit("working", this.value.toString());
    document.getElementById("workValue").innerHTML = this.value.toString();
};

let shortRest = document.getElementById("shortRest");
shortRest.oninput = function() {
    socket.emit("shortRest", this.value.toString());
    document.getElementById("shortRestValue").innerHTML = this.value.toString();
};

let longRest = document.getElementById("longRest");
longRest.oninput = function() {
    socket.emit("longRest", this.value.toString());
    document.getElementById("longRestValue").innerHTML = this.value.toString();
};

function display(json) {

    const jsObject = JSON.parse(json);

    const message = jsObject.message;
    document.getElementById("message").innerHTML = message;

    const time = jsObject.time;
    document.getElementById("time").innerHTML = time;
}