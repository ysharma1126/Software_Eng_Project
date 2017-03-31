package message;

import java.io.Serializable;

/* Stats
* Should be sent only to the client that sent a 
* StatsRequest to the server.
* Stats should send the stats from the DB to the client
*/

public class Stats implements Sendable, Serializable {
	
}
