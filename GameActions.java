import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActions {
	public static final int BOARD_WIDTH = 8, BOARD_HEIGHT = 8;
	public static final int JEWEL_TYPES = 7;

	// stores tile values and locations
	private int[][] board;

	// false = freeplay, true = time attack
	private boolean mode;
	private boolean started;
	private boolean lost;

	private int score;
	private int level;
	private int time;

	// if the first spot has been selected
	private boolean selected;

	// location of first selected spot
	private int selectedX, selectedY;

	public GameActions() {
		board = new int[BOARD_WIDTH][BOARD_HEIGHT];

		mode = false;
		started = false;
		lost = false;

		score = 0;
		level = 1;
		time = 0;

		selected = false;

		selectedX = 0;
		selectedY = 0;

		// used to deplete timer when time attack mode is running
		TimerTask task = new TimerTask() {
			public void run() {
				// only deplete if game is in time attack mode and game has started
				if (mode && started) {
					time--;
				}

				// timer is clamped to interval (0, 1000)

				if (time > 1000) {
					time = 1000;
				} else if (time < 0) {
					lost = true;
					time = 0;
				}
			}
		};

		Timer timer = new Timer();

		long delay = 0;
		long interval = 100;

		timer.scheduleAtFixedRate(task, delay, interval);
	}

	public void newBoard(boolean reset) {
		// if values should be reset
		if (reset) {
			started = false;
			lost = false;

			score = 0;
			level = 1;
			time = 1000;
		}

		// generate new board
		for (int x = 0; x < BOARD_WIDTH; x++) {
			for (int y = 0; y < BOARD_HEIGHT; y++) {
				Random random = new Random();

				// assign random value between 1 and JEWEL_TYPES
				board[x][y] = random.nextInt(JEWEL_TYPES) + 1;

				// checks matches

				if (y > 1) {
					while (board[x][y] == board[x][y - 1] && board[x][y] == board[x][y - 2])
						board[x][y] = random.nextInt(JEWEL_TYPES) + 1;
				}

				if (x > 1) {
					while (board[x][y] == board[x - 1][y] && board[x][y] == board[x - 2][y])
						board[x][y] = random.nextInt(JEWEL_TYPES) + 1;
				}
			}
		}
	}

	public int getBoardTile(int x, int y) {
		return board[x][y];
	}

	public int getScore() {
		// resets score if a new level is achieved (only in freeplay mode)
		if (score >= level * 500 && !mode) {
			score = 0;
			level++;
		}

		return score;
	}

	public int getLevel() {
		return level;
	}

	public int getTime() {
		return time;
	}

	public void resetScore() {
		score = 0;
		level = 0;
	}

	public String toggleMode() {
		mode = !mode;

		return mode ? "Time Attack" : "Freeplay";
	}

	public boolean getMode() {
		return mode;
	}

	public boolean getLost() {
		return lost;
	}

	public boolean selectTile(int x, int y) {
		if (selected) {
			if (x != selectedX ^ y != selectedY) {
				// checks to see if selection is in range
				if (Math.abs(selectedX - x) == 1 ^ Math.abs(selectedY - y) == 1) {
					// if swap is successful
					if (swapTiles(x, y)) {
						selected = false;

						// check if the board has no moves left
						if (!movesLeft()) {
							// depending on which mode either lose or reset board
							if (mode) {
								newBoard(false);
							} else {
								lost = true;
							}
						}
						return true;
					}
				}
			}
		}

		// first selection is made
		selected = true;

		// saves position of first selection

		selectedX = x;
		selectedY = y;

		// returns false when selection is invalid or first time selecting
		return false;
	}

	public void setSelected(boolean s) {
		selected = s;
	}

	private boolean swapTiles(int x, int y) {
		if (lost) {
			// can't swap tiles if player has lost
			return false;
		}

		boolean swapped = false;

		// if its the first move then start the timer
		if (!started && mode) {
			started = true;
		}

		// swaps the tiles

		int temp = board[x][y];
		board[x][y] = board[selectedX][selectedY];
		board[selectedX][selectedY] = temp;

		// calls method to check if swap is valid
		if (clearMatches(1, false)) {
			swapped = true;
		} else {
			// resets tiles if swap is invalid

			board[selectedX][selectedY] = board[x][y];
			board[x][y] = temp;
		}

		return swapped;
	}

	private boolean clearMatches(int cascadeIndex, boolean checking) {
		// if match was found
		boolean cleared = false;

		for (int x = 0; x < BOARD_WIDTH; x++) {
			// how many in a row on the vertical and where the first in the row was
			int yCount = 1, yStart = 0;
			// how many in a row on the horizontal and where the first in the row was
			int xCount = 1, xStart = 0;

			for (int y = 0; y < BOARD_HEIGHT; y++) {
				if (y != 0) {
					// if current is equal to previous
					if (board[x][y] == board[x][y - 1] && y != BOARD_HEIGHT - 1) {
						yCount++;
					} else {
						// special check to ensure last cell in vertical is checked
						if (board[x][y] == board[x][y - 1]) {
							yCount++;
						}

						// sets last
						if (y == BOARD_HEIGHT - 1) {
							y = BOARD_HEIGHT;
						}

						// if equal to or more than 3 in a row
						if (yCount >= 3) {
							// clear tiles if condition is met
							if (!checking) {
								// loops from first in row to current cell
								for (int i = yStart; i < y; i++) {
									board[x][i] = 0;
								}

								addScore(yCount, cascadeIndex);
							} else {
								// if method is checking for matches return true
								return true;
							}

							// a match has been cleared
							cleared = true;
						}

						yCount = 1;
						yStart = y;
					}

					// resets y on special condition
					if (y == BOARD_HEIGHT) {
						y = BOARD_HEIGHT - 1;
					}

					// this block of code does the exact same thing as earlier except on the
					// horizontal by swapping x and y in the array modifier

					if (board[y][x] == board[y - 1][x] && y != BOARD_HEIGHT - 1) {
						xCount++;
					} else {
						if (board[y][x] == board[y - 1][x]) {
							xCount++;
						}

						if (y == BOARD_HEIGHT - 1) {
							y = BOARD_HEIGHT;
						}

						if (xCount >= 3) {
							if (!checking) {
								for (int i = xStart; i < y; i++) {
									board[i][x] = 0;
								}

								addScore(xCount, cascadeIndex);
							} else {
								return true;
							}

							cleared = true;
						}

						xCount = 1;
						xStart = y;
					}
				}
			}
		}

		// if matches were found
		if (cleared) {
			// calls method to remove matches and passes index of cascade
			fillBlanks(cascadeIndex);
		}

		// returns if matches were cleared
		return cleared;
	}

	private boolean movesLeft() {
		// used to break if moves are found
		boolean breaker = false;

		for (int x = 0; x < BOARD_WIDTH; x++) {
			for (int y = 0; y < BOARD_HEIGHT; y++) {
				// stores current cell value
				int temp = board[x][y];

				if (x > 0) {
					// swaps cells

					board[x][y] = board[x - 1][y];
					board[x - 1][y] = temp;

					// checks for matches
					breaker = clearMatches(1, true);

					// swaps cells back

					board[x - 1][y] = board[x][y];
					board[x][y] = temp;
				}

				// breaks if move was found
				if (breaker) {
					return true;
				}

				// next block of code does the same as earlier but on the vertical

				if (y > 0) {
					board[x][y] = board[x][y - 1];
					board[x][y - 1] = temp;

					breaker = clearMatches(1, true);

					board[x][y - 1] = board[x][y];
					board[x][y] = temp;
				}

				if (breaker) {
					return true;
				}
			}
		}

		// if no matches found and timer mode is off, player has lost
		if (!mode) {
			lost = true;
		}

		return false;
	}

	private void addScore(int r, int c) {
		// r is how many in a row, c is index of cascade (multiplier)
		switch (r) {
		case 3:
			score += 10 * c * level;
			time += 10 * c;
			break;
		case 4:
			score += 20 * c * level;
			time += 20 * c;
			break;
		case 5:
			score += 30 * c * level;
			time += 30 * c;
			break;
		default:
			score += 30 * c * level;
			time += 30 * c;
			break;
		}
	}

	private void fillBlanks(int index) {
		// goes from bottom to top
		for (int y = BOARD_HEIGHT - 1; y >= 0; y--) {
			// goes from left to right
			for (int x = 0; x < BOARD_WIDTH; x++) {
				// if cell is empty
				if (board[x][y] == 0) {
					// loops until a non-empty cell is found
					for (int i = y; i >= 0; i--) {
						if (board[x][i] != 0) {
							// drops cell to current empty
							board[x][y] = board[x][i];
							// replaces full cell with empty cell
							board[x][i] = 0;

							break;

						// if the top cell is empty, choose random value
						} else if (i == 0) {
							Random random = new Random();
							board[x][y] = random.nextInt(JEWEL_TYPES) + 1;
						}
					}
				}
			}
		}

		// call clear matches with cascade index++
		clearMatches(index + 1, false);
	}
}
