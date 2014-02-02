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

package bitzguild.mkt.io;

import java.io.File;
import java.util.Iterator;

import bitzguild.ts.datetime.BoundedDateTimeIterator;
import bitzguild.ts.datetime.DateTime;
import bitzguild.ts.datetime.DateTimeIterator;
import bitzguild.ts.datetime.DateTimeRange;
import bitzguild.ts.datetime.MutableDateTime;

public class DateRangeFileNames implements Iterator<String> {

	protected DateTimeRange				_range;
	protected String					_baseDir;
	protected String					_fileExt;
	protected boolean					_partitionYears;
	protected BoundedDateTimeIterator	_dates;
	
	protected DateRangeFileNames() {
		// TODO: generate default range
	}
	
	public DateRangeFileNames(DateTimeRange range, String baseDir, String fileExt) {
		_baseDir = baseDir;
		_fileExt = fileExt.contains(".") ? fileExt : "." + fileExt; 
		_partitionYears = true;
		_range = range;
		_dates = new BoundedDateTimeIterator(range,DateTimeIterator.businessDays(MutableDateTime.DefaultHolidays));
	}
	
	
	public boolean hasNext() { return _dates.hasNext(); }

	public String next() {
		DateTime d = _dates.next();
		int year = d.year();
		int ymd = year;
		ymd = (ymd*100) + d.month();
		ymd = (ymd*100) + d.day();
		
		StringBuilder sb = new StringBuilder();
		sb.append(_baseDir).append(File.separator);
		if (_partitionYears) sb.append(year).append(File.separator);
		sb.append(ymd).append(_fileExt);
		return sb.toString();
	}

	public void remove() {}

}
