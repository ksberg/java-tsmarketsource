package bitzguild.mkt.event;

import bitzguild.ts.event.AbstractTimeEvent;
import bitzguild.ts.event.BinnedTimeEvent;
import bitzguild.ts.event.TimeSpec;


public class ImmutableQuote extends AbstractTimeEvent implements Quote {

    public static final String NoSymbol = "N/A";

    protected String    _symbol;
    protected double    _open;
    protected double    _high;
    protected double    _low;
    protected double    _close;
    protected long      _volume;
    

    /**
     * Default Zero Constructor
     */
    public ImmutableQuote() {
        super();
        _symbol = NoSymbol;
        _open   = 0.0;
        _high   = 0.0;
        _low    = 0.0;
        _close  = 0.0;
        _volume = 0L;
    }

    /**
     * 
     * @param spec
     * @param symbol
     */
    public ImmutableQuote(TimeSpec spec, String symbol) {
    	super(spec, 0L);
    	_symbol = symbol;
        _open   = 0.0;
        _high   = 0.0;
        _low    = 0.0;
        _close  = 0.0;
        _volume = 0L;
    }

    /**
     * Fully parameterized Constructor
     *
     * @param specrep long
     * @param time long
     * @param open double
     * @param high double
     * @param low double
     * @param close double
     * @param volume long
     */
    public ImmutableQuote(long specrep, long time, String symbol, double open, double high, double low, double close, long volume) {
        super(specrep,time);
        _symbol = symbol;
        _open   = open;
        _high   = high;
        _low    = low;
        _close  = close;
        _volume = volume;
    }

    /**
     * TimeSeriesSpec/Time Constructor
     *
     * @param spec TimeSeriesSpec
     * @param time long
     * @param open double
     * @param high double
     * @param low double
     * @param close double
     * @param volume long
     */
    public ImmutableQuote(TimeSpec spec, long time, String symbol, double open, double high, double low, double close, long volume) {
        super(spec,time);
        _symbol = symbol;
        _open   = open;
        _high   = high;
        _low    = low;
        _close  = close;
        _volume = volume;
    }

    /**
     * Copy Constructor
     *
     * @param that ImmutableQuote
     */
    public ImmutableQuote(Quote that) {
        super(that);
        _symbol = that.symbol();
        _open   = that.open();
        _high   = that.high();
        _low    = that.low();
        _close  = that.close();
        _volume = that.volume();
    }
    
    /**
     * Key existence utility to determine mutability.
     * 
     * @param tick
     * @return ImmutableTick or subclass
     */
    protected ImmutableQuote thisOrCopy() {
    	return new ImmutableQuote(this);
    }
    
    
    public String  symbol() { return _symbol; }
    public double  open()   { return _open; }
    public double  high()   { return _high; }
    public double  low()    { return _low;  }
    public double  close()  { return _close; }
    public long    volume() { return _volume; }


    /**
     * Immutable safe modifier for setting symbol
     */
	public Quote withSymbol(String symbol) {
		ImmutableQuote q = thisOrCopy();
		q._symbol = symbol;
		return q;
	}

    
    /**
     * 
     * 
     * Perform Monoid combination; quote will adjust high, low, close, and volume.
     *
     * @param e TimeSeriesEvent
     * @return TimeSeriesEvent as MutableQuote
     */
    public BinnedTimeEvent fold(BinnedTimeEvent e) {
        if (e instanceof ImmutableQuote) merge((Quote) e);
        return null;
    }

    /**
     * Answer new ImmutableQuote that expands OHLC boundaries
     * and increments volume.
     *
     * @param that Quote
     * @return Quote
     */
    public Quote merge(Quote that) {
        return (new ImmutableQuote(this))._merge(that);
    }

    /**
     * Utility modifier that combines two Quote events by
     * possibly extending high and low bounds and incrementing
     * volume. Used differently by mutable and immutable
     * implementations.
     *
     * @param that Quote
     * @return Quote
     */
    protected Quote _merge(Quote that) {
        _symbol = that.symbol();
        _high   = Math.max(_high, that.high());
        _low    = Math.min(_low, that.low());
        _close  = that.close();
        _volume = _volume + that.volume();
        return that;
    }

    /**
     * Answer a new Quote with updated content. Preserves original symbol and time specification.
     * 
     * @param datetime long
     * @param open double
     * @param high double
     * @param low double
     * @param close double
     * @param volume long
     * @return Quote
     */
    public Quote with(long datetime, double open, double high, double low, double close, long volume) {
        return (new ImmutableQuote(this))._with(datetime, open, high, low, close, volume);
    }
    
    /**
     * Utility to replace content. Preserves original symbol and time specification.
     * 
     * @param datetime
     * @param open
     * @param high
     * @param low
     * @param close
     * @param volume
     * @return Quote
     */
    protected Quote _with(long datetime, double open, double high, double low, double close, long volume) {
    	_time 	= datetime;
    	_open	= open;
    	_high	= high;
    	_low	= low;
    	_close	= close;
    	_volume	= volume;
    	return this;
    }
    
    
    
    /**
     * 
     */
	public StringBuffer toBuffer(StringBuffer sb) {

		sb.append('{');

        pairToBuffer("sym", _symbol, sb, true);
        datetimeToBuffer("dt",_time,sb);
        
        pairToBuffer("o", _open, "#.000", sb, true);
        pairToBuffer("h", _high, "#.000", sb, true);
        pairToBuffer("l", _low, "#.000", sb, true);
        pairToBuffer("c", _close, "#.000", sb, true);
        pairToBuffer("v", _volume, sb, true);
        pairToBuffer("spec", _spec, sb, true);

		sb.append('}');
		return sb;
	}

}
