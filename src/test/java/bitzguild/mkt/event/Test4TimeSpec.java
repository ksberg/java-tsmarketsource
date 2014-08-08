package bitzguild.mkt.event;

import bitzguild.ts.event.TimeSpec;
import bitzguild.ts.event.TimeUnits;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Test4TimeSpec {

    public static final boolean VERBOSE = true;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    // ----------------------------------------------------------------------
    // Tests
    // ----------------------------------------------------------------------


    @Test
    public void testRoundTripParse() {

        for (int iunits=TimeUnits.MIN_UNITS; iunits<=TimeUnits.MAX_UNITS; iunits++) {
            TimeSpec specA = new TimeSpec(iunits,3);
            String str = specA.toString() + " ";
            TimeSpec specB = TimeSpec.parse(str);

            assertEquals("length",specA.length, specB.length);
            assertEquals("units",specA.units, specB.units);
            if (VERBOSE) System.out.println(specA + " == " + specB);
        }
    }

    @Test
    public void testRoundTripRep() {
        for (int iunits=TimeUnits.MIN_UNITS; iunits<=TimeUnits.MAX_UNITS; iunits++) {
            TimeSpec specA = new TimeSpec(iunits,3);
            long repA = specA.rep();
            TimeSpec specB = new TimeSpec(repA);

            if (VERBOSE) System.out.println(specA.rep() + " == " + specB.rep() + " ... " + specA + " vs " + specB);

            assertEquals("length",specA.length, specB.length);
            assertEquals("units",specA.units, specB.units);
        }

    }
}
