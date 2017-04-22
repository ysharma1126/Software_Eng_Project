# Software_Eng_Project

RUN: 

make compile, make run, make all (compile+run)

Run in Software_Eng_Project to compile code before actually running the server:
javac -d bin/ -cp src src/server/*.java src/message/*.java src/gamelogic/*.java

Run in src:
java -cp ../lib/mysql-connector-java-5.1.41-bin.jar:../bin server.Server 8080


ISSUES:

If host leaves, host changes, as seen in lobby, but room doesn't (well sometimes) update with [host] (CLIENT)

(Maybe not necessary, check with sable) Make client threaded, to remove refresh button. Continuously clicking it is kinda cancer. Not necessary, but if easy do so. (CLIENT)

Refresh response sent, but need to click back to lobby to get back. Lobby isn't actually refreshed, you have to click refresh again for lobby to refresh and shit to work (CLIENT)

Client disconnect is handled for in lobby, but not in room. (SERVER/CLIENT)
  - Tried to fix it, however those in lobby still see the room, even if 0/10 in it.
  - Cant create room after leaving room, even though sent createroomresponse
  - If in room, cant do shit if host disconnects. Server sends no response
  
Leave Game and Client disconnect not working in game (SERVER)
  - Leave Game, other player sees surrendered but don't return back to lobby
  - Client disconnect, other client doesnt see shit and nothing works






