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
public class Compress2Area implements QuoteCompression {

    public static final int     DEFAULT_VOLUME  = 1000;
    public static final double  DEFAULT_RANGE   = 10.0;
    public static final long    DEFAULT_AREA    = (long) DEFAULT_RANGE * DEFAULT_VOLUME;

    protected QuoteChain        _nextEventProcessor;
    protected QuoteListener     _compressedEventListener;
    protected MutableQuote      _quoteCompressed = null;

    protected int               _cummulativeVolume;
    protected TimeSpec          _compressionSpec;

    protected long              _nextDateTimeFrame;
    protected MutableDateTime   _nextDateTimeFrameDT;
    protected double            _areaThreshold = 0.0;
    protected long              _longThreshold = new Double(_areaThreshold).longValue();


    /**
     * Default Constructor
     */
    public Compress2Area() {
        super();
        commonVolumeInit(new TimeSpec(TimeUnits.AREA,DEFAULT_AREA));
    }

    /**
     * Compression Constructor
     *
     * @param absoluteVolume - specific bar volume
     */
    public Compress2Area(double absoluteVolume) {
        super();
        commonVolumeInit(new TimeSpec(TimeUnits.AREA, (long) absoluteVolume));
    }


    /**
     * Area components Constructor, with area
     * as product of average volume and range.
     *
     * @param volume average volume
     * @param range average range
     */
    public Compress2Area(long volume, double range) {
        super();
        commonVolumeInit(new TimeSpec(TimeUnits.AREA,(long)(volume*range)));
    }

    /**
     * Common initialization
     *
     * @param spec TimeSpec
     */
    private void commonVolumeInit(TimeSpec spec) {
        _areaThreshold = spec.length;
        _cummulativeVolume = 0;
        _compressionSpec = new TimeSpec(spec);
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
            if (area(inquote) > _areaThreshold) {
                int xv = excessVolume(inquote, _areaThreshold);
                _quoteCompressed.setVolume(_longThreshold);
                _nextEventProcessor.update(_quoteCompressed);
                _compressedEventListener.update(_quoteCompressed);
                _quoteCompressed.withDOHLCV(inquote.datetimeRep(), inquote.close(), inquote.close(), inquote.close(), inquote.close(), xv);
            }
        }
    }

    // ------------------------------------------
    // Time Utilities
    // ------------------------------------------

    protected Double area(Quote q) {
        return q.volume() * (q.high() - q.low());
    }

    protected int excessVolume(Quote q, double threshold) {
        // V = T / (H - L)
        if (q.high() == q.low()) return 0;
        else return (int)(_areaThreshold / (q.high() - q.low()));
    }


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
