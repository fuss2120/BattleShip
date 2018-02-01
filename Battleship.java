import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;

public class BattleShip extends JFrame implements ActionListener, KeyListener {

	// static final variables
	private static final int WIDTH = 750;
	private static final int HEIGHT = 450;
	private static final int START_X = 300;
	private static final int START_Y = 150;
	private static final int totalHits = 17;
	
	// counters
	private int moveCount = 0;
	private int hitCount = 0;
	private int missCount = 0;
	private int shipCount = 0;
	private int desShipHits = 0;
	private int cruShipHits = 0;
	private int subShipHits = 0;
	private int batShipHits = 0;
	private int carShipHits = 0;
	private boolean isRevealed = false;
	
	// holds hit and miss marks
	private String[][] marks = new String[11][11];
	// holds ship placements
	private String[][] boardWithShips = new String[11][11];

	// input GUI
	private JTextField text = null;
	private JTable board = null;
	private JButton fire = null;
	private JFrame win = null;

	// GUI labels
	private JLabel numMisses;
	private JLabel numHits;
	private JLabel numMoves;
	private JLabel shipsSunk;
	// checkbox
	private JCheckBox showAnimation;

	// variables ot read from a text file
	private PrintWriter pw = null;
	private Scanner sc = null;

	// constuctor
	public BattleShip() {
		setTitle("Battle Ship");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		setLocation(START_X, START_Y);
		setFocusable(true);
		addKeyListener(this);

		JPanel panel = new JPanel();

		panel.setLayout(new GridLayout(0, 1));

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Menu");
		JMenuItem restart = new JMenuItem("New Game");
		restart.addActionListener(this);
		JMenuItem Load = new JMenuItem("Load");
		Load.addActionListener(this);
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(this);
		menu.add(restart);
		menu.add(Load);
		menuBar.add(menu);
		menu.add(save);

		this.setJMenuBar(menuBar);

		board = new JTable(11, 11);
		board.setGridColor(Color.GREEN);

		// creating columns
		for (int i = 0; i < board.getColumnCount() - 1; i++) {
			board.setValueAt((char) ((int) ('A') + i), 0, i + 1);
		}
		
		// creating rows
		for (int i = 1; i < board.getRowCount(); i++) {
			board.setValueAt(i, i, 0);
		}
		board.setEnabled(false);

		panel.add(board);

		JPanel panel1 = new JPanel();
		panel1.setLayout(new FlowLayout());

		numMisses = new JLabel("Number of Misses: 0");
		numHits = new JLabel("Number of Hits: 0");
		shipsSunk = new JLabel("Number of Ships Sunk: 0");
		panel1.add(numMisses);
		panel1.add(numHits);
		panel1.add(shipsSunk);

		showAnimation = new JCheckBox("Show Animations");
		panel1.add(showAnimation);

		JLabel instructions = new JLabel("To hit a point on the grid, input column, then row. Ex: B4");
		instructions.setPreferredSize(new Dimension(400, 100));
		panel1.add(instructions);

		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(0, 1));

		text = new JTextField();
		text.setPreferredSize(new Dimension(100, 20));
		text.addKeyListener(this);
		panel2.add(text);

		fire = new JButton("Fire!");
		fire.addActionListener(this);
		fire.addKeyListener(this);
		panel2.add(fire);

		panel1.add(panel2);

		numMoves = new JLabel("Number of Moves: 0");
		panel1.add(numMoves);

		panel.add(panel1);

		add(panel, BorderLayout.CENTER);

