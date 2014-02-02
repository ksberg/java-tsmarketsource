package bitzguild.mkt.meta;

public interface MarketInstrument {
	
	public String 	symbol();				// Ticker Symbol: e.g. APPL
	public String	description();			// Long Description: Apple Computer, Inc.
	public String 	units();				// Traded Units
	public String	category();				// Stock, Option, Future, Bond, Currency
	
	public double 	pointValue();			// Money value of 1.0 price move
	public double 	quotedIncrement();		// Quoted trade increments  
	public double 	minimumIncrement();		// Minimum trade increments
	public int 		unitsPerTrade();		// Stocks = 1, Options = 100, etc.
	
	public double	unitCost(double price);	// Funds required, calculation varies by instrument
	
	public boolean 	expires();
	
}
