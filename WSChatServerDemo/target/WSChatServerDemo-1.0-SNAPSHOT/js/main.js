let ws;
function enterRoom() {
    let code = document.getElementById("room-code").value;
    ws = new WebSocket("ws://localhost:8080/WSChatServerDemo-1.0-SNAPSHOT/ws/" + code);

//let  ws = new WebSocket("ws://localhost:8080/WSChatServerDemo-1.0-SNAPSHOT/ws"+code);

    ws.onmessage = function (event) {
        console.log(event.data);
        let message = JSON.parse(event.data);
        document.getElementById("log").value += "[" + timestamp() + "] " + message.message + "\n";
    }
}

document.getElementById("input").addEventListener("keyup", function (event) {
    if (event.keyCode === 13) {
        let request = {"type":"chat", "msg":event.target.value};
        ws.send(JSON.stringify(request));
        event.target.value = "";
    }
});

/*
    on buttons:
    This button work by sending a message to the backend.
    I've made these messages unique, and they need to be
    caught in the backend and parsed for meaning.
    for Kei, I've left you openings in the backend to fill
    with how exactly you'd like to package the map for Gremmy
    to serve to the front end
        ~ Sam
 */
/*
const button1 = document.querySelector('#refresh');
button1.addEventListener('click', () => {
    const data = {
        // so here we would add some identifier message about
        // the button being pressed for refresh.
        message: ''
    };
    const jsonString = JSON.stringify(data);
    ws.send(jsonString);
});

 */

// this button operates the same way.
const button2 = document.querySelector('#submit');
button2.addEventListener('click', () =>{
    const inp = document.getElementById("input");
    let request = {"type":"chat", "msg":inp.value};
    ws.send(JSON.stringify(request));
    inp.value="";
})


function timestamp() {
    var d = new Date(), minutes = d.getMinutes();
    if (minutes < 10) minutes = '0' + minutes;
    return d.getHours() + ':' + minutes;
}