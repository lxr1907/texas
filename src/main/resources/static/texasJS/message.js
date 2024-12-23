/**
 * Created by lxr on 2016/10/30.
 */
var websocket = null;
var wsip = "ws://127.0.0.1:8080/texas/ws/texas";
var wsip_prod = "ws://120.26.217.116:8080/texas/ws/texas";
// 发送消息映射
var mapping = {
    // 注册
    regist: 0,
    // 登陆
    login: 1,
    // 进入房间
    enterRoom: 2,
    // 退出房间
    exitRoom: 3,
    // 坐下
    sitDown: 4,
    // 站起
    standUp: 5,
    // 过牌
    check: 6,
    // 下注
    betChips: 7,
    // 弃牌
    fold: 8,
    //获取排行榜
    getRankList: 9
};
function wsInit() {
    var url =  wsip;
    if ('WebSocket' in window) {
        websocket = new WebSocket(url);
    } else if ('MozWebSocket' in window) {
        websocket = new MozWebSocket(url);
    }
    bindWsFunction(websocket);
}
function wsReOpen() {
    // if (websocket.readState != 1) {
    // websocket = new WebSocket("ws:/" + wsip );
    // }
    // bindWsFunction(websocket);
}
function bindWsFunction(ws) {
    ws.onerror = function (event) {
        onError(event);
    };
    ws.onopen = function (event) {
        onOpen(event);
    };
    ws.onmessage = function (event) {
        onMessage(event);
    };
    ws.onclose = function (event) {
        onClose(event);
    };
}
/**
 * 接收服务器消息
 */
function onMessage(event) {
    if (event.data != null) {
        var dataJson = JSON.parse(event.data);
        var func = eval(dataJson.c);
        new func(null, dataJson);
        console.log(dataJson.c + " is call by server!");
    }
}
/**
 * 建立连接
 */
function onOpen(event) {
    document.getElementById('messages').innerHTML = '连接成功'+wsip;
}
function onError(event) {
    console.log(event.data);
    //无法连接本地,则切换为正式服
    wsip=wsip_prod;
    document.getElementById('messages').innerHTML = '连接失败，切换到'+wsip+'......';
    wsInit();
}
function onClose(event) {
    wsReOpen();
}

// 发送消息
function sendMessage() {
    var data = {};
    data.c = mapping.sendMessage;
    websocket.send(JSON.stringify(data));
}
// 错误消息返回
function onException(e, data) {
    console.log(data.message);
    alert(data.message);
}