package bitzguild.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

public class FileLineReader<T> {

	protected LineReader<T>	_parser;
	protected Updater<T>	_output;
	
	protected FileLineReader() {
	}
	
	public FileLineReader(LineReader<T> parser, Updater<T> updater) {
		_parser = parser;
		_output = updater;
	}
	
	public void read(String filename) throws IOException, ParseException {
		BufferedReader in = null;
		try {
			String str = null;
			in = new java.io.BufferedReader(new FileReader(filename));

			if (_parser.expectHeader()) _parser.readHeader(in.readLine());
			else _output.update(_parser.read(in.readLine()));
			while ((str = in.readLine()) != null) _output.update(_parser.read(str));
		} finally {
			if (in != null) in.close();
		}
	}
}
