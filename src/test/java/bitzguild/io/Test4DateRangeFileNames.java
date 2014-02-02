package bitzguild.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bitzguild.mkt.io.DateRangeFileNames;
import bitzguild.ts.datetime.DateTime;
import bitzguild.ts.datetime.DateTimeRange;
import bitzguild.ts.datetime.MutableDateTime;

public class Test4DateRangeFileNames {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    @Test
    public void testDefault() {
    	DateTime dateTo = MutableDateTime.now();
    	DateTime dateFrom = (new MutableDateTime(dateTo)).addYears(-10);
    	DateTimeRange range = new DateTimeRange(dateFrom,dateTo);
    	
    	DateRangeFileNames fileNames = new DateRangeFileNames(range,"/tmp/subdir",".csv");
    	while(fileNames.hasNext()) System.out.println(fileNames.next());
    	
    }
}
