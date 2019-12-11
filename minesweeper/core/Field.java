package sk.tsystems.gamestudio.game.minesweeper.core;

import java.util.Random;


import sk.tsystems.gamestudio.game.minesweeper.core.Tile.State;

/**
 * Field represents playing field and game logic.
 */
public class Field {
	/**
	 * Playing field tiles.
	 */
	private final Tile[][] tiles;

	/**
	 * Field row count. Rows are indexed from 0 to (rowCount - 1).
	 */
	private final int rowCount;

	/**
	 * Column count. Columns are indexed from 0 to (columnCount - 1).
	 */
	private final int columnCount;

	/**
	 * Mine count.
	 */
	private final int mineCount;

	/**
	 * Game state.
	 */
	private GameState state = GameState.PLAYING;

	/**
	 * Constructor.
	 *
	 * @param rowCount    row count
	 * @param columnCount column count
	 * @param mineCount   mine count
	 */
	public Field(int rowCount, int columnCount, int mineCount) {
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		this.mineCount = mineCount;
		tiles = new Tile[rowCount][columnCount];

		// generate the field content
		generate();
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
	}

	public int getRowCount() {
		return rowCount;
	}

	public int getColumnCount() {
		return columnCount;
	}

	public int getMineCount() {
		return mineCount;
	}

	public Tile getTile(int row, int column) {
		return tiles[row][column];
	}

	public int getNumberOf(Tile.State state) {
		int count = 0;
		for (int row = 0; row < rowCount; row++) {
			for (int column = 0; column < columnCount; column++) {
				if (tiles[row][column].getState() == state) {
					count++;
				}

			}
		}
		return count;
	}

	/**
	 * Opens tile at specified indeces.
	 *
	 * @param row    row number
	 * @param column column number
	 */
	public void openTile(int row, int column) {
		Tile tile = tiles[row][column];
		if (tile.getState() == Tile.State.CLOSED) {
			tile.setState(Tile.State.OPEN);
			if (tile instanceof Mine) {
				state = GameState.FAILED;
				return;
			}

			if (tile instanceof Clue) {
				if (((Clue) tile).getValue() == 0) {
					openAdjacentTiles(row, column);
				}
			}

			if (isSolved()) {
				state = GameState.SOLVED;
				return;
			}
		}
	}

	private void openAdjacentTiles(int row, int column) {
		for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
			int nearRow = row + rowOffset;
			if (nearRow >= 0 && nearRow < rowCount) {
				for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
					int nearColumn = column + columnOffset;
					if (nearColumn >= 0 && nearColumn < columnCount) {
						Tile tile = tiles[nearRow][nearColumn];
						if (tile instanceof Clue && tile.getState() == State.CLOSED) {
							tile.setState(State.OPEN);
							if (((Clue) tile).getValue() == 0) {
								openAdjacentTiles(nearRow, nearColumn);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Marks tile at specified indeces.
	 *
	 * @param row    row number
	 * @param column column number
	 */
	public void markTile(int row, int column) {
		Tile tile = tiles[row][column];
		if (tile.getState() == Tile.State.CLOSED) {
			tile.setState(Tile.State.MARKED);
		} else if (tile.getState() == Tile.State.MARKED) {
			tile.setState(Tile.State.CLOSED);
		}
	}

	/**
	 * Generates playing field.
	 */

	private void generate() {
		Random random = new Random();
		int newMine = 0;
		while (newMine < mineCount) {
			int row = random.nextInt(rowCount);
			int col = random.nextInt(columnCount);
			if (tiles[row][col] == null) {
				tiles[row][col] = new Mine();
				newMine++;
			}
		}
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				if (tiles[i][j] == null) {
					tiles[i][j] = new Clue(countAdjacentMines(i, j));
				}
			}
		}
	}

	/**
	 * Returns true if game is solved, false otherwise.
	 *
	 * @return true if game is solved, false otherwise
	 */
	private boolean isSolved() {
		return rowCount * columnCount - getNumberOf(State.OPEN) == mineCount;
	}

	/**
	 * Returns number of adjacent mines for a tile at specified position in the
	 * field.
	 *
	 * @param row    row number.
	 * @param column column number.
	 * @return number of adjacent mines.
	 */
	private int countAdjacentMines(int row, int column) {
		int count = 0;
		for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
			int actRow = row + rowOffset;
			if (actRow >= 0 && actRow < rowCount) {
				for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
					int actColumn = column + columnOffset;
					if (actColumn >= 0 && actColumn < columnCount) {
						if (tiles[actRow][actColumn] instanceof Mine) {
							count++;
						}
					}
				}
			}
		}

		return count;
	}
	
	public int getRemainingMineCount() {
		int remainingMines = mineCount - getNumberOf(Tile.State.MARKED);
		return remainingMines >= 0 ? remainingMines : 0;
	}
}
