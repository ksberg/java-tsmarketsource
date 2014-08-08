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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.text.ParseException;

import bitzguild.io.Url2File;
import bitzguild.io.UrlLineReader;

import bitzguild.mkt.io.*;
import bitzguild.mkt.event.QuoteListener;
import bitzguild.ts.datetime.DateTime;
import bitzguild.ts.datetime.MutableDateTime;
import bitzguild.ts.event.TimeSpec;
import bitzguild.ts.event.TimeUnits;

import bitzguild.mkt.event.ImmutableQuote;
import bitzguild.mkt.event.Quote;

/**
 * This class parses CSV format table data from Yahoo Finance web services.
 * 
 * @see <a href="https://code.google.com/p/yahoo-finance-managed/wiki/YahooFinanceAPIs">YahooFinanceAPIs</a>
 * 
 * @author Kevin Sven Berg
 */
public class YahooNet extends UrlLineReader<Quote> implements QuoteSource {

	public static int YearsBack = 50;
	
	protected Quote					_prototype;
	protected MutableDateTime		_firstDate;
	protected MutableDateTime		_lastDate;
	

	// ------------------------------------------------------------------------------------
	// Existence
	// ------------------------------------------------------------------------------------
	
	protected YahooNet() {
		// blocked
	}


    /**
     * Symbol Constructor. Specify the market symbol to load from YahooNet.
     *
     * @param symbol String market symbol
     */
	public YahooNet(String symbol) {
		MutableDateTime to = this.generateLastDate(); 
		commonYahooFinanceInit(new ImmutableQuote(new TimeSpec(), symbol), generateBackSpan(to),to);
	}

    /**
     * Prototype Constructor. The prototype will contain needed symbol for loading.
     * Use this constructor when to control mutable vs. immutable update behavior.
     *
     * @param prototype Quote
     */
	public YahooNet(Quote prototype) {
		MutableDateTime to = this.generateLastDate(); 
		commonYahooFinanceInit(prototype, generateBackSpan(to),to);
	}
	
	public YahooNet(Quote prototype, DateTime from, DateTime to) {
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
            YahooBuffer buffer = new YahooBuffer();
            _output = buffer;
            parse(url());
            buffer.reverse(chain);
        } catch (SocketException se) {
            throw new QuoteSourceAccessException(se, _prototype.symbol());
        } catch (ParseException pe) {
            throw new QuoteSourceParseException(pe, _prototype.symbol());
        } catch (FileNotFoundException fnfe) {
            throw new QuoteSourceNotFoundException(fnfe, _prototype.symbol());
    	} catch (Exception e) {
    		throw new QuoteSourceException(e, _prototype.symbol());
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

        StringBuilder urlBuff = new StringBuilder("http://table.finance.yahoo.com/table.csv?");
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
        YahooNet yahoo = new YahooNet(symbol);
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
