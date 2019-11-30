# texas

#### 介绍
一个德州扑克游戏
pc前端用html+js+canvas，服务端java13 springboot2.2 websocket
查看效果访问http://120.26.217.116:8080/texas/texasIndex.html

#### 软件架构
1服务端java13 springboot2.2 websocket
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


#### 码云特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
