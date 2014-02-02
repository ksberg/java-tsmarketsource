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

package bitzguild.mkt.event.compress;

import bitzguild.mkt.event.MutableQuote;
import bitzguild.mkt.event.Quote;
import bitzguild.mkt.event.QuoteChain;
import bitzguild.ts.datetime.MutableDateTime;
import bitzguild.ts.event.TimeSpec;

/**
 * The purpose of this QuoteChain is to constrain Quote updates
 * to a given time range. One possible use: create synthetic
 * trading hours for electronically traded instruments (e.g.
 * a 'day' session verses overnight).
 * 
 * @author c_bergk
 *
 */
public class TimeRangeFilter implements QuoteChain {

	protected QuoteChain        _output;
	protected int 				_lowerBound;
	protected int 				_upperBound;
	protected TimeSpec 			_incomingSpec;
	
	/**
	 * Default Constructor. Assumes time
	 * window of 9AM to 4PM.
	 */
	public TimeRangeFilter() {
		super();
		MutableDateTime ldt = new MutableDateTime();
		MutableDateTime udt = new MutableDateTime();
		ldt.setHoursMinutesSeconds(9, 0, 0);
		udt.setHoursMinutesSeconds(16, 0, 0);
		_lowerBound = ldt.millisecondsSinceMidnight();
		_upperBound = udt.millisecondsSinceMidnight();
		_output = QuoteChain.TERMINAL;
	}

	/**
	 * DateTime constructor
	 * 
	 * @param dtLower
	 * @param dtUpper
	 */
	public TimeRangeFilter(MutableDateTime dtLower, MutableDateTime dtUpper) {
		super();
		_lowerBound = dtLower.millisecondsSinceMidnight();
		_upperBound = dtUpper.millisecondsSinceMidnight();
	}

	/**
	 * Time constructor 
	 * 
	 * @param lower milliseconds since midnight
	 * @param upper milliseconds since midnight
	 */
	public TimeRangeFilter(int lower, int upper) {
		super();
		_lowerBound = lower;
		_upperBound = upper;
	}
	
	
	/* (non-Javadoc)
	 * @see psc.mkt.data.QuoteChain#feeds(psc.mkt.data.QuoteChain)
	 */
	@Override
	public QuoteChain feeds(QuoteChain other) {
		_output = other;
		return _output;
	}

	/** 
	 * Check range of incoming datetime and pass on updates
	 * if within given time range.
	 * 
	 * @see bitzguild.mkt.event.QuoteChain#update(bitzguild.mkt.event.Quote)
	 */
	@Override
	public void update(Quote quote) {
		MutableDateTime dt = new MutableDateTime(quote.datetime());
		int ms = dt.millisecondsSinceMidnight();
		if (ms >= _lowerBound && ms <= _upperBound) {
			_output.update(quote);
		}
	}

	/**
	 * Inform downstream that there are no other updates.
	 * Pin the time to given range (may not always be what
	 * we want, but we need to pass along the notification).
	 * 
	 * @see bitzguild.mkt.event.QuoteChain#close()
	 */
	@Override
	public void close() {
		_output.close();
		_output = QuoteChain.TERMINAL;
	}

}
