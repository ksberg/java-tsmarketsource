package bitzguild.mkt.io;

import java.io.PrintStream;

import bitzguild.mkt.event.Tick;
import bitzguild.mkt.event.TickObserver;

public class TickPrinter implements TickObserver {

	protected PrintStream _ps;
	
	public TickPrinter() 				{ _ps = System.out; }
	public TickPrinter(PrintStream ps) 	{ _ps = ps;}
	
	public void update(Tick tick) {
		_ps.println(tick.toString());
	}
	
	

}
