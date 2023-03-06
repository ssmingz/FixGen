/*
 * Created on Nov 17, 2004
 * 
 * This file is part of ParMol.
 * ParMol is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * ParMol is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ParMol; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package de.parmol.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import de.parmol.graph.*;
import de.parmol.parsers.GraphParser;
import de.parmol.parsers.LineGraphParser;
import de.parmol.parsers.PDGParser;
import de.parmol.parsers.SmilesParser;


/**
 * This class is a converter between different graph file formats.
 * 
 * @author Marc Woerlein <Marc.Woerlein@informatik.uni-erlangen.de>
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class FileConverter {
	private static void usage() {
		System.out.println("Usage: " + FileConverter.class.getName() + " [options] inputFile outputFile");
		System.out.println("Options:");
		System.out.println("  -parser=... (default: " + SmilesParser.class.getName() + 
				"; fully qualified name of the parser class)");
		System.out.println("  -serializer=... (default: " + LineGraphParser.class.getName() + 
				"; fully qualified name of the parser class)");
	}
	

	/**
	 * Converts a graph file into another format. If the file names end with <code>.gz</code> they are automatically
	 * (de)compressed.
	 * @param inputFile the name of the input file
	 * @param parser the parser to use for parsing the input file
	 * @param outputFile the name of the output file
	 * @param serializer the serializer to use for writing the output file
	 * @throws ParseException if the graphs could not be parsed
	 * @throws IOException if there is an error reading or writing a file
	 */
	public static void convert(String inputFile, GraphParser parser, String outputFile, GraphParser serializer) throws ParseException, IOException {
		InputStream in = new FileInputStream(inputFile);
		if (inputFile.endsWith(".gz")) {
			in = new GZIPInputStream(in);
		}
		
		
		OutputStream out = new FileOutputStream(outputFile);
		if (outputFile.endsWith(".gz")) {
			out = new GZIPOutputStream(out);
		}
		
		Graph[] graphs = parser.parse(in, UndirectedListGraph.Factory.instance);
		serializer.serialize(graphs, out);
		
		
		in.close();
		out.close();
	}
	
	/**
	 * 
	 * @param args
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException,
			ParseException, IOException {
		if ((args.length < 2) || (args[0].equals("--help"))) {
			usage();
			System.exit(1);
		}

		GraphParser parser = SmilesParser.instance, serializer = LineGraphParser.instance;
		for (int i = 0; i < args.length; i++) {
			if (args[i].charAt(0) == '-') {
				String[] option = args[i].split("=");
				
				if (option[0].equals("-parser")) {
					parser = (GraphParser) Class.forName(option[1]).newInstance();
				} else if (option[0].equals("-serializer")) {
					serializer = (GraphParser) Class.forName(option[1]).newInstance();
				} else {
					System.err.println("Unknown option: " + option[0]);
					usage();
					System.exit(1);					
				}				
			}
		}

		convert(args[args.length - 2], parser, args[args.length - 1], serializer);
	}
}
