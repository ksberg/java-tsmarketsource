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

package bitzguild.mkt.event.compress;

import bitzguild.mkt.event.MutableQuote;
import bitzguild.mkt.event.QuoteChain;
import bitzguild.mkt.event.Tick;
import bitzguild.mkt.event.TickObserver;
import bitzguild.ts.event.TimeSpec;
import bitzguild.ts.event.TimeUnits;

public class Tick2Minutes implements TickObserver {

    public QuoteCompression minuteCompression;
    public MutableQuote quote;

    public Tick2Minutes() {
        super();
        minuteCompression = new Compress2Minutes();
        quote = new MutableQuote(new TimeSpec(TimeUnits.TICK,1),"");
    }

    public QuoteCompression getMinuteCompression() { return minuteCompression; }

    public QuoteChain feeds(QuoteChain qo) {
        minuteCompression.feeds(qo);
        return qo;
    }

    @Override
    public void update(Tick tick) {
    	quote.setSymbol(tick.symbol());
    	quote.setPrice(tick.price());
    	quote.setVolume(tick.volume());
    	quote.setDateTimeRep(tick.datetimeRep());
        minuteCompression.update(quote);
    }

    public void flush() {
        minuteCompression.close();
    }

}
