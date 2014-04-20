package bitzguild.mkt.event.compress;

import bitzguild.mkt.event.*;
import bitzguild.mkt.event.gen.TickGenerator;
import bitzguild.ts.datetime.DateTimeIterator;
import bitzguild.ts.datetime.MutableDateTime;
import bitzguild.ts.event.TimeSpec;
import bitzguild.ts.event.gen.DoubleGenerator;
import bitzguild.mkt.io.QuoteCollector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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


    /**
     * Ensure 1 second past end of day is sufficient to trigger
     * update for final hour when filtered through lower frame.
     * Fixes end of chain frame waiting on prior frame to trigger.
     * (e.g. ensure 24:00:00.000 is triggered at 24:00:00.001,
     * not 24:30:00.000 for example below).
     */
    @Test
    public void test1DayHourlyCompressionFromSecondTicks() {
        TickGenerator tg = tickGeneratorForDate(2014,4,17,everySecond);
        Tick2Quotes ticker = new Tick2Quotes();
        QuoteCollector collector = new QuoteCollector();

        ticker
                .feeds(new Compress2Minutes(30))
                .feeds(new Compress2Hours(1).connect(collector));

        long seconds = TimeSpecHelper.SecondsInDay + 1;
        for(long i=0; i<seconds; i++) { ticker.update(tg.next()); }

        assertEquals("Expected 24 hourly quotes for 1 day", collector.quotes.size(), 24);

        //for(Quote q : collector.quotes) System.out.println(q);

    }

    @Test
    public void testCascadingCompressionFromTicksOnWeekday() {

        TickGenerator tg = tickGeneratorForDate(2014,4,16,everySecond); // A Wednesday
        Tick2Quotes ticker = new Tick2Quotes();
        QuoteCollector minute01 = new QuoteCollector();
        QuoteCollector minute05 = new QuoteCollector();
        QuoteCollector minute15 = new QuoteCollector();
        QuoteCollector minute30 = new QuoteCollector();
        QuoteCollector hours = new QuoteCollector();
        QuoteCollector days = new QuoteCollector();

        ticker
            .feeds(new Compress2Minutes(1).connect(minute01))
            .feeds(new Compress2Minutes(5).connect(minute05))
            .feeds(new Compress2Minutes(15).connect(minute15))
            .feeds(new Compress2Minutes(30).connect(minute30))
            .feeds(new Compress2Hours(1).connect(hours))
            .feeds(new Compress2Days(1).connect(days));

        long seconds = TimeSpecHelper.SecondsInDay + 1;

        for(long i=0; i<seconds; i++) { ticker.update(tg.next()); }

        checkFeedResult("1-Minute events in 1 day",     minute01.quotes.size(), 60 * 24);
        checkFeedResult("5-Minute events in 1 day",     minute05.quotes.size(), 60 * 24 / 5);
        checkFeedResult("15-Minute events in 1 day",    minute15.quotes.size(), 60 * 24 / 15);
        checkFeedResult("30-Minute events in 1 day",    minute30.quotes.size(), 60 * 24 / 30);
        checkFeedResult("1-Hour events in 1 day",       hours.quotes.size(), 24);
        checkFeedResult("1-Day events in 1 day",        days.quotes.size(), 1);

//        System.out.println("\nSpecifics on 15-Minutes");
//        for(Quote q : minute15.quotes) System.out.println(q);
    }

    /**
     * Test lower resolution bars where feed updates may not be as frequent.
     * Filler Quotes are time frames missing from feed that are inserted into
     * the listener stream to maintain consistent TimeSpec. The behavior is
     * that missing Quotes should have zero volume and OHLC same as close
     * of the prior bar.
     */
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

        double lastClose = 0.0;
        for(Quote q : collector.quotes) {
            System.out.println(t + " vs " + q.datetime() + " from " + q);
            assertTrue("DateTime must increment by TimeSpec units", t.rep() == q.datetimeRep());
            if (t.seconds() == 0) {
                assertTrue("Feed Quote should have non-zero volume",q.volume() > 0);
                lastClose = q.close();
            } else {
                assertTrue("Fill Quote should have zero volume", q.volume() == 0);
                assertTrue("Fill Quote open should equal close", q.open() == q.close());
                assertTrue("Fill Quote high should equal low", q.high() == q.low());
                assertTrue("Fill Quote open should equal last close", q.open() == lastClose);
            }
            t.addSeconds(frame);
        }
    }


    @Test
    public void testOverWeekendFrames() {

        TickGenerator tg = tickGeneratorForDate(2014,4,18,everySecond); // A Friday
        Tick2Quotes ticker = new Tick2Quotes();

        ticker
            .feeds(new Compress2Minutes(1).connect(new QuoteCollector()))
            .feeds(new Compress2Minutes(5).connect(new QuoteCollector()))
            .feeds(new Compress2Minutes(15).connect(new QuoteCollector()))
            .feeds(new Compress2Minutes(30).connect(new QuoteCollector()))
            .feeds(new Compress2Hours(1).connect(new QuoteCollector()))
            .feeds(new Compress2Days(1).connect(new QuoteCollector()));

        int ndays = 2;
        long seconds = ndays * TimeSpecHelper.SecondsInDay + 1;

        for(long i=0; i<seconds; i++) { ticker.update(tg.next()); }

        if (VERBOSE) System.out.println("\nFriday then Saturday, Total Seconds = " + seconds);
        TimeSpecHelper timeSpecHelper = new TimeSpecHelper();
        List<QuoteCompression> compressions = CompressUtil.list(ticker.chain());
        for(QuoteCompression comp : compressions) {
            QuoteCollector coll = (QuoteCollector)comp.listener();
            TimeSpec spec = comp.getCompression();
            long shouldbe = timeSpecHelper.secondsPerSpec(comp.getCompression(),seconds);
            checkFeedResultsDays(ndays, spec, coll.quotes.size(), (int)shouldbe);
        }
    }

    @Test
    public void testGapOverWeekendFrames() {

        TickGenerator tg1 = tickGeneratorForDate(2014,4,18,everySecond); // A Friday
        TickGenerator tg2 = tickGeneratorForDate(2014,4,21,everySecond); // A Monday
        Tick2Quotes ticker = new Tick2Quotes();

        ticker
                .feeds(new Compress2Minutes(1).connect(new QuoteCollector()))
                .feeds(new Compress2Minutes(5).connect(new QuoteCollector()))
                .feeds(new Compress2Minutes(15).connect(new QuoteCollector()))
                .feeds(new Compress2Minutes(30).connect(new QuoteCollector()))
                .feeds(new Compress2Hours(1).connect(new QuoteCollector()))
                .feeds(new Compress2Days(1).connect(new QuoteCollector()));

        int ndays = 2;
        long seconds = TimeSpecHelper.SecondsInDay + 1;

        for(long i=0; i<seconds-1; i++) { ticker.update(tg1.next()); }
        for(long i=0; i<seconds; i++) { ticker.update(tg2.next()); }

        int totalseconds = ((int)seconds * 2 - 1);
        if (VERBOSE) System.out.println("\nFriday then Monday Total Seconds = " + totalseconds);
        TimeSpecHelper timeSpecHelper = new TimeSpecHelper();
        List<QuoteCompression> compressions = CompressUtil.list(ticker.chain());
        for(QuoteCompression comp : compressions) {
            QuoteCollector coll = (QuoteCollector)comp.listener();
            TimeSpec spec = comp.getCompression();
            long shouldbe = timeSpecHelper.secondsPerSpec(comp.getCompression(),totalseconds);
            checkFeedResultsDays(ndays, spec, coll.quotes.size(), (int)shouldbe);
        }
    }


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

    public void checkFeedResultsDays(int ndays, TimeSpec spec, int valA, int valB) {
        checkFeedResult("Total " + spec.toString() + " events in " + ndays + (ndays > 1 ? " days":" day"),valA,valB);
    }
    public void checkFeedResult(String msg, int valA, int valB) {
        if (VERBOSE) System.out.println(msg + " was " + valA + " expected " + valB);
        else assertEquals(msg,valA,valB);
    }


}
