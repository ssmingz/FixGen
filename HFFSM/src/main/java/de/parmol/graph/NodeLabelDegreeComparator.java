/*
 * 
 * This file is part of ParMol. ParMol is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 * 
 * ParMol is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * ParMol; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 * 
 */
package de.parmol.graph;


/**
 * This comparator compares Node by looking at their labels and degrees.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class NodeLabelDegreeComparator implements GraphNodeComparator {
  /**
   * The single instance of this comparator.
   */
	public final static NodeLabelDegreeComparator instance = new NodeLabelDegreeComparator();

  private NodeLabelDegreeComparator() {}

  /* (non-Javadoc)
   * @see de.parmol.graph.GraphNodeComparator#compare(de.parmol.graph.UndirectedGraph, int, de.parmol.graph.UndirectedGraph, int)
   */
  public int compare(Graph g1, int node1, Graph g2, int node2) {
    int diff = g1.getNodeLabel(node1) - g2.getNodeLabel(node2);
    if (diff != 0) return diff;

    diff = g1.getDegree(node1) - g2.getDegree(node2);
    return diff;
  }

}

