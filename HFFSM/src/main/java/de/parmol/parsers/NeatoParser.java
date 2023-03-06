/*
 * Created on Dec 9, 2004
 * 
 * Copyright 2004, 2005 Marc Wörlein
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
 *  
 */
package de.parmol.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;

import de.parmol.graph.Graph;
import de.parmol.graph.GraphFactory;

/**
 * This class is support the neato format knwon from GraphViz.
 * 
 * @author Marc Woerlein <marc.woerlein@gmx.de>
 */
public class NeatoParser implements GraphParser {
	/** A public instance of this parser */
	public final static NeatoParser instance = new NeatoParser();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.graph.GraphParser#parse(java.lang.String, de.parmol.graph.GraphFactory)
	 */
	public Graph parse(String text, GraphFactory factory) throws ParseException {
		throw new ParseException("parsing not supported", -1);
	}


	private void serialize(Graph g, Writer out) throws IOException {
		out.write("graph \"" + g.getName() + "\"{\n");
		out.write("\tlabel=\"" + g.getName() + "\"\n");
		for (int k = 0; k < g.getNodeCount(); k++) {
			out.write("\tK" + k + " [label=\"" + SLNParser.ATOM_SYMBOLS[g.getNodeLabel(k)] + "\"];\n");
		}
		for (int i = 0; i < g.getEdgeCount(); i++) {
			int edge = g.getEdge(i);
			if (g.getEdgeLabel(edge) == 4) {
				out.write("\tK" + g.getNodeA(edge) + " -- K" + g.getNodeB(edge) + "[color=red];\n");
			} else
				for (int j = 0; j < g.getEdgeLabel(edge); j++) {
					out.write("\tK" + g.getNodeA(edge) + " -- K" + g.getNodeB(edge) + ";\n");
				}
		}
		out.write("}\n");
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.graph.GraphParser#serialize(de.parmol.graph.UndirectedGraph)
	 */
	public String serialize(Graph g) {
		StringWriter s = new StringWriter();
		try {
			serialize(g, s);
			return s.toString();
		} catch (IOException e) {
			return "";
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.graph.GraphParser#serialize(de.parmol.graph.UndirectedGraph[],
	 *      java.io.OutputStream)
	 */
	public void serialize(Graph[] graphs, OutputStream out) throws IOException {
		Writer w = new OutputStreamWriter(out);
		for (int i = 0; i < graphs.length; i++) {
			serialize(graphs[i], w);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.graph.GraphParser#parse(java.io.InputStream, de.parmol.graph.GraphFactory)
	 */
	public Graph[] parse(InputStream in, GraphFactory factory) throws IOException, ParseException {
		throw new ParseException("parsing not supported", -1);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.graph.GraphParser#getDesiredGraphFactoryProperties()
	 */
	public int getDesiredGraphFactoryProperties() {
		return GraphFactory.UNDIRECTED_GRAPH;
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.parsers.GraphParser#directed()
	 */
	public boolean directed(){ 
		return false; 
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphParser#getNodeLabel(int)
	 */
	public String getNodeLabel(int nodeLabel) {
		return Integer.toString(nodeLabel);
	}
}