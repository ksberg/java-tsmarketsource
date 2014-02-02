package bitzguild.io;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;

public class FileLineWriter<T> {
	
	protected LineWriter<T>		_writer;
	protected Iterator<T>		_source;

	public FileLineWriter() {
		
	}
	
	public FileLineWriter(LineWriter<T> writer, Iterator<T> source) {
		_writer = writer;
		_source = source;
	}
	
	public void write(String filename) throws IOException {
		
		FileOutputStream fos = null;
		BufferedWriter out = null;
        try {
            fos = new FileOutputStream(filename);
            out = new BufferedWriter(new OutputStreamWriter(fos));
    		StringBuffer strb = new StringBuffer();
    		
            if (_writer.includeHeader()) {
            	_writer.writeHeader(strb);
            	out.write(strb.toString());
            	out.newLine();
            }
            while(_source.hasNext()) {
            	strb.setLength(0);
            	T val = _source.next();
            	_writer.write(strb, val);
            	out.write(strb.toString());
            	out.newLine();
            }
        } finally {
        	if (out != null) out.close();
        }
	}
}
