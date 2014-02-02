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

import bitzguild.mkt.event.ImmutableTick;
import bitzguild.mkt.event.Tick;
import bitzguild.ts.datetime.DateTime;
import bitzguild.ts.datetime.MutableDateTime;
import bitzguild.ts.datetime.DateTimeIterator;
import bitzguild.ts.event.gen.DoubleGenerator;
import bitzguild.ts.event.gen.LongGenerator;

public class TickGenerator implements Iterator<Tick> {
	
	public class BoundedTickGenerator implements Iterator<Tick> {

		protected int 			_count;
		protected TickGenerator	_gen;
		
		public BoundedTickGenerator(TickGenerator gen, int n) {
			_gen = gen;
			_count = Math.abs(n);
		}
		
		public boolean hasNext() { return _count > 0; }
		public void remove() {}

		public Tick next() {
			Tick t = _gen.next();
			_count--;
			return t;
		}

		public List<Tick> toList() {
			LinkedList<Tick> ticks = new LinkedList<Tick>();
			while(this.hasNext()) ticks.add(this.next());
			return ticks;
		}
		
		public List<Tick> toArray() {
			ArrayList<Tick> ticks = new ArrayList<Tick>();
			while(this.hasNext()) ticks.add(this.next());
			return ticks;
		}
		
	}
	
	
	protected boolean				_active;
	protected DateTimeIterator		_timegenerator;
	protected DoubleGenerator		_doublegenerator;
	protected LongGenerator			_longgenerator;
	protected Tick					_prototype;
	
	
	// ------------------------------------------------------------------------------------------
	// Existence
	// ------------------------------------------------------------------------------------------

	/**
	 * Default Constructor
	 */
	public TickGenerator() {
		_active = true;
		_doublegenerator = DoubleGenerator.curve(20, 100.0, 200.0);
		MutableDateTime dt = MutableDateTime.now();
		dt.setMillisSinceMidnight(0);
		_timegenerator = new DateTimeIterator(dt, DateTimeIterator.seconds(1));
		_longgenerator = new LongGenerator(1,5);
		_prototype = new ImmutableTick("DMMY");
	}

	/**
	 * 
	 * @param proto
	 * @param dgen
	 * @param tgen
	 */
	public TickGenerator(Tick proto, DoubleGenerator dgen, DateTimeIterator tgen) {
		_active = true;
		_doublegenerator = dgen;
		_timegenerator = tgen;
		_prototype = proto;
		_longgenerator = new LongGenerator(1,10);
	}

	/**
	 * 
	 * @param proto
	 * @param dgen
	 * @param tgen
	 * @param lgen
	 */
	public TickGenerator(Tick proto, DoubleGenerator dgen, DateTimeIterator tgen, LongGenerator lgen) {
		_active = true;
		_prototype = proto;
		_doublegenerator = dgen;
		_timegenerator = tgen;
		_longgenerator = lgen;
	}

	
	
	
	// ------------------------------------------------------------------------------------------
	// Public methods
	// ------------------------------------------------------------------------------------------
	
	/**
	 * Terminate the infinite series
	 */
	public void close() 		{ _active = false; }
	
	// ------------------------------------------------------------------------------------------
	// Iterator interface
	// ------------------------------------------------------------------------------------------

	@Override
	public boolean hasNext() 	{ return _active; }

	@Override
	public Tick next() 		{
		double price = _doublegenerator.next();
		long volume = _longgenerator.next();
		DateTime dt = _timegenerator.next();
		return _prototype.with(dt.rep(), price, volume);
	}

	@Override
	public void remove() 		{}

	/**
	 * 
	 * @param n
	 * @return
	 */
	public BoundedTickGenerator take(int n) {
		return new BoundedTickGenerator(this,n);
	}
}
