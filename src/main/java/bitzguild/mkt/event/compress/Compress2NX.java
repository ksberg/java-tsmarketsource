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
import bitzguild.ts.event.TimeSpec;


// Renko (compress to block)
// Mod-Renko (block w/tail)
// Count (this)
// Volume
// Area (price x volume)
// Volatility Squashed

/**
 * <p>
 * Compresses multiple quotes into single quote. Final
 * result is an aggregate of N, where N is the compression
 * number. When using this class, the Source and Observer
 * should be different instances.
 * </p>
 * <p>
 * The compressor seeks to minimize object creation,
 * and continually reuses quote objects. Observers should
 * assume any object reference may change and make a copy.
 * Compress implementations make the same assumption
 * on all quote updates.
 * </p>
 */
public class Compress2NX implements QuoteCompression {

	protected QuoteChain        _output;
    protected QuoteListener     _compressedEventListener;

	protected TimeSpec          _compressionSpec;
	protected TimeSpec          _incomingSpec;

	protected MutableQuote      _quoteCompressed = null;

	protected int               _incount;
	protected int               _compression = 2;


	// ------------------------------------------
	// Existence
	// ------------------------------------------

	public Compress2NX() {
		super();
        common2nxInit(2);
	}

	public Compress2NX(int compression) {
		super();
        common2nxInit(compression);
	}

    private void common2nxInit(int compression) {
        _incount = 1;
        _compression = compression;
        _compressionSpec = new TimeSpec();
        _compressionSpec.length = _compression;
		_output = QuoteChain.TERMINAL;
        _compressedEventListener = QuoteListener.TERMINAL;
    }

	// ------------------------------------------
	// QuoteChain interface
	// ------------------------------------------

	public int getCompressionLength() { return _compression; }
	public void setCompressionLength(int i) {
		_compression = i;
		_compressionSpec.length = _compression;
	}

	// ------------------------------------------
	// QuoteChain interface
	// ------------------------------------------


	public void update(Quote quote) {

		if (_incount == 1) {
			// 1st only

			if (_quoteCompressed == null) {
				TimeSpec ispec = quote.timespec();
				TimeSpec cspec = new TimeSpec(ispec);
				cspec.length = ispec.length * _compression;
				_quoteCompressed = new MutableQuote(_quoteCompressed.timespec(),_quoteCompressed.symbol());
			}

			_quoteCompressed.setOpen(quote.open());
			_quoteCompressed.setDateTimeRep(quote.datetimeRep());

			// recurring updates
			_quoteCompressed.setLow(quote.low());
			_quoteCompressed.setHigh(quote.high());
			_quoteCompressed.setClose(quote.close());
			_quoteCompressed.setVolume(quote.volume());

		} else {
			// recurring updates
			_quoteCompressed.setHigh(Math.max(quote.high(), _quoteCompressed.high()));
			_quoteCompressed.setLow(Math.min(quote.low(), _quoteCompressed.low()));
			_quoteCompressed.setClose(quote.close());
			_quoteCompressed.setVolume(_quoteCompressed.volume() + quote.volume());
		}

		if (_incount % _compression == 0) {
            _output.update(_quoteCompressed);
            _compressedEventListener.update(_quoteCompressed);
        }
		_incount = (_incount+1) % _compression;
	}

    /**
     * Terminate the processing chain
     */
    public void close() {
        _output.close();
		_output = QuoteChain.TERMINAL;
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
	public void setInputSpec(TimeSpec inSpec) {
		_incomingSpec = inSpec;
		_compressionSpec.units = _incomingSpec.units;
		_compressionSpec.length = _compression * _incomingSpec.length;
	}


	public Quote snapshot() {
		return new MutableQuote(_quoteCompressed);
	}

	public QuoteChain getOutput() {
		return _output;
	}

    public QuoteChain chain() { return _output; }

	public QuoteChain feeds(QuoteChain o) {
		_output = o;
        return o;
	}

	// ------------------------------------------
	// QuoteChain interface
	// ------------------------------------------

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
		strb.append(_compression);
		strb.append(')');
	}

	public String toString() {
		StringBuffer strb = new StringBuffer();
		toBuffer(strb);
		return strb.toString();
	}
}

