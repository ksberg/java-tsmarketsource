package bitzguild.mkt.event;

import bitzguild.ts.datetime.DateTime;

public interface Tick {

    public DateTime datetime();
    public long datetimeRep();
	
    public String 	symbol();   	// reference market symbol (generic)
    public char		exchange();		// Exchange symbol (single character)
	public double 	price();		// price at transaction
	public long 	volume();		// number of units involved in sale

	
    public Tick withSymbol(String symbol);
    public Tick withExchange(char exchange);
	
	/**
	 * Answer related Tick with given properties. Maintains symbol and exchange.
	 * 
	 * @param datetime long
	 * @param price double
	 * @param volume long
	 * @return Tick
	 */
    public Tick with(long datetime, double price, long volume);
	
}
