import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Client {
	String sentence;
	String modifiedSentence;
	BufferedReader inFromUser;
	Socket clientSocket;
	DataOutputStream outToServer;
	BufferedReader inFromServer;
	ExecutorService executorService;
	ClientThread clientThread;
	
	public static void main(String argv[]) throws Exception {
		if (argv.length != 2){
			System.err.println("ERROR: Invalid number of args. Terminating.");
		} else {
			try {
				System.out.println("Trying to connect\n");
				Client client = new Client(argv[0], Integer.parseInt(argv[1]));
				System.out.println("Connected");
				client.run();
			} catch (NumberFormatException e){
				System.err.println("ERROR: Invalid port. Terminating.");
			} catch (IllegalArgumentException e){
				System.err.println("ERROR: Invalid port. Terminating.");
			} catch (BindException e){
				System.err.println("ERROR: Could not bind port. Terminating.");
			} catch (Exception e){
				System.err.println("ERROR: Could not connect to server. Terminating.");
			}
		}
	}
	
	/**
	 * The TCP client object constructor
	 * @param host
	 * @param port
	 * @throws Exception
	 */
	public Client(String host, int port) throws Exception {
		inFromUser = new BufferedReader( new InputStreamReader(System.in));
		clientSocket = new Socket(host, port);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		executorService = Executors.newFixedThreadPool(1);
		clientThread = new ClientThread(clientSocket, outToServer, inFromServer);
		executorService.execute(clientThread);

	}
	
	/**
	 * Executes the send/receive loop of the client
	 * @throws Exception
	 */
	public void run() throws Exception {
		while (true){
			sentence = inFromUser.readLine();
			// Determine if the sentence is a valid command, returns an ERROR message if not
			sentence = parse(sentence);
			if (!sentence.startsWith("ERROR")){	// If the parser doesn't detect an error
				try {	// Try to send the user's input
					outToServer.writeBytes(sentence + '\n');
				} catch (Exception e){
					System.err.println("ERROR: Failed to receive message. Terminating.");
					System.exit(1);
				}
				
				if (sentence.equals("exit")){
					break;
				}
			} else {
				System.err.println(sentence);
				if (sentence.endsWith("Terminating.")) {	// Exit if the server is terminating
					clientSocket.close();
					System.exit(1);
				}
			}
		}
		
		clientSocket.close();
		System.exit(1);
	}
	
	/**
	 * Determine if the given value is a valid command on the client side
	 * @param val
	 * @return
	 */
	public String parse(String val){
		/*
		String[] vals = val.split(" ");
		if (val.charAt(0) == '?'){				// ?key
			String arg = val.substring(1);
			if (arg.contains("=") || arg.contains("?")) val = "ERROR: Invalid command.";
		} else if (val.contains("=")){			// key=value
		} else if (val.equals("list")){			// list
			if (vals.length > 1) val = "ERROR: Invalid command.";
		} else if (vals[0].equals("listc")){	// listc
			try { // Check if the 2nd value is an integer
				if (Integer.parseInt(vals[1]) == 0){
					val = "ERROR: Invalid command.";
				} else if ( Integer.parseInt(vals[1]) == 0 || vals.length > 3 || (vals.length == 3 && vals[2].contains("="))){
					val = "ERROR: Invalid continuation key.";
				}
		    } catch(NumberFormatException e) { 
		        val = "ERROR: Invalid command.";
		    }
		} else if (val.equals("exit")){			// exit
		} else if (val.equals("help")){			// help
		} else val = "ERROR: Invalid command.";
		*/
		return val;
	}
}