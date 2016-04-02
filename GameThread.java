import java.io.IOException;
import java.util.ArrayList;


public class GameThread implements Runnable{
	public static final int STARTING = 0;
	public static final int GETFAKES = 1;
	public static final int GETANSWERS = 2;
	public static final int RESULTS = 3;
	public static final int FINISHED = 4;
	
	GameLobby lobby;
	Player[] players;
	int state;
	
	public GameThread(Player[] players){
		this.players = players;
		lobby = new GameLobby(players);
		state = STARTING;
	}
	
	@Override
	public void run() {
		boolean playing = true;
		while (playing){
			// Send start message
			HelperFunctions.sendMessage("Starting a new game!", players);
			boolean endRound = false;
			
			for (int i=0; i<5; i++){
				//---------Get user inputs for the fake answers---------
				state = GETFAKES;
				HelperFunctions.sendMessage(lobby.getQuestion(i), players);
				HelperFunctions.sendMessage("Please enter your fake answer:", players);
				endRound = false;
				resetAnswers();
				
				// 30 second countdown between rounds
				long currentTime = System.currentTimeMillis();
				while (System.currentTimeMillis() - currentTime <= 30000 && !endRound){
					// Check if all players have given their fake answer
					endRound = true;
					for (Player player: players){
						if (!player.answered) endRound = false;
					}
				}
				resetAnswers();
				
				//---------Gets user input for their real answer---------
				state = GETANSWERS;
				HelperFunctions.sendMessage(lobby.grabQuestionAndAnswers(), players);
				
				// Wait for players to give their answers
				endRound = false;
				currentTime = System.currentTimeMillis();
				while (System.currentTimeMillis() - currentTime <= 30000 && !endRound){
					// Check if all players have given their fake answer
					endRound = true;
					for (Player player: players){
						if (!player.answered) endRound = false;
					}
				}
				
				//---------Displays results---------
				state = RESULTS;
				HelperFunctions.sendMessage(lobby.returnResults(), players);
			}
			//---------Finished game, ask if they'd like to restart---------
			state = FINISHED;
			ArrayList<Integer> winners = lobby.returnWinner();
			if (winners.size() == 1){
				HelperFunctions.sendMessage("Congratulations! You won!", HelperFunctions.getPlayer(players, winners.get(0)));
			} else {
				for (Integer i: winners){
					HelperFunctions.sendMessage("Congratulations! You've tied for first place!", HelperFunctions.getPlayer(players, winners.get(i)));
				}
			}
			HelperFunctions.sendMessage("Would you like to play another game? (y/n)", players);
			resetAnswers();
			endRound = false;
			while (!endRound){
				endRound = true;
				for (Player player: players){
					// If a player has disconnected from the game 
					// (Either through closing the or entering "n"), end the game
					if (player == null) {
						playing = false;
						break;
					} else if (!player.answered) endRound = false;
				}
			}
		}
		
		// Closing game
		for (Player player: players){
			if (player != null){
				player.game = null;
			}
		}
	}

	public void resetAnswers(){
		for (Player player: players){
			player.answered = false;
		}
	}
}