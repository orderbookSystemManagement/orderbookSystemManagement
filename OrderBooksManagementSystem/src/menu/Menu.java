package menu;

import java.util.Scanner;

import model.Execution;
import model.OrderBook;
import model.OrderBookManager;
import model.orders.LimitOrder;
import model.orders.MarketOrder;
import model.orders.Order;

/**
 * The class dedicated to the menus. It should be refactored because some
 * actions should be in the OrderBookManager. I did not have time to refactor.
 * Another improvement is to make a singleton class of user input reader, to
 * avoid passing the scanner reference in all functions
 *
 *
 * @author Jules
 *
 */
public class Menu {

	public static final int MIN_NAVIGATION_CHOICE = 0;

	OrderBookManager orderBookManager;

	/* ******************** MENUS *********************************** */

	/**
	 * The main menu, displayed at the start of the program
	 */
	public Menu() {

		printWelcome();

		orderBookManager = new OrderBookManager();
		orderBookManager.init();

		try (final Scanner sc = new Scanner(System.in)) {
			int selectedNavigation = 0;
			do {
				printMainMenu();
				selectedNavigation = getPositiveIntegerFromUser(sc, 6);

				switch (selectedNavigation) {

					case 0:
						break;
					case 1:
						addOrder(sc);

						break;
					case 2:
						addExecution(sc);
						break;
					case 3:
						openBook(sc);
						break;
					case 4:
						closeBook(sc);
						break;
					case 5:
						executeBook(sc);
						break;
					case 6:
						navigateToStatisticsMenu(sc);
						break;

					default:
						System.out.println("Something went wrong");
						break;
				}
			} while (selectedNavigation != MIN_NAVIGATION_CHOICE);
		}

	}

	/**
	 * The statistics menu
	 *
	 * @param sc
	 *            the scanner
	 */
	public void navigateToStatisticsMenu(final Scanner sc) {

		int selectedNavigation = 0;
		do {
			printStatisticsMenu();
			selectedNavigation = getPositiveIntegerFromUser(sc, 3);

			switch (selectedNavigation) {

				case 0:
					break;
				case 1:
					orderBookManager.printStatistics1();
					break;
				case 2:
					orderBookManager.printStatistics2();
					break;
				case 3:
					prepareForStatistics3(sc);
					break;
				default:
					System.out.println("Something went wrong");
					break;
			}
		} while (selectedNavigation != MIN_NAVIGATION_CHOICE);
	}

	/* ********************** Reader functions ****************************** */

	/**
	 * Allows to get a positive integer up to a certain value from the user
	 *
	 * @param scanner
	 *            the scanner
	 * @param maxMenuValue
	 *            the maximum returnable value
	 * @return the positive integer specified by the user (with a maximum value)
	 */
	private static int getPositiveIntegerFromUser(final Scanner scanner, final int maxMenuValue) {

		int chosenNumber = -1;

		do {
			System.out.println("Please enter a value within the proposed range");

			if (scanner.hasNextInt()) {
				chosenNumber = scanner.nextInt();
			} else {
				System.out.println("Invalid! You need to type an integer!");
				scanner.nextLine();
			}

		} while ((chosenNumber < 0) || (chosenNumber > maxMenuValue));

		return chosenNumber;
	}

	/**
	 * Allows to get any positive integer from the user
	 *
	 * @param scanner
	 *            the scanner
	 * @return the positive integer specified by the user
	 */
	private static int getPositiveIntegerFromUser(final Scanner scanner) {

		int chosenNumber = -1;

		do {
			System.out.println("Please enter a positive integer");

			if (scanner.hasNextInt()) {
				chosenNumber = scanner.nextInt();
			} else {
				System.out.println("Invalid! You need to type an integer!");
				scanner.nextLine();
			}

		} while ((chosenNumber < 0));

		return chosenNumber;
	}

	/**
	 * Allows to get any positive number from the user
	 *
	 * @param scanner
	 *            the scanner
	 * @return the positive number specified by the user
	 */
	private static double getPositiveDoubleFromUser(final Scanner scanner) {

		double chosenNumber = -1;

		do {
			System.out.println("Please enter a positive number");

			if (scanner.hasNextDouble()) {
				chosenNumber = scanner.nextDouble();
			} else {
				System.out.println("Invalid! You need to type a number!");
				scanner.nextLine();
			}

		} while ((chosenNumber < 0));

		return chosenNumber;
	}

