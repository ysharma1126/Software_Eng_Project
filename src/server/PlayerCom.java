package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ArrayBlockingQueue;

import gamelogic.Player;

public class PlayerCom {
	public Player player = null;
	public ObjectInputStream input = null;
	public ObjectOutputStream output = null;
	public ArrayBlockingQueue<Object> playerToGamePipe = null;
	public ArrayBlockingQueue<String> gameToPlayerPipe = null;
	
	public PlayerCom(Player p, ObjectInputStream in, ObjectOutputStream out, ArrayBlockingQueue<Object> pgp, ArrayBlockingQueue<String> gpp){
		player = p;
		input = in;
		output = out;
		playerToGamePipe = pgp;
		gameToPlayerPipe = gpp;
	}
}