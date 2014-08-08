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

package bitzguild.mkt.event;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;


import bitzguild.ts.datetime.DateTime;
import bitzguild.ts.datetime.ImmutableDateTime;
import bitzguild.ts.event.BufferUtils;

/**
 * Time and Sale data.
 */
public class ImmutableTick implements Tick, Serializable {

    static final long serialVersionUID = "B0D0F33F-62A9-4B8C-B3CA-E7EE5D100C10".hashCode();

    public static String    NoSymbol = "****";
    public static char      NoExchange = '*';


    protected long  	_time;
    protected String   	_symbol;
    protected double   	_price;
    protected long     	_volume;
    protected char		_exchange;

    /**
     * Default Constructor
     */
    public ImmutableTick() {
    	_time = 0L;
        _symbol = NoSymbol;
        _exchange = NoExchange;
    	_price = 0.0;
    	_volume = 0L;
    }

    /**
     * Copy Constructor
     *
     * @param that ImmutableQuote
     */
    public ImmutableTick(Tick that) {
        this._time 		= that.datetimeRep();
        this._symbol 	= that.symbol();
        this._exchange 	= that.exchange();
        this._price  	= that.price();
        this._volume 	= that.volume();
    }
    
    
    /**
     * Constructor with Market Symbol
     * @param symbol
     */
    public ImmutableTick(String symbol) {
        _time = 0L;
        _symbol = symbol;
        _exchange = NoExchange;
    	_price = 0.0;
    	_volume = 0L;
    }


    /**
     * Fully parameterized Constructor
     *
     * @param time bitzguild DateTime serialized representation
     * @param symbol market symbol
     * @param xchg exchange character
     * @param price trade settle price (between bid/ask)
     * @param volume number traded
     */
    public ImmutableTick(long time, String symbol, char xchg, double price, long volume) {
        _time = time;
    	_symbol = symbol;
        _exchange = xchg;
    	_price = price;
        _volume = volume;
    }

    /**
     * Key existence utility to determine mutability.
     * 
     * @return ImmutableTick or subclass
     */
    protected ImmutableTick thisOrCopy() {
    	return new ImmutableTick(this);
    }
    
    
    // --------------------------------------------------------------------------------
    // Accessors (Getters)
    // --------------------------------------------------------------------------------
    
    public String 	symbol()	{ return _symbol; 	}
    public char		exchange()	{ return _exchange; }
	public double 	price()		{ return _price;	}
	public long 	volume()	{ return _volume;	}		
    
    public DateTime datetime() {
        return new ImmutableDateTime(_time);
    }
    
    public long datetimeRep() {
        return _time;
    }
    
    public Tick withSymbol(String symbol) {
    	ImmutableTick t = thisOrCopy();
    	t._symbol = symbol;
    	return t;
    }
    
    public Tick withExchange(char exchange) {
    	ImmutableTick t = thisOrCopy();
    	t._exchange = exchange;
    	return t;
    }

	public Tick with(long datetime, double price, long volume) {
    	ImmutableTick t = thisOrCopy();
    	t._time = datetime;
    	t._price = price;
    	t._volume = volume;
    	return t;
	}

    
    /**
     * 
     * @param jsonStringRep
     * @return
     * @throws ParseException
     */
    public Tick fromString(String jsonStringRep) throws ParseException {
    	ImmutableTick t = thisOrCopy();
        try {
            String str = jsonStringRep.substring(1,jsonStringRep.length()-2);
            StringTokenizer strk = new StringTokenizer(str, ",");

            String[] symSplit = strk.nextToken().split(":");
            String[] dateTimeSplit = strk.nextToken().split(":");
            String[] priceSplit = strk.nextToken().split(":");
            String[] volumeSplit = strk.nextToken().split(":");
            String[] exchangeSplit = strk.nextToken().split(":");

            t._symbol = symSplit[1].substring(1, symSplit[1].length() - 1);
            t._time = Long.parseLong(dateTimeSplit[1]);
            t._price = Double.parseDouble(priceSplit[1]);
            t._volume = Integer.parseInt(volumeSplit[1]);
            t._exchange = exchangeSplit[1].charAt(1);

        } catch (Exception e) {

        }
    	return t;
    }

    
    /**
     * Effecient version of toString()
     * Output for as JSON string
     * Can be combined with other object toBuffer() invocations
     * which dump output into collective StringBuffer.
     *
     * @param sb output buffer
     */
    public StringBuffer toBuffer(StringBuffer sb) {
        sb.append('{');
        
        BufferUtils.pairToBuffer("sym", _symbol, sb, true);
        BufferUtils.pairToBuffer("dt", datetime().toString(), sb, true);
        BufferUtils.pairToBuffer("p", _price, "#.000", sb, true);
        BufferUtils.pairToBuffer("v", _volume, sb, true);
        BufferUtils.pairToBuffer("x", _exchange, sb, false);

        sb.append('}');
        return sb;
    }

    /**
     *
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        toBuffer(sb);
        return sb.toString();
    }

    public static String toString(Collection<ImmutableTick> ticks) {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        for (ImmutableTick tick : ticks) {
            tick.toBuffer(sb);
            sb.append(',');
        }
        sb.setLength(sb.length()-1);
        sb.append(']');
        return sb.toString();
    }

    public static Collection<ImmutableTick> fromArrayString(String json) {
        ArrayList<ImmutableTick> ticklist = new ArrayList<ImmutableTick>();
        // TODO: parse string into ticks
        return ticklist;
    }


}
