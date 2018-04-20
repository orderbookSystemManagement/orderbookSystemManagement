package model;

import java.util.ArrayList;

import model.orders.LimitOrder;
import model.orders.MarketOrder;
import model.orders.Order;

/**
 * Manages a list of books
 *
 *
 * @author Jules
 *
 */
public class OrderBookManager {

	ArrayList<OrderBook> orderBooks = new ArrayList<OrderBook>();

	/**
	 * A simple initialisation function to have some books filled with orders and
	 * executions when the program starts
	 */
	public void init() {

		// contains 2 MarketOrders, 2 LimitOrders, no execution, and is open when the
		// program starts
		final FinancialInstrument fi1 = new FinancialInstrument("A");
		final OrderBook orderBook1 = new OrderBook(fi1);
		orderBook1.setOpen(true);
		orderBook1.addOrder(new MarketOrder(20));
		orderBook1.addOrder(new MarketOrder(15));
		orderBook1.addOrder(new LimitOrder(50, 20));
		orderBook1.addOrder(new LimitOrder(30, 10));
		orderBooks.add(orderBook1);

		// contains 0 MarketOrders, 2 LimitOrders, no execution, and is open when the
		// program starts
		final FinancialInstrument fi2 = new FinancialInstrument("B");
		final OrderBook orderBook2 = new OrderBook(fi2);
		orderBook2.setOpen(true);
		orderBook2.addOrder(new LimitOrder(40, 10));
		orderBook2.addOrder(new LimitOrder(20, 5));
		orderBooks.add(orderBook2);

		// contains 2 MarketOrders, 0 LimitOrders, no execution, and is open when the
		// program starts
		final FinancialInstrument fi3 = new FinancialInstrument("C");
		final OrderBook orderBook3 = new OrderBook(fi3);
		orderBook3.setOpen(true);
		orderBook3.addOrder(new LimitOrder(40, 10));
		orderBook3.addOrder(new LimitOrder(20, 5));
		orderBooks.add(orderBook3);

		// contains no orders, and is closed when the program starts
		final FinancialInstrument fi4 = new FinancialInstrument("C");
		final OrderBook orderBook4 = new OrderBook(fi4);
		orderBooks.add(orderBook4);

		// contains 2 market orders, 1 invalid limit order, 1 valid limit order, 1
		// execution, and its closed when the program starts. The book is not executed
		// yet: a book is automatically executed only when demand meets offer
		final FinancialInstrument fi5 = new FinancialInstrument("D");
		final OrderBook orderBook5 = new OrderBook(fi5);
		orderBook5.setOpen(true);
		orderBook5.addOrder(new MarketOrder(12));
		orderBook5.addOrder(new MarketOrder(15));
		orderBook5.addOrder(new LimitOrder(2, 25));
		orderBook5.addOrder(new LimitOrder(2, 15));
		orderBook5.setOpen(false);
		orderBook5.addExecution(new Execution(10, 20));
		orderBooks.add(orderBook5);

		// contains 2 market orders, 1 valid limit order, 1
		// execution, and its closed when the program starts. Total demand = total
		// execution offer, therefore it is automatically executed!
		final FinancialInstrument fi6 = new FinancialInstrument("E");
		final OrderBook orderBook6 = new OrderBook(fi6);
		orderBook6.setOpen(true);
		orderBook6.addOrder(new MarketOrder(16));
		orderBook6.addOrder(new MarketOrder(16));
		orderBook6.addOrder(new LimitOrder(10, 26));
		orderBook6.setOpen(false);
		orderBook6.addExecution(new Execution(40, 20));
		orderBooks.add(orderBook6);

	}

	/**
	 * Processes a book
	 *
	 * @param orderBookPosition
	 */
	public void processBook(final int orderBookPosition) {
		orderBooks.get(orderBookPosition).processExecutions();
	}

	/**
	 * Prints the first set of information - for each book: amount of orders,
	 * demand, biggest / smallest / earliest / latest orders, limit break-down
	 */
	public void printStatistics1() {
		for (final OrderBook orderBook : orderBooks) {
			orderBook.printStatistics1();
		}
	}

	/**
	 * Prints the second set of information - for each book: amount of valid/invalid
	 * orders, amount of valid/invalid demand, biggest / smallest / earliest /
	 * latest orders, limit break-down, accumulated execution quantity, execution
	 * price
	 */
	public void printStatistics2() {
		for (final OrderBook orderBook : orderBooks) {
			orderBook.printStatistics2();
		}
	}

	/**
	 * Prints the third set of information - for a given order id: validity,
	 * execution quantity, order's price, execution price
	 */
	public void printStatistics3(final String orderId) {

		boolean orderExists = false;
		for (final OrderBook orderBook : orderBooks) {
			for (final Order order : orderBook.getOrders()) {
				if (order.getId().toString().equals(orderId)) {
					orderExists = true;
					orderBook.printStatistics3(order.getId().toString());
					break;
				}
			}

			if (orderExists) {
				break;
			}
		}

		if (!orderExists) {
			System.out.println("The id that you entered is not associated to any order of any book.");
		}
	}

	/**
	 * Prints the books held in the book manager
	 */
	public void displayOrderBooks() {
		System.out.println("LIST OF BOOKS:");
		if (orderBooks.isEmpty()) {
			System.out.println("There is no book.");
		} else {
			for (int i = 0; i < orderBooks.size(); i++) {
				System.out.println(i + " - Order book for financial instrument: "
						+ orderBooks.get(i).getFinancialInstrument().getName());
			}
		}
	}

	public ArrayList<OrderBook> getOrderBooks() {
		return orderBooks;
	}

}
