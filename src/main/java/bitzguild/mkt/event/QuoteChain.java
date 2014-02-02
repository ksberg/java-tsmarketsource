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

/**
 * QuoteChain strings together any number of QuoteListeners in to form
 * a sequence where the each member of the chain may modify the result
 * for the next member of the chain. One common application is time unit
 * compression, or the re-bin of time one time unit into larger time units
 * (e.g. 1-minute into 5-minute into 1-hour into 1-day). QuoteChain can also
 * forms the basis of Source Reader output.
 * 
 * @author Kevin Sven Berg
 */
public interface QuoteChain extends QuoteListener {

    /**
     * Assign next in feeds. This characteristic
     * is central to forming a linked series of
     * entities that receive and process Quote
     * updates in a similar manner.
     *
     * @param other
     * @return QuoteChain
     */
    public QuoteChain feeds(QuoteChain other);

    /**
     * All upstream processing has completed.
     * Enable downstream observers to complete
     * and wrap any intermediate state (if desired).
     * Implementations should perform house-cleaning
     * and then relay message to next listener in
     * the feeds.
     */
    public void close();

    
    /**
     * Answer terminating QuoteChain. Use as a safe "Null" value.
     * 
     * @return QuoteChain
     */
    public static final QuoteChain TERMINAL = new QuoteChain() {
        public QuoteChain feeds(QuoteChain other) { return null; }
        public void update(Quote q) { }
        public void close() {}
    };
}
