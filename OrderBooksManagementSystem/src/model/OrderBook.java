package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import customexceptions.OrderBookExceptionCode;
import model.orders.LimitOrder;
import model.orders.MarketOrder;
import model.orders.Order;

/**
 * The book contains a list of orders and a list of executions that will be
 * matched
 *
 *
 * @author Jules
 *
 */
public class OrderBook {

	/**
	 * Whether the book is open or not. If the book is open, orders can be added but
	 * execution cannot be added. On the other hand, if the book is close, orders
	 * cannot be added but execution can be added.
	 *
	 */
	private boolean isOpen;

	/**
	 * Stores whether the book was already open at some point in time. Since a book
	 * cannot be reopened, this attribute is useful.
	 */
	private boolean wasAlreadyOpenedOnce = false;

	/**
	 * Stores whether a process of executions already happened in the past. I
	 * assumed that it is not possible to add new executions after a book is
	 * processed, therefore this attribute is useful.
	 */
	private boolean areExecutionsProcessed = false;

	/**
	 * The financial instrument "traded" in the order book
	 */
	private final FinancialInstrument financialInstrument;

	/**
	 * The list of executions received from brokers into the book
	 */
	private final ArrayList<Execution> executions = new ArrayList<Execution>();

	/**
	 * The list of orders
	 */
	private final ArrayList<Order> orders = new ArrayList<Order>();

	/**
	 * Constructor
	 *
	 * @param financialInstrument
	 *            the financial instrument
	 */
	public OrderBook(final FinancialInstrument financialInstrument) {
		this.financialInstrument = financialInstrument;
	}

	/**
	 * Adds an execution to the book if 1) it is open 2) the book was never
	 * processed before AND 3) the new quantity offered by the execution will not
	 * make the new total execution amount bigger than the demand
	 *
	 * @param execution
	 *            the execution to be added
	 */
	public void addExecution(final Execution execution) {

		// cannot add an execution on an open book
		if (!isOpen) {
			// cannot add an execution if the book was already processed
			if (!areExecutionsProcessed) {
				final int demand = getDemand();
				final int currentTotalExecutionOffer = getTotalExecutionOffer();
				final int possibleExecutionQuantityLeft = demand - currentTotalExecutionOffer;

				// If adding the new execution would make the total book demand lower than the
				// total
				// execution offer, do not add the execution
				if (execution.getOfferedQuantity() > possibleExecutionQuantityLeft) {
					System.out.println("It is not possible to offer more than " + possibleExecutionQuantityLeft);
					System.out.println("Current demand " + demand + ", current total execution offer: "
							+ currentTotalExecutionOffer);
					System.out.println("The execution was not added.");
				} else {
					executions.add(execution);

					// if the execution inserted is the first one, limit orders with limit price
					// lower than execution price must become invalid (since all execution have the
					// same price, only the first one matters)
					if (executions.size() == 1) {
						validateAppropriateOrders();
					}

					// after adding the execution, the executions must be processed if total valid
					// book demand = total execution offer
					final int newTotalExecutionOffer = currentTotalExecutionOffer + getDemandOfValidOrders();
					if (newTotalExecutionOffer == demand) {
						processExecutions();
					}
				}
			} else {
				System.out.println(OrderBookExceptionCode.ADD_EXECUTION_ON_PROCESSED_BOOK.exceptionMessage());
			}

		} else {
			System.out.println(OrderBookExceptionCode.ADD_EXECUTION_ON_OPEN_BOOK_EXCEPTION_MESSAGE.exceptionMessage());
		}
	}

	/**
	 * Returns the total execution offer
	 *
	 * @return totalExecutionOffer the total execution offer
	 */
	public int getTotalExecutionOffer() {
		int totalExecutionOffer = 0;
		for (final Execution execution : executions) {
			totalExecutionOffer += execution.getOfferedQuantity();
		}
		return totalExecutionOffer;
	}

	/**
	 * Gets the execution price. The execution price is the same for all executions,
	 * therefore it is either 0 (if the execution list is empty) or the unit price
	 * of the first execution (if the execution list is not empty)
	 *
	 * @return the execution price
	 */
	public double getExecutionPrice() {
		return (executions.isEmpty() ? 0 : executions.get(0).getUnitPrice());
	}

