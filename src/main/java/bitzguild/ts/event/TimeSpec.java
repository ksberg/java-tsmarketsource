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

package bitzguild.ts.event;

/**
 * Insert the type's description here.
 * Creation date: (3/9/2001 5:53:48 PM)
 * @author: Administrator
 */
public class TimeSpec implements Cloneable, java.io.Serializable, Comparable<TimeSpec> {

	static final long serialVersionUID = 0L;

	public int units = TimeUnits.DAY;
	public int length = 1;

	// ------------------------------------------------------
	// Existence
	// ------------------------------------------------------

    /**
     * Default Constructor
     */
    public TimeSpec() {
		units = TimeUnits.DAY;
        length = 1;
    }

    /**
     * TimeSeriesSpec constructor with _units and _length
     *
     * @param units Time Units
     * @param length cardinal unit multiplier (e.g. 5 * Minute)
     */
    public TimeSpec(int units, int length) {
        this.units = boundUnits(units);
        this.length = length;
    }

    /**
     * Copy constructor.
     *
     * @param that TimeSeriesSpec src
     */
    public TimeSpec(TimeSpec that) {
        this.length = that.length;
        this.units = that.units;
    }

    /**
     * Serial representation constructor
     *
     * @param rep long
     */
    public TimeSpec(long rep) {
        _setRep(rep);
    }


	/**
	 *
	 */
	public Object clone() throws CloneNotSupportedException {
		TimeSpec qs = (TimeSpec)super.clone();
		qs.units = units;
		qs.length = length;
		return qs;
	}

	public void copy(TimeSpec other) {
		length 	= other.length;
		units 	= other.units;
	}
	
	// ------------------------------------------------------
	// Comparable interface
	// ------------------------------------------------------

	/**
	 * <p>
	 * Compare one spec to another (Greater=1, Equal=0, Less=-1).
	 * Throws <code>ClassCastException</code>
	 * if it's fed the wrong class of object.
	 * </p>
	 *
	 * @param o other TimeSpec
	 * @return int (Greater=1, Equal=0, Less=-1)
	 */
	public int compareTo(TimeSpec o) {

		TimeSpec s1 = this;
		TimeSpec s2 = o;

		if (s1.units > s2.units) return 1;
		if (s1.units < s2.units) return -1;

		if (s1.length > s2.length) return 1;
		if (s1.length < s2.length) return -1;

		return 0;
	}

