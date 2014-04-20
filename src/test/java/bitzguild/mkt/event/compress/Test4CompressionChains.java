package bitzguild.mkt.event.compress;

import bitzguild.mkt.event.QuoteChain;
import bitzguild.ts.event.TimeSpec;
import bitzguild.ts.event.TimeUnits;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class Test4CompressionChains {

    public static boolean VERBOSE = false;
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testForTimeSpecFramesTick() {
        if (VERBOSE) System.out.println("--------------------"); // Explicit listing of all TimeUnits
        compareFrames(true, new TimeSpec(TimeUnits.TICK,1), new TimeSpec(TimeUnits.MINUTE, 1));
        compareFrames(true, new TimeSpec(TimeUnits.TICK,1), new TimeSpec(TimeUnits.SECOND,1));
        compareFrames(true, new TimeSpec(TimeUnits.TICK,1), new TimeSpec(TimeUnits.HOUR,1));
        compareFrames(true, new TimeSpec(TimeUnits.TICK,1), new TimeSpec(TimeUnits.DAY,1));
        compareFrames(true, new TimeSpec(TimeUnits.TICK,1), new TimeSpec(TimeUnits.WEEK,1));
        compareFrames(true, new TimeSpec(TimeUnits.TICK,1), new TimeSpec(TimeUnits.MONTH,1));
        compareFrames(true, new TimeSpec(TimeUnits.TICK,1), new TimeSpec(TimeUnits.YEAR,1));
        compareFrames(true, new TimeSpec(TimeUnits.TICK,1), new TimeSpec(TimeUnits.DECADE,1));
        compareFrames(true, new TimeSpec(TimeUnits.TICK,1), new TimeSpec(TimeUnits.CENTURY,1));

        if (VERBOSE) System.out.println("--------------------");
        for(int units = TimeUnits.TICK+1; units <= TimeUnits.CENTURY; units++) {
            compareNotFramedBy(new TimeSpec(TimeUnits.TICK, 1), new TimeSpec(units, 1));
        }
    }

    @Test
    public void testForTimeSpecFramesFromSecondToDay() {
        if (VERBOSE) System.out.println("====================");
        for(int unitsA = TimeUnits.SECOND; unitsA <= TimeUnits.DAY; unitsA++) {
            if (VERBOSE) System.out.println("--------------------");
            for(int unitsB = unitsA+1; unitsB <= TimeUnits.CENTURY; unitsB ++) {
                compareFrames(true, new TimeSpec(unitsA, 1), new TimeSpec(unitsB, 1));
            }
        }
    }

    @Test
    public void testForTimeSpecFramesFromMonthOn() {
        if (VERBOSE) System.out.println("==================== COMPARE FRAMES (a) to (b) ");
        for(int unitsA = TimeUnits.MONTH; unitsA < TimeUnits.CENTURY; unitsA++) {
            if (VERBOSE) System.out.println("--------------------");
            for(int unitsB = unitsA+1; unitsB <= TimeUnits.CENTURY; unitsB ++) {
                compareFrames(true, new TimeSpec(unitsA, 1), new TimeSpec(unitsB, 1));
            }
        }
    }

    @Test
    public void testForTimeSpecSecondMultiples() {
        if (VERBOSE) System.out.println("--------------------");
        compareFrames(true, new TimeSpec(TimeUnits.SECOND,3), new TimeSpec(TimeUnits.SECOND,30));

        compareFrames(true, new TimeSpec(TimeUnits.SECOND,5), new TimeSpec(TimeUnits.SECOND,5));
        compareFrames(true, new TimeSpec(TimeUnits.SECOND,5), new TimeSpec(TimeUnits.SECOND,10));
        compareFrames(true, new TimeSpec(TimeUnits.SECOND,5), new TimeSpec(TimeUnits.SECOND,15));
        compareFrames(true, new TimeSpec(TimeUnits.SECOND,5), new TimeSpec(TimeUnits.SECOND,30));

        compareFrames(true, new TimeSpec(TimeUnits.SECOND,10), new TimeSpec(TimeUnits.SECOND,10));
        compareFrames(true, new TimeSpec(TimeUnits.SECOND,10), new TimeSpec(TimeUnits.SECOND,30));

        compareFrames(true, new TimeSpec(TimeUnits.SECOND,15), new TimeSpec(TimeUnits.SECOND,30));
    }

    @Test
    public void testForTimeSpecSecondInvalidMultiples() {
        if (VERBOSE) System.out.println("-------------------- ");
        compareFrames(false, new TimeSpec(TimeUnits.SECOND,10), new TimeSpec(TimeUnits.SECOND,15));
        compareFrames(false, new TimeSpec(TimeUnits.SECOND,25), new TimeSpec(TimeUnits.SECOND,30));
        compareFrames(false, new TimeSpec(TimeUnits.SECOND,90), new TimeSpec(TimeUnits.MINUTE,3));

        compareFrames(false, new TimeSpec(TimeUnits.WEEK,1), new TimeSpec(TimeUnits.MONTH,1));
    }

    @Test
    public void testForTimeSpecSpecificMultiples() {
        if (VERBOSE) System.out.println("-------------------- ");
        compareFrames(true, new TimeSpec(TimeUnits.MINUTE,30), new TimeSpec(TimeUnits.HOUR,1));
    }





    @Test
    public void testCompressionChains() {
        Tick2Quotes ticker = new Tick2Quotes();
        ticker
                .feeds(new Compress2Minutes(1))
                .feeds(new Compress2Minutes(5))
                .feeds(new Compress2Minutes(10))
                .feeds(new Compress2Minutes(30))
                .feeds(new Compress2Hours(1))
                .feeds(new Compress2Days(1))
                .feeds(new Compress2Weeks(1));
        checkFeedConsistency(true, ticker.chain());
    }

    @Test
    public void testCompressionChainsWithIncompatibleMinutes() {
        Tick2Quotes ticker = new Tick2Quotes();
        ticker
                .feeds(new Compress2Minutes(1))
                .feeds(new Compress2Minutes(5))
                .feeds(new Compress2Minutes(10))
                .feeds(new Compress2Minutes(15))
                .feeds(new Compress2Minutes(30))
                .feeds(new Compress2Hours(1))
                .feeds(new Compress2Days(1));
        checkFeedConsistency(false, ticker.chain());
    }

    @Test
    public void testCompressionChainsWithRepeats() {
        Tick2Quotes ticker = new Tick2Quotes();
        ticker
                .feeds(new Compress2Seconds(1))
                .feeds(new Compress2Minutes(1))
                .feeds(new Compress2Minutes(5))
                .feeds(new Compress2Minutes(5))
                .feeds(new Compress2Minutes(10))
                .feeds(new Compress2Minutes(30))
                .feeds(new Compress2Hours(1))
                .feeds(new Compress2Hours(1))
                .feeds(new Compress2Days(1))
                .feeds(new Compress2Weeks(1));
        checkFeedConsistency(true, ticker.chain());
    }

    @Test
    public void testCompressionChainsWithWeekAndMonth() {
        Tick2Quotes ticker = new Tick2Quotes();
        ticker
                .feeds(new Compress2Hours(1))
                .feeds(new Compress2Days(1))
                .feeds(new Compress2Weeks(1))
                .feeds(new Compress2Months(1));
        checkFeedConsistency(false, ticker.chain());
    }

    @Test
    public void testCompressionChainsOutOfOrder() {
        Tick2Quotes ticker = new Tick2Quotes();
        ticker
                .feeds(new Compress2Seconds(1))
                .feeds(new Compress2Minutes(1))
                .feeds(new Compress2Minutes(5))
                .feeds(new Compress2Minutes(10))
                .feeds(new Compress2Days(1))
                .feeds(new Compress2Hours(1))
                .feeds(new Compress2Weeks(1))
                .feeds(new Compress2Months(1));
        checkFeedConsistency(false, ticker.chain());
    }




    public void checkFeedConsistency(boolean expectSuccess, QuoteChain root) {
        ArrayList<QuoteCompression> list = CompressUtil.list(root);
        StringBuilder strb = new StringBuilder("CHAIN FEED: ");
        for(QuoteCompression c : list) strb.append(c.getCompression()).append(" > ");
        if (!list.isEmpty()) strb.setLength(strb.length()-3);
        if (VERBOSE) {
            System.out.println("\n---------------------");
            System.out.println(strb.toString());
            System.out.println("CHAIN IS CONSISTENT: " + CompressUtil.consistent(list) + " EXPECT: " + expectSuccess);
        } else {
            assertTrue(strb.toString(),CompressUtil.consistent(list) == expectSuccess);
        }
    }

    public void compareFrames(boolean expectSuccess, TimeSpec a, TimeSpec b) {
        if (VERBOSE) {
            System.out.println(a + " frames    " + b + " => " + a.frames(b));
        } else {
            assertTrue(a + " frames " + b, a.frames(b) == expectSuccess);
        }
    }

    public void compareNotFramedBy(TimeSpec a, TimeSpec b) {
        if (VERBOSE) {
            System.out.println(a + " framed by " + b + " => " + a.framedBy(b));
        } else {
            assertTrue(a + " framed by " + b, a.frames(b));
        }
    }

}
