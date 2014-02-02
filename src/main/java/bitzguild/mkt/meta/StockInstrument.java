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

package bitzguild.mkt.meta;

import bitzguild.ts.event.BufferUtils;

public class StockInstrument extends BaseInstrument {

	/**
	 * Default Constructor
	 */
	public StockInstrument() {
		super("TEST","Test Stock","USD");
	}

	/**
	 * Convenience Constructor
	 * @param symbol
	 */
	public StockInstrument(String symbol, String description) {
		super(symbol,description,"USD");
	}
	
	/**
	 * Fully Parameterized Constructor
	 * @param symbol
	 */
	public StockInstrument(String symbol, String description, String units) {
		super(symbol,description,units);
	}
	
	public StockInstrument(StockInstrument that) {
		super(that);
	}
	
	protected StockInstrument thisOrCopy() {
		return new StockInstrument(this);
	}
	
	public StockInstrument with(String symbol, String description) {
		StockInstrument inst = thisOrCopy();
		inst._symbol = symbol;
		inst._description = description;
		return inst;
	}

	public StringBuffer toBuffer(StringBuffer sb) {
        sb.append('{');
        
        BufferUtils.pairToBuffer("symbol", _symbol, sb, true);
        BufferUtils.pairToBuffer("description", _description, sb, true);
        BufferUtils.pairToBuffer("pointVal", _bigPtVal, "#.0000", sb, true);
        BufferUtils.pairToBuffer("quoted", _quotedPts, "#.0000", sb, true);
        BufferUtils.pairToBuffer("traded", _pointIncr, "#.0000", sb, false);

        sb.append('}');
        return sb;
    }
}
