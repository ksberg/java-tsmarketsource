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
import bitzguild.mkt.event.QuoteListener;
import bitzguild.ts.datetime.DateTime;
import bitzguild.ts.datetime.ImmutableDateTime;
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

    protected static long       ZERODAY = new MutableDateTime(1000,1,1).rep();
	protected QuoteChain        _nextEventProcessor;
    protected QuoteListener     _compressedEventListener;

	protected TimeSpec 			_compressionSpec;
	protected TimeSpec 			_incomingSpec;

	protected MutableQuote      _quoteCompressed = null;
	protected int               _increment = 1;

	protected MutableDateTime   _datetimeIncr;
	protected MutableDateTime   _datetimeTmp;
	protected long              _nextDateTimeFrame;
    protected long              _nextFrameBoundary;

    protected MutableDateTime   _nextDateTimeFrameDT;
    protected MutableDateTime   _priorDateTimeFrameDT;

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
        _nextDateTimeFrame = ZERODAY;
        _nextFrameBoundary = _nextDateTimeFrame;
		_nextEventProcessor = QuoteChain.TERMINAL;
        _compressedEventListener = QuoteListener.TERMINAL;

        _nextDateTimeFrameDT = new MutableDateTime();
        _priorDateTimeFrameDT = new MutableDateTime(_nextDateTimeFrame);
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
        //System.out.println("ALIGNED: " + dt);
    }

    protected long alignFrameWithSpec(long inrep) {
        _datetimeTmp.setRep(inrep);
        alignTime(_datetimeTmp);
        return _datetimeTmp.rep();
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

    public QuoteChain chain() { return _nextEventProcessor; }

    public QuoteChain feeds(QuoteChain other) {
        _nextEventProcessor = other;
        return other;
    }


    // TODO: use DateTimeRange for pinning current and next frames

    public void update(Quote inquote) {

        if (inquote.datetimeRep() < _nextDateTimeFrame) {
            _quoteCompressed.merge(inquote);    // add to existing
        } else {                                // changing up

            long specAlignedIncomdingFrame = alignFrameWithSpec(inquote.datetimeRep());

			if (_quoteCompressed != null) {
				_nextEventProcessor.update(_quoteCompressed);
                _compressedEventListener.update(_quoteCompressed);
				_datetimeIncr.setRep(specAlignedIncomdingFrame);
			} else {
				_quoteCompressed = new MutableQuote(_compressionSpec, specAlignedIncomdingFrame,inquote.symbol(),
                                inquote.open(), inquote.high(), inquote.low(), inquote.close(), inquote.volume());
				_datetimeIncr = new MutableDateTime(specAlignedIncomdingFrame);
			}

            if (specAlignedIncomdingFrame > _nextDateTimeFrame
                    && shouldFillGaps()
                    && gapIsWithinThreshold(_nextDateTimeFrame,specAlignedIncomdingFrame)) {
                fillGap(_nextDateTimeFrame, specAlignedIncomdingFrame, _quoteCompressed, inquote);
            } else {
                _quoteCompressed.with( specAlignedIncomdingFrame,
                        inquote.open(), inquote.high(), inquote.low(), inquote.close(),
                        inquote.volume());
            }

            _priorDateTimeFrameDT.setRep(_nextDateTimeFrame);   // tmp debug
            incrementToNextPeriod(_datetimeIncr);
			_nextDateTimeFrame = _datetimeIncr.rep();
            _nextDateTimeFrameDT.setRep(_nextDateTimeFrame);    // tmp debug

            _nextEventProcessor.update(_quoteCompressed);
		}
	}


    /**
     * Convey any in-process data to downstream listener.
     */
    public void close() {
        _nextEventProcessor.close();
		_nextEventProcessor = QuoteChain.TERMINAL;
    }

    /**
     * Generic policy is fill gaps on MINUTE frame and below.
     *
     * @return boolean
     */
    public boolean shouldFillGaps() { return this._compressionSpec.units < TimeUnits.HOUR; }

    /**
     * Needs to be explicitly enabled in subclass
     *
     * @param millis milliseconds since midnight
     * @return whether threshold meets gap criteria
     */
    public boolean meetsGapThreshold(int millis) {
        return false;
    }

    private boolean gapIsWithinThreshold(long firstDT, long lastDT) {
        if (firstDT == ZERODAY) return false;
        MutableDateTime fdt = new MutableDateTime(firstDT);
        MutableDateTime ldt = new MutableDateTime(lastDT);
        return meetsGapThreshold(Math.abs(ldt.millisecondsSinceMidnight() - fdt.millisecondsSinceMidnight()));
    }

    public void fillGap(long firstDT, long lastDT, Quote compression, Quote inquote) {

        MutableDateTime fdt = new MutableDateTime(firstDT);
        MutableDateTime ldt = new MutableDateTime(lastDT);

        if (fdt.day() != ldt.day()) return; // skip gaps between trading day periods

        MutableQuote mergeQuote = new MutableQuote(inquote);
        mergeQuote.setDateTimeRep(lastDT);
        _quoteCompressed.with( firstDT,compression.close(), compression.close(), compression.close(), compression.close(),0L);

        long fillerDT = firstDT;

        int count = 0;
        while(fillerDT < lastDT) {
            _quoteCompressed.setDateTimeRep(fillerDT);
            _compressedEventListener.update(_quoteCompressed);
            incrementToNextPeriod(fdt);
            fillerDT = fdt.rep();
            count++;
        }
        _quoteCompressed.merge(mergeQuote);    // add to existing
        _quoteCompressed.setDateTimeRep(lastDT);

//        System.out.print("*** FILLED GAP *** ");
//        System.out.println(new ImmutableDateTime(finalDT) + " > "
//                        + new ImmutableDateTime(firstDT)
//                        + " with " + count
//                        + " " + _compressionSpec
//                        + " quotes"
//        );
    }


	// ------------------------------------------
	// QuoteCompression interface
	// ------------------------------------------

    public QuoteListener listener() { return _compressedEventListener; }

    /**
     * Assign listener
     *
     * @param listener for compressed events
     */
    public QuoteCompression connect(QuoteListener listener) {
        _compressedEventListener = listener;
        return this;
    }

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
		return _nextEventProcessor;
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

