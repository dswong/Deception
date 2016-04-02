Software Requirements:

This project requires that the latest version of Java be installed in order to compile and run. As of the time of writing, this is Java Version 8.

Compilation/Instructions:

To compile, open the command line, navigate to the directory where the project files are, and type “javac Player.java Client.java Server.java ClientThread.java GameLobby.java GameThread.java HelperFunctions.java” without the quotes.

To start the server: “java Server [port]”, where port is the the port number you wish to use. 

To run the client: “java Client [ip] [port]”, where ip is the ip address and port is the specified port that was used in running the server. The server must be running before the client can connect to it.

Once connected, the client interface will allow you to play the game.
