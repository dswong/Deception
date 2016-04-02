import java.util.ArrayList;
import java.util.Collections;

/*
 * GameLobby.java - handles the game mechanics and parses questions and answers
 * Calling class handles the running of games for one round, which is 5 questions 
 * */

public class GameLobby
{
	private int playerCount;
	private PlayerInfo[] playerArray;
	private int currentQuestion;
	private String currentCorrectAnswerLetter;
	
	public GameLobby(int[] playerIds)
	{
		playerCount = playerIds.length;
		playerArray = new PlayerInfo[playerCount];
		for (int i=0;i<playerCount;i++)
		{
			playerArray[i] = new PlayerInfo();
			playerArray[i].id = playerIds[i];
			playerArray[i].falseAnswer = "";
			playerArray[i].score = 0;
		}
	}
	
	public class PlayerInfo
	{
		int id;
		String falseAnswer;
		String assignedLetter;
		int score;
	}
	
	//questions and 2 default answers, each row is a question with one question and six answers slots 
	// [row][0] = question, [row][1] = right answer
	private String[][] questionsAnswers = { 	
			{"Question 1: In 2008, Mexican scientists found a way to make diamonds out of what alcoholic drink?","Tequila"},
			{"Question 2: In 1980, Saddam Hussein was named an honorary citizen of _________","Detroit"},
			{"Question 3: The color of the Golden Gate Bridge is officially called _________ Orange","International"},
			{"Question 4: Amerigo Vespucci, the man America was named after, was a _________ dealer.","Pickle"},
			{"Question 5: El Colacho is a Spanish festival where people dress up like the devil and jump over ________","Babies"},
			{"Question 6: A third of all divorce filings in 2011 in the U.S. contained the word ________","Facebook"},
			{"Question 7: Cap'n Crunch's first name.","Horatio"}  
			};


	/*
	 * Helper function to grab the index of appropriate player given playerId
	 * */
	public int getPlayerIndexId(int playerId)
	{
		int i;
		for (i=0;i<playerCount;i++)
		{
			if (playerArray[i].id == playerId)
			{
				return i;
			}
		}
		return 0;
	}
	
	/*
	 * Helper function to grab the index of the player based on their false answer
	 * */
	public int getPlayerIndexFalseAnswer(String fAnswer)
	{
		int i;
		for (i=0;i<playerCount;i++)
		{
			if (playerArray[i].falseAnswer.equals(fAnswer))
			{
				return i;
			}
		}
		return 0;
	}
	
	/*
	 * Helper function to grab the index of the player based on their false answer letter assignment
	 * */
	public int getPlayerIndexAssignedLetter(String letter)
	{
		int i;
		for (i=0;i<playerCount;i++)
		{
			if (playerArray[i].assignedLetter.equals(letter))
			{
				return i;
			}
		}
		return 0;
	}
	
	//----------------------------Main Running Methods-----------------------
	
	/*
	 * Grab question to print out
	 * */
	public String getQuestion(int question)
	{
		currentQuestion = question;
		return questionsAnswers[question][0];
	}
	
	/*
	 * Get the answers from players and build question and answer screen
	 * */
	public void grabFakeAnswer(String answer, int playerId)
	{
		playerArray[getPlayerIndexId(playerId)].falseAnswer = answer;
	}
	
	/*
	 * Grab the questions and answers screen
	 * */
	public String grabQuestionAndAnswers()
	{
		String screen = questionsAnswers[currentQuestion][0]+"\n";
		String correctAnswer = questionsAnswers[currentQuestion][1];
		
		//put all false answers into array, then shuffle
		ArrayList<String> answers = new ArrayList<String>();
		for (int i=0;i<playerCount;i++)
		{
			answers.add(playerArray[i].falseAnswer);
		}
		answers.add(correctAnswer);
		Collections.shuffle(answers);
		String[] a = new String[playerCount+1];
		answers.toArray(a);
		
		//assign letters, change for each player, and then build screen to return
		int asciiA = 97;
		for (int j=0;j<playerCount+1;j++)
		{
			screen = screen + Character.toString((char)(asciiA+j)) +") "+ a[j] +"\n";
			if (a[j].equals(correctAnswer)) //if its the correct answer, mark that letter
			{
				currentCorrectAnswerLetter = Character.toString((char)(asciiA+j));
			}
			else //else set the playerInfo's assigned letter on screen
			{
				playerArray[getPlayerIndexFalseAnswer(a[j])].assignedLetter = Character.toString((char)(asciiA+j));
			}
		}
		
		return screen;
	}
	
	/*
	 * Grab player answers from clients and update scores
	 * */
	public void grabRealAnswersAndUpdateScores(String letterAnswer, int playerId)
	{
		//check if answer is in bounds
		char answerCheck = letterAnswer.charAt(0);
		int ascii = (int) answerCheck;
		if (ascii < 97 || ascii > (97+playerCount))
		{
			return;
		}
		else
		{
			//if correct answer, player gets 100 points
			if (letterAnswer.equals(currentCorrectAnswerLetter))
			{
				playerArray[getPlayerIndexId(playerId)].score = playerArray[getPlayerIndexId(playerId)].score + 100;
			}
			else //else 50 points goes to the person who created the false answer
			{
				playerArray[getPlayerIndexAssignedLetter(letterAnswer)].score = playerArray[getPlayerIndexAssignedLetter(letterAnswer)].score + 50;
			}
		}
	}
	
	
	/*
	 *  Return results screen
	 * */
	public String returnResults()
	{
		String trueAnswer = "True Answer: "+ questionsAnswers[currentQuestion][1];
		return trueAnswer;
	}
	
	/*
	 *  Return winner id
	 * */
	public ArrayList<Integer> returnWinner()
	{
		ArrayList<Integer> winnerList = new ArrayList<Integer>();
		
		int highest = playerArray[0].score;
		PlayerInfo winner = playerArray[0];
		
		for (int i=1;i<playerCount;i++)
		{
			if (playerArray[i].score > highest)
			{
				highest = playerArray[i].score;
				winner = playerArray[i];
			}
		}
		winnerList.add(winner.id);
		//check for ties
		for (int j=0;j<playerCount;j++)
		{
			if (playerArray[j].score == highest && !winnerList.contains(playerArray[j].id))
			{
				winnerList.add(playerArray[j].id);
			}
		}
		
		return winnerList;
	}
}
