/* ***** BEGIN LICENSE BLOCK *****
 *
 * Copyright (c) 2001-2014, Kevin Sven Berg. All rights reserved.
 *
 * This package is part of the Bitzguild Time Series Distribution
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ***** END LICENSE BLOCK ***** */

package bitzguild.mkt.event;

import bitzguild.ts.datetime.MutableDateTime;
import bitzguild.ts.event.AbstractTimeEvent;
import bitzguild.ts.event.BinnedTimeEvent;
import bitzguild.ts.event.TimeSpec;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;


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
    public Quote withDOHLCV(long datetime, double open, double high, double low, double close, long volume) {
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
        pairToBuffer("spec", _spec, sb, false);

		sb.append('}');
		return sb;
	}


    public String toCSV() {
        StringBuffer sb = new StringBuffer();
        toCSVBufer(sb);
        return sb.toString();
    }

    public String csvHeader() { return "Date,Open,High,Low,Close,Volume"; }

    /**
     * Print Quote as one comma-separated-value row into given StringBuffer.
     * StringBuffer is with Formatter is more performant than StringBuilder
     * and formatted decimal string.
     *
     * @param sb StringBuffer
     * @return StringBuffer
     */
    public StringBuffer toCSVBufer(StringBuffer sb) {

        (new MutableDateTime(_time)).toBuffer(sb).append(",");
        formatDoubleInto(_open, sb, true);
        formatDoubleInto(_high, sb, true);
        formatDoubleInto(_low, sb, true);
        formatDoubleInto(_close, sb, true);
        sb.append(_volume);

        return sb;
    }

    public Quote fromCSV(String csvRow) throws ParseException {
        ImmutableQuote q = thisOrCopy();
        String[] column = csvRow.split(",");

        MutableDateTime dt = MutableDateTime.parse(column[0]);
        q._time = dt.rep();

        q._open = Double.parseDouble(column[1]);
        q._high = Double.parseDouble(column[2]);
        q._low = Double.parseDouble(column[3]);
        q._close = Double.parseDouble(column[4]);
        q._volume = Long.parseLong(column[5]);

        return q;
    }

    public static DecimalFormat DefaultDecimalFormat = new DecimalFormat("#.0000");
    private static FieldPosition _FieldPos = new FieldPosition(NumberFormat.FRACTION_FIELD);

    protected void formatDoubleInto(Double d, StringBuffer sb, boolean comma) {
        DefaultDecimalFormat.format(d, sb, _FieldPos);
        sb.setLength(sb.length()-1);
        if(comma) sb.append(',');
    }

}
