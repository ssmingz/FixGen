/*
 * Created on Jan 11, 2005
 * 
 * Copyright 2005 Sebastian Seifert
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import de.parmol.graph.DirectedGraph;
import de.parmol.graph.DirectedListGraph;
import de.parmol.graph.Graph;
import de.parmol.graph.GraphFactory;
import de.parmol.graph.LabelRegistry;
import de.parmol.graph.MutableGraph;
import de.parmol.parsers.antlr.DotLexer;
import de.parmol.parsers.antlr.DotParser;

/**
 * This parser parses directed graphs that are in the dot format (known from GraphViz).
 * 
 * @author Sebastian Seifert <sebastian@kontextfrei.de>
 */
public class DotGraphParser implements GraphParser {
	/** A public instance of this parser */
	public final static DotGraphParser instance = new DotGraphParser();
	private boolean directed=false;
	
	/** */
	public LabelRegistry labelReg;
	
	private HashMap nodeIDStr2Int = new HashMap();
	/**
	 * Create a new Parser with no initally known labels.
	 *
	 */
	public DotGraphParser() {
		this(new LabelRegistry());
	}
	
	/**
	 * Create a new Parser with an initial set of labels.
	 * @param labelRegistry Initially known edge and node labels. 
	 * */
	public DotGraphParser(LabelRegistry labelRegistry) {
		this.labelReg = labelRegistry;
	}
	
