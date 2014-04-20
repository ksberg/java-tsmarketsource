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

package bitzguild.mkt.io.yahoo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import bitzguild.io.LineReader;
import bitzguild.io.Updater;
import bitzguild.io.Url2File;
import bitzguild.io.UrlLineReader;

import bitzguild.mkt.event.QuoteListener;
import bitzguild.ts.datetime.DateTime;
import bitzguild.ts.datetime.MutableDateTime;
import bitzguild.ts.event.TimeSpec;
import bitzguild.ts.event.TimeUnits;

import bitzguild.mkt.event.ImmutableQuote;
import bitzguild.mkt.event.Quote;
import bitzguild.mkt.event.QuoteChain;
import bitzguild.mkt.io.QuotePrinter;
import bitzguild.mkt.io.QuoteSource;
import bitzguild.mkt.io.QuoteSourceException;

/**
 * This class parses CSV format table data from Yahoo Finance web services.
 * 
 * @see <a href="https://code.google.com/p/yahoo-finance-managed/wiki/YahooFinanceAPIs">YahooFinanceAPIs</a>
 * 
 * @author Kevin Sven Berg
 */
public class YahooFinance extends UrlLineReader<Quote> implements QuoteSource {

	public static int YearsBack = 50;
	
	/**
	 * Yahoo CSV lines are streamed in date descending order.
	 * This buffer captures Quotes and reverses the replay to
	 * output receiver.
	 * 
	 * @author Kevin Sven Berg
	 *
	 */
	public class QuoteBuffer implements Updater<Quote> {
		ArrayList<Quote>	_buffer;
		Updater<Quote>		_output;
		public QuoteBuffer() {
			_buffer = new ArrayList<Quote>();
		}
		public void update(Quote value) {
			_buffer.add(new ImmutableQuote(value));
		}
		public void reverse(Updater<Quote> output) {
			int size = _buffer.size();
			for (int i=size-1; i>=0; i--) output.update(_buffer.get(i));
		}
	}
	
	/**
	 * 
	 */
	public class YahooCsvLineParser implements LineReader<Quote> {
		protected Quote 			_prototype;
	    protected MutableDateTime 	_tmpDateTime;
		public YahooCsvLineParser(Quote prototype) {
			_prototype = prototype;
	        _tmpDateTime = new MutableDateTime();
		}
		@Override
		public Quote read(String line) throws ParseException {

	        StringTokenizer st = new StringTokenizer(line, ",");
	        if (st.countTokens() > 5) {
	            // LINE: 'Date,Open,High,Low,Close,Volume, Adj Close
	            parseYYYYMMDDWithSep(_tmpDateTime,st.nextToken(),"-");
	            long datetime = _tmpDateTime.rep();
	        	double open = Double.parseDouble(st.nextToken());
	        	double high	= Double.parseDouble(st.nextToken());
	        	double low	= Double.parseDouble(st.nextToken());
	        	double close= Double.parseDouble(st.nextToken());
	        	long volume = Long.parseLong(st.nextToken().trim());
	            
	        	return _prototype.with(datetime, open, high, low, close, volume);
	        }
	        return null;
	    }
		public void readHeader(String line) throws ParseException {}
		public boolean expectHeader() { return true; }
		
	    private void parseYYYYMMDDWithSep(MutableDateTime dt, String dateStr, String sep) {
	        StringTokenizer st = new StringTokenizer(dateStr, sep);
	        int numTokens = st.countTokens();
	        dt.setMillisSinceMidnight(0);
	        if (numTokens > 2) {
	            int iyr = Integer.parseInt(st.nextToken());
	            int imo = Integer.parseInt(st.nextToken());
	            int idy = Integer.parseInt(st.nextToken());
	            dt.setFromYearMonthDay(iyr, imo, idy);
	        } else {
	            try {
	                int ymd = Integer.parseInt(dateStr);
	                dt.setFromYYYYMMDD(ymd);
	            } catch(Exception e) {
	            	e.printStackTrace();
	            }
	        }
	    }
		
	}
	
	protected Quote					_prototype;
	protected MutableDateTime		_firstDate;
	protected MutableDateTime		_lastDate;
	

	// ------------------------------------------------------------------------------------
	// Existence
	// ------------------------------------------------------------------------------------
	
	protected YahooFinance() {
		// blocked
	}


    /**
     * Symbol Constructor. Specify the market symbol to load from YahooFinance.
     *
     * @param symbol String market symbol
     */
	public YahooFinance(String symbol) {
		MutableDateTime to = this.generateLastDate(); 
		commonYahooFinanceInit(new ImmutableQuote(new TimeSpec(), symbol), generateBackSpan(to),to);
	}

    /**
     * Prototype Constructor. The prototype will contain needed symbol for loading.
     * Use this constructor when to control mutable vs. immutable update behavior.
     *
     * @param prototype Quote
     */
	public YahooFinance(Quote prototype) {
		MutableDateTime to = this.generateLastDate(); 
		commonYahooFinanceInit(prototype, generateBackSpan(to),to);
	}
	
	public YahooFinance(Quote prototype, DateTime from, DateTime to) {
		commonYahooFinanceInit(prototype, from, to);
	}
	
	
	private void commonYahooFinanceInit(Quote prototype, DateTime from, DateTime to) {
		TimeSpec ts = prototype.timespec();
		if (ts.units < TimeUnits.DAY) {
			_prototype = new ImmutableQuote(new TimeSpec(TimeUnits.DAY,1),prototype.symbol());
		} else {
			_prototype = prototype;
		}
		_parser = new YahooCsvLineParser(_prototype);
		setDateSpan(from,to);
	}
	
