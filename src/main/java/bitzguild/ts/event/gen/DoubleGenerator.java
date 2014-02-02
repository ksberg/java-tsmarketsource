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

public class DoubleGenerator implements Iterator<Double> {

	public class BoundedDoubleGenerator implements Iterator<Double> {

		protected int 				_count;
		protected DoubleGenerator	_gen;
		
		public BoundedDoubleGenerator(DoubleGenerator gen, int n) {
			_gen = gen;
			_count = Math.abs(n);
		}
		
		public boolean hasNext() { return _count > 0; }
		public void remove() {}

		public Double next() {
			Double v = _gen.next();
			_count--;
			return v;
		}

		public List<Double> toList() {
			LinkedList<Double> ticks = new LinkedList<Double>();
			while(this.hasNext()) ticks.add(this.next());
			return ticks;
		}
		
		public List<Double> toArray() {
			ArrayList<Double> ticks = new ArrayList<Double>();
			while(this.hasNext()) ticks.add(this.next());
			return ticks;
		}
		
	}
	
	protected int 		_index;
	protected int		_period;
	protected double	_amplitude;
	protected double	_offset;
	protected boolean	_active;
	
	// ------------------------------------------------------------------------------------------
	// Existence
	// ------------------------------------------------------------------------------------------

	/**
	 * Private Constructor - only used locally for scoping rules
	 */
	private DoubleGenerator() {}
	
