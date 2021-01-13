import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.BorderFactory;

public class GameWindow extends JFrame implements ActionListener, MouseListener {
	private static final long serialVersionUID = 0L;

	private static final int FRAME_WIDTH = 640, FRAME_HEIGHT = 700;
	private static final int BUTTON_WIDTH = 50, BUTTON_HEIGHT = 50;

	// stores instance of gameActions
	private GameActions gameActions;

	// buttons

	private JButton newGameButton;
	private JButton exitGameButton;
	private JButton toggleModeButton;

	private JButton[][] boardButtons;

	// labels

	private JLabel titleLabel;
	private JLabel creatorLabel;
	private JLabel scoreLabel;
	private JLabel levelLabel;
	private JLabel timerLabel;
	private JLabel modeLabel;
	private JLabel loseMessageLabel;

	// location of selection
	private int selectedX, selectedY;

	// initializes all UI elements
	public GameWindow(String title) {
		super(title);

		// creates instance of gameActions class to use
		gameActions = new GameActions();
		// sets grid size
		boardButtons = new JButton[GameActions.BOARD_WIDTH][GameActions.BOARD_HEIGHT];

		// creates button grid
		for (int x = 0; x < GameActions.BOARD_WIDTH; x++) {
			for (int y = 0; y < GameActions.BOARD_HEIGHT; y++) {
				boardButtons[x][y] = new JButton();
				boardButtons[x][y].setLocation((BUTTON_WIDTH * 2 + (x * BUTTON_WIDTH + x * 5)),
						(BUTTON_HEIGHT * 2 + (y * BUTTON_HEIGHT + y * 5)));
				boardButtons[x][y].setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
				boardButtons[x][y].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
				boardButtons[x][y].setBackground(Color.WHITE);
				boardButtons[x][y].setVisible(false);
				boardButtons[x][y].addActionListener(this);
				boardButtons[x][y].addMouseListener(this);
				boardButtons[x][y].setActionCommand("BLANK");

				getContentPane().add(boardButtons[x][y]);
			}
		}

		// sets default selection to 0

		selectedX = 0;
		selectedY = 0;

		// sets up the UI buttons and labels

		titleLabel = new JLabel("JEWEL CLONE");
		titleLabel.setFont(new Font("Dialog", Font.BOLD, 35));
		titleLabel.setBounds(100, -150, 800, 400);
		titleLabel.setForeground(Color.BLACK);

		creatorLabel = new JLabel("By: Leo McCarthy-Kennedy");
		creatorLabel.setFont(new Font("Dialog", Font.PLAIN, 15));
		creatorLabel.setBounds(100, -120, 800, 400);
		creatorLabel.setForeground(Color.BLACK);

		scoreLabel = new JLabel("Score: 0");
		scoreLabel.setFont(new Font("Dialog", Font.PLAIN, 15));
		scoreLabel.setBounds(375, -140, 800, 400);
		scoreLabel.setForeground(Color.BLACK);

		levelLabel = new JLabel("Level: 1");
		levelLabel.setFont(new Font("Dialog", Font.PLAIN, 15));
		levelLabel.setBounds(375, -160, 800, 400);
		levelLabel.setForeground(Color.BLACK);

		timerLabel = new JLabel("Time: 0");
		timerLabel.setFont(new Font("Dialog", Font.PLAIN, 15));
		timerLabel.setBounds(375, -160, 800, 400);
		timerLabel.setForeground(Color.BLACK);
		timerLabel.setVisible(false);

		modeLabel = new JLabel("Mode: Freeplay");
		modeLabel.setFont(new Font("Dialog", Font.PLAIN, 15));
		modeLabel.setBounds(375, -120, 800, 400);
		modeLabel.setForeground(Color.BLACK);

		loseMessageLabel = new JLabel("Status: None");
		loseMessageLabel.setFont(new Font("Dialog", Font.PLAIN, 25));
		loseMessageLabel.setBounds(100, 360, 800, 400);
		loseMessageLabel.setForeground(Color.BLACK);

		getContentPane().add(titleLabel);
		getContentPane().add(creatorLabel);
		getContentPane().add(scoreLabel);
		getContentPane().add(levelLabel);
		getContentPane().add(timerLabel);
		getContentPane().add(modeLabel);
		getContentPane().add(loseMessageLabel);
		getContentPane().setLayout(null);
		getContentPane().setBackground(Color.WHITE);

		newGameButton = new JButton("New Game");
		newGameButton.setBackground(SystemColor.control);
		newGameButton.setForeground(Color.BLACK);
		newGameButton.setLocation(100, 590);
		newGameButton.setSize(105, 40);
		newGameButton.addActionListener(this);

		getContentPane().add(newGameButton);

		toggleModeButton = new JButton("Mode");
		toggleModeButton.setBackground(SystemColor.control);
		toggleModeButton.setForeground(Color.BLACK);
		toggleModeButton.setLocation(265, 590);
		toggleModeButton.setSize(105, 40);
		toggleModeButton.addActionListener(this);

		getContentPane().add(toggleModeButton);

		exitGameButton = new JButton("Exit Game");
		exitGameButton.setBackground(SystemColor.control);
		exitGameButton.setForeground(Color.BLACK);
		exitGameButton.setLocation(430, 590);
		exitGameButton.setSize(105, 40);
		exitGameButton.addActionListener(this);

		getContentPane().add(exitGameButton);

		// creates a new board
		newBoard();

		repaint();

		// runs every 0.1 seconds to update UI (score, timer)
		TimerTask task = new TimerTask() {
			public void run() {
				timerLabel.setText("Time: " + gameActions.getTime());

				repaint();

				// checks if player lost
				if (gameActions.getLost()) {
					// checks mode
					if (gameActions.getMode()) {
						// lost on timer mode
						loseMessageLabel.setText("Status: Out of time!");
					} else {
						// lost on freeplay mode
						loseMessageLabel.setText("Status: No more moves!");
					}
				}
			}
		};

		Timer timer = new Timer();

		long delay = 0;
		long interval = 100;

		timer.scheduleAtFixedRate(task, delay, interval);
	}

