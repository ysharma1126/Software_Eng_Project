compile: src/server/*.java
	javac -d bin/ -cp src src/server/*.java 
run:
	java -cp lib/mysql-connector-java-5.1.41-bin.jar:bin server/Server 8080
all: compile run
