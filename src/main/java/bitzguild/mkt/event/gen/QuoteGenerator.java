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

package bitzguild.mkt.event.gen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import bitzguild.mkt.event.ImmutableQuote;
import bitzguild.mkt.event.Quote;
import bitzguild.ts.datetime.DateTime;
import bitzguild.ts.datetime.DateTimeIterator;
import bitzguild.ts.datetime.MutableDateTime;
import bitzguild.ts.event.TimeSpec;
import bitzguild.ts.event.TimeUnits;
import bitzguild.ts.event.gen.DoubleGenerator;
import bitzguild.ts.event.gen.LongGenerator;

public class QuoteGenerator implements Iterator<Quote> {

	public class BoundedQuoteGenerator implements Iterator<Quote> {

		protected int 				_count;
		protected QuoteGenerator	_gen;
		
		public BoundedQuoteGenerator(QuoteGenerator gen, int n) {
			_gen = gen;
			_count = Math.abs(n);
		}
		
		public boolean hasNext() { return _count > 0; }
		public void remove() {}

		public Quote next() {
			Quote q = _gen.next();
			_count--;
			return q;
		}

		public List<Quote> toList() {
			LinkedList<Quote> quotes = new LinkedList<Quote>();
			while(this.hasNext()) quotes.add(this.next());
			return quotes;
		}
		
		public List<Quote> toArray() {
			ArrayList<Quote> quotes = new ArrayList<Quote>();
			while(this.hasNext()) quotes.add(this.next());
			return quotes;
		}
		
	}
	
	
	protected boolean				_active;
	protected DateTimeIterator		_timegenerator;
	protected DoubleGenerator		_doublegenerator;
	protected LongGenerator			_longgenerator;
	protected Quote					_prototype;
	protected double				_span;
	
	// ------------------------------------------------------------------------------------------
	// Existence
	// ------------------------------------------------------------------------------------------

	/**
	 * Private Constructor - only used locally for scoping rules
	 */
	public QuoteGenerator() {
		_active = true;
		_doublegenerator = DoubleGenerator.curve(20, 100.0, 200.0);
		MutableDateTime dt = MutableDateTime.now();
		dt.setMillisSinceMidnight(0);
		_timegenerator = new DateTimeIterator(dt, DateTimeIterator.seconds(1));
		_longgenerator = new LongGenerator(1,5);
		_prototype = new ImmutableQuote(new TimeSpec(TimeUnits.MINUTE,1), "DMMY");
		_span = 20.0;
	}
	
	/**
	 * 
	 * @param proto
	 * @param dgen
	 * @param tgen
	 */
	public QuoteGenerator(Quote proto, DoubleGenerator dgen, DateTimeIterator tgen) {
		_active = true;
		_doublegenerator = dgen;
		_timegenerator = tgen;
		_prototype = proto;
		_longgenerator = new LongGenerator(1,10);
		_span = 20.0;
	}
	
	/**
	 * 
	 * @param proto
	 * @param dgen
	 * @param tgen
	 * @param lgen
	 */
	public QuoteGenerator(Quote proto, DoubleGenerator dgen, DateTimeIterator tgen, LongGenerator lgen, double span) {
		_active = true;
		_prototype = proto;
		_doublegenerator = dgen;
		_timegenerator = tgen;
		_longgenerator = lgen;
		_span = span;
	}
	
	// ------------------------------------------------------------------------------------------
	// Public methods
	// ------------------------------------------------------------------------------------------
	
	/**
	 * Terminate the infinite series
	 */
	public void close() 		{ _active = false; }
	
	
	/**
	 * 
	 * @param n int count
	 * @return
	 */
	public BoundedQuoteGenerator take(int n) {
		return new BoundedQuoteGenerator(this,n);
	}
	
	// ------------------------------------------------------------------------------------------
	// Iterator interface
	// ------------------------------------------------------------------------------------------

	@Override
	public boolean hasNext() 	{ return _active; }

	@Override
	public Quote next() {
    	double open,high,low,close;
    	double overHigh, underLow;

		double price = _doublegenerator.next();
		long volume = _longgenerator.next();
		DateTime dt = _timegenerator.next();

		high 	= price + Math.random()*_span;
		low 	= price - Math.random()*_span;
		overHigh = high*1.05;
		underLow = low*0.95;
		open 	= (overHigh-underLow)*Math.random() + underLow;
		close 	= (overHigh-underLow)*Math.random() + underLow;
		open 	= Math.max(low,Math.max(high,open));
		close 	= Math.max(low,Math.max(high,close));
		
		return _prototype.with(dt.rep(), open, overHigh, underLow, close, volume);
	}

	@Override
	public void remove() 		{}
	

}
