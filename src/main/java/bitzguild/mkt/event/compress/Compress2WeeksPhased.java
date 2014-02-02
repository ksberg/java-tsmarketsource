/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (c) 2001-2013, Kevin Sven Berg
 * All rights reserved.
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
import bitzguild.ts.event.TimeUnits;


/**
 * <p>
 * Compresses input to virtual 'weekly' data, where a
 * week is defined as the days prior to an offset day.
 * When the offset is zero, compression equals the normal
 * weekly definition. Offsets correspond to the day-of-week
 * constants in the Date class. If the offset is Tuesday (1)
 * then a week is defined as Tuesday through Monday; open
 * price is the first price on Tuesday, and close is the last
 * price on Monday; high and low are bounded by the same
 * period.
 * <p>
 * The compressor seeks to minimize object creation,
 * and continually reuses quote objects. Observers should
 * assume any object reference may change and make a copy.
 * Compressor implementations make the same assumption
 * on all quote updates.
 * </p>
 */
public class Compress2WeeksPhased extends CompressDayOrGreater {

	protected int   _offset = 0;

	// ------------------------------------------
	// Existence
	// ------------------------------------------

	public Compress2WeeksPhased() {
		super();
		_compressionSpec.units = TimeUnits.WEEK;
	}

	public Compress2WeeksPhased(int dayOfWeek) {
		super();
		_offset = Math.max(0, Math.min(5, dayOfWeek));
		_compressionSpec.units = TimeUnits.WEEK;
	}

	// ------------------------------------------
	// Base Utilities
	// ------------------------------------------

	protected void incrementToNextPeriod(MutableDateTime dt) {
		dt.nextWeek();
		int decr = _offset;
		while(decr-->0) {
			dt.nextWeekday();
		}
	}

}

