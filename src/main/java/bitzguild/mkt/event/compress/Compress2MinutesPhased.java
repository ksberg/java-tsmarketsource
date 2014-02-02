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

/**
 * <p>
 * Creates a compression filter that is a multiple
 * and phase offset from a base time period. For instance,
 * a 5-minute base, 6x multiple, and 1st phase would result
 * in 30-minute bars (5x6), offset 5 minutes. For example,
 * the resulting half-hour bars would appear at 9:35, 10:05,
 * 10:35, 11:05, and so on. With several compression filters
 * used in parallel, continually rolling half-hour bars can
 * be made available for analysis.
 * <p>
 * The compressor seeks to minimize object creation,
 * and continually reuses quote objects. Observers should
 * assume any object reference may change and make a copy.
 * Compressor implementations make the same assumption
 * on all quote updates.
 * </p>
 */
public class Compress2MinutesPhased extends Compress2Minutes {

	protected int   _multiple = 6;
	protected int   _offset = 0;
	protected int   _phase = 0;

	// ------------------------------------------
	// Existence
	// ------------------------------------------

	public Compress2MinutesPhased() {
		super();
	}

	/**
	 * <p>
	 * Creates a compression filter that is a multiple
	 * and phase offset from a base time period. For instance,
	 * a 5-minute base, 6x multiple, and 1st phase would result
	 * in 30-minute bars (5x6), offset 5 minutes. For example,
	 * the resulting half-hour bars would appear at 9:35, 10:05,
	 * 10:35, 11:05, and so on. With several compression filters
	 * used in parallel, continually rolling half-hour bars can
	 * be made available for analysis.
	 * </p>
	 */
	public Compress2MinutesPhased(int base, int multiple, int phase) {
		super(base*multiple);
		_multiple = multiple;
		_offset = 0;
		_phase = phase % multiple;
	}

	// ------------------------------------------
	// Base Utilities
	// ------------------------------------------

	protected void incrementToNextPeriod(MutableDateTime dt) {
		int minutes = dt.minutes();
		int next = ((minutes-1) / _increment)*_increment + _increment - minutes;
		next = (next == 0) ? _increment : next;

		next += _offset;
		dt.addMinutes(next);
	}

}

