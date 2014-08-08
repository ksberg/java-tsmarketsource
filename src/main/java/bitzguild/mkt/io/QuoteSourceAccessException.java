package bitzguild.mkt.io;

public class QuoteSourceAccessException extends QuoteSourceException {

    public QuoteSourceAccessException(Throwable cause, String symbol) {
        super(cause, symbol);
    }

}
