package bitzguild.mkt.meta;

import bitzguild.ts.datetime.DateTime;
import bitzguild.ts.datetime.DateTimePredicate;
import bitzguild.ts.datetime.ImmutableDateTime;
import bitzguild.ts.datetime.MutableDateTime;
import bitzguild.ts.event.BufferUtils;

public class FutureInstrument extends BaseInstrument implements MarginInstrument {

	protected double	_margin;
	protected double	_maintMargin;
	protected long		_lastTradingDay;
	protected long		_firstNoticeDay;
	
	/**
	 * Default Constructor
	 */
	public FutureInstrument() {
		super("FUTR","Test Future","USD");
		_margin = 1000.0;
		_maintMargin = 1000.0;
		dummyFndLtd();
	}

	/**
	 * Parameterized Constructor
	 * @param symbol
	 */
	public FutureInstrument(String symbol, double bigPt, double quoted, double mintrade) {
		super(symbol,symbol + " Future","USD");
		this._bigPtVal = bigPt;
		this._quotedPts = quoted;
		this._pointIncr = mintrade;
		_margin = 1000.0;
		_maintMargin = 1000.0;
		dummyFndLtd();
	}
	
	/**
	 * Copy Constructor
	 * 
	 * @param that
	 */
	public FutureInstrument(FutureInstrument that) {
		super(that);
		this._margin = that._margin;
		this._maintMargin = that._maintMargin;
		this._lastTradingDay = that._lastTradingDay;
		this._firstNoticeDay = that._firstNoticeDay;
	}
	
	private void dummyFndLtd() {
		MutableDateTime dt = MutableDateTime.now();
		dt.setMillisSinceMidnight(0);
		dt.addYears(100);
		_lastTradingDay = dt.rep();
		_firstNoticeDay = _lastTradingDay;
	}
	
	protected FutureInstrument thisOrCopy() {
		return new FutureInstrument(this);
	}
	
	public FutureInstrument with(String symbol, String description) {
		FutureInstrument inst = thisOrCopy();
		inst._symbol = symbol;
		inst._description = description;
		return inst;
	}

	public FutureInstrument withMargins(double margin, double maintenace) {
		FutureInstrument inst = thisOrCopy();
		inst._margin = margin;
		inst._maintMargin = maintenace;
		return inst;
	}

	
	public StringBuffer toBuffer(StringBuffer sb) {
        sb.append('{');
        
        BufferUtils.pairToBuffer("symbol", _symbol, sb, true);
        BufferUtils.pairToBuffer("description", _description, sb, true);
        BufferUtils.pairToBuffer("pointVal", _bigPtVal, "#.0000", sb, true);
        BufferUtils.pairToBuffer("quoted", _quotedPts, "#.0000", sb, true);
        BufferUtils.pairToBuffer("traded", _pointIncr, "#.0000", sb, false);

        BufferUtils.pairToBuffer("margin", _margin, "#.00", sb, true);
        BufferUtils.pairToBuffer("maint", _maintMargin, "#.00", sb, true);

        BufferUtils.datetimeToBuffer("fnd", _firstNoticeDay, sb);
        BufferUtils.datetimeToBuffer("ltd", _lastTradingDay, sb);
        
        sb.append('}');
        return sb;
    }

	
	public double unitCost(double price) 	{ return _margin; }
	
	
	public DateTime lastTradingDay() {
		return new ImmutableDateTime(_lastTradingDay);
	}

	public DateTime firstNoticeDay() {
		return new ImmutableDateTime(_firstNoticeDay);
	}

	public int tradingDaysToExpiration(DateTime d, DateTimePredicate holidays) {
		return 0;
	}

	public double margin() {
		return _margin;
	}

	public double maintenance() {
		return _maintMargin;
	}
	
	
	
}
