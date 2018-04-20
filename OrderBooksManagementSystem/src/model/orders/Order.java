package model.orders;

import java.util.Date;
import java.util.UUID;

/**
 * Abstract mother class for all types of orders
 *
 * @author Jules
 *
 */
public abstract class Order {

	/**
	 * The unique identifier, automatically generated
	 */
	protected final UUID id = UUID.randomUUID();

	/**
	 * The quantity (= units amount) of financial instruments requested
	 */
	protected int requestedQuantity;

	/**
	 * The quantity actually sold to the purchaser of the order
	 */
	protected int satisfiedQuantity = 0;

	/**
	 * The entry date on an order book. I assumed that it is the same as the order
	 * creation time (after all, the order is added to the book right after being
	 * created).
	 */
	protected final Date entryDate = new Date();

	/**
	 * Whether an order is valid or not. "The order is invalid if it has a limit
	 * price and the price is lower than execution price."
	 */
	protected boolean isValid;

	/**
	 * Constructor, used in inheriting classes.
	 *
	 * @param requestedQuantity
	 *            the quantity (= units amount) of financial instruments requested
	 *            for this order
	 *
	 */
	public Order(final int requestedQuantity) {
		this.requestedQuantity = requestedQuantity;
	}

	public int getSatisfiedQuantity() {
		return satisfiedQuantity;
	}

	public void setSatisfiedQuantity(final int satisfiedQuantity) {
		this.satisfiedQuantity = satisfiedQuantity;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(final boolean isValid) {
		this.isValid = isValid;
	}

	public UUID getId() {
		return id;
	}

	public int getRequestedQuantity() {
		return requestedQuantity;
	}

	public Date getEntryDate() {
		return entryDate;
	}

}
