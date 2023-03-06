/*
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
import de.parmol.graph.MutableGraph;
import de.parmol.graph.SimpleEdgeComparator;
import de.parmol.graph.SimpleNodeComparator;
import de.parmol.graph.SimpleSubgraphComparator;
import de.parmol.graph.UndirectedListGraph;
import de.parmol.util.GraphGenerator;

/**
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class SimpleSubgraphComparatorTest extends TestCase {
  /**
   * @param name
   */
  public SimpleSubgraphComparatorTest(String name) {
    super(name);
  }

  public void testComparator() {
		GraphGenerator.instance.setGraphFactory(UndirectedListGraph.Factory.instance);
		MutableGraph g1 = (MutableGraph) GraphGenerator.instance.generateGraph(10, 50);

    SimpleSubgraphComparator comp = new SimpleSubgraphComparator(SimpleNodeComparator.instance,
        SimpleEdgeComparator.instance);
    assertTrue(comp.compare(g1, g1) == 0);

    MutableGraph g2 = (MutableGraph) g1.clone();
    assertTrue(comp.compare(g1, g2) == 0);
    
    g2.addNodeAndEdge(g2.getNode(0), 123, 456);

    assertTrue(comp.compare(g1, g2) == 0);
    
    g1.addNodeAndEdge(g2.getNode(0), 321, 654);
    assertFalse(comp.compare(g1, g2) == 0);
  }
}

