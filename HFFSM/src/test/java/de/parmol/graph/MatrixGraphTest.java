/*
 * Created on Jun 14, 2004
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
import de.parmol.graph.MatrixGraph;
import de.parmol.graph.NodeLabelDegreeComparator;
import de.parmol.graph.SimpleEdgeComparator;
import de.parmol.graph.SimpleGraphComparator;
import de.parmol.graph.UndirectedListGraph;
import de.parmol.graph.UndirectedMatrixGraph;
import de.parmol.util.GraphGenerator;

/**
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class MatrixGraphTest extends TestCase {
  /**
   * @param name
   */
  public MatrixGraphTest(String name) {
    super(name);
  }

  public void testAdd() {
  	GraphGenerator.instance.setGraphFactory(UndirectedMatrixGraph.Factory.instance);
    MatrixGraph g1 = (MatrixGraph) GraphGenerator.instance.generateGraph(20, 200);

    int nodeCount = g1.getNodeCount();
    int edgeCount = g1.getEdgeCount();

    int node = g1.addNode(123);
    assertEquals(nodeCount + 1, g1.getNodeCount());

    g1.addEdge(node, g1.getNode(0), edgeCount);
    assertEquals(edgeCount + 1, g1.getEdgeCount());

    g1.addNodeAndEdge(node, 7890, 77654);
    assertEquals(nodeCount + 2, g1.getNodeCount());
    assertEquals(edgeCount + 2, g1.getEdgeCount());
  }

  public void testConvert() {    
  	GraphGenerator.instance.setGraphFactory(UndirectedListGraph.Factory.instance);
    UndirectedListGraph g1 = (UndirectedListGraph) GraphGenerator.instance.generateGraph(20, 200);

    MatrixGraph g2 = new UndirectedMatrixGraph(g1);
    assertEquals(g1.getNodeCount(), g2.getNodeCount());
    assertEquals(g1.getEdgeCount(), g2.getEdgeCount());

    SimpleGraphComparator comp =
      new SimpleGraphComparator(NodeLabelDegreeComparator.instance, SimpleEdgeComparator.instance);
    assertTrue(comp.compare(g1, g2) == 0);
  }
  
  public void testCopy() {
  	GraphGenerator.instance.setGraphFactory(UndirectedMatrixGraph.Factory.instance);
    MatrixGraph g1 = (MatrixGraph) GraphGenerator.instance.generateGraph(20, 200);

   MatrixGraph g2 = (MatrixGraph) g1.clone();

   SimpleGraphComparator comp =
     new SimpleGraphComparator(NodeLabelDegreeComparator.instance, SimpleEdgeComparator.instance);
   assertTrue(comp.compare(g1, g2) == 0);
  }
  
  public void testIndexFunction() {
  	GraphGenerator.instance.setGraphFactory(UndirectedMatrixGraph.Factory.instance);
    MatrixGraph g1 = (MatrixGraph) GraphGenerator.instance.generateGraph(20, 200);

  	
  	
  	for (int i = 0; i < 20; i++) {
  		assertEquals(g1.getNodeIndex(g1.getNode(i)), i);
  	}  	
  }
  
  
  public void testRemove() {
  	GraphGenerator.instance.setGraphFactory(UndirectedMatrixGraph.Factory.instance);
    UndirectedMatrixGraph g1 = (UndirectedMatrixGraph) GraphGenerator.instance.generateGraph(20, 200);

  	MatrixGraph g2 = new UndirectedMatrixGraph(g1);
  	
  	g2.addNodeAndEdge(1, 14, 16);  	
  	g2.removeNode(g2.getNodeCount() - 1);
  	
    SimpleGraphComparator comp =
      new SimpleGraphComparator(NodeLabelDegreeComparator.instance, SimpleEdgeComparator.instance);
  	
    assertTrue(comp.compare(g1, g2) == 0);
  }
  
}
