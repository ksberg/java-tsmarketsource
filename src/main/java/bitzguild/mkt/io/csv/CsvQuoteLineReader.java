package bitzguild.mkt.io.csv;

import bitzguild.io.LineReader;
import bitzguild.mkt.event.ImmutableQuote;
import bitzguild.mkt.event.Quote;

import java.text.ParseException;
import java.util.IllegalFormatException;

public class CsvQuoteLineReader implements LineReader<Quote> {


    protected Quote _quote;
    public CsvQuoteLineReader() {
        _quote = new ImmutableQuote();
    }

    public CsvQuoteLineReader(Quote prototype) {
        _quote = prototype;
    }

    @Override
    public Quote read(String line) throws ParseException {
        Quote q = null;
        try {
            q = _quote.fromCSV(line);
        } catch(ParseException pe) {
            System.err.println("LINE: " + line + " - cause " + pe.getMessage());
            throw pe;
        } catch(Throwable t) {
            throw new ParseException("LINE: " + line + " - cause " + t.getMessage(),0);
        }
        return q;
    }

    @Override
    public void readHeader(String line) throws ParseException {
        // ignored
    }

    @Override
    public boolean expectHeader() {
        return true;
    }
}
