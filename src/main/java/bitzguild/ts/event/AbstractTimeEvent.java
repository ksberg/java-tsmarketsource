package bitzguild.ts.event;

import bitzguild.ts.datetime.DateTime;
import bitzguild.ts.datetime.ImmutableDateTime;

public class AbstractTimeEvent extends BufferUtils implements BinnedTimeEvent {

    protected long  _spec;
    protected long  _time;

    /**
     * Default Constructor
     */
    protected AbstractTimeEvent() {
        _spec = (new TimeSpec()).rep();
        _time = 0L;
    }

    /**
     * Copy Constructor
     */
    protected AbstractTimeEvent(BinnedTimeEvent that) {
        _spec = that.timespecRep();
        _time = that.datetimeRep();
    }


    /**
     * TimeSeriesSpec Constructor
     *
     * @param spec TimeSeriesSpec
     */
    protected AbstractTimeEvent(TimeSpec spec, long time) {
        _spec = spec.rep();
        _time = time;
    }


    /**
     * Serial TimeSeriesSpec Constructor
     *
     * @param specrep long
     */
    protected AbstractTimeEvent(long specrep, long time) {
        _spec = specrep;
        _time = time;
    }
    
    
    public DateTime datetime() {
        return new ImmutableDateTime(_time);
    }

    public TimeSpec timespec() {
        return new TimeSpec(_spec);
    }


    public long timespecRep() {
        return _spec;
    }

    public long datetimeRep() {
        return _time;
    }


    public BinnedTimeEvent fold(BinnedTimeEvent e) {
       return this;
    }

    
	public StringBuffer toBuffer(StringBuffer sb) {
		return sb;
	}
	
	public String toString() {
		return toBuffer(new StringBuffer()).toString();
	}


    
}
