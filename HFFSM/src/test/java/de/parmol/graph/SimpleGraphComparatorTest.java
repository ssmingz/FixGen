/*
 * Created on Jun 15, 2004
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

import junit.framework.TestCase;
import de.parmol.graph.Graph;
import de.parmol.graph.MutableGraph;
import de.parmol.graph.NodeLabelDegreeComparator;
import de.parmol.graph.SimpleEdgeComparator;
import de.parmol.graph.SimpleGraphComparator;
import de.parmol.graph.UndirectedListGraph;
import de.parmol.graph.Util;
import de.parmol.parsers.SimpleUndirectedGraphParser;
import de.parmol.util.GraphGenerator;

/**
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class SimpleGraphComparatorTest extends TestCase {
	/**
	 * @param name
	 */
	public SimpleGraphComparatorTest(String name) {
		super(name);
	}

	public void testComparator() throws InstantiationException, IllegalAccessException {
		GraphGenerator.instance.setGraphFactory(UndirectedListGraph.Factory.instance);
		Graph g1 = GraphGenerator.instance.generateGraph(5, 15);

		SimpleGraphComparator comp = new SimpleGraphComparator(NodeLabelDegreeComparator.instance,
				SimpleEdgeComparator.instance);
		assertTrue(comp.compare(g1, g1) == 0);

		MutableGraph g2 = (MutableGraph) g1.clone();
		assertTrue(comp.compare(g1, g2) == 0);

		int nodeA, nodeB, nodeCount = g2.getNodeCount();
		do {
			nodeA = g2.getNode((int) (Math.random() * nodeCount));
			nodeB = g2.getNode((int) (Math.random() * nodeCount));
		} while ((nodeA == nodeB) || (g1.getEdge(nodeA, nodeB) != Graph.NO_EDGE));

		g2.addEdge(nodeA, nodeB, hashCode());

		assertFalse(comp.compare(g1, g2) == 0);

		g2 = Util.shuffleGraph(g1);

		SimpleUndirectedGraphParser p = new SimpleUndirectedGraphParser();
		System.out.println(p.serialize(g1));
		System.out.println(p.serialize(g2));

		assertTrue(comp.compare(g1, g2) == 0);
	}
}