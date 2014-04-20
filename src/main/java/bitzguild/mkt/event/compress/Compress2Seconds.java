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

import bitzguild.ts.datetime.DateTime;
import bitzguild.ts.datetime.MutableDateTime;
import bitzguild.ts.event.TimeSpec;
import bitzguild.ts.event.TimeUnits;


/**
 * <p>
 * The compressor seeks to minimize object creation,
 * and continually reuses quote objects. Observers should
 * assume any object reference may change and make a copy.
 * Compressor implementations make the same assumption
 * on all quote updates.
 * </p>
 */


public class Compress2Seconds extends Compress2Time {

	public static final int COMPRESSION_05  = 5;
	public static final int COMPRESSION_10  = 10;
	public static final int COMPRESSION_15  = 15;
	public static final int COMPRESSION_30  = 30;

	// ------------------------------------------
	// Existence
	// ------------------------------------------

	public Compress2Seconds() {
		super(new TimeSpec(TimeUnits.SECOND, COMPRESSION_30));
	}

	public Compress2Seconds(int increment) {
		super(new TimeSpec(TimeUnits.SECOND, increment));
	}

	// ------------------------------------------
	// Accessors
	// ------------------------------------------

    @Override
	public void setCompressionLength(int incr) {
		_increment = Math.max(1, Math.min(incr, COMPRESSION_30));
		_compressionSpec.length = _increment;
	}

    /**
     * Needs to be explicitly enabled in subclass
     *
     * @param millis milliseconds since midnight
     * @return whether threshold meets gap criteria
     */
    public boolean meetsGapThreshold(int millis) {
        return millis < DateTime.MillisInMinute*2;
    }


	// ------------------------------------------
	// QuoteChain interface
	// ------------------------------------------

    // ------------------------------------------
    // Compress2Time utilities
    // ------------------------------------------

	protected void incrementToNextPeriod(MutableDateTime dt) {
		int seconds = dt.seconds();
		int next = ((seconds-1) / _increment)*_increment + _increment - seconds;
		next = (next == 0) ? _increment : next;

		dt.addSeconds(next);
	}

    protected void alignTime(MutableDateTime dt) {
        dt.setHoursMinutesSeconds(dt.hours(), dt.minutes(), dt.seconds() % _increment);
    }

}

