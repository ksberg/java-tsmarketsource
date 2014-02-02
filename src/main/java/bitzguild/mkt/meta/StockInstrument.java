package bitzguild.mkt.meta;

import bitzguild.ts.event.BufferUtils;

public class StockInstrument extends BaseInstrument {

	/**
	 * Default Constructor
	 */
	public StockInstrument() {
		super("TEST","Test Stock","USD");
	}

	/**
	 * Convenience Constructor
	 * @param symbol
	 */
	public StockInstrument(String symbol, String description) {
		super(symbol,description,"USD");
	}
	
	/**
	 * Fully Parameterized Constructor
	 * @param symbol
	 */
	public StockInstrument(String symbol, String description, String units) {
		super(symbol,description,units);
	}
	
	public StockInstrument(StockInstrument that) {
		super(that);
	}
	
	protected StockInstrument thisOrCopy() {
		return new StockInstrument(this);
	}
	
	public StockInstrument with(String symbol, String description) {
		StockInstrument inst = thisOrCopy();
		inst._symbol = symbol;
		inst._description = description;
		return inst;
	}

	public StringBuffer toBuffer(StringBuffer sb) {
        sb.append('{');
        
        BufferUtils.pairToBuffer("symbol", _symbol, sb, true);
        BufferUtils.pairToBuffer("description", _description, sb, true);
        BufferUtils.pairToBuffer("pointVal", _bigPtVal, "#.0000", sb, true);
        BufferUtils.pairToBuffer("quoted", _quotedPts, "#.0000", sb, true);
        BufferUtils.pairToBuffer("traded", _pointIncr, "#.0000", sb, false);

        sb.append('}');
        return sb;
    }
}