		setUpBoard();
	}

	// creating a menu bar with differnet actions
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Load")) {
			load(); 
		} else if (e.getActionCommand().equals("Save")) {
			save();
		} else if (e.getActionCommand().equals("Restart")) {
			restart();
		} else if (e.getActionCommand().equals("Fire!")) {
			fire();
		}

	}

	// creates the frame and runs the program
	public static void main(String[] args) {
		BattleShip gmWindow = new BattleShip();
		gmWindow.setVisible(true);
	}

	// places the ship into the JTable, holds ship size and name of the ship
	public void setShips(int size, String name) {
		// variables
		int randomRow;
		int randomCol;
		boolean vertical = getRandomBoolean();
		boolean occupied = false;

		// checks vertial placment
		if (vertical) {
			do {
				occupied = false;
				randomRow = ((int) Math.floor(Math.random() * 10) + 1);
				randomCol = ((int) Math.floor(Math.random() * 10) + 1);

				while (randomRow + size > 10) {
					randomRow--;
				}

				for (int i = 0; i < size; i++) {
					if (boardWithShips[randomRow][randomCol] != null)
						occupied = true;
					randomRow++;
				}
				randomRow -= size;
			} while (occupied);

			for (int i = 0; i < size; i++) {
				boardWithShips[randomRow][randomCol] = name;
				randomRow++;
			}
		}
		// checks horizontal placemnt
		else {
			do {
				occupied = false;
				randomRow = ((int) Math.floor(Math.random() * 10) + 1);
				randomCol = ((int) Math.floor(Math.random() * 10) + 1);
				while (randomCol + size > 10) {
					randomCol--;
				}
				for (int i = 0; i < size; i++) {
					if (boardWithShips[randomRow][randomCol] != null)
						occupied = true;
					randomCol++;
				}
				randomCol -= size;
			} while (occupied);

			for (int i = 0; i < size; i++) {
				boardWithShips[randomRow][randomCol] = name;
				randomCol++;
			}
		}
	}

	// generates a random number 
	public boolean getRandomBoolean() {
		return Math.random() < .5;
	}

	// sets the spots where the ships will go
	public void setUpBoard() {
		for (int i = 0; i < boardWithShips.length; i++) {
			for (int j = 0; j < boardWithShips[i].length; j++)
				boardWithShips[i][j] = null;
		}

		// calls the method to place all the ships
		setShips(3, "Cruiser");
		setShips(3, "Submarine");
		setShips(4, "Battleship");
		setShips(5, "Carrier");
		setShips(2, "Destroyer");

	}

	// the end state of the game
	public void isOver() {

		// checks the total hits
		if (hitCount == totalHits) {
			// checks if animation box is checked
			if (showAnimation.isSelected())
				// plays animaition and sound file
				playVictoryAnimationAndSound();

			// shows pop up pane
			int won = JOptionPane.showConfirmDialog(null,
					"Congratulations, you won!!! It took you " + moveCount + " moves!\nWant to play again?", null,
					JOptionPane.YES_NO_OPTION);
			// checks to see if user wants to play again
			if (won == JOptionPane.YES_OPTION) {
				restart();
			} else {
				System.exit(0);
			}

		}

	}

	// loads from a text file
	public void load() {
		String fileName = "";
		String label = "";
		try {
			fileName = JOptionPane
					.showInputDialog("Please input the file you would like to load from(exclude \".txt\"): ") + ".txt";
			File file = new File(fileName);
			sc = new Scanner(file);
			label = sc.nextLine();
			if (!label.equals("Ship Placement:")) {
				throw new Exception();
			}
			for (int i = 0; i < boardWithShips.length; i++) {
				String row = sc.nextLine();
				for (int j = 0; j < boardWithShips[i].length; j++) {
					int semi = row.indexOf(';');
					String s = row.substring(0, semi);
					if (s.equals("null")) {
						boardWithShips[i][j] = null;
					} else {
						boardWithShips[i][j] = s;
					}
					row = row.substring(semi + 1);
				}
			}
			sc.nextLine();
			label = sc.nextLine();
			if (!label.equals("User Guesses:")) {
				throw new Exception();
			}
			for (int i = 0; i < marks.length; i++) {
				String row = sc.nextLine();
				for (int j = 0; j < marks[i].length; j++) {
					int semi = row.indexOf(';');
					String s = row.substring(0, semi);
					if (s.equals("null")) {
						marks[i][j] = null;
					} else {
						marks[i][j] = s;
						board.setValueAt(s, i, j);
					}
					row = row.substring(semi + 1);
				}
			}
			sc.nextLine();
			label = sc.nextLine();
			if (!label.equals("Number of Hits:")) {
				throw new Exception();
			}
			hitCount = Integer.parseInt(sc.nextLine());
			sc.nextLine();
			label = sc.nextLine();
			if (!label.equals("Number of Misses:")) {
				throw new Exception();
			}
			missCount = Integer.parseInt(sc.nextLine());
			sc.nextLine();
			label = sc.nextLine();
			if (!label.equals("Number of Moves:")) {
				throw new Exception();
			}
			moveCount = Integer.parseInt(sc.nextLine());
			sc.nextLine();
			label = sc.nextLine();
			if (!label.equals("Number of Ships Sunk:")) {
				throw new Exception();
			}
			shipCount = Integer.parseInt(sc.nextLine());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "The file name " + fileName + " is invalid. File not loaded.");
		}
		sc.close();
	}

	// save to a text file
	public void save() {

		String name = JOptionPane.showInputDialog(null, "Enter the file name(exclude \".txt\")");
		name += ".txt";

		File savedGame = null;

		try {

			savedGame = new File(name);

			int yesOrNo = JOptionPane.YES_OPTION;

			if (savedGame.exists()) { // if a file with that name already
										// exists, ask if they want to override
										// it
				yesOrNo = JOptionPane.showConfirmDialog(null,
						"A text file with that name already exists. Do you want to override it?", null,
						JOptionPane.YES_NO_OPTION);
			}

			if (yesOrNo == JOptionPane.NO_OPTION) {
				throw new Exception();
			}

			int confirm = JOptionPane.showConfirmDialog(null, "Is the correct file name: " + name, null,
					JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.NO_OPTION) {
				throw new Exception();
			}

			pw = new PrintWriter(savedGame);

			pw.println("Ship Placement:");
			for (int i = 0; i < boardWithShips.length; i++) {
				String s = "";
				for (int j = 0; j < boardWithShips[0].length; j++) {
					s += boardWithShips[i][j] + ";";
				}
				pw.println(s);
			}

			pw.println();
			pw.println("User Guesses:");

			for (int i = 0; i < marks.length; i++) {
				String s = "";
				for (int j = 0; j < marks[0].length; j++) {
					s += marks[i][j] + ";";
				}
				pw.println(s);
			}

			pw.println();
			pw.println("Number of Hits:");
			pw.println(hitCount);
			pw.println();
			pw.println("Number of Misses:");
			pw.println(missCount);
			pw.println();
			pw.println("Number of Moves:");
			pw.println(moveCount);
			pw.println();
			pw.println("Number of Ships Sunk:");
			pw.println(shipCount);

			pw.flush();
			pw.close();

			JOptionPane.showMessageDialog(null, "File saved.");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "File not saved.");
		}

	}

	// creates a new game
	public void restart() {
		this.dispose();
		if (win != null)
			win.dispose();
		BattleShip.main(null);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (keyCode == 112) {
			if (!isRevealed) {
				for (int i = 1; i < boardWithShips.length; i++) {
					for (int j = 1; j < boardWithShips[0].length; j++) {
						if (boardWithShips[i][j] != null && boardWithShips[i][j] != "X" && boardWithShips[i][j] != "O")
							board.setValueAt(boardWithShips[i][j], i, j);
						else
							board.setValueAt(boardWithShips[i][j], i, j);
					}
				}
				isRevealed = true;
				text.setEditable(false);
			} else {
				for (int i = 1; i < boardWithShips.length; i++) {
					for (int j = 1; j < boardWithShips[0].length; j++) {
						board.setValueAt(marks[i][j], i, j);
					}
				}
				isRevealed = false;
				text.setEditable(true);
			}
		} else if (keyCode == 10) {
			fire();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	// gets input from the user and tells whether it is a hit or a miss
	public void fire() {
		if(isRevealed){
			JOptionPane.showMessageDialog(null, "You must leave the answer key to fire.");
		} else{
			String str = text.getText(); //puts user input into String
			String inputStr = str.toUpperCase();
			int row = 0, column = 0;
			try {
				if (inputStr.length() == 3) {
					if (inputStr.substring(1, 3).equals("10") && Character.isLetter(inputStr.charAt(0))) {
						row = 10;
						column = (int)inputStr.charAt(0) - (int)'A' + 1;
					} else {
						throw new Exception();
					}
				} else if (inputStr.length() != 2) {
					throw new Exception();
				} else if (Character.isDigit(inputStr.charAt(1)) && Character.isLetter(inputStr.charAt(0))) { //if input is column then row
					row = Integer.parseInt(inputStr.substring(1, 2)); //turn digit character into integer with ParseInt
					column = (int)inputStr.charAt(0) - (int)'A' + 1; //turns character into digit with typecasting
				} else {
					throw new Exception();
				}
				if(row > 11 || column > 11) {
					throw new Exception();
				}
				HitOrMiss hom = new HitOrMiss();

				String result = hom.Hit_Miss(row, column, boardWithShips, marks);
				if(result.equals("hit")){
					board.setValueAt("X", row, column);
					marks[row][column] = "X";
					hitCount++;
					numHits.setText("Number of hits: " + hitCount);
					moveCount++;
					numMoves.setText("Number of moves: " + moveCount);
					if (showAnimation.isSelected())
						showHitAnimation();
					checkShipsSunk(row, column);
				}
				else if(result.equals("miss")){
					board.setValueAt("O", row, column);
					marks[row][column] = "O";
					missCount++;
					numMisses.setText("Number of misses: " + missCount);
					moveCount++;
					numMoves.setText("Number of moves: " + moveCount);
				}
				else if(result.equals("already missed")){
					JOptionPane.showMessageDialog(null, "You have already missed this spot.");
				}
				else if(result.equals("already hit")){
					JOptionPane.showMessageDialog(null, "You have already hit this spot.");
				}
			} catch(Exception ee) {
				if (str.length() == 0) {
					JOptionPane.showMessageDialog(null, "No input detected. Please enter the column, then row. (ex: B4)");
				} else {
					JOptionPane.showMessageDialog(null, "Invalid Input. You entered \"" + str + "\" Please enter the column, then row. (ex: B4)");
				}
			}
			text.setText("");
			isOver();
		}
	}

	// tells whether a ship is sunk
	public void checkShipsSunk(int row, int col) {
		String ship = boardWithShips[row][col];
		if (ship.equals("Destroyer")) {
			desShipHits++;
			if (desShipHits >= 2) {
				shipCount++;
				JOptionPane.showMessageDialog(null, "You sunk the Destroyer!");
			}
		} else if (ship.equals("Cruiser")) {
			cruShipHits++;
			if (cruShipHits >= 3) {
				shipCount++;
				JOptionPane.showMessageDialog(null, "You sunk the Cruiser!");
			}
		} else if (ship.equals("Submarine")) {
			subShipHits++;
			if (subShipHits >= 3) {
				shipCount++;
				JOptionPane.showMessageDialog(null, "You sunk the Submarine!");
			}
		} else if (ship.equals("Battleship")) {
			batShipHits++;
			if (batShipHits >= 4) {
				shipCount++;
				JOptionPane.showMessageDialog(null, "You sunk the Battleship!");
			}
		} else if (ship.equals("Carrier")) {
			carShipHits++;
			if (carShipHits >= 5) {
				shipCount++;
				JOptionPane.showMessageDialog(null, "You sunk the Carrier!");
			}
		}
		shipsSunk.setText("Number of Ships Sunk: " + shipCount);
	}

	// plays sound file and displays anniamtion
	public void playVictoryAnimationAndSound() {
		try {

			String wave = "https://img.buzzfeed.com/buzzfeed-static/static/enhanced/webdr06/2013/4/1/16/anigif_enhanced-buzz-3434-1364846445-23.gif";
			URL pic = new URL(wave);
			ImageIcon image = new ImageIcon(pic);

			win = new JFrame("You win!");
			JLabel lbl = new JLabel(image);

			win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			win.setSize(500, 400);

			win.add(lbl);
			win.setVisible(true);
			playSound();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			System.out.println("Something went wrong");
		}

	}

	// creates a sound file
	public static void playSound() {
		AudioInputStream din = null;
		try {
			AudioInputStream in = AudioSystem.getAudioInputStream(
					new URL("https://dl.dropboxusercontent.com/s/xbs42i9b5q74p90/Jamaican%20Horn%20Siren.wav"));
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16,
					baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
			din = AudioSystem.getAudioInputStream(decodedFormat, in);
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
			SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
			if (line != null) {
				line.open(decodedFormat);
				byte[] data = new byte[4096];

				line.start();

				int nBytesRead;
				while ((nBytesRead = din.read(data, 0, data.length)) != -1) {
					line.write(data, 0, nBytesRead);
				}

				line.drain();
				line.stop();
				line.close();
				din.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (din != null) {
				try {
					din.close();
				} catch (Exception e) {
				}
			}
		}
	}

	// when user hits a ship, animation plays
	public void showHitAnimation() {
		String wave = "http://www.mscs.mu.edu/~tgillen/BSHit.gif";
		URL pic = null;
		try {
			pic = new URL(wave);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		ImageIcon icon = new ImageIcon(pic);
		JOptionPane.showMessageDialog(null, null, "It's a Hit", JOptionPane.INFORMATION_MESSAGE, icon);
	}

	// tells whether the users guess is a hit or miss
	public class HitOrMiss {

		String guessedAndMiss = "O";
		String guessedAndHit = "X";

		public String Hit_Miss(int a, int b, String[][] ships, String[][] guessPlacement) {
			boolean alreadyGuessed = alreadyGuessed(a, b, guessPlacement);
			if (ships[a][b] != null && !alreadyGuessed) {
				guessPlacement[a][b] = guessedAndHit;
				System.out.println("hit");
				return "hit";
			} else if (alreadyGuessed && guessPlacement[a][b].equals(guessedAndHit)) {
				System.out.println("already hit");
				return "already hit";
			} else if (alreadyGuessed && guessPlacement[a][b].equals(guessedAndMiss)) {
				System.out.println("alreadymissed");
				return "already missed";
			} else {
				System.out.println("miss");
				guessPlacement[a][b] = guessedAndMiss;
				return "miss";
			}
		}

		public boolean alreadyGuessed(int a, int b, String[][] guessPlacement) {
			if (guessPlacement[a][b] != null)
				return true;
			else
				return false;
		}
	}

}
