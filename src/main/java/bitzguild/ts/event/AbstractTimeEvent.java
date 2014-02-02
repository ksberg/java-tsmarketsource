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

 package bitzguild.ts.event;

import bitzguild.ts.datetime.DateTime;
import bitzguild.ts.datetime.ImmutableDateTime;


public class AbstractTimeEvent extends BufferUtils implements BinnedTimeEvent {

    protected long  _spec;
    protected long  _time;

    /**
     * Default Constructor
     */
    protected AbstractTimeEvent() {
        _spec = (new TimeSpec()).rep();
        _time = 0L;
    }

    /**
     * Copy Constructor
     */
    protected AbstractTimeEvent(BinnedTimeEvent that) {
        _spec = that.timespecRep();
        _time = that.datetimeRep();
    }


    /**
     * TimeSeriesSpec Constructor
     *
     * @param spec TimeSeriesSpec
     */
    protected AbstractTimeEvent(TimeSpec spec, long time) {
        _spec = spec.rep();
        _time = time;
    }


    /**
     * Serial TimeSeriesSpec Constructor
     *
     * @param specrep long
     */
    protected AbstractTimeEvent(long specrep, long time) {
        _spec = specrep;
        _time = time;
    }
    
    
    public DateTime datetime() {
        return new ImmutableDateTime(_time);
    }

    public TimeSpec timespec() {
        return new TimeSpec(_spec);
    }


    public long timespecRep() {
        return _spec;
    }

    public long datetimeRep() {
        return _time;
    }


    public BinnedTimeEvent fold(BinnedTimeEvent e) {
       return this;
    }

    
	public StringBuffer toBuffer(StringBuffer sb) {
		return sb;
	}
	
	public String toString() {
		return toBuffer(new StringBuffer()).toString();
	}


    
}
