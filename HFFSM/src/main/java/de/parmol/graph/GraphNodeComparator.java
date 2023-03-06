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
 * This interface describes comparators that compare two nodes.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public interface GraphNodeComparator {
	/**
	 * Compares the two nodes in the two graphs.
	 * @param g1 the first graph
	 * @param node1 a node in the first graph
	 * @param g2 the second graph
	 * @param node2 a node in the second graph
	 * @return a positive value, of the first node is greater than the second, a negative value, if the second node
	 * is greater than the first an zero if they are the same
	 */
  public int compare(Graph g1, int node1, Graph g2, int node2);
}