	/**
	 * Adds the order to the book. It is possible to add the order only if the book
	 * is open.
	 *
	 * @param order
	 *            the order to be added
	 */
	public void addOrder(final Order order) {
		if (isOpen) {
			orders.add(order);
		} else {
			System.out.println(OrderBookExceptionCode.ADD_ORDER_ON_CLOSED_BOOK_EXCEPTION_MESSAGE.exceptionMessage());
		}
	}

	/**
	 * Validates all orders that have a limit price (therefore the Limit Orders)
	 * bigger than or equal to the unit price offered in the executions (all
	 * executions have the same price). Invalid all order that have a limit price
	 * (therefore the Limit Orders) lower than the unit price offered
	 */
	private void validateAppropriateOrders() {
		if (!executions.isEmpty()) {
			final double offeredExecutionPrice = executions.get(0).getUnitPrice();
			for (final Order order : orders) {
				if (order instanceof LimitOrder) {
					if (((LimitOrder) order).getLimitPrice() >= offeredExecutionPrice) {
						order.setValid(true);
					} else {
						order.setValid(false);
					}
				}
			}
		}
	}

	/* ********************** functions used in stats *********************** */

	/**
	 * Returns the total amount of orders in the book
	 *
	 * It is assumed that the list of orders is not null
	 *
	 * @return the number of orders in the book
	 */
	public int getTotalAmountOfOrders() {
		return orders.size();
	}

	/**
	 * Returns the total demand in the book
	 *
	 * It is assumed that the list of orders is not null
	 *
	 * @return demand the total demand in the book
	 */
	public int getDemand() {

		int demand = 0;
		for (final Order order : orders) {
			demand += order.getRequestedQuantity();
		}
		return demand;
	}

	/**
	 * Returns the biggest order in the book
	 *
	 * It is assumed that the list of orders is not null
	 *
	 * To simplify, I did not treat the case where there can be multiple biggest
	 * orders
	 *
	 * @return biggestOrder the biggest order
	 */
	public Order getBiggestOrder() {

		Order biggestOrder = null;

		if (!orders.isEmpty()) {
			biggestOrder = orders.get(0);
			for (int i = 1; i < orders.size(); i++) {
				if (orders.get(i).getRequestedQuantity() > biggestOrder.getRequestedQuantity()) {
					biggestOrder = orders.get(i);
				}
			}
		}

		return biggestOrder;
	}

	/**
	 * Returns the smallest order in the book
	 *
	 * It is assumed that the list of orders is not null
	 *
	 * To simplify, I did not treat the case where there can be multiple smallest
	 * orders
	 *
	 * @return smallestOrder the smallest order
	 */
	public Order getSmallestOrder() {

		Order smallestOrder = null;

		if (!orders.isEmpty()) {
			smallestOrder = orders.get(0);
			for (int i = 1; i < orders.size(); i++) {
				if (orders.get(i).getRequestedQuantity() < smallestOrder.getRequestedQuantity()) {
					smallestOrder = orders.get(i);
				}
			}
		}

		return smallestOrder;
	}

	/**
	 * Returns the earliest order in the book
	 *
	 * It is assumed that the list of orders is not null
	 *
	 * To simplify, I did not treat the case where there can be multiple earliest
	 * orders
	 *
	 * @return earliestOrder the earliest order
	 */
	public Order getEarliestOrder() {

		Order earliestOrder = null;

		if (!orders.isEmpty()) {
			earliestOrder = orders.get(0);
			for (int i = 1; i < orders.size(); i++) {
				if (orders.get(i).getEntryDate().before(earliestOrder.getEntryDate())) {
					earliestOrder = orders.get(i);
				}
			}
		}

		return earliestOrder;
	}

