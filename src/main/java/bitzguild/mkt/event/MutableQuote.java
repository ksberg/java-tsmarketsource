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

import bitzguild.ts.event.TimeSpec;



/**
 * <p>
 * General-purpose historical price quote expressed as open, high, low, close, volume,
 * and open interest. Contains embedded specification for how to apply a single Quote
 * in a larger context, including the period units, length of period, and subset fields.
 * Subset can be used to relay close-only data, for example. The units include minute,
 * hour, day, week, month, and year. Together with the period, this enables expression
 * of quotes such as 3-minute, 5-minute, 1-hour, 1-day, 3-day, 1-week, 2-weeks, 1-month,
 * or 1-year, for example.
 * </p>
 * <p>
 * Quote is designed as a data-relay mechanism, and is not suitable for run-time or
 * persistent storage (although the usefulness of a QuoteDAO might be debatable).
 * A large number of Quote instances will greatly tax the system GC. 
 * </p>
 *
 * @author Kevin Sven Berg
 * @date May 2001
 */
public class MutableQuote extends ImmutableQuote {

	public static final long serialVersionUID = 1L;
	
	public static boolean	_TraceOn = false;

	// -----------------------------------------------------------
	// Existence
	// -----------------------------------------------------------

    /**
     * Default Constructor
     */
    public MutableQuote() {
        super();
    }

    /**
     * Copy Constructor
     *
     * @param that Quote
     */
    public MutableQuote(Quote that) {
        super(that);
    }

    /**
     * 
     * @param spec
     * @param symbol
     */
    public MutableQuote(TimeSpec spec, String symbol) {
    	super(spec,symbol);
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
    public MutableQuote(long specrep, long time, String symbol, double open, double high, double low, double close, long volume) {
        super(specrep, time, symbol,open,high,low,close,volume);
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
    public MutableQuote(TimeSpec spec, long time, String symbol, double open, double high, double low, double close, long volume) {
        super(spec, time, symbol,open,high,low,close,volume);
    }

    /**
     * Key existence utility to determine mutability.
     * 
     * @param tick
     * @return ImmutableTick or subclass
     */
    protected ImmutableQuote thisOrCopy() {
    	return this;
    }
    
    
	public MutableQuote setSymbol(String sym) {
		this._symbol = sym;
		return this;
	}
	
	public MutableQuote setOpen(double d) {
		this._open = d;
		return this;
	}

	public MutableQuote setHigh(double d) {
		this._high = d;
		return this;
	}

	public MutableQuote setLow(double d) {
		this._low = d;
		return this;
	}

	public MutableQuote setClose(double d) {
		this._close = d;
		return this;
	}

	public MutableQuote setVolume(long v) {
		this._volume = v;
		return this;
	}

	public MutableQuote setPrice(double p) {
		this._open = p;
		this._high = p;
		this._low = p;
		this._close = p;
		return this;
	}
	
	
	/**
	 * Assign the LDateTime rep
	 * 
	 * @param dtr
	 */
	public MutableQuote setDateTimeRep(long dtr) {
		this._time = dtr;
		return this;
	}


	public MutableQuote setTimeSpec(TimeSpec timespec) {
		this._spec = timespec.rep();
		return this;
	}

    public void zeroValues() {
        this._open = 0.0;
        this._high = 0.0;
        this._low = 0.0;
        this._close = 0.0;
        this._volume = 0;
        this._time = 0L;
    }
    
    
    /**
     * Modify current MutableQuote by expanding OHLC boundaries
     * and incrementing volume.
     *
     * @param that Quote
     * @return Quote
     */
    public Quote merge(Quote that) {
        return this._merge(that);
    }
    
    /**
     * Answer a modified Quote with updated content. Preserves original symbol and time specification.
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
        return this._with(datetime, open, high, low, close, volume);
    }
    
    

}