package bitzguild.mkt.meta;

import bitzguild.ts.datetime.DateTime;
import bitzguild.ts.datetime.DateTimePredicate;

public interface FixedMarketInstrument {

	public DateTime	lastTradingDay();
	public DateTime	firstNoticeDay();
	
	public int tradingDaysToExpiration(DateTime d, DateTimePredicate holidays);
	
}
