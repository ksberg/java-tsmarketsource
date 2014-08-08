package bitzguild.mkt.event;

import bitzguild.ts.datetime.MutableDateTime;
import bitzguild.ts.event.TimeSpec;
import bitzguild.ts.event.TimeUnits;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;

public class Test4Quote {

    public static boolean VERBOSE = true;


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
    public void testCopy() {
        MutableDateTime dt = MutableDateTime.yearMonthDay(2014,5,24);
        Quote q1 = new ImmutableQuote(new TimeSpec(TimeUnits.MINUTE, 5), dt.rep(), "IBM", 50.0, 55.0, 45.0, 50, 987654321L);
        Quote q2 = q1.withSymbol("IBM");

        if (VERBOSE) {
            System.out.println(q1);
            System.out.println(q2);
        }

        assertTrue("DateTime",  q1.datetimeRep() == q2.datetimeRep());
        assertTrue("TimeSpec",  q1.timespecRep() == q2.timespecRep());
        assertTrue("Open",      q1.open() == q2.open());
        assertTrue("High",      q1.high() == q2.high());
        assertTrue("Low",       q1.low() == q2.low());
        assertTrue("Close",     q1.close() == q2.close());
        assertTrue("Volume",    q1.volume() == q2.volume());

    }

    @Test
    public void testRoundtripCsvFormat() {
        try {
            MutableDateTime dt = MutableDateTime.yearMonthDay(2014,5,24);
            dt.setHoursMinutesSeconds(9,30,25);
            Quote q1 = new ImmutableQuote(new TimeSpec(TimeUnits.HOUR, 2), dt.rep(), "IBM", 50.0, 55.0, 45.0, 50, 987654321L);
            StringBuffer sb = new StringBuffer();
            String csvRow = q1.toCSV();
            Quote q2 = q1.fromCSV(csvRow);

            if (VERBOSE) {
                System.out.println(q1);
                System.out.println(q2);
            }

            assertTrue("DateTime",  q1.datetimeRep() == q2.datetimeRep());
            assertTrue("TimeSpec",  q1.timespecRep() == q2.timespecRep());
            assertTrue("Open",      q1.open() == q2.open());
            assertTrue("High",      q1.high() == q2.high());
            assertTrue("Low",       q1.low() == q2.low());
            assertTrue("Close",     q1.close() == q2.close());
            assertTrue("Volume",    q1.volume() == q2.volume());

        } catch(ParseException p) {
           p.printStackTrace();
           fail("ParseException encountered");
        } catch(Exception e) {
            e.printStackTrace();
            fail("Unexpected exception on Quote parse");
        }
    }


}
