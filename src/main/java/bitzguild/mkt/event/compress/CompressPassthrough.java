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

/**
 * <p>
 * Does zero compression or but does pass quote through
 * to the observer. Like <code>CompressorNull</code> this
 * class enables one algorithm to serve special cases.
 * When using this class, the Source and Observer
 * should be different instances.
 * </p>
 */
public class CompressPassthrough implements QuoteCompression {

	protected QuoteChain    _output;
    protected QuoteListener _compressedEventListener;

	protected TimeSpec 		_incomingSpec;
    protected MutableQuote  _quote;


	public CompressPassthrough() {
		super();
        _quote = new MutableQuote();
	}

	// ------------------------------------------
	// QuoteChain interface
	// ------------------------------------------

    public QuoteChain chain() { return _output; }

    /**
     * Assign next in feeds. This characteristic
     * is central to forming a linked series of
     * entities that receive and process Quote
     * updates in a similar manner.
     *
     * @param other
     * @return QuoteChain
     */
    public QuoteChain feeds(QuoteChain other) {
        _output = other;
        return other;
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


	public void update(Quote quote) {
		_output.update(quote);
	}

    public void close() {
        _output.close();
    }


	// ------------------------------------------
	// QuoteCompression interface
	// ------------------------------------------

    public QuoteListener listener() { return _compressedEventListener; }

    /**
     * Passthrough does not produce compressed events
     * so listener will be ignored
     *
     * @param listener ignored
     */
    public QuoteCompression connect(QuoteListener listener) {
        _compressedEventListener = listener;
        return this;
    }

	/**
	 * Answer intermediate accumulated Quote representation
	 * 
	 * @return Quote
	 */
	public Quote snapshot() {
		return new MutableQuote(this._quote);
	}
    
	/**
	 * Answers a copy of the TimeSpec for compression.
	 *
	 * @return TimeSpec market data quote spec
	 */
	public TimeSpec getCompression() {
		return null;
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
	}


	public void snapshot(Quote q) {
	}

	public QuoteChain getOutput() {
		return _output;
	}

	// ------------------------------------------
	// QuoteChain interface
	// ------------------------------------------


	// ------------------------------------------
	// Printing / Debug
	// ------------------------------------------

	public void toBuffer(StringBuffer strb) {
		strb.append(getClass().getSimpleName());
		strb.append('(');
		strb.append(')');
	}

	public String toString() {
		StringBuffer strb = new StringBuffer();
		toBuffer(strb);
		return strb.toString();
	}

}