	/**
	 * Returns the latest order in the book
	 *
	 * It is assumed that the list of orders is not null
	 *
	 * To simplify, I did not treat the case where there can be multiple latest
	 * orders
	 *
	 * @return earliestOrder the earliest order
	 */
	public Order getLatestOrder() {

		Order latestOrder = null;

		if (!orders.isEmpty()) {
			latestOrder = orders.get(0);
			for (int i = 1; i < orders.size(); i++) {
				if (orders.get(i).getEntryDate().after(latestOrder.getEntryDate())) {
					latestOrder = orders.get(i);
					// if the date is equal (happens if orders were created very close to each
					// other), then the one further in the list is the latest
				} else if (orders.get(i).getEntryDate().equals(latestOrder.getEntryDate())) {
					latestOrder = orders.get(i);
				}
			}
		}

		return latestOrder;
	}

	/**
	 * Returns the list of limit orders within the list of orders
	 *
	 * @return the list of limit orders
	 */
	public ArrayList<LimitOrder> getLimitOrders() {
		final ArrayList<LimitOrder> limitOrders = new ArrayList<LimitOrder>();
		for (final Order order : orders) {
			if (order instanceof LimitOrder) {
				limitOrders.add((LimitOrder) order);
			}
		}
		return limitOrders;
	}

	/**
	 * Returns the list of market orders within the list of orders
	 *
	 * @return the list of market orders
	 */
	public ArrayList<MarketOrder> getMarketOrders() {
		final ArrayList<MarketOrder> marketOrders = new ArrayList<MarketOrder>();
		for (final Order order : orders) {
			if (order instanceof MarketOrder) {
				marketOrders.add((MarketOrder) order);
			}
		}
		return marketOrders;
	}

	/**
	 * Returns a map, with the key = limit price, and the value = the total demand
	 * for this limit price
	 *
	 * @return demandPerLimitPrice
	 */
	public HashMap<Double, Integer> getDemandPerLimitPrice() {
		final HashMap<Double, Integer> demandPerLimitPrice = new HashMap<>();

		final ArrayList<LimitOrder> limitOrders = getLimitOrders();

		for (final LimitOrder order : limitOrders) {
			if (demandPerLimitPrice.containsKey(order.getLimitPrice())) {
				final int newDemandForGivenLimitPrice = demandPerLimitPrice.get(order.getLimitPrice())
						+ order.getRequestedQuantity();
				demandPerLimitPrice.put(order.getLimitPrice(), newDemandForGivenLimitPrice);
			} else {
				demandPerLimitPrice.put(order.getLimitPrice(), order.getRequestedQuantity());
			}
		}

		return demandPerLimitPrice;
	}

	/**
	 * Gets the amount/number of invalid orders
	 *
	 * @return amountOfInvalidOrders the amount/number of invalid orders
	 */
	public int getAmountOfInvalidOrders() {
		int amountOfInvalidOrders = 0;
		for (final Order order : orders) {
			if (!order.isValid()) {
				amountOfInvalidOrders++;
			}
		}
		return amountOfInvalidOrders;
	}

	/**
	 * Gets the amount/number of valid orders
	 *
	 * @return amountOfValidOrders the amount/number of valid orders
	 */
	public int getAmountOfValidOrders() {
		int amountOfValidOrders = 0;
		for (final Order order : orders) {
			if (order.isValid()) {
				amountOfValidOrders++;
			}
		}
		return amountOfValidOrders;
	}

	/**
	 * Gets the total demand of invalid orders
	 *
	 * @return demandOfInvalidOrders the amount/number of invalid orders
	 */
	public int getDemandOfInvalidOrders() {
		int demandOfInvalidOrders = 0;
		for (final Order order : orders) {
			if (!order.isValid()) {
				demandOfInvalidOrders += order.getRequestedQuantity();
			}
		}
		return demandOfInvalidOrders;
	}

	/**
	 * Gets the total demand of valid orders
	 *
	 * @return demandOfValidOrders the amount/number of valid orders
	 */
	public int getDemandOfValidOrders() {
		int demandOfValidOrders = 0;
		for (final Order order : orders) {
			if (order.isValid()) {
				demandOfValidOrders += order.getRequestedQuantity();
			}
		}
		return demandOfValidOrders;
	}

	/**
	 * Processes the list of executions
	 */
	public void processExecutions() {

		final ArrayList<Order> validOrders = getValidOrders();

		for (final Execution execution : executions) {
			distributeExecutionAmongOrders(execution, validOrders);
		}

		areExecutionsProcessed = true;
	}

