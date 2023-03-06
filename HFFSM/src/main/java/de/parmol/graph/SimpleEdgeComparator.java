/*
 * Created on Jun 15, 2004
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
 * This comparator compares Edges by just looking at their labels.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *  
 */
public final class SimpleEdgeComparator implements GraphEdgeComparator, SubgraphEdgeComparator {
	/**
	 * The single instance of this comparator.
	 */
	public final static SimpleEdgeComparator instance = new SimpleEdgeComparator();

  private SimpleEdgeComparator() {}

  /* (non-Javadoc)
   * @see de.parmol.graph.GraphEdgeComparator#compare(de.parmol.graph.UndirectedGraph, int, de.parmol.graph.UndirectedGraph, int)
   */
  public int compare(Graph g1, int edge1, Graph g2, int edge2) {
    return g1.getEdgeLabel(edge1) - g2.getEdgeLabel(edge2);
  }

}