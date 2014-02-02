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

public class BaseInstrument implements MarketInstrument {

	protected String	_symbol;
	protected String	_description;
	protected String	_units;
	protected double	_bigPtVal;
	protected double	_quotedPts;
	protected double	_pointIncr;
	
	/**
	 * Default Constructor
	 */
	public BaseInstrument() {
		_bigPtVal 	= 1.0;
		_quotedPts 	= 0.01;
		_pointIncr 	= 0.01;
	}
	
	/**
	 * Fully Parameterized Constructor
	 * @param symbol
	 */
	public BaseInstrument(String symbol, String description, String units) {
		_symbol = symbol;
		_description = description;
		_units = units;
		
		_bigPtVal 	= 1.0;
		_quotedPts 	= 0.01;
		_pointIncr 	= 0.01;
	}

	/**
	 * Copy Constructor
	 * 
	 * @param that
	 */
	public BaseInstrument(BaseInstrument that) {
		this._symbol		= that._symbol;
		this._description	= that._description;
		this._units			= that._units;
		this._bigPtVal		= that._bigPtVal;
		this._quotedPts		= that._quotedPts;
		this._pointIncr		= that._pointIncr;
	}
	
	
	public String symbol() 					{ return _symbol; }
	public String description() 			{ return _description; }
	public String units() 					{ return _units; }
	public String category() 				{ return "Stock"; }
	public double pointValue() 				{ return _bigPtVal; }
	public double quotedIncrement() 		{ return _quotedPts; }
	public double minimumIncrement() 		{ return _pointIncr; }
	public int unitsPerTrade() 				{ return 1; }
	public double unitCost(double price) 	{ return price; }
	public boolean expires() 				{ return false; }

	
	
	public StringBuffer toBuffer(StringBuffer strb) {
		return strb;
	}
	
	public String toString() {
		return toBuffer(new StringBuffer()).toString();
	}
}