	/**
	 * Returns the list of valid orders
	 *
	 * @return validOrders the list of valid orders
	 */
	public ArrayList<Order> getValidOrders() {

		final ArrayList<Order> validOrders = new ArrayList<Order>();

		for (final Order order : orders) {
			if (order.isValid()) {
				validOrders.add(order);
			}
		}

		return validOrders;
	}

	/**
	 * Distributes the execution offer between the offers, proportionately to the
	 * amount requested in the offer
	 *
	 * @param execution
	 *            the execution to distribute
	 * @param validOrders
	 *            the valid orders which will potentially get units of the execution
	 */
	public void distributeExecutionAmongOrders(final Execution execution, final ArrayList<Order> validOrders) {

		int remainingQuantityToDistribute = execution.getOfferedQuantity();

		int quantityToAllocateForCurrentOrder = 0;

		// I made the 3 variables double in order not to lose precision when doing the
		// division
		double requestedOrderQuantity = 0;
		double demand = 0;
		double executionOffer = 0;

		// Proportionate distribution, i.e. the more you order, the more you get if
		// there is insufficient execution offer to satisfy everyone
		for (final Order order : validOrders) {

			requestedOrderQuantity = (order.getRequestedQuantity());
			demand = getDemand();
			executionOffer = execution.getOfferedQuantity();

			// floor, because units cannot be divided
			quantityToAllocateForCurrentOrder = (int) Math.floor((requestedOrderQuantity * executionOffer) / demand);

			// this cannot happen here, because I did not allow to have a total execution
			// offer bigger than the total demand
			if (quantityToAllocateForCurrentOrder > requestedOrderQuantity) {
				quantityToAllocateForCurrentOrder = (int) requestedOrderQuantity;
			}
			order.setSatisfiedQuantity(quantityToAllocateForCurrentOrder);
			remainingQuantityToDistribute -= quantityToAllocateForCurrentOrder;
		}

		// Distributing the few units left that have not been distributed yet
		while (remainingQuantityToDistribute != 0) {

			for (final Order order : validOrders) {

				if (order.getRequestedQuantity() == order.getSatisfiedQuantity()) {
					// do nothing, cannot sell more than asked!
				} else {
					if (remainingQuantityToDistribute != 0) {
						order.setSatisfiedQuantity(order.getSatisfiedQuantity() + 1);
						remainingQuantityToDistribute--;
					}
				}

			}

		}

	}

	/**
	 * Gets an order by id
	 *
	 * @param orderId
	 *            the id of the searched order
	 * @return searchedOrder the order
	 */
	private Order getOrderById(final String orderId) {
		Order searchedOrder = null;
		for (final Order order : orders) {
			if (orderId.equals(order.getId().toString())) {
				searchedOrder = order;
				break;
			}
		}
		return searchedOrder;
	}

	/*
	 * ****************** Printing functions *******************************
	 */

	/**
	 * Header for statistics
	 */
	public void printStatisticsIntro() {
		System.out
				.println("Statistics for Order book related to financial instrument " + financialInstrument.getName());
		;
		System.out.println("-----------------------------------------------------------------------------");

	}

	/**
	 * Adds a space separation after the statistics of the book
	 */
	public void printStatisticsOutro() {
		for (int i = 0; i < 4; i++) {
			System.out.println();
		}
	}

	/**
	 * Used when no records are found
	 */
	public void printNoRecordFound() {
		System.out.println("No record found.");
	}

	/**
	 * Prints an order
	 *
	 * @param rowTitle
	 *            the row title
	 * @param order
	 *            the order to be printed
	 */
	public void printOrder(final String rowTitle, final Order order) {
		System.out.printf("%15s %40s %25s %25s %30s %10s", rowTitle, order.getId(), order.getRequestedQuantity(),
				order.getSatisfiedQuantity(), order.getEntryDate(), order.isValid());
		System.out.println();
	}

