package bitzguild.mkt.event.compress;

import bitzguild.mkt.event.*;
import bitzguild.mkt.event.gen.TickGenerator;
import bitzguild.ts.datetime.DateTimeIterator;
import bitzguild.ts.datetime.DateUtil;
import bitzguild.ts.datetime.MutableDateTime;
import bitzguild.ts.event.gen.DoubleGenerator;
import bitzguild.mkt.io.QuoteCollector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class Test4Compression {

    public static boolean VERBOSE = false;

    protected static final int everySecond = 1;
    protected static final int every90Seconds = 90;
    protected static final int every240Seconds = 240;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // Test Week/Weekend boundaries, especially for gaps and Day or greater
    // Test gaps that span underlying unit

    // ----------------------------------------------------------------------
    // Tests
    // ----------------------------------------------------------------------



    @Test
    public void test1DayHourlyCompressionFromSecondTicks() {
        TickGenerator tg = tickGeneratorForDate(2014,4,17,everySecond);
        CompressPassthrough root = new CompressPassthrough();
        Tick2Quotes ticker = new Tick2Quotes(root);
        QuoteCollector collector = new QuoteCollector();

        ticker
            .feeds(root)
            .feeds(new Compress2Hours(1).connect(collector));

        long seconds = TimeSpecHelper.SecondsInDay + 1;
        for(long i=0; i<seconds; i++) { ticker.update(tg.next()); }

        assertEquals("Expected 24 hourly quotes for 1 day", collector.quotes.size(), 24);

        //for(Quote q : collector.quotes) System.out.println(q);

    }

    @Test
    public void testCascadingCompressionFromTicksOnWeekday() {

        TickGenerator tg = tickGeneratorForDate(2014,4,16,everySecond); // A Wednesday
        CompressPassthrough root = new CompressPassthrough();
        Tick2Quotes ticker = new Tick2Quotes(root);
        QuoteCollector minute01 = new QuoteCollector();
        QuoteCollector minute05 = new QuoteCollector();
        QuoteCollector minute10 = new QuoteCollector();
        QuoteCollector minute15 = new QuoteCollector();
        QuoteCollector minute30 = new QuoteCollector();
        QuoteCollector hours = new QuoteCollector();
        QuoteCollector days = new QuoteCollector();

        ticker
            .feeds(root)
            .feeds(new Compress2Minutes(1).connect(minute01))
            .feeds(new Compress2Minutes(5).connect(minute05))
            .feeds(new Compress2Minutes(15).connect(minute15))
            .feeds(new Compress2Minutes(30).connect(minute30))
            .feeds(new Compress2Hours(1).connect(hours))
            .feeds(new Compress2Days(1).connect(days));

        long seconds = TimeSpecHelper.SecondsInDay + 1;

        for(long i=0; i<seconds; i++) { ticker.update(tg.next()); }

        printOrAssert(VERBOSE, "1-Minute events in 1 day",    minute01.quotes.size(), 60*24);
        printOrAssert(VERBOSE, "5-Minute events in 1 day",    minute05.quotes.size(), 60*24 / 5);
        printOrAssert(VERBOSE, "15-Minute events in 1 day",   minute15.quotes.size(), 60*24 / 15);
        printOrAssert(VERBOSE, "30-Minute events in 1 day",   minute30.quotes.size(), 60*24 / 30);
        printOrAssert(VERBOSE, "1-Hour events in 1 day",      hours.quotes.size(), 24);
        printOrAssert(VERBOSE, "1-Day events in 1 day",       days.quotes.size(), 1);

        System.out.println("\nSpecifics on 15-Minutes");
        for(Quote q : minute15.quotes) System.out.println(q);
    }


    @Test
    public void testSecondFrameGaps() {
        int factor = 60;
        int frame = 15; // seconds
        TickGenerator tg = tickGeneratorForDate(2014,4,16,factor); // A Wednesday
        Tick2Quotes ticker = new Tick2Quotes();
        QuoteCollector collector = new QuoteCollector();
        ticker.feeds(new Compress2Seconds(frame).connect(collector));

        long times = (2 * TimeSpecHelper.SecondsInMinute / factor) + 2;
        for(long i=0; i<times; i++) { ticker.update(tg.next()); }

        assertTrue(collector.quotes.size() > 0);

        Quote first = collector.quotes.get(0);
        MutableDateTime t = new MutableDateTime(first.datetimeRep());

        for(Quote q : collector.quotes) {
            // System.out.println(t + " vs " + q.datetime() + " from " + q);
            assertTrue("DateTime must increment by TimeSpec units", t.rep() == q.datetimeRep());
            t.addSeconds(frame);
        }

        System.out.println("TEST quotes = " + collector.quotes.size());

//        printOrAssert(false, "30-Second events in 1 hour",    collector.quotes.size(), 60*60 / 30);

        // verify time increment is monotonic increasing
        // according to timespec
    }

    /*
    @Test
    public void test1DayCompressionFromSecondTicks() {

        TickGenerator tg = tickGeneratorForDate(2014,4,17);
        CompressPassthrough root = new CompressPassthrough();
        Tick2Quotes ticker = new Tick2Quotes(root);
        QuoteCollector days = new QuoteCollector();
        Compress2Days compressor = new Compress2Days(1);

        ticker
                .feeds(root)
                .feeds(compressor.connect(days));

        long seconds = TimeSpecHelper.SecondsInDay + 1;

        for(long i=0; i<seconds-2; i++) { ticker.update(tg.next()); }

        Tick t = tg.next();
        System.out.println("TICK = " + t);
        ticker.update(t);
        System.out.println("Snapshot = " + compressor.snapshot());

        t = tg.next();
        System.out.println("TICK = " + t);
        ticker.update(t);
        System.out.println("Snapshot = " + compressor.snapshot());

        MutableDateTime dt = new MutableDateTime();
        dt.setRep(compressor._nextDateTimeFrame);
        System.out.println("Compressor Next DT = " + dt.toString());

        System.out.println("1-Day    = " + days.quotes.size() + ", Expected = " + 1);


        //System.out.println(days.quotes.get(days.quotes.size()-1));

        //assertEquals("Expected 1 daily quotes for 1 day", days.quotes.size(), 1);
    }
    */

    // ----------------------------------------------------------------------
    // Test Fixtures
    // ----------------------------------------------------------------------

    public TickGenerator tickGeneratorForDate(int year, int month, int day, int everyNSeconds) {
        MutableDateTime tickDT = new MutableDateTime(year,month,day);
        tickDT.setMillisSinceMidnight(0);
        DateTimeIterator tGen = new DateTimeIterator(tickDT, DateTimeIterator.seconds(everyNSeconds));
        Tick proto = new MutableTick("IBV");
        DoubleGenerator dGen = DoubleGenerator.curve(20, 100.0, 200.0);
        return new TickGenerator(proto, dGen, tGen);
    }


    public void printOrAssert(boolean whether, String msg, int valA, int valB) {
        if (whether) assertEquals(msg,valA,valB);
        else System.out.println(msg + " was " + valA + " expected " + valB);
    }

/*
    System.out.println("Total Quotes = " + collector.quotes.size());
    for(Quote q : collector.quotes) {
       System.out.println(q);
    }
*/

}
