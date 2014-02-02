package bitzguild.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class Url2File {

	private static final boolean DEBUG = false;
	protected URL		_url;
	protected String	_fileName;
	
	public Url2File(URL url, String fileName) {
		_url = url;
		_fileName = fileName;
	}
	
	public void read() throws IOException {
			
		FileOutputStream fos = new FileOutputStream(_fileName);
		URLConnection connection = _url.openConnection();

		if (DEBUG) {
			System.out.println("Transfering URL content to File");
			System.out.println("\tURL  = " + _url.getPath());
			System.out.println("\tFile = " + _fileName);
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));

		String str;

		while ((str = in.readLine()) != null) {
			out.write(str);
			out.newLine();
		}
		in.close();
		out.close();
	}
}
