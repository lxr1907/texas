# Texas
Introduction
A Texas Hold'em game.

## Software Architecture
#### Server-side:

Java 13
Spring Boot 3.3.5
WebSocket

#### Frontend:

HTML + JavaScript + Canvas
#### Database:
MySQL
#### Cache:

Memcache

#### Installation Guide
1.Install MySQL and Memcache.

2.Execute the database table initialization script located in texas/databasesql directory.

3.In the Java project, run mvn update.

4.Run mvn install.
Start the application with

java -jar texas.jar.

#### Usage Instructions

Modify the backend configuration file as needed: src/main/resources/application-local.properties.

Local address: http://127.0.0.1:8080/texas/texasIndex.html.

Configure the frontend WebSocket connection address as needed:

In texas/src/main/resources/static/texasJS/message.js, update the following variables:

var wsip = "ws://127.0.0.1:8080/texas/ws/texas";
var wsip_prod = "ws://127.0.0.1:8080/texas/ws/texas";
