package bitzguild.mkt.event.compress;

import bitzguild.mkt.event.MutableQuote;
import bitzguild.mkt.event.Quote;
import bitzguild.mkt.event.QuoteChain;
import bitzguild.mkt.event.QuoteListener;
import bitzguild.ts.datetime.MutableDateTime;
import bitzguild.ts.event.TimeSpec;
import bitzguild.ts.event.TimeUnits;

public class Compress2Renko implements QuoteCompression {

    public static long          DEFAULT_RENKO_BLOCK = (long)(0.25 * 12);

    public double               _renkoBlock = 1;

    protected QuoteChain        _nextEventProcessor;
    protected QuoteListener     _compressedEventListener;
    protected MutableQuote      _quoteCompressed = null;
    protected TimeSpec          _compressionSpec;

    protected long              _nextDateTimeFrame;
    protected MutableDateTime   _nextDateTimeFrameDT;

    /**
     * Default Constructor
     */
    public Compress2Renko() {
        super();
        commonRenkoInit(new TimeSpec(TimeUnits.RENKO, DEFAULT_RENKO_BLOCK));
    }


    /**
     * Renko Block Constructor
     *
     * @param renkoBlock block size
     */
    public Compress2Renko(double renkoBlock) {
        super();
        commonRenkoInit(new TimeSpec(TimeUnits.RENKO, (long) renkoBlock));
    }

    /**
     * Renko Alternate Constructor
     *
     * @param increment minimum instrument quoted increment
     * @param steps multiple of quoted increment
     */
    public Compress2Renko(double increment, int steps) {
        super();
        commonRenkoInit(new TimeSpec(TimeUnits.RENKO, (long)(steps *increment)));
    }

    /**
     *
     * @param spec TimeSpec unit specification
     */
    private void commonRenkoInit(TimeSpec spec) {
        _compressionSpec = new TimeSpec(spec);
        _renkoBlock = spec.length;
        _nextEventProcessor = QuoteChain.TERMINAL;
        _compressedEventListener = QuoteListener.TERMINAL;
        _nextDateTimeFrame = ZERODAY;
        _nextDateTimeFrameDT = new MutableDateTime(_nextDateTimeFrame);
    }


    public void update(Quote inquote) {

        if (inquote.datetimeRep() >= _nextDateTimeFrame) {

            if (_quoteCompressed != null) relayQuote(_quoteCompressed);

            // start next event with incoming data
            long specAlignedIncomdingFrame = alignFrameWithSpec(inquote.datetimeRep());
            MutableQuote priorQuote = _quoteCompressed;
            _quoteCompressed = new MutableQuote(_compressionSpec, specAlignedIncomdingFrame,inquote.symbol(),
                    inquote.open(), inquote.high(), inquote.low(), inquote.close(), inquote.volume());

            boolean up = (priorQuote == null) ? true : inquote.close() > priorQuote.high();
            _quoteCompressed = centerRenko(_quoteCompressed, _renkoBlock, up);

            // roll frame forward
            incrementToNextPeriod(_nextDateTimeFrameDT, specAlignedIncomdingFrame);
            _nextDateTimeFrame = _nextDateTimeFrameDT.rep();

        } else if (inquote.close() > _quoteCompressed.high()) {
            _quoteCompressed = nextRenkoUpWithFill(_quoteCompressed, inquote, _renkoBlock);
        } else if (inquote.close() < _quoteCompressed.low())  {
            _quoteCompressed = nextRenkoDnWithFill(_quoteCompressed, inquote, _renkoBlock);
        } else {
            _quoteCompressed.setVolume(_quoteCompressed.volume() + inquote.volume());
        }

        // re-frame on new day
        // clean out prior

        // if first time
        // then establish renko block (centered)
        // ... what if Quote exceeds renko block?
        // else if close > current.high then create up renko (fill gaps)
        // else if close < current.low  then create dn renko (fill gaps)
        // else accumulate volume

    }

    // ------------------------------------------
    // Utilities
    // ------------------------------------------

    public MutableQuote centerRenko(Quote q, double blocksize, boolean up) {
        double halfblock = blocksize / 2.0;
        double centerpoint = (q.high() + q.low()) / 2.0;
        double top = centerpoint + halfblock;
        double bottom = centerpoint - halfblock;
        double open  = up ? bottom : top;
        double close = up ? top : bottom;
        return (MutableQuote)q.withDOHLCV(q.datetimeRep(), open, top, bottom, close, q.volume());
    }

    public MutableQuote nextRenko(MutableQuote prior, Quote q, double blocksize) {
        double bottom = prior.close();
        double top = bottom + blocksize;
        return (MutableQuote) (q.close() > prior.high()
            ? q.withDOHLCV(q.datetimeRep(), bottom, top, bottom, top, q.volume())
            : q.withDOHLCV(q.datetimeRep(), top, bottom, top, bottom, q.volume()));
    }

    public MutableQuote nextRenkoUpWithFill(MutableQuote prior, Quote inquote, double blocksize) {
        MutableQuote priorQuote = prior;
        while(inquote.close() > priorQuote.high()) {
            relayQuote(priorQuote);
            priorQuote = nextRenko(priorQuote, inquote, blocksize);
        }
        return priorQuote;
    }

    public MutableQuote nextRenkoDnWithFill(MutableQuote prior, Quote inquote, double blocksize) {
        MutableQuote priorQuote = prior;
        while(inquote.close() < priorQuote.low()) {
            relayQuote(priorQuote);
            priorQuote = nextRenko(priorQuote, inquote, blocksize);
        }
        return priorQuote;
    }

    public void relayQuote(Quote quote) {
        _nextEventProcessor.update(quote);
        _compressedEventListener.update(quote);
    }

    // ------------------------------------------
    // Time Utilities
    // ------------------------------------------

    protected MutableDateTime alignTime(MutableDateTime dt) {
        dt.setHoursMinutesSeconds(dt.hours(), dt.minutes(), 0);
        return dt;
        //System.out.println("ALIGNED: " + dt);
    }

    protected long alignFrameWithSpec(long inrep) { return alignTime(new MutableDateTime(inrep)).rep(); }

    protected void incrementToNextPeriod(MutableDateTime dt, long minimumFrame) {
        while(dt.rep() < minimumFrame) dt.addDays(1);
    }


    // ------------------------------------------
    // QuoteCompression interface
    // ------------------------------------------

    /**
     *
     * @return QuoteListener
     */
    public QuoteListener listener() { return _compressedEventListener; }

    /**
     * Assign listener
     *
     * @param listener for compressed events
     */
    public QuoteCompression connect(QuoteListener listener) {
        _compressedEventListener = listener;
        return this;
    }


    /**
     * Answers a copy of the TimeSpec for compression.
     *
     * @return TimeSpec market data quote spec
     */
    public TimeSpec getCompression() {
        return new TimeSpec(_compressionSpec);
    }

    /**
     * Answer a copy of the current Quote aggregate
     *
     * @return Quote
     */
    public Quote snapshot() {
        return new MutableQuote(_quoteCompressed);
    }


    /**
     * Answer the feed chain
     *
     * @return QuoteChain
     */
    public QuoteChain chain() { return _nextEventProcessor; }

    /**
     * Assign the feed chain
     *
     * @param o QuoteChain
     * @return QuoteChain
     */
    public QuoteChain feeds(QuoteChain o) {
        _nextEventProcessor = o;
        return o;
    }

    /**
     * Terminate the processing chain
     */
    public void close() {
        _nextEventProcessor.close();
        _nextEventProcessor = QuoteChain.TERMINAL;
    }

}
