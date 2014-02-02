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
import bitzguild.ts.event.TimeSpec;
import bitzguild.ts.event.TimeUnits;


/**
 * <p>
 * Does zero compression or but does pass quote through
 * to the observer. Like <code>CompressorNull</code> this
 * class enables one algorithm to serve special cases.
 * When using this class, the Source and Observer
 * should be different instances.
 * </p>
 * <p>
 * The compressor seeks to minimize object creation,
 * and continually reuses quote objects. Observers should
 * assume any object reference may change and make a copy.
 * Compressor implementations make the same assumption
 * on all quote updates.
 * </p>
 */


public class Compress2Minutes extends Compress2Time {

	public static final int COMPRESSION_02  = 2;
	public static final int COMPRESSION_03  = 3;
	public static final int COMPRESSION_05  = 5;
	public static final int COMPRESSION_09  = 9;
	public static final int COMPRESSION_10  = 10;
	public static final int COMPRESSION_15  = 15;
	public static final int COMPRESSION_30  = 30;

	// ------------------------------------------
	// Existence
	// ------------------------------------------

	public Compress2Minutes() {
		super(new TimeSpec(TimeUnits.MINUTE, 1));
	}

	public Compress2Minutes(int increment) {
		super(new TimeSpec(TimeUnits.MINUTE, increment));
	}

	// ------------------------------------------
	// Accessors
	// ------------------------------------------

    @Override
	public void setCompressionLength(int incr) {
		_increment = Math.max(1, Math.min(incr, COMPRESSION_30));
		_compressionSpec.length = _increment;
	}

	// ------------------------------------------
	// QuoteChain interface
	// ------------------------------------------

	protected void incrementToNextPeriod(MutableDateTime dt) {
		int minutes = dt.minutes();
		int next = ((minutes-1) / _increment)*_increment + _increment - minutes;
		next = (next == 0) ? _increment : next;

		dt.addMinutes(next);
	}

}

