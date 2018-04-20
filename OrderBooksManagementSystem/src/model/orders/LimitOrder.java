package model.orders;

/**
 * A Limit Order is an order only executed if its limit price is higher than the
 * current market value. Therefore its limit price needs to be specified.
 *
 * @author Jules
 *
 */
public class LimitOrder extends Order {

	/**
	 * The limit price of an order (i.e. the maximum that a purchaser is willing to
	 * pay)
	 *
	 */
	private final double limitPrice;

	/**
	 * Constructor. A Limit Order is an order only executed if its price is higher
	 * than the execution value. Therefore its limit price needs to be specified.
	 *
	 */
	public LimitOrder(final int quantity, final double limitPrice) {
		super(quantity);
		this.limitPrice = limitPrice;
	}

	public double getLimitPrice() {
		return limitPrice;
	}

}
