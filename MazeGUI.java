package edu.txst.midterm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * Main GUI window for the 16-Bit Maze game.
 * Handles user input, rendering, and game state transitions.
 */
public class MazeGUI extends JFrame {
	private Board originalBoard;
	private Board currentBoard;
	private GameEngine engine;
	private GamePanel gamePanel;
	private InfoPanel infoPanel;
	private JMenuItem resetItem;
	private int stepCounter;

	/**
	 * Constructs the main game window, initializes UI components,
	 * and registers keyboard listeners for player movement.
	 */
	public MazeGUI() {
		setTitle("16-Bit Maze");
		setSize(640, 480); // Adjusted for 10x5 grid with scaling
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		initMenu();

		infoPanel = new InfoPanel();
		gamePanel = new GamePanel();
		add(infoPanel, BorderLayout.NORTH);
		add(gamePanel, BorderLayout.CENTER);

		// Handle Keyboard Input
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (engine == null)
					return;

				switch (e.getKeyCode()) {
					case KeyEvent.VK_UP -> engine.movePlayer(-1, 0);
					case KeyEvent.VK_DOWN -> engine.movePlayer(1, 0);
					case KeyEvent.VK_LEFT -> engine.movePlayer(0, -1);
					case KeyEvent.VK_RIGHT -> engine.movePlayer(0, 1);
				}
				infoPanel.setInfoSteps(engine.getSteps());
				infoPanel.setInfoCoins(engine.getCoins());
				gamePanel.repaint();

				// Check for victory
				if (engine.playerWins()) {
					JOptionPane.showMessageDialog(MazeGUI.this,
							"Congratulations! You found the exit.\nYour got "
									+ (infoPanel.getInfoSteps() * -1 + infoPanel.getInfoCoins() * 5)
									+ " points",
							"Level Complete", JOptionPane.INFORMATION_MESSAGE);

					// Optional: Disable engine to prevent movement after win
					engine = null;
					resetItem.setEnabled(false);
				}
			}
		});
	}

	/**
	 * Initializes the menu bar with Open and Reset menu items.
	 */
	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");

		JMenuItem openItem = new JMenuItem("Open");
		resetItem = new JMenuItem("Reset");
		resetItem.setEnabled(false); // Disabled by default

		openItem.addActionListener(e -> openFile());
		resetItem.addActionListener(e -> resetGame());

		gameMenu.add(openItem);
		gameMenu.add(resetItem);
		menuBar.add(gameMenu);
		setJMenuBar(menuBar);
	}

	/**
	 * Opens a file chooser dialog to load a CSV board file.
	 * Initializes the game engine and board with the selected file.
	 */
	private void openFile() {
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
		int result = fileChooser.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			CSVBoardLoader loader = new CSVBoardLoader();

			// Load and Store
			originalBoard = loader.load(selectedFile.getAbsolutePath());
			currentBoard = originalBoard.clone();
			engine = new GameEngine(currentBoard);

			resetItem.setEnabled(true);
			gamePanel.setBoard(currentBoard);
			gamePanel.repaint();
		}
	}

	/**
	 * Resets the game to its original state by reloading the original board.
	 */
	private void resetGame() {
		if (originalBoard != null) {
			currentBoard = originalBoard.clone();
			engine = new GameEngine(currentBoard);
			gamePanel.setBoard(currentBoard);
			gamePanel.repaint();
		}
	}

	/**
	 * Panel that displays the current step and coin counts.
	 */
	private class InfoPanel extends JPanel {
		private JLabel infoSteps;
		private JLabel infoCoins;

		/**
		 * Constructs the info panel with step and coin labels initialized to zero.
		 */
		public InfoPanel() {
			this.setLayout(new FlowLayout());
			this.add(new JLabel("Steps: "));
			// infoRemainingSteps is a label which value can be changed using its method called
			// setText
			infoSteps = new JLabel("0");
			this.add(infoSteps);
			this.add(new JLabel("Coins: "));
			// infoCoins is a label which value can be changed using its method called setText
			infoCoins = new JLabel("0");
			this.add(infoCoins);
		}

		/**
		 * Updates the steps label with the given value.
		 *
		 * @param remainingSteps the current step count to display
		 */
		public void setInfoSteps(int remainingSteps) {
			this.infoSteps.setText(Integer.toString(remainingSteps));
		}

		/**
		 * Returns the current step count shown in the label.
		 *
		 * @return current step count
		 */
		public int getInfoSteps() {
			return Integer.parseInt(this.infoSteps.getText());
		}

		/**
		 * Updates the coins label with the given value.
		 *
		 * @param infoCoins the current coin count to display
		 */
		public void setInfoCoins(int infoCoins) {
			this.infoCoins.setText(Integer.toString(infoCoins));
		}

		/**
		 * Returns the current coin count shown in the label.
		 *
		 * @return current coin count
		 */
		public int getInfoCoins() {
			return Integer.parseInt(this.infoCoins.getText());
		}
	}

	/**
	 * Panel responsible for rendering the maze board using colored tiles.
	 */
	private class GamePanel extends JPanel {
		private Board board;
		private final int TILE_SIZE = 64; // Scale up for visibility

		/**
		 * Sets the board to be rendered.
		 *
		 * @param board the board to display
		 */
		public void setBoard(Board board) {
			this.board = board;
		}

		/**
		 * Paints the maze by iterating over all cells and drawing each tile.
		 *
		 * @param g the Graphics context used for drawing
		 */
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (board == null)
				return;

			for (int r = 0; r < 6; r++) {
				for (int c = 0; c < 10; c++) {
					int cell = board.getCell(r, c);
					drawTile(g, cell, c * TILE_SIZE, r * TILE_SIZE);
				}
			}
		}

		/**
		 * Draws a single tile at the specified pixel coordinates using a color
		 * corresponding to the cell type.
		 *
		 * @param g    the Graphics context
		 * @param type the cell type (0=floor, 1=wall, 2=coin, 5=exit, 6=player)
		 * @param x    the x pixel coordinate
		 * @param y    the y pixel coordinate
		 */
		private void drawTile(Graphics g, int type, int x, int y) {
			// Placeholder colors until you link the sprite loading logic
			switch (type) {
				case 0 -> g.setColor(Color.LIGHT_GRAY); // Floor
				case 1 -> g.setColor(Color.DARK_GRAY); // Wall
				case 2 -> g.setColor(Color.YELLOW); // Coin
				case 5 -> g.setColor(Color.MAGENTA); // Exit
				case 6 -> g.setColor(Color.BLUE); // Player
				default -> g.setColor(Color.BLACK);
			}
			g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
			g.setColor(Color.WHITE);
			g.drawRect(x, y, TILE_SIZE, TILE_SIZE); // Grid lines
		}
	}

	/**
	 * Entry point for the application. Launches the GUI on the Swing event dispatch thread.
	 *
	 * @param args command-line arguments (not used)
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new MazeGUI().setVisible(true));
	}
}
