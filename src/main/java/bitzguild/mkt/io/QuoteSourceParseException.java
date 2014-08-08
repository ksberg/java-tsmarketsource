package bitzguild.mkt.io;

public class QuoteSourceParseException extends QuoteSourceException {

    public QuoteSourceParseException(Throwable cause, String symbol) {
        super(cause,symbol);
    }
}
