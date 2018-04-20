package customexceptions;

/**
 * A list of exceptions messages printed when something not authorised happens
 * (not exception is actually thrown)
 *
 *
 * @author Jules
 *
 */
public enum OrderBookExceptionCode {

	ADD_ORDER_ON_CLOSED_BOOK_EXCEPTION_MESSAGE("It is not possible to add an order on a closed book!"),

	ADD_EXECUTION_ON_OPEN_BOOK_EXCEPTION_MESSAGE("It is not possible to add an execution on an open book!"),

	ADD_EXECUTION_ON_PROCESSED_BOOK("It is not possible to add an execution when the book has already been processed!");

	private String exceptionMessage;

	OrderBookExceptionCode(final String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public String exceptionMessage() {
		return exceptionMessage;
	}

}
