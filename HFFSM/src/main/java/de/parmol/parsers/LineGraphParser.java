/*
 * Created on Dec 9, 2004
 * 
 * Copyright 2004, 2005 Marc Wörlein, Thorsten Meinl
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;

import de.parmol.graph.Graph;
import de.parmol.graph.GraphFactory;
import de.parmol.graph.MutableGraph;

/**
 * This parser parses graphs in the node list-edge list format.
 * 
 * @author Marc Wörlein <marc.woerlein@gmx.de>
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class LineGraphParser implements GraphParser {
	/**
	 * A public instance of this parser.
	 */
	public final static LineGraphParser instance = new LineGraphParser();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.graph.GraphParser#parse(java.lang.String, de.parmol.graph.GraphFactory)
	 */
	public Graph parse(String text, GraphFactory factory) throws ParseException {
		String[] lines = text.split("[\\n\\r]");
		
		if (! lines[0].startsWith("t # ")) {
			throw new ParseException("Expected 't # id' but found '" + lines[0] + "'", 0);
		}
		
		MutableGraph g = factory.createGraph(lines[0].substring("t # ".length()));
		
		int i;
		for (i = 1; (i < lines.length) && (lines[i].charAt(0) == 'v'); i++) {
			String[] parts = lines[i].split("\\s+");
			
			int index = Integer.parseInt(parts[1]);
			if (index != i - 1) {
				throw new ParseException("The node list ist not sorted", i);
			}
			
			g.addNode(Integer.parseInt(parts[2]));
		}
		
		for (;(i < lines.length) && (lines[i].charAt(0) == 'e'); i++) {
			String[] parts = lines[i].split("\\s+");
			
			g.addEdge(g.getNode(Integer.parseInt(parts[1])), g.getNode(Integer.parseInt(parts[2])),
				Integer.parseInt(parts[3]));
		}
		
		return g;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.graph.GraphParser#serialize(de.parmol.graph.UndirectedGraph)
	 */
	public String serialize(Graph g) {
		StringBuffer buf = new StringBuffer(1024);
		
		buf.append("t # " + g.getName().substring(0) + "\n");
		for (int k = 0; k < g.getNodeCount(); k++) {
			buf.append("v " + k + " " + g.getNodeLabel(g.getNode(k))+"\n");
		}
		
		for (int i = 0; i < g.getEdgeCount(); i++) {
			int edge = g.getEdge(i);
			buf.append("e " + g.getNodeA(edge) + " " + g.getNodeB(edge) + " "+g.getEdgeLabel(edge)+"\n");
		}		
		
		return buf.toString();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.graph.GraphParser#serialize(de.parmol.graph.UndirectedGraph[],
	 *      java.io.OutputStream)
	 */
	public void serialize(Graph[] graphs, OutputStream out) throws IOException {
		for (int i = 0; i < graphs.length; i++) {
			out.write(serialize(graphs[i]).getBytes());
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.graph.GraphParser#parse(java.io.InputStream, de.parmol.graph.GraphFactory)
	 */
	public Graph[] parse(InputStream in, GraphFactory factory) throws IOException, ParseException {
		ArrayList graphs = new ArrayList(1024);
		
		BufferedReader bin = new BufferedReader(new InputStreamReader(in));
		
		int i = 0;
		String line = bin.readLine();
		while (line != null) {
			if (! line.startsWith("t # ")) {
				throw new ParseException("Expected 't # id' but found '" + line + "'", i);
			}

			MutableGraph g = factory.createGraph(line.substring("t # ".length()));
			
			int k = 0;
			while (((line = bin.readLine()) != null) && (line.charAt(0) == 'v')) {				
				String[] parts = line.split("\\s+");
					
				int index = Integer.parseInt(parts[1]);
				if (index != k) {
					throw new ParseException("The node list ist not sorted", i);
				}
					
				g.addNode(Integer.parseInt(parts[2]));
				k++;
			}
				
			k = 0;
			while ((line != null) && (line.charAt(0) == 'e')) {
				String[] parts = line.split("\\s+");
				
				g.addEdge(g.getNode(Integer.parseInt(parts[1])), g.getNode(Integer.parseInt(parts[2])),
					Integer.parseInt(parts[3]));
				
				line = bin.readLine();
			}
			
			graphs.add(g);
			i++;
		}
		
		return (Graph[]) graphs.toArray(new Graph[graphs.size()]); 
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