package edu.txst.midterm;

/**
 * Manages the game logic for the maze, including player movement,
 * coin collection, step tracking, and win detection.
 */
public class GameEngine {
	private Board board;
	private int playerRow;
	private int playerCol;
	private int exitRow;
	private int exitCol;
	private StepCounter stepCounter = new StepCounter();
	private int coinCount = 0;

	// Cell Type Constants
	private static final int FLOOR = 0;
	private static final int WALL = 1;
	private static final int COIN = 2;
	private static final int EXIT = 5;
	private static final int PLAYER = 6;

	/**
	 * Constructs a GameEngine for the given board.
	 * Locates the player and exit positions on initialization.
	 *
	 * @param board the game board to operate on
	 */
	public GameEngine(Board board) {
		this.board = board;
		findPlayer();
		findExit();
	}

	/**
	 * Checks whether the player has reached the exit.
	 *
	 * @return true if the player's position matches the exit position, false otherwise
	 */
	public boolean playerWins() {
		return playerRow == exitRow && playerCol == exitCol;
	}

	/**
	 * Scans the board to find the player's starting position.
	 */
	private void findPlayer() {
		for (int r = 0; r < 5; r++) {
			for (int c = 0; c < 10; c++) {
				if (board.getCell(r, c) == PLAYER) {
					playerRow = r;
					playerCol = c;
					return;
				}
			}
		}
	}

	/**
	 * Scans the board to find the exit position.
	 */
	private void findExit() {
		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 11; c++) {
				if (board.getCell(r, c) == EXIT) {
					exitRow = r;
					exitCol = c;
					return;
				}
			}
		}
	}

	/**
	 * Attempts to move the player in the given direction.
	 * Movement is blocked by walls and board boundaries.
	 * Coins are collected when the player moves onto a coin cell.
	 *
	 * @param dRow Change in row (-1 = up, 1 = down, 0 = no vertical movement)
	 * @param dCol Change in column (-1 = left, 1 = right, 0 = no horizontal movement)
	 */
	public void movePlayer(int dRow, int dCol) {
		int targetRow = playerRow + dRow;
		int targetCol = playerCol + dCol;
		int targetCell = board.getCell(targetRow, targetCol);

		// Check for Walls or Out of Bounds
		if (targetCell == WALL || targetCell == -1) {
			return; // Movement blocked
		}

		// Move the Player
		// Current position becomes Floor (or Goal if player was standing on one)
		// Note: For simplicity, this engine assumes player replaces the cell.
		// If you want "Player on Goal", you'd add a 6th constant.
		board.setCell(playerRow, playerCol, FLOOR);

		if (targetCell == COIN) {
			coinCount++;
		}

		playerRow = targetRow;
		playerCol = targetCol;
		board.setCell(playerRow, playerCol, PLAYER);
		stepCounter.increaseSteps();
	}

	/**
	 * Returns the total number of steps taken by the player.
	 *
	 * @return step count
	 */
	public int getSteps() {
		return stepCounter.getSteps();
	}

	/**
	 * Returns the total number of coins collected by the player.
	 *
	 * @return coin count
	 */
	public int getCoins() {
		return coinCount;
	}
}
