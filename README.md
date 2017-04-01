# Software_Eng_Project

TODO:

GUI needs a listener thread for server responses
GUI has to not make holes in the grid if past 12 cards

Run in src:
java -cp ../lib/mysql-connector-java-5.1.41-bin.jar:../bin server.Server 8080

Run in Software_Eng_Project to compile code before actually running the server:
javac -d bin/ -cp src src/server/*.java