	/**
	 * Returns a non-empty String filled by the user
	 *
	 * @param sc
	 *            the scanner
	 * @return a non-empty String filled by the user
	 */
	private String getNonEmptyStringFromUserInput(final Scanner sc) {

		String string = "";

		sc.nextLine();

		do {
			System.out.println("Please enter the string");

			string = sc.nextLine();

			if (string.isEmpty()) {
				System.out.println("Invalid! Empty string!");
			}

		} while (string.isEmpty());

		return string;
	}

	/* ****************** Actions **************************** */

	/**
	 * Allows to add a customisable order to a book selected by the user
	 *
	 * @param sc
	 *            the scanner
	 */
	public void addOrder(final Scanner sc) {
		System.out.println("Add an order to the book");
		System.out.println();

		// part 1 : select the book
		if (orderBookManager.getOrderBooks().isEmpty()) {
			System.out.println("There is no book - you cannot add an order.");
		} else {
			orderBookManager.displayOrderBooks();
			System.out.println("To which book would you like to add an order?");

			final int bookNumber = getPositiveIntegerFromUser(sc, orderBookManager.getOrderBooks().size());

			final OrderBook orderBook = orderBookManager.getOrderBooks().get(bookNumber);

			// part 2 : create the order
			if (orderBook.isOpen()) {
				final Order order = createOrder(sc);
				orderBookManager.getOrderBooks().get(bookNumber).addOrder(order);

			} else {
				System.out.println("It is not possible to add an order to a closed book!");
			}

		}
	}

	/**
	 * Allows to create a customisable order
	 *
	 * @param sc
	 *            the scanner
	 * @return the created order
	 */
	public Order createOrder(final Scanner sc) {
		System.out.println("Which type of order would you like to create?");
		System.out.println("0 - Market Order");
		System.out.println("1 - Limit Order");
		System.out.println();
		final int orderType = getPositiveIntegerFromUser(sc, orderBookManager.getOrderBooks().size());
		System.out.println("Specify quantity:");
		final int quantity = getPositiveIntegerFromUser(sc);

		Order order = null;

		switch (orderType) {
			case 0:
				order = new MarketOrder(quantity);
				break;

			case 1:
				System.out.println("Specify limit price:");
				final double limitPrice = getPositiveDoubleFromUser(sc);
				order = new LimitOrder(quantity, limitPrice);
				break;
		}

		return order;

	}

	/**
	 * Allows to add a customisable execution to a book selected by the user
	 *
	 * @param sc
	 */
	public void addExecution(final Scanner sc) {
		System.out.println("Add an execution to the book");
		System.out.println();

		// part 1 : select the book
		if (orderBookManager.getOrderBooks().isEmpty()) {
			System.out.println("There is no book - you cannot add an an execution.");
		} else {
			orderBookManager.displayOrderBooks();
			System.out.println("To which book would you like to add an execution?");

			final OrderBook orderBook = getOrderBookFromUser(sc);

			// part 2 : add the execution
			if (orderBook.isOpen()) {
				System.out.println("It is not possible to add an execution to an open book!");
			} else {

				Execution execution = null;

				// if there is no execution in the order, the price must be specified
				if (orderBook.getExecutions().isEmpty()) {
					execution = createExecution(sc);

					// if there is an execution in the order, get the price of the first one
					// (because all executions have the same price)
				} else {
					final double currentExecutionUnitPriceForThisBook = orderBook.getExecutions().get(0).getUnitPrice();
					execution = createExecution(sc, currentExecutionUnitPriceForThisBook);
				}

				orderBook.addExecution(execution);
			}

		}
	}

	/**
	 * returns the book selected by the user
	 *
	 * @param sc
	 *            the scanner
	 * @return the book selected by the user
	 */
	public OrderBook getOrderBookFromUser(final Scanner sc) {
		final int bookNumber = getPositiveIntegerFromUser(sc, orderBookManager.getOrderBooks().size());
		return orderBookManager.getOrderBooks().get(bookNumber);
	}

