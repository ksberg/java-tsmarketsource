package bitzguild.mkt.io.tickdata;

import java.io.File;
import java.util.Iterator;

import bitzguild.io.FileLineReader;
import bitzguild.mkt.event.Tick;
import bitzguild.mkt.event.TickObserver;
import bitzguild.mkt.io.TickSource;
import bitzguild.mkt.io.TickSourceException;

public class TickDataMultiFileReader extends FileLineReader<Tick> implements TickSource {

	protected Tick				_prototype;
	protected Iterator<String>	_fileNames;
	
	TickDataMultiFileReader(Tick prototype, Iterator<String> fileNames) {
		_prototype = prototype;
		_fileNames = fileNames;
	}
	
	public void open(TickObserver observer) throws TickSourceException {
		try {
			while(_fileNames.hasNext()) {
				String fileName = _fileNames.next();
				File f = new File(fileName);
				if (f.exists()) {
					TickDataReader reader = new TickDataReader(_prototype, fileName);
					reader.open(observer);
					reader.close();
				}
			}
		} catch (Exception e) {
			throw new TickSourceException(e);
		}
	}

	public void close() {
		_fileNames = null;
	}

}
