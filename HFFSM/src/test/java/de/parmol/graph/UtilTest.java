/*
 * Created on Aug 13, 2004
 * 
 * Copyright 2004,2005 Thorsten Meinl
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
package de.parmol.graph;

import java.text.ParseException;

import junit.framework.TestCase;
import de.parmol.graph.Graph;
import de.parmol.graph.UndirectedGraph;
import de.parmol.graph.UndirectedListGraph;
import de.parmol.graph.UndirectedMatrixGraph;
import de.parmol.graph.Util;
import de.parmol.parsers.SLNParser;
import de.parmol.parsers.SimpleUndirectedGraphParser;

/**
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class UtilTest extends TestCase {

	/**
	 * 
	 */
	public UtilTest() {
		super();
	}


	/**
	 * @param name
	 */
	public UtilTest(String name) {
		super(name);
	}


	public void testConnectedComponents() throws ParseException {
		final String graph1 = "1\n" + "1 1 - - -\n" + "1 2 - - -\n" + "- - 3 1 1\n" + "- - 1 4 -\n" + "- - 1 - 5\n";
		final String graph2 = "2\n" + "1 - - - -\n" + "- 2 - - -\n" + "- - 3 - -\n" + "- - - 4 -\n" + "- - - - 5\n";
		final String graph3 = "2\n" + "1 1 - - -\n" + "1 2 1 - -\n" + "- 1 3 1 -\n" + "- - 1 4 1\n" + "- - - 1 5\n";

		SimpleUndirectedGraphParser p = new SimpleUndirectedGraphParser();

		UndirectedGraph g = (UndirectedGraph) p.parse(graph1, UndirectedMatrixGraph.Factory.instance);
		assertEquals(2, Util.getConnectedComponents(g).length);

		g = (UndirectedGraph) p.parse(graph2, UndirectedMatrixGraph.Factory.instance);
		assertEquals(5, Util.getConnectedComponents(g).length);

		g = (UndirectedGraph) p.parse(graph3, UndirectedMatrixGraph.Factory.instance);
		assertEquals(1, Util.getConnectedComponents(g).length);
	}
	
	public void testNodePartitions() throws ParseException {
		final String[] graphs = { "C[1]C(Br)CC@1", "C[1]CCCCC@1", "C[1]CCNCCOC@1" };
		final int[] expectedPartitions = { 4, 1, 8 }; 
		
		for (int i = 0; i < graphs.length; i++) {
			Graph g = SLNParser.instance.parse(graphs[i], UndirectedListGraph.Factory.instance);
		
			int[] parts = Util.getNodePartitions(g);
			int partCount = 0;
			
    	for (int k = 0; k < parts.length; k++) {
    		partCount = (parts[k] > partCount) ? parts[k] : partCount;
    	}
			assertEquals(expectedPartitions[i], partCount);
		}
	}
}