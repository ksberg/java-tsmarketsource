package bitzguild.mkt.event.gen;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bitzguild.mkt.event.Tick;
import bitzguild.mkt.event.gen.TickGenerator;

public class Test4TickGen {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    @Test
    public void testDefaultTickGenerator() {
    	TickGenerator tg = new TickGenerator();
    	
    	for(int i=0; i<100; i++) {
    		Tick t = tg.next();
    		System.out.println(t.toString());
    	}
    	assertTrue("bogus",true);
    }
    
    
}
