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

package bitzguild.mkt.io.tickdata;

import java.text.ParseException;

import bitzguild.ts.datetime.MutableDateTime;
import bitzguild.io.FileLineReader;
import bitzguild.io.LineReader;
import bitzguild.mkt.event.MutableTick;
import bitzguild.mkt.event.Tick;
import bitzguild.mkt.event.TickObserver;
import bitzguild.mkt.io.TickPrinter;
import bitzguild.mkt.io.TickSource;
import bitzguild.mkt.io.TickSourceException;

/**
 * This class parses CSV format data from the vendor TickData.
 * 
 * @see <a href="http://www.tickdata.com">http://www.tickdata.com</a>
 * 
 * @author Kevin Sven Berg
 */
public class TickDataReader extends FileLineReader<Tick> implements TickSource {

	public class TDCsvLineParser implements LineReader<Tick> {
		protected Tick				_prototype;
		protected MutableDateTime	_tmpDateTime;
		
		public TDCsvLineParser(Tick prototype) {
			_prototype = prototype;
	        _tmpDateTime = new MutableDateTime();
		}
		
		/**
		 * Sample Data
		 * Date,Time,Open,High,Low,Close,Volume,Tick Count
		 * 19970910,00:06:49.000,934.00,934.00,934.00,934.00,0,1
		 * 20131115,16:14:59.957,1794.25,1794.25,1794.25,1794.25,12,1
		 */
		public Tick read(String line) throws ParseException {
	        String splits[] = line.split(",");
	        String yymmdd = splits[0];

	        // --> 19970910
	        int year = Integer.parseInt(yymmdd.substring(0,4));
	        int month = Integer.parseInt(yymmdd.substring(4, 6));
	        int day = Integer.parseInt(yymmdd.substring(6, 8));

	        if (month > 12 || day > 31) {
	        	throw new ParseException("Bad Date: " + line,0);
	        }

	        // --> 00:06:49.000
	        String hhmmssmmm = splits[1];
	        int hour = Integer.parseInt(hhmmssmmm.substring(0, 2));
	        int minute = Integer.parseInt(hhmmssmmm.substring(3, 5));
	        int second = Integer.parseInt(hhmmssmmm.substring(6, 8));
	        int millis = Integer.parseInt(hhmmssmmm.substring(9,12));

	        _tmpDateTime.setFromYearMonthDay(year, month, day);
	        _tmpDateTime.setHoursMinutesSecondsMillis(hour, minute, second, millis);

	        long datetime = _tmpDateTime.rep();
			double price = Double.parseDouble(splits[5]);
	        int volume = Integer.parseInt(splits[6]);
			
			return _prototype.with(datetime, price, volume);
		}

		public void readHeader(String line) throws ParseException {}
		public boolean expectHeader() { return true;}
		
	}
	protected Tick				_prototype;
	protected MutableDateTime	_firstDate;
	protected MutableDateTime	_lastDate;
	protected String			_fileName;
	
	protected TickDataReader() {
		// blocked
	}
	
	public TickDataReader(Tick prototype, String fileName) {
		_prototype = prototype;
		_fileName = fileName;
		_parser = new TDCsvLineParser(prototype);
	}

	public void open(TickObserver observer) throws TickSourceException {
		try {
			_output = observer;
			read(_fileName);
		} catch (Exception e) {
			throw new TickSourceException(e);
		}
	}

	@Override
	public void close() {
	}
	
    public static void main(String[] args) {
    	try {
    		String fileName = args[0];
    		MutableTick tick = new MutableTick("ES");
    		TickDataReader reader = new TickDataReader(tick,fileName);
        	reader.open(new TickPrinter());
    	} catch (TickSourceException e) {
    		e.printStackTrace();
    	}
    }
	
}
