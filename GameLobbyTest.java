import java.util.ArrayList;
import java.util.Scanner;


public class GameLobbyTest
{
	public static void main(String[] args)
	{
		int playerIds[] = {0,1,2,3};
		GameLobby gL = new GameLobby(playerIds);
		System.out.println(gL.getQuestion(0));
		gL.grabFakeAnswer("Beer",0);
		gL.grabFakeAnswer("Mead", 1);
		gL.grabFakeAnswer("Vodka",2);
		gL.grabFakeAnswer("Whiskey",3);
		System.out.println(gL.grabQuestionAndAnswers());
		
		Scanner in = new Scanner(System.in);
		for (int i=0;i<4;i++)
		{
			String answer = in.nextLine();
			gL.grabRealAnswersAndUpdateScores(answer.substring(0,1), Integer.parseInt(answer.substring(1,2)));
		}
		
		System.out.println(gL.returnResults());
		ArrayList<Integer> a = new ArrayList<Integer>();
		a = gL.returnWinner();
		for (int i=0;i<a.size();i++)
		{
			System.out.println(a.get(i));
		}
	}
}
