/*
 * Created on 12.12.2004
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

/**
 * This interface describes graphs whose edges are directed, i.e. have a direction.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public interface DirectedGraph extends Graph {
	/**
	 * Constant for edges that are incoming edges.
	 */
	public final static int INCOMING_EDGE = 1;

	/**
	 * Constant for edges that are outgoing edges.
	 */
	public final static int OUTGOING_EDGE = 2;
	
	/**
	 * Returns the number of incoming edges.
	 * @param node a node
	 * @return the number of incoming edges
	 */
	public int getInDegree(int node);
	
	/**
	 * Returns the incoming edge with the given index for the given node.
	 * @param node a node
	 * @param number the number of the incoming edge, ranging between 0 and getInDegree() - 1
	 * @return an edge
	 */
	public int getIncomingNodeEdge(int node, int number);
	
	/**
	 * Returns the number of outgoing edges.
	 * @param node a node
	 * @return the number of outgoing edges
	 */
	public int getOutDegree(int node);
	
	/**
	 * Returns the outgoing edge with the given index for the given node.
	 * @param node a node
	 * @param number the number of the outgoing edge, ranging between 0 and getOutDegree() - 1
	 * @return an edge
	 */
	public int getOutgoingNodeEdge(int node, int number);
	
	/**
	 * Returns the direction of the given edge as it is seen from the given node  
	 * @param edge an edge
	 * @param node a node connected to the edge
	 * @return <code>INCOMING_EDGE</code> if the edge points towards the node, <code>OUTGOING_EDGE</code> if the edge goes away from the node
	 */
	public int getEdgeDirection(int edge, int node);
}
