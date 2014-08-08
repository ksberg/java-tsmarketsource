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

import bitzguild.ts.datetime.MutableDateTime;
import bitzguild.ts.event.TimeSpec;
import bitzguild.ts.event.TimeUnits;


/**
 * <p>
 * Foundation for DAY, WEEK, MONTH compression.
 * </p>
 * <p>
 * The compressor seeks to minimize object creation,
 * and continually reuses quote objects. Observers should
 * assume any object reference may change and make a copy.
 * Compressor implementations make the same assumption
 * on all quote updates.
 * </p>
 * <p>
 * Increasing QuoteCompression can be chained by
 * </p>
 */


public class CompressDayOrGreater extends Compress2Time {

	public static final boolean DEBUG           = false;
	public static final boolean DEBUG_RECUR     = false;
	public static final boolean DEBUG_SWITCH    = false;


	protected int               _boundsLower;
	protected int               _boundsUpper;


	// ------------------------------------------
	// Existence
	// ------------------------------------------

	public CompressDayOrGreater() {
		super(new TimeSpec(TimeUnits.DAY,1));
		setHMBounds(9, 30, 16, 30);
	}

	public CompressDayOrGreater(long increment) {
        super(new TimeSpec(TimeUnits.DAY,increment));
		setHMBounds(9, 30, 16, 30);
	}

    public CompressDayOrGreater(TimeSpec spec) {
        super(spec);
        setHMBounds(9, 30, 16, 30);
    }

	// ------------------------------------------
	// Base Utilities
	// ------------------------------------------

	protected void incrementToNextPeriod(MutableDateTime dt) {
        for(int incr=0; incr<_increment; incr++)
            dt.nextBusinessDay(MutableDateTime.DefaultHolidays);
	}

    protected void alignTime(MutableDateTime dt) {
        dt.setHoursMinutesSeconds(0,0,0);
    }

	// ------------------------------------------
	// Accessors
	// ------------------------------------------

	public int getLowerBound() { return _boundsLower; }
	public int getUpperBound() { return _boundsUpper; }

	public void setBounds(int lower, int upper) {
		_boundsLower = Math.min(lower, upper);
		_boundsUpper = Math.max(lower, upper);

		System.out.println(_boundsLower);
		System.out.println(_boundsUpper);
	}

	public void setHMBounds(int lowerHours, int lowerMinutes, int upperHours, int upperMinutes) {
		StringBuffer strb = new StringBuffer();

		_datetimeTmp.setHoursMinutesSeconds(lowerHours, lowerMinutes, 0);
		_boundsLower = _datetimeTmp.millisecondsSinceMidnight();
		if (DEBUG) System.out.println(_datetimeTmp.toString());

		_datetimeTmp.setHoursMinutesSeconds(upperHours, upperMinutes, 0);
		_boundsUpper = _datetimeTmp.millisecondsSinceMidnight();
		if (DEBUG) System.out.println(_datetimeTmp.toString());
	}

}

