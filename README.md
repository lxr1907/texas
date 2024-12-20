# texas

### 介绍
一个德州扑克游戏
a texas game
### 软件架构
#### 服务端server
java13 springboot3.3.5 websocket
#### 前端web
html+js+canvas
#### 数据库database
mysql
#### 缓存cache
memcache


### 安装教程
1. 安装 mysql，memcache
  
3. 执行 texas/databasesql目录下的数据库表初始化脚本
  
5.  java项目中 mvn update
   
7.  mvn install
   
9.  启动java -jar texas.jar

#### 使用说明

1.  本地根据需要改后端配置文件:
   src/main/resources/application-local.properties
   
3.   本地地址：http://127.0.0.1:8080/texas/texasIndex.html
   
5.  前端websocket链接地址配置，根据自己需要进行修改：

texas/src/main/resources/static/texasJS/message.js
var wsip = "ws://127.0.0.1:8080/texas/ws/texas";
var wsip_prod = "ws://127.0.0.1:8080/texas/ws/texas";


#### 参与贡献



