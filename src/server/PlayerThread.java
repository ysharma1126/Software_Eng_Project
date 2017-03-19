package server;
import java.io.*;
import java.net.*;

/**
 * A thread for a single player.
 * @author Shalin
 *
 */

public class PlayerThread implements Runnable {
    private Socket socket = null;

    public PlayerThread(Socket socket) {
        this.socket = socket;
        Server.connected_players.add(socket);
    }

    public void run() {

        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()));
        ) {
            for(int i=0; i<10; i++) {
                out.println("HI "+i);
            }
            this.terminate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void terminate() throws IOException{
    	Server.connected_players.remove(socket);
    	socket.close();
    }
}