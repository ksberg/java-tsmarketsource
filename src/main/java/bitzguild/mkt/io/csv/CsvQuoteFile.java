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

package bitzguild.mkt.io.csv;

import bitzguild.io.FileLineReader;
import bitzguild.mkt.event.ImmutableQuote;
import bitzguild.mkt.event.MutableQuote;
import bitzguild.mkt.event.Quote;
import bitzguild.mkt.event.QuoteListener;
import bitzguild.mkt.io.QuotePrinter;
import bitzguild.mkt.io.QuoteSource;
import bitzguild.mkt.io.QuoteSourceException;
import bitzguild.mkt.io.yahoo.YahooBuffer;
import bitzguild.mkt.io.yahoo.YahooCsvLineParser;
import bitzguild.ts.event.TimeSpec;

/**
 * This class parses CSV format table data from Yahoo Finance web services.
 * 
 * @see <a href="https://code.google.com/p/yahoo-finance-managed/wiki/YahooFinanceAPIs">YahooFinanceAPIs</a>
 * 
 * @author Kevin Sven Berg
 */
public class CsvQuoteFile extends FileLineReader<Quote> implements QuoteSource {

	public static int YearsBack = 50;

	protected Quote					_prototype;
    protected String                _fileName;


	// ------------------------------------------------------------------------------------
	// Existence
	// ------------------------------------------------------------------------------------

	protected CsvQuoteFile() {
		// blocked
	}


    /**
     * Symbol Constructor. Specify the market symbol to load from YahooNet.
     *
     * @param symbol String market symbol
     */
	public CsvQuoteFile(String fileName, String symbol) {
        _prototype = new MutableQuote(new TimeSpec(), symbol);
        _fileName = fileName;
        _parser = new CsvQuoteLineReader(_prototype);
	}

	// ------------------------------------------------------------------------------------
	// QuoteFeed Interface
	// ------------------------------------------------------------------------------------
	
    public QuoteListener open(QuoteListener chain) throws QuoteSourceException {
    	try {
            this._output = chain;
            this.read(_fileName);
    	} catch (Exception e) {
    		throw new QuoteSourceException(e);
    	}
    	return chain;
    }
    
    public void close() {
    	this._output = null;
    }

	// ------------------------------------------------------------------------------------
	// Utilities
	// ------------------------------------------------------------------------------------
	

    /**
     *
     * @param args command line: SYMBOL [FILE]
     */
    public static void main(String[] args) {
        String symbol = args.length > 0 ? args[0] : "AAPL";
        String fileName = args.length > 0 ? args[1] : symbol + ".csv";
        String output = args.length > 1 ? args[2] : symbol + "_out.csv";

        CsvQuoteFile yahoo = new CsvQuoteFile(fileName, symbol);
    	try {
        	yahoo.open(new QuotePrinter());
    	} catch (QuoteSourceException qse) {
            System.out.println("Quote processing exception: " + qse.getMessage());
        } finally {
            yahoo.close();
        }
    }
}
