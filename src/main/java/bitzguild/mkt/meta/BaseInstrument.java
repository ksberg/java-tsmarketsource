package bitzguild.mkt.meta;

public class BaseInstrument implements MarketInstrument {

	protected String	_symbol;
	protected String	_description;
	protected String	_units;
	protected double	_bigPtVal;
	protected double	_quotedPts;
	protected double	_pointIncr;
	
	/**
	 * Default Constructor
	 */
	public BaseInstrument() {
		_bigPtVal 	= 1.0;
		_quotedPts 	= 0.01;
		_pointIncr 	= 0.01;
	}
	
	/**
	 * Fully Parameterized Constructor
	 * @param symbol
	 */
	public BaseInstrument(String symbol, String description, String units) {
		_symbol = symbol;
		_description = description;
		_units = units;
		
		_bigPtVal 	= 1.0;
		_quotedPts 	= 0.01;
		_pointIncr 	= 0.01;
	}

	/**
	 * Copy Constructor
	 * 
	 * @param that
	 */
	public BaseInstrument(BaseInstrument that) {
		this._symbol		= that._symbol;
		this._description	= that._description;
		this._units			= that._units;
		this._bigPtVal		= that._bigPtVal;
		this._quotedPts		= that._quotedPts;
		this._pointIncr		= that._pointIncr;
	}
	
	
	public String symbol() 					{ return _symbol; }
	public String description() 			{ return _description; }
	public String units() 					{ return _units; }
	public String category() 				{ return "Stock"; }
	public double pointValue() 				{ return _bigPtVal; }
	public double quotedIncrement() 		{ return _quotedPts; }
	public double minimumIncrement() 		{ return _pointIncr; }
	public int unitsPerTrade() 				{ return 1; }
	public double unitCost(double price) 	{ return price; }
	public boolean expires() 				{ return false; }

	
	
	public StringBuffer toBuffer(StringBuffer strb) {
		return strb;
	}
	
	public String toString() {
		return toBuffer(new StringBuffer()).toString();
	}
}
