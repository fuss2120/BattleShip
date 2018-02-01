public class HitOrMiss {

	String empty = "O";
	String filled = "X";
	int hit = 1, miss = 0, guessed = 2; 
	int hitCount = 0 , missCount = 0;
	int[][] shipPlacement = new int [10][10];
	
	//this method determines if the user hit or miss
	public boolean Hit_Miss(int a, int b )//the coordinates from Gr.4 will go in this method
	{
		
		if (shipPlacement[a][b] == hit)
		{
			hitCount++;
			shipPlacement[a][b] = guessed;
			System.out.println("It's a Hit!");
			return true;
		}
		else 
		{
			shipPlacement[a][b] = guessed;
			System.out.println("It's a Miss!");
			return false;
		}
		
	}
	//this method will determine if the user has already guessed a coordinate
	public boolean alreadyGuessed(int a, int b)
	{
		if (shipPlacement[a][b] == guessed)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
}
