package bitzguild.mkt.io;

import bitzguild.mkt.event.QuoteChain;

public interface QuoteSource {
	public QuoteChain open(QuoteChain chain) throws QuoteSourceException;
    public void close();
}
