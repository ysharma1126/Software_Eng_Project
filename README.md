# Software_Eng_Project

RUN: 

make compile, make run, make all (compile+run)

Run in Software_Eng_Project to compile code before actually running the server:
javac -d bin/ -cp src src/server/*.java

Run in src:
java -cp ../lib/mysql-connector-java-5.1.41-bin.jar:../bin server.Server 8080


ISSUES:

Not punishing client disconnects in game (SERVER)

Not handling user signup failing because already in DB (SERVER)

Surrender button isnt functional (CLIENT)

When host leaves, the host is changed, that's updated on the lobby UI but that isnt updated on the room UI  (CLIENT)

Playerthread doesn't refresh on EndGameResponse (SERVER)

Same person can log in multiple times (SERVER)
