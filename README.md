# Software_Eng_Project

RUN: 

make compile, make run, make all (compile+run)

Run in Software_Eng_Project to compile code before actually running the server:
javac -d bin/ -cp src src/server/*.java src/message/*.java src/gamelogic/*.java

Run in src:
java -cp ../lib/mysql-connector-java-5.1.41-bin.jar:../bin server.Server 8080


ISSUES:

Sometimes the setcount doesn't update, even though "Correct!" displays on the bottom of the screen

Maybe not necessary, check with sable) Make client threaded, to remove refresh button. Continuously clicking it is kinda cancer. Not necessary, but if easy do so. (CLIENT)