	// ------------------------------------------------------------------------------------
	// QuoteFeed Interface
	// ------------------------------------------------------------------------------------
	
    public QuoteListener open(QuoteListener chain) throws QuoteSourceException {
    	try {
    		QuoteBuffer buffer = new QuoteBuffer();
    		_output = buffer;
        	parse(url());
        	buffer.reverse(chain);
    	} catch (Exception e) {
    		throw new QuoteSourceException(e);
    	}
    	return chain;
    }
    
    public void close() {
    	this._output = null;
    }
    
    public URL url() throws MalformedURLException {
    	return new URL(urlString());
    }
    
    public String urlString() {
    	return yahooQuoteURL(_prototype.symbol(), _prototype.timespec(), _firstDate, _lastDate);
    }
	
	// ------------------------------------------------------------------------------------
	// Utilities
	// ------------------------------------------------------------------------------------
	
    protected void setDateSpan(DateTime from, DateTime to) {
    	MutableDateTime a = new MutableDateTime(from);
    	_firstDate  = new MutableDateTime(from);
    	_lastDate = new MutableDateTime(to);
		if (_lastDate.compareTo(_firstDate) < 0) {
			MutableDateTime tmp = _lastDate;
			_lastDate = _firstDate;
			_firstDate = tmp;
		}
    }
    
    protected MutableDateTime generateBackSpan(DateTime to) {
    	MutableDateTime dtb = new MutableDateTime(to);
    	dtb.addYears(-Math.abs(YearsBack));
    	return dtb;
    }

    protected MutableDateTime generateLastDate() {
    	MutableDateTime dt = MutableDateTime.now();
		dt.setMillisSinceMidnight(0);
		return dt;
    }
    
    protected void generateDateSpan() {
		_lastDate = MutableDateTime.now();
		_lastDate.setMillisSinceMidnight(0);
		_firstDate = generateBackSpan(_lastDate);
    }
    
    /**
     * <p>
     * Forms a URL for access to Yahoo financial data tables.
     * 
     * NOTE: this is an older format than specified on Yahoo API pages, but still works
     * </p>
     * <ul>
     * <li>http://table.finance.yahoo.com/table.csv?a=8&b=19&c=1984&d=11&e=21&f=2002&s=msft&y=0&g=d&ignore=.csv
     * <li>http://table.finance.yahoo.com/table.csv?a=8&b=19&c=1984&d=11&e=21&f=2002&s=msft&y=0&g=w&ignore=.csv
     * <li>http://table.finance.yahoo.com/table.csv?a=8&b=19&c=1984&d=11&e=21&f=2002&s=msft&y=0&g=m&ignore=.csv
     * <li>http://table.finance.yahoo.com/table.csv?a=8&b=19&c=1984&d=11&e=21&f=2002&s=msft&y=0&g=v&ignore=.csv
     * </ul>
     */
    protected String yahooQuoteURL(String symbol, TimeSpec spec, MutableDateTime fromDate, MutableDateTime toDate) {

        String tickerStr = symbol;

        int idyFrom = fromDate.day();
        int imoFrom = fromDate.month()-1;
        int iyrFrom = fromDate.year();
        int idyTo = toDate.day();
        int imoTo = toDate.month()-1;
        int iyrTo = toDate.year();

        String unitStr = "d";
        switch(spec.units) {
            case TimeUnits.DAY:
                unitStr = "d";
                break;
            case TimeUnits.WEEK:
                unitStr = "w";
                break;
            case TimeUnits.MONTH:
                unitStr = "m";
                break;
            default:
                unitStr = "d";
                break;
        }

        StringBuffer urlBuff = new StringBuffer("http://table.finance.yahoo.com/table.csv?");
        urlBuff.append("a=").append(imoFrom);
        urlBuff.append("&b=").append(idyFrom);
        urlBuff.append("&c=").append(iyrFrom);
        urlBuff.append("&d=").append(imoTo);
        urlBuff.append("&e=").append(idyTo);
        urlBuff.append("&f=").append(iyrTo);
        urlBuff.append("&s=").append(tickerStr);
        urlBuff.append("&y=0&g=").append(unitStr); // g = d (daily), w (weekly), m (monthly)
        urlBuff.append("&ignore=.csv");

        return urlBuff.toString();
    }

    /**
     *
     * @param args command line: SYMBOL [FILE]
     */
    public static void main(String[] args) {
        String symbol = args.length > 0 ? args[0] : "AAPL";
        String fileName = args.length > 1 ? args[1] : "symbol_out.csv";
        YahooFinance yahoo = new YahooFinance(symbol);
    	try {
            Url2File u2f = new Url2File(yahoo.url(),fileName);
            u2f.read();
        	yahoo.open(new QuotePrinter());
    	} catch (QuoteSourceException qse) {
            System.out.println("Quote processing exception: " + qse.getMessage());
    	} catch (MalformedURLException me) {
            System.out.println("Malformed URL: " + yahoo.urlString());
        } catch (IOException ioe) {
            System.out.println("File I/O Problem: " + fileName);
        } finally {
            yahoo.close();
        }
    }
}
