package bitzguild.mkt.io;

import bitzguild.mkt.io.QuoteSourceException;

public class QuoteSourceNotFoundException extends QuoteSourceException {

    public QuoteSourceNotFoundException(Throwable cause, String symbol) {
        super(cause, symbol);
    }

}
