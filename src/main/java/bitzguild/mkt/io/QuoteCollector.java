package bitzguild.mkt.io;

import bitzguild.mkt.event.ImmutableQuote;
import bitzguild.mkt.event.Quote;
import bitzguild.mkt.event.QuoteChain;
import bitzguild.mkt.event.QuoteListener;

import java.util.ArrayList;

public class QuoteCollector implements QuoteListener {
    public ArrayList<Quote> quotes;
    public QuoteChain next;

    public QuoteCollector() {
        quotes = new ArrayList<>();
        next = QuoteChain.TERMINAL;
    }

    @Override
    public void update(Quote q) {
        quotes.add(new ImmutableQuote(q));
    }
}
