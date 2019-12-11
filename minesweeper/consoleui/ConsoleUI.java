package sk.tsystems.gamestudio.game.minesweeper.consoleui;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sk.tsystems.gamestudio.game.minesweeper.UserInterface;
import sk.tsystems.gamestudio.game.minesweeper.core.Clue;
import sk.tsystems.gamestudio.game.minesweeper.core.Field;
import sk.tsystems.gamestudio.game.minesweeper.core.GameState;
import sk.tsystems.gamestudio.game.minesweeper.core.Mine;
import sk.tsystems.gamestudio.game.minesweeper.core.Tile;

/**
 * Console user interface.
 */
public class ConsoleUI implements UserInterface  {
    /** Playing field. */
    private Field field;
    
    
    
    /** Input reader. */
    private BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    
    /**
     * Reads line of text from the reader.
     * @return line as a string
     */
    private String readLine() {
        try {
            return input.readLine();
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * Starts the game.
     * @param field field of mines and clues
     */
    @Override
	public void newGameStarted(Field field) {
        this.field = field;
        do {
            update();
            if(field.getState() == GameState.SOLVED) {
            	System.out.println("Gratz, you won!");
            	return;
            } 
            
            if(field.getState() == GameState.FAILED) {
            	System.out.println("Gratz, you lost :D ! ");
            	return;
            }
            processInput();
        } while(true);
    }
    
    /**
     * Updates user interface - prints the field.
     */
    @Override
	public void update() { 
    	System.out.print("  ");
    	for(int i = 0; i < field.getColumnCount();i++) {
    		System.out.print(i + " ");
    	}
    	
    	System.out.println();
    	
    	for(int i = 0; i < field.getRowCount(); i++) {
    		System.out.printf("%c " , 'A' + i);
    	    for (int j = 0; j < field.getColumnCount(); j++) {
    	    	Tile tile = field.getTile(i, j);
    	    	if (tile.getState() == Tile.State.CLOSED){
    	    		System.out.print("- ");
    	    	} 
    	    	
    	    	if (tile.getState() == Tile.State.MARKED) {
    	    		System.out.print("M ");
    	    	}
    	    	
    	    	if (tile.getState() == Tile.State.OPEN) {
    	    		if (tile instanceof Mine) {
    	    			System.out.print("X ");    	    			
    	    		}
    	    		if (tile instanceof Clue) {
    	    			System.out.print(((Clue)tile).getValue() + " ");
    	    		}    	    	
    	    	}
    	    }
	    	System.out.println();
    	}
    	System.out.println("Remaining mines : " + field.getRemainingMineCount());
    	
    }
    
    /**
     * Processes user input.
     * Reads line from console and does the action on a playing field according to input string.
     */
    private void processInput() {
        System.out.println("X – ukonèenie hry, MA1 – oznaèenie dlaždice v riadku A a ståpci 1, OB4 – odkrytie dlaždice v riadku B a ståpci 4");
        String userInput = readLine().toUpperCase();
        try {
        	handleInput(userInput);
        } catch (WrongFormatException ex) {
        	System.err.println(ex.getMessage());
        }
    }

	private void handleInput(String userInput) throws WrongFormatException {
		Pattern pattern = Pattern.compile("(O|M)([A-J])([0-9])");
	    Matcher matcher = pattern.matcher(userInput);
	    if(matcher.matches()) {
	    	char userOption = matcher.group(1).charAt(0);
	    	char userRow = matcher.group(2).charAt(0);
	    	int row = userRow - 'A';
	    	int column = matcher.group(3).charAt(0) - '0';
	    	if (userOption == 'O') {
	    		field.openTile(row,  column);
	     	} else if (userOption =='M') {
	     		field.markTile(row, column);
	     	}
	    } else if (userInput.equals("X")) {
	    	System.out.println("Game Over! :D ");
	    	System.exit(0);
	    } else {
	    	throw new WrongFormatException ("You 've entered wrong input"); 
	    }   		
	}
}
