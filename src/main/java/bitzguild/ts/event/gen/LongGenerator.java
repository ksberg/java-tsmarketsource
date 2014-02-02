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
