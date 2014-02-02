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

import bitzguild.mkt.event.MutableQuote;
import bitzguild.mkt.event.Quote;
import bitzguild.mkt.event.QuoteChain;
import bitzguild.ts.datetime.MutableDateTime;
import bitzguild.ts.event.TimeSpec;
import bitzguild.ts.event.TimeUnits;


/**
 * <p>
 * Foundation for all time-based compression (Seconds, Minutes,
 * Hours, Days, Weeks, Months). Implemented as Minutes default.
 * </p>
 * <p>
 * Quote compression is process of converting Quote-based
 * market data from one lesser time frame into another greater
 * time frame (e.g. MINUTE to HOUR, HOUR to DAY, etc). It assumes
 * a sequence of increasing date-time values and OHLC and volumes
 * corresponding to the given time input.
 * </p>
 * <p>
 * The compressor seeks to minimize object creation,
 * and continually reuses quote objects. Observers should
 * assume any object reference may change and make a copy.
 * Compressor implementations make the same assumption
 * on all quote updates.
 * </p>
 */


public class Compress2Time implements QuoteCompression {

	protected QuoteChain        _output;
	protected TimeSpec 			_compressionSpec;
	protected TimeSpec 			_incomingSpec;

	protected MutableQuote      _quoteCompressed = null;
	protected int               _increment = 1;

	protected MutableDateTime          _datetimeIncr;
	protected MutableDateTime          _datetimeTmp;
	protected long               _nextDateTime;


	// ------------------------------------------
	// Existence
	// ------------------------------------------

	public Compress2Time() {
		super();
        commonCompressTimeInit(new TimeSpec(TimeUnits.MINUTE, 1));
	}

    public Compress2Time(int increment) {
        super();
        commonCompressTimeInit(new TimeSpec(TimeUnits.MINUTE, increment));
    }

    public Compress2Time(TimeSpec spec) {
        super();
        commonCompressTimeInit(spec);
    }

    protected void commonCompressTimeInit(TimeSpec spec) {
        _compressionSpec = spec;
        _increment = spec.length;

        _datetimeTmp = new MutableDateTime();
        _nextDateTime = Long.MIN_VALUE;
		_output = QuoteChain.TERMINAL;
    }

	// ------------------------------------------
	// Accessors
	// ------------------------------------------

	public int getCompressionLength() { return _increment; }
	public void setCompressionLength(int incr) {
		_increment = Math.max(1, incr);
		_compressionSpec.length = _increment;
	}

	// ------------------------------------------
	// Time Utilities
	// ------------------------------------------

    protected void alignTime(MutableDateTime dt) {
        dt.setHoursMinutesSeconds(dt.hours(), dt.minutes(), 0);
    }

    protected void incrementToNextPeriod(MutableDateTime dt) {
        int minutes = dt.minutes();
        int next = ((minutes-1) / _increment)*_increment + _increment - minutes;
        next = (next == 0) ? _increment : next;

        dt.addMinutes(next);
    }

    // ------------------------------------------
    // QuoteChain interface
    // ------------------------------------------

    public QuoteChain feeds(QuoteChain other) {
        _output = other;
        return other;
    }


    public void update(Quote inquote) {

		if (inquote.datetimeRep() >= _nextDateTime) {

			// Change up
			if (_quoteCompressed != null) {
				_output.update(_quoteCompressed);
                //_compressionListener.innerUpdate(symbol, _quoteCompressed);

				_datetimeIncr.setRep(inquote.datetimeRep());
			} else {
				_quoteCompressed = new MutableQuote(_compressionSpec,inquote.symbol());
				_datetimeIncr = new MutableDateTime(inquote.datetime());
			}

			// 1st time only stuff
			_quoteCompressed.setOpen(inquote.open());
			_datetimeTmp.setRep(inquote.datetimeRep());
            alignTime(_datetimeTmp);

            _quoteCompressed.setDateTimeRep(_datetimeTmp.rep());

			// recurring updates
            _quoteCompressed.setLow(inquote.low());
            _quoteCompressed.setHigh(inquote.high());
            _quoteCompressed.setClose(inquote.close());
            _quoteCompressed.setVolume(inquote.volume());
            
			incrementToNextPeriod(_datetimeIncr);
			_nextDateTime = _datetimeIncr.rep();

		} else {
			// recurring updates
            _quoteCompressed.setHigh(Math.max(inquote.high(), _quoteCompressed.high()));
            _quoteCompressed.setLow(Math.min(inquote.low(), _quoteCompressed.low()));
            _quoteCompressed.setClose(inquote.close());
            _quoteCompressed.setVolume(_quoteCompressed.volume() + inquote.volume());
		}
	}

    /**
     * Convey any in-process data to downstream listener.
     * @param inquote
     */
    public void close() {
        _output.close();
		_output = QuoteChain.TERMINAL;
    }


	// ------------------------------------------
	// QuoteCompression interface
	// ------------------------------------------

	/**
	 * Answers a copy of the TimeSpec for compression.
	 *
	 * @return TimeSpec market data quote spec
	 */
	public TimeSpec getCompression() {
		return new TimeSpec(_compressionSpec);
	}

	/**
	 * <p>
	 * Assigns the data format this filter
	 * will be accepting. While there is no strict
	 * enforcement for only accepting this input
	 * format, this attribute enables the Compression
	 * to act as a surrogate to data loading
	 * on behalf of the output data stream. In other
	 * words, a 1-month data request can look like a
	 * 1-day data request to the loader.
	 * </p>
	 *
	 * @param inSpec TimeSpec input data specification
	 */
	public void setInputSpec(TimeSpec inSpec) {            // this is redundant (passed on info() call)
		_incomingSpec = inSpec;
	}

	/**
	 * Answer intermediate accumulated Quote representation
	 * 
	 * @return Quote
	 */
	public Quote snapshot() {
		return new MutableQuote(_quoteCompressed);
	}
	
	public QuoteChain getOutput() {
		return _output;
	}



	/**
	 * <p>
	 * Answers the incoming data format specification.
	 * While there is no strict enforcement on input
	 * data, this attribute enables the Compression
	 * to act as a surrogate to data loading
	 * on behalf of the output data stream. In other
	 * words, a 1-month data request can now look like a
	 * 1-day data request to the loader, and the
	 * compression handles the translation.
	 * <p>
	 *
	 * @return TimeSpec expected incoming data format
	 */
	public TimeSpec getTimeSpec() { return _incomingSpec; }

	// ------------------------------------------
	// Printing / Debug
	// ------------------------------------------

	public void toBuffer(StringBuffer strb) {
		strb.append(getClass().getSimpleName());
        strb.append('(');
        strb.append(_compressionSpec.toString());
        strb.append(')');
	}

	public String toString() {
		StringBuffer strb = new StringBuffer();
		toBuffer(strb);
		return strb.toString();
	}

}

