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

package bitzguild.ts.event.gen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class LongGenerator implements Iterator<Long> {

	public class BoundedLongGenerator implements Iterator<Long> {

		protected int 			_count;
		protected LongGenerator	_gen;
		
		public BoundedLongGenerator(LongGenerator gen, int n) {
			_gen = gen;
			_count = Math.abs(n);
		}
		
		public boolean hasNext() { return _count > 0; }
		public void remove() {}

		public Long next() {
			Long v = _gen.next();
			_count--;
			return v;
		}

		public List<Long> toList() {
			LinkedList<Long> ticks = new LinkedList<Long>();
			while(this.hasNext()) ticks.add(this.next());
			return ticks;
		}
		
		public List<Long> toArray() {
			ArrayList<Long> ticks = new ArrayList<Long>();
			while(this.hasNext()) ticks.add(this.next());
			return ticks;
		}
	}	
	
	
	protected long		_min;
	protected long		_max;
	protected boolean	_active;

	// ------------------------------------------------------------------------------------------
	// Existence
	// ------------------------------------------------------------------------------------------

	
	/**
	 * Default Constructor
	 */
	public LongGenerator() {
		_active = true;
		_min = 0L;
		_max = Long.MAX_VALUE-1;
	}

	/**
	 * Parameterized Constructor
	 * 
	 * @param min
	 * @param max
	 */
	public LongGenerator(long min, long max) {
		_active = true;
		_min = min;
		_max = max;
	}

	// ------------------------------------------------------------------------------------------
	// Public methods
	// ------------------------------------------------------------------------------------------
	
	/**
	 * Terminate the infinite series
	 */
	public void close() 		{ _active = false; }
	

	// ------------------------------------------------------------------------------------------
	// Bounds
	// ------------------------------------------------------------------------------------------

	/**
	 * 
	 * @param n
	 * @return
	 */
	public BoundedLongGenerator take(int n) {
		return new BoundedLongGenerator(this,n);
	}
	
	// ------------------------------------------------------------------------------------------
	// Iterator interface
	// ------------------------------------------------------------------------------------------

	
	public boolean hasNext() 	{ return _active; }

	public Long next() {
		return (long)((_max-_min)*Math.random()) + _min;
	}

	public void remove() {}
	
}
