package bitzguild.mkt.io.csv;

import bitzguild.mkt.event.Quote;
import bitzguild.mkt.io.QuoteCollector;
import bitzguild.mkt.io.QuoteSourceException;
import bitzguild.mkt.io.yahoo.YahooNet;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class Test4CsvQuoteFile {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


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

    @Test
    public void testEnvironment() {
        try {
            CsvQuoteFile cqf = new CsvQuoteFile(getSamplePath() + "/ESR1.csv","ES");
            QuoteCollector collector = new QuoteCollector();
            cqf.open(collector);
            for(Quote q : collector.quotes) System.out.println(q);
        } catch(Throwable t) {
            t.printStackTrace();
        }

    }

    public String getSamplePath() {
        String path = "";
        try {
            File f = new File(".");
            path = f.getCanonicalPath() + "/samples/mkt/csv";
        } catch(Throwable t) {
            t.printStackTrace();
        }
        return path;
    }
}