	/**
	 * Prints particular orders: biggest, smallest, earliest, latest
	 */
	public void printParticularOrders() {
		System.out.println("Characteristics of particular orders:");
		System.out.println(
				"----------------------------------------------------------------------------------------------------------------------------------------------------------");
		System.out.printf("%15s %40s %25s %25s %30s %10s", "", "ID |", "REQUESTED QUANTITY |", "SATISFIED QUANTITY |",
				"ENTRY DATE |", "IS VALID |");
		System.out.println();
		System.out.println(
				"----------------------------------------------------------------------------------------------------------------------------------------------------------");

		if (orders.isEmpty()) {
			printNoRecordFound();
		} else {
			printOrder("Biggest order:", getBiggestOrder());
			printOrder("Smallest order:", getSmallestOrder());
			printOrder("Earliest order:", getEarliestOrder());
			printOrder("Latest order:", getLatestOrder());
		}

		System.out.println();
		System.out.println();
	}

	/**
	 * Print the limit break-down, ie the demand per limite price
	 */
	public void printLimitBreakDown() {
		final HashMap<Double, Integer> demandPerLimitPrice = getDemandPerLimitPrice();

		System.out.println("Limit break down: demand per limit price");
		System.out.println("---------------------------------------------------");
		System.out.printf("%15s %10s", "LIMIT PRICE |", "DEMAND |");
		System.out.println();
		System.out.println("---------------------------------------------------");

		if (demandPerLimitPrice.isEmpty()) {
			printNoRecordFound();
		} else {
			for (final Map.Entry<Double, Integer> entry : demandPerLimitPrice.entrySet()) {
				System.out.format("%15s %10s", entry.getKey(), entry.getValue());
				System.out.println();
			}
		}
	}

	/**
	 * Prints the first set of information - amount of orders, demand, biggest /
	 * smallest / earliest / latest orders, limit break-down
	 */
	public void printStatistics1() {
		printStatisticsIntro();
		System.out.println("Total amount of orders: " + getTotalAmountOfOrders());
		System.out.println("Demand: " + getDemand());
		System.out.println();
		printParticularOrders();
		printLimitBreakDown();
		printStatisticsOutro();
	}

	/**
	 * Prints the second set of information - amount of valid/invalid orders, amount
	 * of valid/invalid demand, biggest / smallest / earliest / latest orders, limit
	 * break-down, accumulated execution quantity, execution price
	 */
	public void printStatistics2() {
		printStatisticsIntro();

		System.out.println("Total amount of valid orders: " + getAmountOfValidOrders());
		System.out.println("Total amount of invalid orders: " + getAmountOfInvalidOrders());
		System.out.println("Total demand of valid orders: " + getDemandOfValidOrders());
		System.out.println("Total demand of invalid orders: " + getDemandOfInvalidOrders());
		System.out.println();

		printParticularOrders();
		printLimitBreakDown();

		System.out.println("Total execution quantity: " + getTotalExecutionOffer());
		System.out.println("Total execution price: " + getExecutionPrice());

		printStatisticsOutro();
	}

	/**
	 * Prints the third set of information - for a given order id: validity,
	 * execution quantity, order's price, execution price
	 *
	 * @param uuid
	 */
	public void printStatistics3(final String uuid) {

		printStatisticsIntro();

		final Order order = getOrderById(uuid);

		if (order != null) {
			System.out.println("Valid: " + order.isValid());
			System.out.println("Execution quantity (=satisfied quantity): " + order.getSatisfiedQuantity());
			System.out.println("Order price: " + (areExecutionsProcessed ? executions.get(0).getUnitPrice() : 0));
			System.out.println("Execution price: "
					+ (areExecutionsProcessed ? (order.getSatisfiedQuantity() * executions.get(0).getUnitPrice()) : 0));
		}

		printStatisticsOutro();
	}

	public FinancialInstrument getFinancialInstrument() {
		return financialInstrument;
	}

	public ArrayList<Execution> getExecutions() {
		return executions;
	}

	public ArrayList<Order> getOrders() {
		return orders;
	}

	public boolean isWasAlreadyOpenedOnce() {
		return wasAlreadyOpenedOnce;
	}

	public void setWasAlreadyOpenedOnce(final boolean wasAlreadyOpenedOnce) {
		this.wasAlreadyOpenedOnce = wasAlreadyOpenedOnce;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(final boolean isOpen) {
		this.isOpen = isOpen;
		wasAlreadyOpenedOnce = true;
	}

}
