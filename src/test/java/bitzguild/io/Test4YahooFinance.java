package bitzguild.io;

import bitzguild.mkt.event.Quote;
import bitzguild.mkt.io.QuoteCollector;
import bitzguild.mkt.io.QuoteSourceException;
import bitzguild.mkt.io.yahoo.YahooNet;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class Test4YahooFinance {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // revise this to mock URL

    @Ignore
    public void testIt() {
        YahooNet yahoo = new YahooNet("AAPL");
        try {
            QuoteCollector collector = new QuoteCollector();
            yahoo.open(collector);
            for(Quote q : collector.quotes) System.out.println(q);
        } catch (QuoteSourceException qse) {
            qse.printStackTrace();
        } finally {
            yahoo.close();
        }


    }
}