	/**
	 * Creates an execution - it should be only used if no other execution exists in
	 * the book
	 *
	 * @param sc
	 *            the scanner
	 * @return the created execution
	 */
	public Execution createExecution(final Scanner sc) {
		System.out.println("Specify the common unit price for all executions on this book?");
		final double unitPrice = getPositiveDoubleFromUser(sc);
		System.out.println("Specify quantity:");
		final int quantity = getPositiveIntegerFromUser(sc);
		return new Execution(quantity, unitPrice);
	}

	/**
	 * Creates an execution - it should be only used if there is already an
	 * execution existing in the book
	 *
	 * @param sc
	 *            the scanner
	 * @return the created execution
	 */
	public Execution createExecution(final Scanner sc, final double unitPrice) {
		System.out.println("Specify quantity:");
		final int quantity = getPositiveIntegerFromUser(sc);
		return new Execution(quantity, unitPrice);
	}

	/**
	 * Allows the user to select a book and open it.
	 *
	 * @param sc
	 *            the scanner
	 */
	public void openBook(final Scanner sc) {
		orderBookManager.displayOrderBooks();
		System.out.println("Which book would you like to open?");
		final OrderBook orderBook = getOrderBookFromUser(sc);

		if (orderBook.isOpen()) {
			System.out.println("The book is already open!");
		} else {
			if (orderBook.isWasAlreadyOpenedOnce()) {
				System.out.println("Cannot reopen a book!");
			} else {
				orderBook.setOpen(true);
				System.out.println("Order book opened");
			}
		}

	}

	/**
	 * Allows the user to select a book and close it.
	 *
	 * @param sc
	 *            the scanner
	 */
	public void closeBook(final Scanner sc) {
		orderBookManager.displayOrderBooks();
		System.out.println("Which book would you like to close?");
		final OrderBook orderBook = getOrderBookFromUser(sc);

		if (orderBook.isOpen()) {
			orderBook.setOpen(false);
			System.out.println("Order book closed");
		} else {
			System.out.println("The book is already closed!");
		}
	}

	/**
	 * Allows to select a book and to execute it
	 *
	 * @param sc
	 *            the scanner
	 */
	public void executeBook(final Scanner sc) {
		orderBookManager.displayOrderBooks();
		System.out.println("Which book would you like to execute?");
		final int bookNumber = getPositiveIntegerFromUser(sc, orderBookManager.getOrderBooks().size());
		final OrderBook orderBook = orderBookManager.getOrderBooks().get(bookNumber);

		if (orderBook.isOpen()) {
			System.out.println("You cannot execute an open book - there are no executions anyway");
		} else {
			orderBookManager.processBook(bookNumber);
		}
	}

	/**
	 * A function that prepares for statistics3: make the user choose which order he
	 * is interested in
	 *
	 * @param sc
	 *            the scanner
	 */
	public void prepareForStatistics3(final Scanner sc) {
		System.out.println("Enter the id associated to the order you are looking for:");
		final String orderId = getNonEmptyStringFromUserInput(sc);

		orderBookManager.printStatistics3(orderId);

	}

	/* ****************** Pure printing Functions ****************** */

	/**
	 * Welcomes the user
	 */
	private void printWelcome() {
		System.out.println("Welcome to the order books management System");
		System.out.println(
				"To navigate through the menu, select the number located on the left of the option that you want to choose");
		System.out.println("");
	}

	/**
	 * Prints the main menu
	 */
	private void printMainMenu() {
		System.out.println("MAIN MENU");
		System.out.println("1 - Add an order to a book");
		System.out.println("2 - Add an execution to a book");
		System.out.println("3 - Open a book");
		System.out.println("4 - Close a book");
		System.out.println("5 - Process executions for a book");
		System.out.println("6 - Print statistics");
		System.out.println("0 - exit");
	}

	/**
	 * Prints the statistics menu
	 */
	private void printStatisticsMenu() {
		System.out.println("STATISTICS MENU");
		System.out.println("1 - Print statistics 1");
		System.out.println(
				"=> for each book: amount of orders, demand, biggest / smallest / earliest / latest orders, limit break-down");
		System.out.println("2 - Print statistics 2");
		System.out.println(
				"=> for each book: amount of valid/invalid orders, amount of valid/invalid demand, biggest / smallest / earliest / latest orders, limit break-down, accumulated execution quantity, execution price");
		System.out.println("3 - Print statistics 3");
		System.out.println("=> for a given order id: validity, execution quantity, order's price, execution price");
		System.out.println("0 - Return to main menu");
	}

}
