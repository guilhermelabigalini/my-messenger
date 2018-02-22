//var wsUri = "ws://" + document.location.hostname + ":" + document.location.port + document.location.pathname + "chat";

var wsUri = "ws://" + document.location.hostname + ":" + document.location.port + "/ws/messaging";

var websocket = null;

function closesocket() {
    if (websocket !== null) {
        console.log("closing ", websocket);
        websocket.close();
        websocket = null;
    } else {
        console.log("already closed");
    }
}

function connect() {
       
    console.log("connecting to " + wsUri);
    websocket = new WebSocket(wsUri);
    websocket.onclose = function (evt) {
        onClose(evt);
    };
    websocket.onopen = function (evt) {
        onOpen(evt);
    };
    websocket.onmessage = function (evt) {
        onMessage(evt);
    };
    websocket.onerror = function (evt) {
        onError(evt);
    };
}

function sendping()
{
    var msg = {
        streamMessageType: "Ping"
    };
    
    send(JSON.stringify(msg));
}


function sendinfo()
{
    var msg = {
        streamMessageType: "Info"
    };
    
    send(JSON.stringify(msg));
}

function sendlogin(tokenid)
{
    var msg = {
        streamMessageType: "Singin",
        tokenId: tokenid        
    };
    
    send(JSON.stringify(msg));
}

function send(msg) {
    console.log("sending message: " ,msg);
    websocket.send(msg);
}
function onError(evt) {
    console.log("onError ");
    console.log(evt);
}
function onClose(evt) {
    console.log("Closed ");
    console.log(evt);
}

function onOpen() {
    console.log("Connected to " + wsUri);
}

function onMessage(evt) {
    // var msg = JSON.parse(evt.data);

    console.log("onMessage: ");
    console.log(evt.data);
}
