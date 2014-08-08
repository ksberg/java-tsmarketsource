package bitzguild.mkt.io.yahoo;

/**
 * Created by ksvenberg on 5/5/14.
 */

import bitzguild.io.LineReader;
import bitzguild.mkt.event.Quote;
import bitzguild.ts.datetime.MutableDateTime;

import java.text.ParseException;
import java.util.StringTokenizer;

/**
 *
 */
public class YahooCsvLineParser implements LineReader<Quote> {
    protected Quote 			_prototype;
    protected MutableDateTime _tmpDateTime;
    public YahooCsvLineParser(Quote prototype) {
        _prototype = prototype;
        _tmpDateTime = new MutableDateTime();
    }
    @Override
    public Quote read(String line) throws ParseException {

        StringTokenizer st = new StringTokenizer(line, ",");
        if (st.countTokens() > 5) {
            // LINE: 'Date,Open,High,Low,Close,Volume, Adj Close
            parseYYYYMMDDWithSep(_tmpDateTime,st.nextToken(),"-");
            long datetime = _tmpDateTime.rep();
            double open = Double.parseDouble(st.nextToken());
            double high	= Double.parseDouble(st.nextToken());
            double low	= Double.parseDouble(st.nextToken());
            double close= Double.parseDouble(st.nextToken());
            long volume = Long.parseLong(st.nextToken().trim());

            return _prototype.withDOHLCV(datetime, open, high, low, close, volume);
        }
        return null;
    }
    public void readHeader(String line) throws ParseException {}
    public boolean expectHeader() { return true; }

    private void parseYYYYMMDDWithSep(MutableDateTime dt, String dateStr, String sep) {
        StringTokenizer st = new StringTokenizer(dateStr, sep);
        int numTokens = st.countTokens();
        dt.setMillisSinceMidnight(0);
        if (numTokens > 2) {
            int iyr = Integer.parseInt(st.nextToken());
            int imo = Integer.parseInt(st.nextToken());
            int idy = Integer.parseInt(st.nextToken());
            dt.setFromYearMonthDay(iyr, imo, idy);
        } else {
            try {
                int ymd = Integer.parseInt(dateStr);
                dt.setFromYYYYMMDD(ymd);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

}