	// used to create a new board
	private void newBoard() {
		// creates new board in game actions
		gameActions.newBoard(true);

		// resets selection

		selectedX = 0;
		selectedY = 0;

		scoreLabel.setText("Score: " + gameActions.getScore()); // resets score
		levelLabel.setText("Level: " + gameActions.getLevel()); // resets level
		loseMessageLabel.setText("Status: None"); // resets lost message

		gameActions.setSelected(false); // resets selection

		for (int x = 0; x < GameActions.BOARD_WIDTH; x++) {
			for (int y = 0; y < GameActions.BOARD_HEIGHT; y++) {
				// initializes button information and behaviour

				boardButtons[x][y].setActionCommand(new String(Integer.toString(x) + " " + Integer.toString(y) + " "
						+ Integer.toString(gameActions.getBoardTile(x, y))));
				boardButtons[x][y].setVisible(true);
				boardButtons[x][y].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
				boardButtons[x][y].setBackground(getColor(gameActions.getBoardTile(x, y)));
			}
		}
		repaint();
	}

	private void updateBoard() {
		// sets score
		scoreLabel.setText("Score: " + gameActions.getScore());
		// sets level
		levelLabel.setText("Level: " + gameActions.getLevel());

		for (int x = 0; x < GameActions.BOARD_WIDTH; x++) {
			for (int y = 0; y < GameActions.BOARD_HEIGHT; y++) {
				// updates button information and behaviour

				boardButtons[x][y].setActionCommand(new String(Integer.toString(x) + " " + Integer.toString(y) + " "
						+ Integer.toString(gameActions.getBoardTile(x, y))));
				boardButtons[x][y].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
				boardButtons[x][y].setBackground(getColor(gameActions.getBoardTile(x, y)));
			}
		}
		repaint();
	}

	private void updateSelection(int x, int y) {
		// updates selection information

		boardButtons[selectedX][selectedY].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		boardButtons[x][y].setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

		selectedX = x;
		selectedY = y;

		repaint();
	}

	private void toggleMode() {
		// changes mode
		modeLabel.setText("Mode: " + gameActions.toggleMode());

		// sets UI accordingly

		if (gameActions.getMode()) {
			timerLabel.setVisible(true);
			levelLabel.setVisible(false);
		} else {
			timerLabel.setVisible(false);
			levelLabel.setVisible(true);
		}

		// resets game

		gameActions.resetScore();

		newBoard();
	}

	public void actionPerformed(ActionEvent e) {
		// gets source button
		JButton button = (JButton) e.getSource();
		// exit is pressed
		if (e.getActionCommand().equals("Exit Game")) {
			System.exit(0);
		// new game is pressed
		} else if (e.getActionCommand().equals("New Game")) {
			// resets game

			gameActions.resetScore();

			newBoard();
		// toggle is pressed
		} else if (e.getActionCommand().equals("Mode")) {
			toggleMode();
		} else {
			// gets selection

			int x = Character.getNumericValue((button.getActionCommand().charAt(0)));
			int y = Character.getNumericValue((button.getActionCommand().charAt(2)));

			// perform swap action

			if (gameActions.selectTile(x, y)) {
				updateBoard();
			} else {
				updateSelection(x, y);
			}
		}
	}

	// used to quickly get colour when updating and initializing board
	private Color getColor(int number) {
		switch (number) {
		case 1:
			return Color.RED;
		case 2:
			return Color.MAGENTA;
		case 3:
			return Color.CYAN;
		case 4:
			return Color.BLUE;
		case 5:
			return Color.GREEN;
		case 6:
			return Color.PINK;
		case 7:
			return Color.YELLOW;
		default:
			return Color.WHITE;
		}
	}

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public static void main(String[] args) {
		// creates new frame
		GameWindow frame = new GameWindow("Jewel Clone");

		// sets frame to exit when closed
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		// sets frame size
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		// sets frame to visible
		frame.setVisible(true);
		// locks frame size
		frame.setResizable(false);
	}
}