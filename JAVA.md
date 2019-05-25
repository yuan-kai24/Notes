# JAVA

笔记

## WebSocket

一种基于socket的技术，可以让浏览器和服务端保持联系从而达到即时信息传输的技术。

这是一种后来的协议(ws)，tomcat7之前不支持，后来才内置的。

采用异步通讯方式，用线程池来处理，可并发量非常乐观。

### 前端代码

```javascript
//创建
var socket = new WebSocket('ws://localhost:8080')
//打开
socket.onopen = function(event){
    // 发送初始消息
	socket.send('info')
    
    // 监听消息
    socket.onmessage = function(event){
        console.log('监听')
    }
    
    // 监听socket关闭
    socket.onclose = function(event){
        console.log('关闭')
    }
    
    // 关闭socket
    socket.close()
}
```

### 后端代码

以下是tomcat自带的，其他的也大同小异

```java
// 自带线程安全的map
private static Map<String, Session> sessionCache = new ConcurrentHashMap<String, Session>();

// 服务端链接
@OnOpen
public void onOpen(Session session) throws IOException {
    sessionCache.put(session.getId(), session);
    System.out.println("ID为" + session.getId() + "的客户端接入");
    RemoteEndpoint.Basic basic = session.getBasicRemote();
    basic.sendText("欢迎你");
    System.out.println("当前人数：" + sessionCache.size());
}

@OnMessage
public void onMessage(Session session, String msg) {
    System.out.println("收到消息：" + msg);
}

@OnClose
public void onClose(Session session) {
    sessionCache.remove(session.getId());
    System.out.println("ID为" + session.getId() + "的客户端断开");
}

@OnError
public void onError(Throwable throwable, Session session) {
    System.out.println("进入错误处理");
}
```

maven

```html
<dependency>
    <groupId>javax.websocket</groupId>
    <artifactId>javax.websocket-api</artifactId>
    <version>1.1</version>
    <scope>provided</scope>
</dependency>
```