	/**
	 * <p>
	 * Compares two spec instances. For specifications
	 * to be equal, the units and length must be equal.
	 * </p>
	 *
	 * @param o other TimeSpec
	 * @return boolean true when equal
	 */
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof TimeSpec)) return false;
		TimeSpec ts = (TimeSpec)o;
		return compareTo(ts) == 0;
	}

	public int hashCode() {
		return (length << 5) + units;
	}

	// ------------------------------------------------------
	// Accessors
	// ------------------------------------------------------

    public int units()	{ return this.units; }
    public int length() { return this.length; }
    public long rep() 	{ return (long)(units << 16) | length; }
    public long rank()  { return rep(); }

    private void _setRep(long rep) {
        units = (int)(rep >> 16);
        length = (int)(rep & 0xFFFF);
    }
	

	// ------------------------------------------------------
	// Query
	// ------------------------------------------------------

    public boolean framedBy(TimeSpec that) {
        return this.rank() >= that.rank() && frameLowerIntoUpper(that, this);
    }

    public boolean frames(TimeSpec that) {
        //System.out.println("RANK " + this.rank() + " vs " + that.rank());
        return this.rank() <= that.rank() && frameLowerIntoUpper(this,that);
    }

    /**
     * Answer whether the lower ranking TimeSpec can fit as a time multiple into the
     * upper ranking TimeSpec.
     *
     * @param lower TimeSpec
     * @param upper TimeSpec
     * @return boolean whether
     */
    protected static boolean frameLowerIntoUpper(TimeSpec lower, TimeSpec upper) {
        if (lower.units == TimeUnits.TICK) return true;     // TICK frames everything
        if (lower.units == TimeUnits.WEEK) return false;    // WEEK is oddball, frames nothing
        if (lower.units <= TimeUnits.DAY) {
            if (upper.units <= TimeUnits.WEEK) {
                long lowerSeconds = secondsPerSpec(lower);
                long upperSeconds = secondsPerSpec(upper);
                //System.out.print("Frame Test: " + lower+ " vs " + upper);
                //System.out.print(" -> " + lowerSeconds + " % " + upperSeconds);
                //System.out.println(" = " + (upperSeconds % lowerSeconds));
                return (upperSeconds % lowerSeconds) == 0
                        && withinUnitBounds(lower) && withinUnitBounds(upper);
            }
            return true;
        }

        if (lower.units >= TimeUnits.MONTH) {
            int lowerMonths = monthsPerSpec(lower);
            int upperMonths = monthsPerSpec(upper);
            //System.out.print("Frame Test: " + lower+ " vs " + upper);
            //System.out.print(" -> " + lowerMonths + " % " + upperMonths);
            //System.out.println(" = " + (lowerMonths % upperMonths));
           return (upperMonths % lowerMonths) == 0
                    && withinUnitBounds(lower) && withinUnitBounds(upper);
        }

        //System.out.print("Not Testing This Yet ... ");
        return false;
    }

	/**
	* @param qs TimeSpec comparison object
	* @return boolean whether quote objects can be used together
	*/
	public boolean isCompatableWith(TimeSpec qs) {

		if ( qs.units != units ) return false;
		if ( qs.length != length ) return false;

		return true;
	}

	public String getSpecStr() {
		StringBuffer strb = new StringBuffer();

		if (units >= TimeUnits.DAY) {
			TimeUnits.unitAdjToBuffer(units, strb);
		} else {
			strb.append(length);
			strb.append('-');
			strb.append(TimeUnits.getUnitStr(units));
		}
		return strb.toString();
	}


    public StringBuffer toBuffer(StringBuffer strb) {
        strb.append(length);
        strb.append('-');
        strb.append(TimeUnits.getUnitStr(this.units));
        return strb;
    }

	public String toString() {
		StringBuffer strb = new StringBuffer();
        toBuffer(strb);
		return strb.toString();
	}

    /**
     * Corresponds to mkString
     * @param qsString
     * @return
     */
    public static TimeSpec parse(String qsString) {
        TimeSpec qs = null;
        try {
            qs = new TimeSpec();
            String splits[] = qsString.split("-");
            qs.length = Integer.parseInt(splits[0]);
            qs.units = TimeUnits.unitsFromString(splits[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qs;
    }

    /**
     * Pin the input _units between min and max allowed
     *
     * @param units int
     * @return int _units
     */
    private static int boundUnits(int units) {
        return Math.min(Math.max(units,TimeUnits.MIN_UNITS),TimeUnits.MAX_UNITS);
    }


    static long MinutesPerHour = 60;
    static long SecondsPerMinute = 60;
    static long HoursPerDay = 24;
    static long DaysPerWeek = 7;

    static long SecondsInMinute = 60;
    static long SecondsInHour   = SecondsInMinute * MinutesPerHour;
    static long SecondsInDay    = SecondsInHour * HoursPerDay;
    static long SecondsInWeek   = SecondsInDay * DaysPerWeek;

    public static long secondsPerSpec(TimeSpec ts) {
        switch(ts.units) {
            case TimeUnits.SECOND:
                return ts.length;

            case TimeUnits.MINUTE:
                return (ts.length * SecondsPerMinute);

            case TimeUnits.HOUR:
                return (ts.length * SecondsInHour);

            case TimeUnits.DAY:
                return (ts.length * SecondsInDay);

            case TimeUnits.WEEK:
                return (ts.length * SecondsInWeek);

            default:
        }
        return 1;
    }

    public static int monthsPerSpec(TimeSpec ts) {
        switch (ts.units) {
            case TimeUnits.MONTH:
                return ts.length;
            case TimeUnits.QUARTER:
                return ts.length * 3;
            case TimeUnits.YEAR:
                return ts.length * 12;
            case TimeUnits.DECADE:
                return ts.length * 12 * 10;
            case TimeUnits.CENTURY:
                return ts.length * 12 * 10 * 10;
            default:
        }
        return 1;
    }

    public static boolean withinUnitBounds(TimeSpec ts) {
        switch(ts.units) {

            case TimeUnits.SECOND:
                return ts.length < 60;

            case TimeUnits.MINUTE:
                return ts.length < 60;

            case TimeUnits.HOUR:
                return ts.length < 24;

            case TimeUnits.DAY:
                return ts.length == 1;

            case TimeUnits.WEEK:
                return ts.length == 1;

            case TimeUnits.MONTH:
                return ts.length < 3;

            case TimeUnits.QUARTER:
                return ts.length < 3;

            case TimeUnits.YEAR:
                return ts.length <= 5;

            case TimeUnits.DECADE:
                return ts.length <= 5;

            case TimeUnits.CENTURY:
                return ts.length == 1;

            default:
        }
        return true;
    }

}