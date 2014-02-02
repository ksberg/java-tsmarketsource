package bitzguild.io;

import java.text.ParseException;

public interface LineReader<T> {
	public T read(String line) throws ParseException;
	public void readHeader(String line) throws ParseException;
	public boolean expectHeader();
}
