package bitzguild.mkt.event.compress;

import bitzguild.ts.event.TimeSpec;
import bitzguild.ts.event.TimeUnits;

public class TimeSpecHelper {

    static long MinutesPerHour = 60;
    static long SecondsPerMinute = 60;
    static long HoursPerDay = 24;
    static long DaysPerWeek = 7;

    static long SecondsInMinute = 60;
    static long SecondsInHour   = SecondsInMinute * MinutesPerHour;
    static long SecondsInDay    = SecondsInHour * HoursPerDay;
    static long SecondsInWeek   = SecondsInDay * DaysPerWeek;

    public TimeSpecHelper() {

    }

    public long secondsPerSpec(TimeSpec ts, long seconds) {
        switch(ts.units) {
            case TimeUnits.SECOND:
                return seconds / ts.length;

            case TimeUnits.MINUTE:
                return seconds / (ts.length * SecondsPerMinute);

            case TimeUnits.HOUR:
                return seconds / (ts.length * SecondsInHour);

            case TimeUnits.DAY:
                return seconds / (ts.length * SecondsInDay);

            case TimeUnits.WEEK:
                return seconds / (ts.length * SecondsInWeek);

            default:
        }
        return seconds;
    }

}
