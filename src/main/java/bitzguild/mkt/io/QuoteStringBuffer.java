package bitzguild.mkt.io;

import bitzguild.mkt.event.Quote;
import bitzguild.mkt.event.QuoteChain;

public class QuoteStringBuffer implements QuoteChain {

	protected StringBuffer 	_buffer;
	protected QuoteChain	_output;
	
	public QuoteStringBuffer() {
		_buffer = new StringBuffer();
		_output = QuoteChain.TERMINAL;
	}
	
	public QuoteStringBuffer(QuoteChain chain) {
		_buffer = new StringBuffer();
		_output = chain;
	}

	public void update(Quote q) {
		q.toBuffer(_buffer).append("\n");
	}

	public QuoteChain feeds(QuoteChain other) {
		_output = other;
		return other;
	}

	public void close() {}
	
	public String toString() {
		return _buffer.toString();
	}
}
