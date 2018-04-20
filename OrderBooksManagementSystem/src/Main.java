import menu.Menu;

/**
 * Main class. Starts the main menu.
 *
 *
 * @author Jules
 *
 */
public class Main {

	public static void main(final String[] args) {
		final Menu mainMenu = new Menu();
	}

	/*
	 * Overall remarks: Due to the time constraint, I could not deliver the best I
	 * can and to complete all requirements. Here is a list of things that could be
	 * improved
	 *
	 */

	// 1. class Menu:
	// 1.A) some functions should be moved to OrderBookManager (e.g.
	// adding / creating an order, add / creating an execution),

	// 1.B) the printing should be separated from the logic in another class

	// 1.C) String literals should be removed from the class and rather stored in a
	// file

	// 1.D) a singleton class that reads the user input would be nice, to avoid
	// passing the Scanner around between functions

	// 2. class OrderBook: is a bit big, maybe the printing part could be done in a
	// separate class

	// 3. Finally if I had more time, I would have added JUnit tests
}
