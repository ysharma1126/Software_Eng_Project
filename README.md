# Software_Eng_Project

RUN: 

make compile, make run, make all (compile+run)

Run in Software_Eng_Project to compile code before actually running the server:
javac -d bin/ -cp src src/server/*.java

Run in src:
java -cp ../lib/mysql-connector-java-5.1.41-bin.jar:../bin server.Server 8080


ISSUES:

Not handling client disconnects

Not handling user signup failing because already in DB

When game is started, the room isn't removed from the lobby

Surrender button isnt functional

Leaving a room with 2+ people doesn't properly change host

Leaving a room with 2+ people can't create game after

When host leaves, the host is changed, but that isnt updated on the UI
If Shalin's the host and Sahils in the room, if Shalin leaves, in Shalin's lobby he sees 1 person in room, sahil who is the host
However Sahil's UI doesnt change, if Shalin joins room again, Sahil's UI has Shalin (host), Sahil, Shalin while Shalin's UI has Sahil (host), Shalin, which is correct

Same person can log in multiple times

Unlimited people can join a room
