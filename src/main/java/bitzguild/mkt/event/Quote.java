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

import bitzguild.ts.event.BinnedTimeEvent;

/**
 * <p>
 * General-purpose historical price quote expressed as open, high, low, close, and volume.
 * Since a quote represents 1 or more market sales events, TimeSpec indicates the span
 * of time (or number of events) represented by the given Quote. The units include minute,
 * hour, day, week, month, and year. Together with the period, this enables expression
 * of quotes such as 3-minute, 5-minute, 1-hour, 1-day, 3-day, 1-week, 2-weeks, 1-month,
 * or 1-year, for example. In this library, Quote is designed as a data-relay mechanism;
 * that is, Quote consumers take information from an immutable Quote. Producers may 
 * implement mutable Quote classes for re-use efficiency. 
 * </p>
 *
 * @author Kevin Sven Berg
 * @date May 2001
 */
public interface Quote extends BinnedTimeEvent {

    public String 	symbol();   	// reference market symbol (generic)
	public double 	open();			// price at beginning of period
	public double 	high();			// highest price during period
	public double 	low();			// lowest price during period
	public double 	close();		// price at end of period
	public long 	volume();		// number of transactions during period

	public Quote withSymbol(String symbol);
    public Quote with(long datetime, double open, double high, double low, double close, long volume);
	
    public Quote merge(Quote that);
    
    public StringBuffer toBuffer(StringBuffer strb);
	

}