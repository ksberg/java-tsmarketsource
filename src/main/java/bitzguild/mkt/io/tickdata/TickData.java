/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (c) 2001-2013, Kevin Sven Berg
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * ***** END LICENSE BLOCK ***** */

package bitzguild.mkt.io.tickdata;

import bitzguild.ts.datetime.MutableDateTime;

import java.io.*;
import java.text.ParseException;

/**
 * Created by ksvenberg on 12/1/13.
 */
public class TickData {
    public static final String EXT = ".csv";
    public static final String DEFAULT = "/tmp";

    public String baseDirectory = null;

    /**
     * Default Constructor
     */
    public TickData() {
        super();
        baseDirectory = DEFAULT;
    }

    /**
     * Parameterized Constructor
     *
     * @param baseDir base dirctory string
     */
    public TickData(String baseDir) {
        super();
        baseDirectory = baseDir;
    }

    public static void main(String[] args) {
        TickData td = new TickData();
        td.run(args);

    }

    /**
     * Splits archive CSV file into chunks
     * arg[0] = input file
     * arg[1] = symbol
     * 
     * @param args
     */
    public void run(String[] args) {
    	this.partitionBuffered(args[0], args[1]);
    }

    /**
     * Ensure each directory of the given path exists,
     * and if not create it. Assumes every element is
     * a directory, not a file.
     *
     * @param path directory path string
     */
    public void ensurePath(String path) {
        StringBuffer strb = new StringBuffer();
        String[] splits = path.split("/");
        for(int i=0; i<splits.length; i++) {
            strb.append(splits[i]).append("/");
            File d = new File(strb.toString());
            if (!d.exists()) d.mkdir();
        }
    }

    public StringBuffer pathForContinuous(StringBuffer strb) {
        strb.append(baseDirectory).append("/TickData/Continuous");
        return strb;
    }

    public StringBuffer pathForData(StringBuffer strb, String symbol, MutableDateTime dt) {
        pathForContinuous(strb).append("/").append(symbol).append("/").append(dt.year());
        return strb;
    }

    public StringBuffer fileForData(StringBuffer strb, String symbol, MutableDateTime dt) {

        return strb;
    }

    public StringBuffer appendFileName(StringBuffer strb, String symbol, MutableDateTime dt) {
        strb.append("/").append(dt.toString()).append(EXT);
        return strb;
    }

    public void partitionBuffered(String csvFile, String symbol) {
        StringBuffer strb = new StringBuffer();
        StringBuffer strbFile = new StringBuffer();
        pathForContinuous(strbFile);
        ensurePath(strbFile.toString());
        strbFile.setLength(0);

        try {
            BufferedReader in = new BufferedReader(new java.io.FileReader(csvFile));
            String header = in.readLine();
            String str = null;
            String priorDateStr = null;

            long report = 100000; // 10000000;
            long nlines = 0;
            
            while ((str = in.readLine()) != null) {
            	strb.append(str).append("\n");

            	nlines++;
                if (nlines % report == 0) System.out.println(nlines + " -> " + str);
            	
                String newDateStr = str.substring(0,8);
                if (priorDateStr != null && newDateStr.compareTo(priorDateStr) != 0) {
                    writeOneTickFile(symbol, strb, header, newDateStr);
                    strb.setLength(0);	// reset for next day
                }
                priorDateStr = newDateStr;
            }
            if (strb.length() > 0 && priorDateStr != null) {
                writeOneTickFile(symbol, strb, header, priorDateStr);
            }
            in.close();

        } catch(Exception e) {
        	e.printStackTrace();
        }
    }

	private void writeOneTickFile(String symbol, StringBuffer strb,
			String header, String newDateStr) throws FileNotFoundException, ParseException, IOException {
		
		StringBuffer strbFile = new StringBuffer();
		FileOutputStream fos;
		BufferedWriter out;
		MutableDateTime dt = MutableDateTime.parse(newDateStr);
		
		this.pathForData(strbFile,symbol,dt);
		String directory =  strbFile.toString();
		this.ensurePath(directory);
		this.appendFileName(strbFile,symbol,dt);
		String filePath = strbFile.toString();
		
		fos = new FileOutputStream(filePath);
		out = new BufferedWriter(new OutputStreamWriter(fos));
		out.write(header);
		out.newLine();
		out.write(strb.toString());
		out.newLine();
		out.flush();
		out.close();
	}
    
    public void partition(String csvFile, String symbol) {
        StringBuffer strb = new StringBuffer();
        pathForContinuous(strb);
        ensurePath(strb.toString());
        strb.setLength(0);

        try {
            BufferedReader in = new BufferedReader(new java.io.FileReader(csvFile));
            String header = in.readLine();
            String str = null;
            String priorDateStr = null;
            FileOutputStream fos = null;
            BufferedWriter out = null;

            long report = 10000000;
            long nlines = 0;
            
            while ((str = in.readLine()) != null) {
                String newDateStr = str.substring(0,8);
                if (!newDateStr.equalsIgnoreCase(priorDateStr)) {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                    MutableDateTime dt = MutableDateTime.parse(newDateStr);
                    this.pathForData(strb,symbol,dt);
                    String directory =  strb.toString();
                    this.ensurePath(directory);
                    this.appendFileName(strb,symbol,dt);
                    String filePath = strb.toString();
                    fos = new FileOutputStream(filePath);
                    out = new BufferedWriter(new OutputStreamWriter(fos));
                    out.write(header);
                    out.newLine();

                    nlines++;
                    if (nlines % report == 0) System.out.println(nlines + " -> " + str);
                }
                out.write(str);
                out.newLine();
            }
            out.flush();
            out.close();
            in.close();

        } catch(Exception e) {
        	e.printStackTrace();
        }
    }



}
