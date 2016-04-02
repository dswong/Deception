import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class ClientThread implements Runnable {
	Socket socket;
	DataOutputStream outToServer;
	BufferedReader inFromServer;
	String sentence;
	String modifiedSentence;
	
	public ClientThread(Socket socket, DataOutputStream output, BufferedReader input){
		this.socket = socket;
		this.outToServer = output;
		this.inFromServer = input;
	}
	@Override
	public void run() {
		while (true){
			try {
				modifiedSentence = inFromServer.readLine();
			} catch (IOException e) {
				System.out.println("The server has disconnected; now exiting");
				System.exit(0);
				e.printStackTrace();
			}
			int i = 0;
			if (modifiedSentence.length() < 4){	// Hacky solution for a weird problem where the first line
				modifiedSentence += '\n';		// is sometimes only the length, in which case the remaining
				i--;							// lines are the actual sentence
			}
			
			if (modifiedSentence.length() >= 4){
				// Gets the length appended to the front of the string and removes it
				int fullLength = HelperFunctions.getLength(modifiedSentence);
				modifiedSentence = HelperFunctions.removeLength(modifiedSentence);
				
				// If the server returned an error
				if (modifiedSentence.startsWith("ERROR")){
					System.err.println(modifiedSentence);
				} else {
					System.out.println(modifiedSentence);
				}
				
				i += modifiedSentence.length() + 1;
				// Loop through the string in order to get all lines
				while (i < fullLength){
					try {
						modifiedSentence = inFromServer.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println(modifiedSentence);
					i += modifiedSentence.length() + 1;
				}
			}
		}
	}

}
