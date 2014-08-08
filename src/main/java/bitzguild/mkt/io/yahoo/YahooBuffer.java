package bitzguild.mkt.io.yahoo;

import bitzguild.io.Updater;
import bitzguild.mkt.event.ImmutableQuote;
import bitzguild.mkt.event.Quote;

import java.util.ArrayList;

/**
 * Yahoo CSV lines are streamed in date descending order.
 * This buffer captures Quotes and reverses the replay to
 * output receiver.
 *
 * @author Kevin Sven Berg
 *
 */
public class YahooBuffer implements Updater<Quote> {
    ArrayList<Quote> _buffer;
    Updater<Quote>		_output;
    public YahooBuffer() {
        _buffer = new ArrayList<Quote>();
    }
    public void update(Quote value) {
        _buffer.add(new ImmutableQuote(value));
    }
    public void reverse(Updater<Quote> output) {
        int size = _buffer.size();
        for (int i=size-1; i>=0; i--) output.update(_buffer.get(i));
    }
}
