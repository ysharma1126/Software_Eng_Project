# Software_Eng_Project

RUN: 

make compile, make run, make all (compile+run)

Run in Software_Eng_Project to compile code before actually running the server:
javac -d bin/ -cp src src/server/*.java src/message/*.java src/gamelogic/*.java

Run in src:
java -cp ../lib/mysql-connector-java-5.1.41-bin.jar:../bin server.Server 8080


ISSUES:

Client Disconnect
- If sable in room and shalin in lobby, if sable disconnects in room, shalin sees after refreshing in lobby (Game 0 0/10  sable) The game should disappear from the lobby
- If both sable and shalin in room, if sable disconencts in room, shalin sees sable has left however his buttons don't work
- Furthermore, if both sable and shalin in room, and sable disconnects in room, and sable was host, shalin doesn't become host and get start game option
- Furthermore, if sahil in lobby sees both sable and shalin in room, and sable disconnects, while shalin is trapped in the room, that room disappears from sahils lobby on refresh
- If sable and shalin are in game, and sable disconnects, sahil in lobby sees sable has disconnected, but shalin doesn't get 'SURRENDERED' and his buttons don't work

Sometimes the setcount doesn't update, even though "Correct!" displays on the bottom of the screen

On surrender, if my setcount isn't 0, surrender is displayed as my setcount but I don't get sent to the back to lobby screen. If my setcount is 0, I do...


(Maybe not necessary, check with sable) Make client threaded, to remove refresh button. Continuously clicking it is kinda cancer. Not necessary, but if easy do so. (CLIENT)