	/**
	 * Default Constructor
	 * 
	 * @param period
	 * @param amplitude
	 * @param offset
	 */
	public DoubleGenerator(int period, double amplitude, double offset) {
		_index = 0;
		_period = period == 0 ? 1 : Math.abs(period);
		_amplitude = amplitude;
		_offset = offset;
		_active = true;
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
	public Double next() 		{ return Math.random();}

	@Override
	public void remove() 		{}
	

	// ------------------------------------------------------------------------------------------
	// Bounds
	// ------------------------------------------------------------------------------------------

	/**
	 * 
	 * @param n
	 * @return
	 */
	public BoundedDoubleGenerator take(int n) {
		return new BoundedDoubleGenerator(this,n);
	}
	
	// ------------------------------------------------------------------------------------------
	// Subclass / Specialization
	// ------------------------------------------------------------------------------------------
	
	/**
	 * Infinite Saw Wave Generator
	 */
	public class SawGenerator extends DoubleGenerator {
		protected double _slope;
		public SawGenerator(int period, double amplitude, double offset) {
			super(period, amplitude, offset);
			_slope = amplitude / (double)_period;
		}
		public Double next() {
			double result = _offset + (_index*_slope);
			_index = ((_index+1) % _period);
			return result;
		}
	}

	/**
	 * Infinite Sin Wave Generator
	 */
	public class SinGenerator extends DoubleGenerator {
		protected double _sliceOfPi;
		public SinGenerator(int period, double amplitude, double offset) {
			super(period, amplitude, offset);
			_sliceOfPi =  Math.PI*2.0 / (double)_period;
		}
		public Double next() {
			double result = Math.sin(_sliceOfPi*_index) * _amplitude + _offset;
			_index = ((_index+1) % _period);
			return result;
		}
	}
	
	
	/**
	 * Infinite Square Wave Generator
	 */
	public class SquareGenerator extends DoubleGenerator {
		public SquareGenerator(int period, double amplitude, double offset) {
			super(period/2, amplitude, offset);
		}
		public Double next() {
			double d = (_index / _period) % 2 == 0 ? _amplitude + _offset : _offset;
			_index = ((_index+1) % (_period<<1));
			return d;
		}
	}
	
	/**
	 * Infinite Curve Generator. Renders a random curve with smoothing bias. 
	 * The intent is to mimic average price movements (e.g. AvgHLC) using an
	 * exponential moving average to smooth randomized results.
	 * and then randomizing results. This can be used as a seed for testing other functions.
	 */
	public class CurveGenerator extends DoubleGenerator {
		protected double	_alpha;
		protected double	_updn, _bias;
		protected double	_run, _minRun, _maxRun;
		protected double	_lastPoint, _priorPoint, _lastXma;
		
		public CurveGenerator(int period, double amplitude, double offset) {
			super(period, amplitude, offset);
			_alpha = 2.0 / (_period + 1.0);
			_updn = 1.0;
			_lastPoint = _offset;
			_priorPoint = _lastPoint;
			_lastXma = _lastPoint;
			_bias = 0.0;
			setMinMax(1,10);
		}
		public CurveGenerator(int period, double amplitude, double offset, int minRun, int maxRun) {
			super(period, amplitude, offset);
			_alpha = 2.0 / (_period + 1.0);
			_updn = 1.0;
			_lastPoint = _offset;
			_priorPoint = _lastPoint;
			_lastXma = _lastPoint;
			_bias = 0.0;
			setMinMax(minRun,maxRun);
		}
		private void setMinMax(int a, int b) {
			int absA = Math.abs(a);
			int absB = Math.abs(b);
			_minRun = Math.min(absA,absB);
			_maxRun = Math.max(absA,absB);
		}
		public Double next() {
			double candidate, result;

    		if (_index % _run == 0) {
        		_updn = Math.random() >= 0.5 ? 1.0 : -1.0;
    			_run = (int)(Math.random() * (_maxRun-_minRun) + _minRun);
    			_index = 0;
    		}
    		_bias = _lastPoint - _priorPoint;
    		candidate = _lastPoint + _bias + (_updn * Math.random()*_amplitude);
    		result = _alpha * (candidate - _lastXma) + _lastXma; // Exponential Moving Average (XMA) damping function
			return result;
		}
	}

	/**
	 * Infinite Scatter Generator. Adds parameterized noise to an existing generator.
	 */
	public class ScatterGenerator extends DoubleGenerator {
		protected DoubleGenerator	_generator;
		protected double			_percent;
		protected double			_factor;
		public ScatterGenerator(DoubleGenerator generator, double percent, double factor) {
			super(1, percent, factor);
			_percent = percent;
			_factor = factor;
			_generator = generator;
		}
		public Double next() {
			double d = _generator.next();
			double updn = Math.random() >= 0.5 ? 1.0 : -1.0;
			if (_percent > 0.001) {
				d = d + (updn * Math.random() * _percent * d);
			} else {
				d = d + updn * Math.random() * _factor;
			}
			return d;
		}
	}

	
	// ------------------------------------------------------------------------------------------
	// Static Convenience Functions
	// ------------------------------------------------------------------------------------------

	
	/**
	 * Answer Saw Wave Generator (Iterator<Double>)
	 *  
	 * @param period
	 * @param amplitude
	 * @param offset
	 * @return DoubleGenerator
	 */
	public static DoubleGenerator saw(int period, double amplitude, double offset) {
		return (new DoubleGenerator()).sawHelper(period, amplitude, offset);
	}

	/**
	 * Answer Sin Wave Generator (Iterator<Double>)
	 *  
	 * @param period
	 * @param amplitude
	 * @param offset
	 * @return DoubleGenerator
	 */
	public static DoubleGenerator sin(int period, double amplitude, double offset) {
		return (new DoubleGenerator()).sinHelper(period, amplitude, offset);
	}
	
	/**
	 * Answer Square Wave Generator (Iterator<Double>)
	 *  
	 * @param period
	 * @param amplitude
	 * @param offset
	 * @return DoubleGenerator
	 */
	public static DoubleGenerator square(int period, double amplitude, double offset) {
		return (new DoubleGenerator()).squareHelper(period, amplitude, offset);
	}
	
	/**
	 * Answer Curve Generator (Iterator<Double>)
	 *  
	 * @param period of damping function
	 * @param amplitude of how random will vary
	 * @param offset
	 * @return DoubleGenerator
	 */
	public static DoubleGenerator curve(int period, double amplitude, double offset) {
		return (new DoubleGenerator()).curveHelper(period, amplitude, offset);
	}

	
	
	/**
	 * Answer Scatter Generator (Iterator<Double>)
	 * 
	 * Takes an existing generator and adds randomization parameterized by
	 * percent and factor. If percent is zero, then factor is used. If percent
	 * is non-zero, then original value is changed by at most X percent. This
	 * will often produce greater different between adjacent values than the
	 * original series.
	 *  
	 * @param DoubleGenerator another Double generator
	 * @param percent variation
	 * @param factor variation
	 * @return DoubleGenerator
	 */
	public static DoubleGenerator scatter(DoubleGenerator generator, double percent, double factor) {
		return (new DoubleGenerator()).scatterHelper(generator, percent, factor);
	}
	
	
	// ------------------------------------------------------------------------------------------
	// Private Helpers for Scoping Rules
	// ------------------------------------------------------------------------------------------

	private DoubleGenerator sawHelper(int period, double amplitude, double offset) {
		return new SawGenerator(period, amplitude, offset);
	}

	private DoubleGenerator sinHelper(int period, double amplitude, double offset) {
		return new SinGenerator(period, amplitude, offset);
	}

	private DoubleGenerator squareHelper(int period, double amplitude, double offset) {
		return new SquareGenerator(period, amplitude, offset);
	}
	
	private DoubleGenerator curveHelper(int period, double amplitude, double offset) {
		return new CurveGenerator(period, amplitude, offset);
	}
	
	private DoubleGenerator scatterHelper(DoubleGenerator generator, double percent, double factor) {
		return new ScatterGenerator(generator, percent, factor);
	}
	
	
}
