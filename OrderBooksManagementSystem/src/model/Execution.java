package model;

import java.util.UUID;

/**
 * An Execution is sent from a broker and, when it is processed, its quantity of
 * financial instrument is distributed among the valid orders.
 *
 *
 * @author Jules
 *
 */
public class Execution {

	/**
	 * The unique identifier, automatically generated
	 */
	private final UUID id = UUID.randomUUID();

	/**
	 * The quantity offered. Can differ from the actual satisfied quantity (e.g. if
	 * too few financial instrument units are supplied compared to the demand).
	 */
	private final int offeredQuantity;

	/**
	 * The price of a financial instrument unit
	 */
	private final double unitPrice;

	/**
	 * Constructor.
	 *
	 * @param offeredQuantity
	 *            the quantity supplied
	 * @param unitPrice
	 *            the price of a financial instrument unit
	 */
	public Execution(final int offeredQuantity, final double unitPrice) {
		this.offeredQuantity = offeredQuantity;
		this.unitPrice = unitPrice;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public UUID getId() {
		return id;
	}

	public int getOfferedQuantity() {
		return offeredQuantity;
	}

}
