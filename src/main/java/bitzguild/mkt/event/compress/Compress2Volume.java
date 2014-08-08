package bitzguild.mkt.event.compress;

import bitzguild.mkt.event.MutableQuote;
import bitzguild.mkt.event.Quote;
import bitzguild.mkt.event.QuoteChain;
import bitzguild.mkt.event.QuoteListener;
import bitzguild.ts.datetime.MutableDateTime;
import bitzguild.ts.event.TimeSpec;
import bitzguild.ts.event.TimeUnits;


/**
 * Compression to create Quote bars of equal volume.
 * This is designed to work intra-day and the volume
 * count is reset on day boundary.
 */
public class Compress2Volume implements QuoteCompression {

    public static final int     DEFAULT_VOLUME = 1000;

    protected QuoteChain        _nextEventProcessor;
    protected QuoteListener     _compressedEventListener;
    protected MutableQuote      _quoteCompressed = null;

    protected TimeSpec          _compressionSpec;

    protected long              _nextDateTimeFrame;
    protected MutableDateTime   _nextDateTimeFrameDT;


    /**
     * Default Constructor
     */
    public Compress2Volume() {
        super();
        commonVolumeInit(DEFAULT_VOLUME);
    }

    /**
     * Compression Constructor
     *
     * @param volume constant bar volume
     */
    public Compress2Volume(int volume) {
        super();
        commonVolumeInit(volume);
    }

    /**
     * Common initialization
     *
     * @param compression volume count
     */
    private void commonVolumeInit(int compression) {
        _compressionSpec = new TimeSpec(TimeUnits.VOLUME, compression);
        _nextEventProcessor = QuoteChain.TERMINAL;
        _compressedEventListener = QuoteListener.TERMINAL;
        _nextDateTimeFrame = ZERODAY;
        _nextDateTimeFrameDT = new MutableDateTime(_nextDateTimeFrame);
    }


    /**
     * Process the incoming quote by either
     * aggregating new information into existing Quote
     * or generating Quote snapshot for downstream
     * consumer and starting new aggregation.
     *
     * @param inquote upstream Quote
     */
    public void update(Quote inquote) {


        // on next day frame ...
        if (inquote.datetimeRep() >= _nextDateTimeFrame) {

            // relay compressed event
            if (_quoteCompressed != null) {
                _nextEventProcessor.update(_quoteCompressed);
                _compressedEventListener.update(_quoteCompressed);
            }

            // start next event with incoming data
            long specAlignedIncomdingFrame = alignFrameWithSpec(inquote.datetimeRep());
            _quoteCompressed = new MutableQuote(_compressionSpec, specAlignedIncomdingFrame,inquote.symbol(),
                    inquote.open(), inquote.high(), inquote.low(), inquote.close(), inquote.volume());

            // roll frame forward
            incrementToNextPeriod(_nextDateTimeFrameDT, specAlignedIncomdingFrame);
            _nextDateTimeFrame = _nextDateTimeFrameDT.rep();

        } else {

            // aggregate event
            // if above volume threshold
            // then cap threshold
            //      andThen relay event
            //      andThen start next event with balance

            _quoteCompressed.merge(inquote);    // aggregate event
            if (_quoteCompressed.volume() > _compressionSpec.length) {
                long excessVolume = _quoteCompressed.volume() - _compressionSpec.length;
                _quoteCompressed.setVolume(_compressionSpec.length);
                _nextEventProcessor.update(_quoteCompressed);
                _compressedEventListener.update(_quoteCompressed);
                _quoteCompressed.withDOHLCV(inquote.datetimeRep(), inquote.open(), inquote.high(), inquote.low(), inquote.close(), excessVolume);
            }
        }
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
