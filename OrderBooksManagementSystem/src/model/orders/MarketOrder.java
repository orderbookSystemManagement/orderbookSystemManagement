package model.orders;

/**
 * A Market Order is an order executed at the best available price. Therefore it
 * does not have a limit price specified, and hence, is always valid.
 *
 * @author Jules
 *
 */
public class MarketOrder extends Order {

	/**
	 * Constructor for a market order. Market Order is an order made at the best
	 * available price. Therefore it does not have a limit price specified, and
	 * hence, is always valid.
	 *
	 */
	public MarketOrder(final int requestedQuantity) {
		super(requestedQuantity);
		isValid = true;
	}

}
