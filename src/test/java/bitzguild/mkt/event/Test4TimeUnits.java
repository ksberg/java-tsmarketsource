package bitzguild.mkt.event;

import bitzguild.ts.event.TimeUnits;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;

public class Test4TimeUnits {

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
    public void testStringUnits() {

        for (int i=TimeUnits.MIN_UNITS; i<=TimeUnits.MAX_UNITS; i++) {
            String str = TimeUnits.getUnitStr(i);
            int units = TimeUnits.unitsFromString(str);
            if (VERBOSE) System.out.println(units + " == " + str);
            assertEquals("TimeUnits",i,units);
        }
    }
}
