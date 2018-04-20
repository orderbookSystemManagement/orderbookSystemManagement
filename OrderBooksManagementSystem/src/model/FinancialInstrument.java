package model;

import java.util.UUID;

/**
 * A financial instrument can be a share, an option, a future, etc..
 *
 *
 * @author Jules
 *
 */
public class FinancialInstrument {

	/**
	 * The unique identifier
	 */
	private final UUID instrumentID = UUID.randomUUID();

	/**
	 * The name
	 */
	private final String name;

	/**
	 * Default constructor
	 */
	public FinancialInstrument(final String name) {
		this.name = name;
	}

	public UUID getInstrumentID() {
		return instrumentID;
	}

	public String getName() {
		return name;
	}

}