	public String serialize(Graph g) {
		final boolean directed = (g instanceof DirectedGraph);		
		
		StringBuffer buf = new StringBuffer(2048);
		
		buf.append((directed ? "digraph " : "graph ") + "\"" +g.getName() +"\"" + " {\n");
		
		for (int i = g.getNodeCount() - 1; i >= 0; i--) {
			int labelInt = g.getNodeLabel(g.getNode(i));
			String labelStr;
			
			if( labelReg.existsNodeLabel(labelInt) ) {
				labelStr = labelReg.nodeLabelStr(labelInt);
			} else {
				labelStr = "" + labelInt;
			}
			
			Object o = g.getNodeObject(g.getNode(i));
			if (o != null) {
				buf.append("\tNode_" +  i +" [label=\"" +  labelStr + "\"");
				
				
				if (o instanceof Map) {
					for (Iterator it = ((Map) o).entrySet().iterator(); it.hasNext();) {
						Map.Entry e = (Map.Entry) it.next();
						
						buf.append("," + e.getKey() + "=\"" + e.getValue().toString().replaceAll("\"", "\\\"") + "\"");
					}
				} else {
					buf.append(o.toString());
				}								
				buf.append("];\n");
			} else {
				buf.append("\tNode_" +  i +" [label=\"" +  labelStr + "\"];\n");
			}
		}
		
		
		for (int i = g.getEdgeCount() - 1; i >= 0; i--) {
			int edge = g.getEdge(i);
			
			buf.append("\tNode_" +  g.getNodeIndex(g.getNodeA(edge)) + 
					(directed ? " -> " : " -- ") +
					"Node_" + g.getNodeIndex(g.getNodeB(edge)));
			
			int labelInt = g.getEdgeLabel(g.getEdge(i));
			String labelStr;
			if( labelReg.existsEdgeLabel(labelInt) ) {
				labelStr = labelReg.edgeLabelStr(labelInt);
			} else {
				labelStr = "" + labelInt;
			}
			
			Object o = g.getEdgeObject(edge);
			if (o != null) {
				buf.append(" [label=\"" + labelStr + "\"");
				
				if (o instanceof Map) {
					for (Iterator it = ((Map) o).entrySet().iterator(); it.hasNext();) {
						Map.Entry e = (Map.Entry) it.next();
						
						buf.append("," + e.getKey() + "=\"" + e.getValue().toString().replaceAll("\"", "\\\"") + "\"");
					}
				} else {
					buf.append(o.toString());
				}								
				buf.append("];\n");
			} else {
				buf.append(" [label=\"" + labelStr + "\"];\n");
			}
			
			
			
		}
		
		
		buf.append("}\n");
		return buf.toString();
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphParser#serialize(de.parmol.graph.Graph[], java.io.OutputStream)
	 */
	public void serialize(Graph[] graphs, OutputStream out) throws IOException {
  	BufferedOutputStream bout = new BufferedOutputStream(out);
  	for (int i = 0; i < graphs.length; i++) {
    	bout.write(serialize(graphs[i]).getBytes());
    	bout.write("\n".getBytes());
    }
  	bout.flush();
	}
	
	/* (non-Javadoc)
	 * @see de.parmol.parsers.GraphParser#parse(java.lang.String, de.parmol.graph.GraphFactory)
	 */
	public Graph parse(String text, GraphFactory factory) throws ParseException {
		StringReader sr = new StringReader(text);
		
		DotLexer l = new DotLexer(sr);
		DotParser p = new DotParser(l);
		Graph g = parseSingleGraph(p, factory);
		
		return g;
	}

	/* (non-Javadoc)
	 * @see de.parmol.parsers.GraphParser#parse(java.io.InputStream, de.parmol.graph.GraphFactory)
	 */
	public Graph[] parse(InputStream in, GraphFactory factory)
			throws IOException, ParseException {
		
		DotLexer l = new DotLexer(in);
		DotParser p = new DotParser(l);
		
		LinkedList graphs = new LinkedList();
		
			//while(true) {
				graphs.add(parseSingleGraph(p,factory));
			//}
		
		Graph[] g = new Graph[graphs.size()];
		Iterator it = graphs.iterator();
		int i=0;
		while(it.hasNext()) {
			g[i++] = (Graph)it.next();
		}
		return g;
	}


	/* (non-Javadoc)
	 * @see de.parmol.parsers.GraphParser#getNodeLabel(int)
	 */
	public String getNodeLabel(int nodeLabel) {
		return labelReg.nodeLabelStr(nodeLabel);
	}


	private Graph parseSingleGraph(DotParser p, GraphFactory factory) throws ParseException {
		
		nodeIDStr2Int.clear();
		
		try {
			p.graph();
		} catch (RecognitionException e) {
			e.printStackTrace();
			throw new ParseException(e.toString(), e.getLine());
		} catch (TokenStreamException e) {
			e.printStackTrace();
			throw new ParseException(e.toString(), 0);
		}
		
		if(!p.directed()) {
			// an undirected graph file was read
			throw new ParseException("TODO: Change something about GraphFactory semantics so an undirected graph can be read.", 0);
		}
		
		MutableGraph g = factory.createGraph(p.getName());
		
		// Add nodes to graph.
		Map nodes = p.getNodeMap();
		Iterator nodeIt = nodes.entrySet().iterator();
		
		while(nodeIt.hasNext()) {
			Map.Entry e = (Map.Entry)nodeIt.next();
			int nodeID = g.addNode(labelReg.nodeLabelInt((String)e.getValue()));
			nodeIDStr2Int.put(e.getKey(), new Integer(nodeID));
		}
		
		//Add edges to graph.
		Collection edges = p.getEdges();
		Iterator edgeIt = edges.iterator();
		while(edgeIt.hasNext()) {
			DotParser.EdgeDesc e = (DotParser.EdgeDesc) edgeIt.next();
			int edge = g.addEdge(	((Integer)nodeIDStr2Int.get(e.nodeA)).intValue(),
						((Integer)nodeIDStr2Int.get(e.nodeB)).intValue(),
						labelReg.edgeLabelInt(e.label) );
			if(e.attributes != null) {
				g.setEdgeObject(edge, e.attributes);
			}
		}
		directed=p.directed();
		return g;
	}
	
	public boolean directed(){ 
		return directed; 
	}
	
	public int getDesiredGraphFactoryProperties() {
		return GraphFactory.DIRECTED_GRAPH;
	}
	
	
	// ---------------------------------------------------------------- //
	
	/**
	 * small test main-function
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length != 1) {
			System.err.println("Give me a file.");
		}
		
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(args[0]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		DotGraphParser dgp = new DotGraphParser();
		//GraphFactory gf = GraphFactory.getFactory(GraphFactory.DIRECTED_GRAPH|GraphFactory.LIST_GRAPH);
		Graph[] g = null;
		try {
			g = dgp.parse(fis, DirectedListGraph.Factory.instance);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		System.out.println(dgp.serialize(g[0]));
	}
}
