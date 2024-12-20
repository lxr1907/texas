# texas

#### 介绍
一个德州扑克游戏
pc前端用html+js+canvas，服务端java13 springboot 3.3.5  websocket
查看效果访问http://127.0.0.1:8080/texas/texasIndex.html

#### 软件架构
1服务端java13 springboot3.3.5 websocket
2前端pcweb html+js+canvas
3数据库mysql
4缓存memcache


#### 安装教程
1. 安装mysql，memcache
2. 执行 texas/databasesql目录下的数据库表初始化脚本
3.  java项目中 mvn update
4.  mvn install
5.  启动java -jar texas.jar

#### 使用说明

1.  本地根据需要改后端配置文件texas/src/main/resources/application-local.properties
2.   本地地址：http://127.0.0.1:8080/texas/texasIndex.html
3.  前端websocket链接地址配置，根据自己需要进行修改：
texas/src/main/resources/static/texasJS/message.js
var wsip = "ws://127.0.0.1:8080/texas/ws/texas";
var wsip_prod = "ws://120.26.217.116:8080/texas/ws/texas";

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


