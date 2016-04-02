import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Scanner;


public class Player implements Runnable{
	public DataOutputStream outToClient;
	public BufferedReader inFromClient;
	public Socket socket;
	public Scanner input;
	public Formatter output;
	public int id;
	public boolean answered = false;
	public GameThread game;
	public String username;
	public static final char[] validAnswers = {'a', 'b', 'c', 'd', 'e'};
	
	public Player(Socket socket, int id){
		this.id = id;
		this.socket = socket;
		
		try {
			inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outToClient = new DataOutputStream(socket.getOutputStream());
			//input = new Scanner(socket.getInputStream());
			//output = new Formatter(socket.getOutputStream());
		} catch (IOException exception){
			exception.printStackTrace();	// DEBUG
		}
	}

	public void setGame(GameThread game){
		this.game = game;
	}
	
	@Override
	public void run() {
		try {
			//System.out.println("Player " + id + " connected\n");
			//output.format("%i\n", id);
			//output.flush();

			String clientSentence;
			//---------Introduction---------
			/*
			HelperFunctions.sendMessage("Welcome to Deception!\nPlease enter your display name.", this);
			clientSentence = inFromClient.readLine();
			username = clientSentence;
			HelperFunctions.sendMessage("Please wait while we search for players", this);
			*/
			
			//---------Main Input Loop---------
			while(true) {
				clientSentence = inFromClient.readLine();
	            if (clientSentence == null) break;	// The client has disconnected
	            System.out.println("Received: " + clientSentence);
	            if (clientSentence.equals("exit")) {
	            	System.out.println("Player " + id + " exited\n");
	            	break;
	            }
	            if (game != null){
	            	switch (game.state) {
	            		case GameThread.STARTING:
	            			break;
	            		case GameThread.GETFAKES:
	            			if (!answered) {
	            				game.lobby.grabFakeAnswer(clientSentence, id);
	            				answered = true;
	            			}
	            			break;
	            		case GameThread.GETANSWERS:
	            			if (!answered) {
	            				//if (Arrays.asList(validAnswers).contains(clientSentence.charAt(0))){
		            				game.lobby.grabRealAnswersAndUpdateScores(clientSentence.substring(0,1), id);
		            				answered = true;
	            				//} else {
	            				//	HelperFunctions.sendMessage("Please enter a valid answer.", this);
	            				//}
	            			}
	            			break;
	            		case GameThread.RESULTS:
	            			break;
	            		case GameThread.FINISHED:
	            			if (!answered){
	            				if (clientSentence.equals("n")){
	            					socket.close();
	            					System.exit(1);
	            				} else if (clientSentence.equals("y")){
	            					answered = true;
	            				} else {
	            					HelperFunctions.sendMessage("Please enter either 'y' or 'n'.", this);
	            				}
	            				answered = true;
	            			}
	            			break;
	            		default: break;
	            	}
	            }
			}
		} catch (IOException e1) {
			System.out.println("Player " + id + " disconnected\n");
		} finally {
			try {
				socket.close();
			} catch (IOException exception){
				exception.printStackTrace();	// DEBUG
			}
		}
	}
}
