package bitzguild.io;

import java.io.IOException;
import java.text.ParseException;
import java.util.Scanner;

public class ScannerLineReader<T> {

	protected LineReader<T>	_parser;
	protected Updater<T>	_output;
	
	protected ScannerLineReader() {
	}
	
	/**
	 * 
	 * @param parser
	 * @param updater
	 */
	public ScannerLineReader(LineReader<T> parser, Updater<T> updater) {
		_parser = parser;
		_output = updater;
	}
	
	/**
	 * 
	 * @param scanner
	 * @throws IOException
	 * @throws ParseException
	 */
	public void read(Scanner scanner) throws IOException, ParseException {
		if (_parser.expectHeader()) _parser.readHeader(scanner.nextLine());
		else _output.update(_parser.read(scanner.nextLine()));
		while(scanner.hasNext()) _output.update(_parser.read(scanner.nextLine()));
	}
	
}
