/*
 * Created on Dec 13, 2004
 *
 * Copyright 2004, 2005 Thorsten Meinl
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

import java.text.ParseException;

import de.parmol.graph.Graph;
import de.parmol.graph.GraphFactory;
import de.parmol.graph.MutableGraph;


/**
 * @author Thorsten.Meinl@informatik.uni-erlangen.de
 *
 */
public class SimpleDirectedGraphParser extends SimpleUndirectedGraphParser {
	
	/**
	 * An instance of the SimpleDirectedGraphParser.
	 */
	public final static SimpleDirectedGraphParser instance = new SimpleDirectedGraphParser();
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphParser#getDesiredGraphFactoryProperties()
	 */
	public int getDesiredGraphFactoryProperties() {
		return GraphFactory.DIRECTED_GRAPH; 
	}
	
	/*
	 *  (non-Javadoc)
	 * @see de.parmol.parsers.GraphParser#directed()
	 */
	public boolean directed(){ 
		return true; 
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphParser#parse(java.lang.String, de.parmol.graph.GraphFactory)
	 */
	public Graph parse(String text, GraphFactory factory) throws ParseException {
    String[] rows = text.split("\n");
		
    final MutableGraph g = factory.createGraph(rows[0]);

    int[] nodes = new int[rows.length - 1];
    for (int row = 0; row < rows.length - 1; row++) {
      String[] cols = rows[row + 1].split(" ");

      nodes[row] = g.addNode(Integer.parseInt(cols[row]));
    }
    
    for (int row = 0; row < rows.length - 1; row++) {
    	String[] cols = rows[row + 1].split(" ");
    	
      for (int col = cols.length - 1; col >= 0; col--) {
        if ((row != col) && (cols[col].charAt(0) != '-')) {
          try {
            int label = Integer.parseInt(cols[col]);
            g.addEdge(nodes[row], nodes[col], label);
          } catch (NumberFormatException ex) { }
        }
      }
    }
    return g;
	}
}
