package bitzguild.mkt.io;

import java.io.PrintStream;

import bitzguild.mkt.event.Quote;
import bitzguild.mkt.event.QuoteChain;

public class QuotePrinter implements QuoteChain {

	protected PrintStream _ps;
	
	public QuotePrinter() 				{ _ps = System.out; }
	public QuotePrinter(PrintStream ps) { _ps = ps;}
	
	public void update(Quote q) {	_ps.println(q.toString()); }
	public QuoteChain feeds(QuoteChain other) { return null; }
	public void close() {}

}
