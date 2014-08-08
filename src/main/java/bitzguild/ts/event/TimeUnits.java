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
 * TimeUnits describe the packaging of data within a bar
 * or collection of bars. MktStudy bars are given by the OHLC
 * prices, timestamp, number of transactions, and volume.
 */
public class TimeUnits {

	// variable time units
    public static final int EVENT_TIME_MIN  = 0;
	public static final int TICK            = EVENT_TIME_MIN;
    public static final int EVENT_TIME_MAX  = TICK;

	// constant time units
	public static final int MIN_TIME    = EVENT_TIME_MIN +1;
    public static final int SECOND      = MIN_TIME;
    public static final int MINUTE	    = MIN_TIME+1;
	public static final int HOUR 		= MIN_TIME+2;
	public static final int DAY 		= MIN_TIME+3;
	public static final int WEEK		= MIN_TIME+4;
	public static final int MONTH		= MIN_TIME+5;
	public static final int QUARTER 	= MIN_TIME+6;
	public static final int YEAR		= MIN_TIME+7;
	public static final int DECADE		= MIN_TIME+8;
	public static final int CENTURY		= MIN_TIME+9;
	public static final int MAX_TIME    = CENTURY;



    public static final int VARTIME_MIN = MAX_TIME + 1;

    public static final int RENKO       = VARTIME_MIN;
    public static final int VOLUME      = VARTIME_MIN + 1;
    public static final int AREA        = VARTIME_MIN + 2;
    public static final int CUSTOM      = VARTIME_MIN + 3;
    public static final int VARTIME_MAX = CUSTOM;


    public static final int MIN_UNITS   = EVENT_TIME_MIN;
	public static final int MAX_UNITS   = VARTIME_MAX;
	public static final int NUM_UNITS   = MAX_UNITS + 1;

    public static final int UNDEFINED   = NUM_UNITS;

	private static final String[] UnitLabels = {
							"tick", 
                            "second", "minute", "hour", 
                            "day", "week", "month", "quarter", 
                            "year", "decade", "century",
                            "renko","volume", "area", "custom",
							"***UNKNOWN***" };

	private static final String[] ShortUnitLabels = {
							"tic",
							"sec", "min", "hr", 
							"day", "wk", "mo", "qtr", 
							"yr","dec", "c",
                            "rnko","vol", "area", "cust",
							"***UNKNOWN***" };

	private static final String[] UnitAdjectives = {
							"by trade",
							"by second","by minute", "hourly", 
							"daily", "weekly", "monthly", "quarterly", 
							"yearly","by decade","by century",
                            "by renko","by volume", "by area", "as custom",
							"***UNKNOWN***" };

	/**
	 * Get the full unit name.
	 *
	 * @param iUnits unit constant
	 * @return String representation
	 */
	public static String getUnitStr(int iUnits) {
		if (iUnits < 0) return UnitLabels[UNDEFINED];
		if (iUnits > MAX_UNITS) return UnitLabels[UNDEFINED];
		return UnitLabels[iUnits];
	}

	/**
	 * Print the full unit name into the buffer
	 *
	 * @param iUnits time unit
	 * @param strb buffer for representation
	 */
	public static void unitToBuffer(int iUnits, StringBuffer strb) {
		if (iUnits < 0 || iUnits > MAX_UNITS) strb.append(UnitLabels[UNDEFINED]);
		else strb.append(UnitLabels[iUnits]);
	}

	/**
	 * Print the unit adjective into the buffer (e.g. "daily")
	 *
     * @param iUnits time unit
     * @param strb buffer for representation
	 */
	public static void unitAdjToBuffer(int iUnits, StringBuffer strb) {
		if (iUnits < 0 || iUnits > MAX_UNITS) strb.append(UnitAdjectives[UNDEFINED]);
		else strb.append(UnitAdjectives[iUnits]);
	}

	public static int unitsFromString(String strU) {
		for(int i=0; i<=MAX_UNITS; i++) {
			if( UnitLabels[i].equalsIgnoreCase(strU) ) return i;
		}
		for(int i=0; i<=MAX_UNITS; i++) {
			if( ShortUnitLabels[i].equalsIgnoreCase(strU) ) return i;
		}
		for(int i=0; i<=MAX_UNITS; i++) {
			if( UnitAdjectives[i].equalsIgnoreCase(strU) ) return i;
		}
		return UNDEFINED;
	}

	public static int boundUnits(int units) {
		return Math.min(Math.max(units,0),MAX_UNITS);
	}

	public static int maxGranularUnits(int unitA, int unitB) {
		return Math.min(boundUnits(unitA), boundUnits(unitB));
	}

    public static boolean isEventUnit(int unit) { return unit <= EVENT_TIME_MAX; }

	public static boolean isVariableUnit(int unit) {
		return (unit >= EVENT_TIME_MIN && unit <= EVENT_TIME_MAX);
	}

	public static boolean isTickUnit(int unit)      { return unit == TICK; }

	public static boolean isIntraday(int unit) {
		return (unit >= MINUTE && unit <= HOUR);
	}

	public static boolean isMinuteOrHour(int unit) {
		return (unit >= MINUTE && unit <= HOUR);
	}

	public static boolean isEndOfDay(int unit) {
		return (unit >= DAY && unit <= YEAR);
	}

}