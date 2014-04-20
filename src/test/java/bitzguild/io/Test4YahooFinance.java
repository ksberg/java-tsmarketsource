package bitzguild.io;

import bitzguild.mkt.event.Quote;
import bitzguild.mkt.io.QuoteCollector;
import bitzguild.mkt.io.QuoteSourceException;
import bitzguild.mkt.io.yahoo.YahooFinance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Test4YahooFinance {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    @Test
    public void testIt() {
        YahooFinance yahoo = new YahooFinance("AAPL");
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
