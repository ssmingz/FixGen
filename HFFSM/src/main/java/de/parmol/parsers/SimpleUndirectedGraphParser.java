/*
 * Created on Jun 14, 2004
 *
 * Copyright 2004 Thorsten Meinl
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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.StringTokenizer;

import de.parmol.Util;
import de.parmol.graph.Graph;
import de.parmol.graph.GraphFactory;
import de.parmol.graph.MutableGraph;


/**
 * This GraphParser just reads and writes table representations of graphs.
 * 
 * @author Thorsten.Meinl@informatik.uni-erlangen.de
 */
public class SimpleUndirectedGraphParser implements GraphParser {
	private DecimalFormat m_format;

	/**
	 * An instance of the SimpleUndirectedGraphParser.
	 */
	public final static SimpleUndirectedGraphParser instance = new SimpleUndirectedGraphParser();


	/*
	 * @see de.parmol.graph.GraphParser#parse(java.lang.String)
	 */
	public Graph parse(String text, GraphFactory factory) throws ParseException {
		StringTokenizer rows = new StringTokenizer(text, "\n");

		final MutableGraph g = factory.createGraph(rows.nextToken());

		int row = 0;
		int[] nodes = new int[rows.countTokens()];
		while (rows.hasMoreTokens()) {
			String[] cols = rows.nextToken().split(" ");

			nodes[row] = g.addNode(Integer.parseInt(cols[row]));
			for (int col = row - 1; col >= 0; col--) {
				if (cols[col].charAt(0) != '-') {
					try {
						int label = Integer.parseInt(cols[col]);
						g.addEdge(nodes[row], nodes[col], label);
					} catch (NumberFormatException ex) {
					}
				}
			}
			row++;
		}
		return g;
	}


	/*
	 * @see de.parmol.graph.GraphParser#serialize(de.parmol.graph.UndirectedGraph)
	 */
	public String serialize(Graph g) {
		final int nodeCount = g.getNodeCount();

		int maxLength = 0;
		for (int row = nodeCount - 1; row >= 0; row--) {
			for (int col = nodeCount - 1; col >= 0; col--) {
				if (row == col) {
					maxLength = Math.max(maxLength, Util.getDigits(g.getNodeLabel(g.getNode(row))));
				} else {
					int edge = g.getEdge(g.getNode(row), g.getNode(col));
					if (edge != Graph.NO_EDGE) {
						maxLength = Math.max(maxLength, Util.getDigits(g.getEdgeLabel(edge)));
					}
				}

			}
		}

		m_format = new DecimalFormat("0000000000000000000000000000000000000".substring(0, maxLength));

		StringBuffer b = new StringBuffer(nodeCount * nodeCount * maxLength + 2 * nodeCount + g.getName().length() + 2);
		b.append(g.getName()).append('\n');
		for (int row = 0; row < nodeCount; row++) {
			for (int col = 0; col < nodeCount; col++) {
				if (row == col) {
					b.append(m_format.format(g.getNodeLabel(g.getNode(row))));
				} else {
					int edge = g.getEdge(g.getNode(row), g.getNode(col));
					if (edge != Graph.NO_EDGE) {
						b.append(getEdgeLabel(g.getEdgeLabel(edge)));
					} else {
						b.append("---------------------------------------------".substring(0, maxLength));
					}
				}
				if (col < nodeCount) b.append(' ');
			}
			if (row < nodeCount) b.append('\n');
		}

		return b.toString();
	}


	public String getNodeLabel(int nodeLabel) {
		return Integer.toString(nodeLabel);
	}


	protected String getEdgeLabel(int edgeLabel) {
		//return m_format.format(edgeLabel);
		return (char)edgeLabel+"";
	}


	public void serialize(Graph[] graphs, OutputStream outStream) throws IOException {
		BufferedOutputStream out = new BufferedOutputStream(outStream);

		out.write((graphs.length + "\n").getBytes());
		for (int i = 0; i < graphs.length; i++) {
			String s = serialize(graphs[i]);
			out.write(s.getBytes());
			out.write("#\n".getBytes());
		}
		out.flush();
	}


	public Graph[] parse(InputStream inStream, GraphFactory factory) throws IOException, ParseException {
		BufferedReader in = new BufferedReader(new InputStreamReader(inStream));

		String line = in.readLine();
		int count = Integer.parseInt(line);
		Graph[] graphs = new Graph[count];
		count = 0;

		StringBuffer buf = new StringBuffer(2048);
		while ((line = in.readLine()) != null) {
			if (line.startsWith("#")) {
				graphs[count++] = parse(buf.toString(), factory);
				buf = new StringBuffer(2048);
			} else {
				buf.append(line).append('\n');
			}
		}
		return graphs;
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
}