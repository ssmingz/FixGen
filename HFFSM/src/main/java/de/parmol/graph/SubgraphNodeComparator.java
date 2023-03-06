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
 * This interface describes comparators that compare two nodes in a subgraph isomorphism test.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public interface SubgraphNodeComparator {
	/**
	 * Compares two nodes, where the first node is from a (possible) subgraph of the second graph.
	 * @param g1 the first graph (which is a possible subgraph of the second graph)
	 * @param node1 a node in the first graph
	 * @param g2 the second graph (which is possible supergraph of the first graph)
	 * @param node2 a node in the second graph
	 * @return a positive value, if the first node is greater than the second node, a negative value, if the first node is
	 * smaller than the second node or zero if both edges are the same.
	 */  public int compare(Graph g1, int node1, Graph g2, int node2);
}

