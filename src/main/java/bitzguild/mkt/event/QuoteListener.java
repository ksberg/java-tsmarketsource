package bitzguild.mkt.event;

import bitzguild.io.Updater;

public interface QuoteListener extends Updater<Quote> {

	/**
	 * Quote event notification. Consumer should assume mutable
	 * content and take or copy content as appropriate. Implementation
	 * can pass either mutable or immutable Quote.
	 * 
	 * @param q
	 */
	public void update(Quote q);
}
