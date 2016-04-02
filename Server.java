
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* CS176A Homework Assignment 1 - TCP Server
 * Author: Daniel Ly
 * October 19, 2014
 * 
 * A simple TCP server that sends and receives messages following the given command guidelines:
 * ?key, key=value, list, listc num, listc num continuationKey, exit, help
 * 
 * Source: The basic framework was taken from http://systembash.com/content/a-simple-java-tcp-server-and-tcp-client/,
 * who took it from the textbook (Computer Networking: A Top-Down Approach)
 */

class Server {
	HashMap<String, String> map;
	ArrayList<String> list, continuationKeys;
	String clientSentence, result;
	ServerSocket serverSocket;
	Socket connectionSocket;
	BufferedReader inFromClient;
	DataOutputStream outToClient;
	// ^ Old
	private ExecutorService playerExecutor, gameExecutor;
	private Player[] players;
	private GameThread[] lobbies;
	private int[] playerIds; 
	private LinkedList<Player> waiting;
	
	public static void main(String argv[]) throws Exception {
		Server server = new Server(Integer.parseInt(argv[0]));
		System.out.println("Started server at " + InetAddress.getLocalHost() + "\n");	// DEBUG
		server.run();
		/*
		if (argv.length != 1){
			System.err.println("ERROR: Invalid number of args. Terminating.");
		} else {
			try {
				Server server = new Server(Integer.parseInt(argv[0]));
				server.run();
			} catch (IllegalArgumentException e){
				System.err.println("ERROR: Invalid port. Terminating.");
			} catch (BindException e){
				System.err.println("ERROR: Could not bind port. Terminating.");
			} catch (Exception e){
				System.err.println("ERROR: Could not connect to server. Terminating.");
			}
		}
		*/
	}
	
	/**
	 * The TCP server object constructor
	 * @param port
	 * @throws Exception
	 */
	public Server(int port) throws Exception {
		map = new HashMap<String, String>();
		list = new ArrayList<String>();
		continuationKeys = new ArrayList<String>();
		serverSocket = new ServerSocket(port, 100);
		playerExecutor = Executors.newFixedThreadPool(100);
		gameExecutor = Executors.newFixedThreadPool(100);
		playerIds = new int[100];
		players = new Player[100];
		waiting = new LinkedList<Player>();
		lobbies = new GameThread[100];
	}
	
	/**
	 * Executes the receive/send loop of the server
	 * The Player class handles the sending/receiving of each player
	 * This function itself only listens for new connections
	 * @throws Exception
	 */
	public void run() throws Exception {
		int i=0;
		String sentence;
		while(true){
			//System.out.println("Waiting\n");
			players[i] = new Player(serverSocket.accept(), i);
			System.out.println("Player "+ i + " Connected");
			playerExecutor.execute(players[i]);
			
			/*
			for (Player player: players){
				if (player != null){
					sentence = "Found player " + i;
					player.outToClient.write(HelperFunctions.appendLength(sentence + '\n'));
				}
			}
			*/
			waiting.add(players[i]);	// Add player to waiting list
			if (waiting.size() >= 4){
				System.out.println("Starting a game!");
				Player p[] = new Player[4];
				for (int j=0; j<4; j++){
					p[j] = waiting.pop();
				}
				int gameID = firstOpenLobby();
				lobbies[gameID] = new GameThread(p);
				for (int j=0; j<4; j++){
					p[j].game = lobbies[gameID];
				}
				gameExecutor.execute(lobbies[gameID]);
			}
			i++;
		}
	}
	
	public int firstOpenLobby(){
		int i = 0;
		while (lobbies[i] != null){
			i++;
		}
		return i;
	}
}