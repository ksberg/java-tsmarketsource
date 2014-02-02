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
