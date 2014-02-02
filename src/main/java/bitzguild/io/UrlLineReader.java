package bitzguild.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;

public class UrlLineReader<T> {

	protected LineReader<T>	_parser;
	protected Updater<T>	_output;

	protected UrlLineReader() {
	}
	
	public UrlLineReader(LineReader<T> parser, Updater<T> updater) {
		_parser = parser;
		_output = updater;
	}
	
	public void parse(URL url) throws IOException, ParseException {
		BufferedReader in = null;
		try {
			String str = null;
			URLConnection connection = url.openConnection();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			if (_parser.expectHeader()) _parser.readHeader(in.readLine());
			else _output.update(_parser.read(in.readLine()));
			while ((str = in.readLine()) != null) _output.update(_parser.read(str));
		} finally {
			if (in != null) in.close();
		}
	}
	
}
