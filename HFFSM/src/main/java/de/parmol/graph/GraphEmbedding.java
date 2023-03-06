/*
 * Created on May 17, 2004
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
 * This interface simply stores that there is an embedding in a graph.
 * 
 * @author Thorsten Meinl <Thorsten@meinl.bnv-bamberg.de>
 * 
 */
public interface GraphEmbedding {
	/**
	 * @return the graph in which this subgraph occurs
	 */
	public Graph getSuperGraph();


	/**
	 * Returns the subgraph of the embedding
	 * 
	 * @return the subgraph of the embedding
	 */
	public Graph getSubGraph();


	/**
	 * Returns if this embedding refers to a directed or undirected graph.
	 * 
	 * @return <code>true</code> if the graph is directed, <code>false</code>
	 *         otherwise
	 */
	public boolean isDirectedGraphEmbedding();


	/**
	 * Creates a copy of this embedding
	 * 
	 * @return a new embedding
	 */
	public Object clone();


	/**
	 * Returns the node in the <b>super </b>graph that corresponds the the given
	 * node in the <b>sub </b>graph.
	 * 
	 * @param subgraphNode a node in the subgraph
	 * @return a node in the supergraph
	 */
	public int getSupergraphNode(int subgraphNode);


	/**
	 * Returns the edge in the <b>super </b>graph that corresponds the the given
	 * edge in the <b>sub </b>graph.
	 * 
	 * @param subgraphEdge an edge in the subgraph
	 * @return an edge in the supergraph
	 */
	public int getSupergraphEdge(int subgraphEdge);


	/**
	 * Returns the edge in the <b>sub </b>graph that corresponds the the given
	 * edge in the <b>super </b>graph.
	 * 
	 * @param supergraphEdge an edge in the supergraph
	 * @return an edge in the subgraph or Graph.NO_EDGE if no mapping exists
	 */
	public int getSubgraphEdge(int supergraphEdge);


	/**
	 * Returns the node in the <b>sub </b>graph that corresponds the the given
	 * node in the <b>super </b>graph.
	 * 
	 * @param supergraphNode a node in the supergraph
	 * @return a node in the subgraph or Graph.NO_NODE if no mapping exists
	 */
	public int getSubgraphNode(int supergraphNode);


	/**
	 * Returns the object assiocated with the given node in the supergraph.
	 * 
	 * @param subgraphNode the node
	 * @return the node object or <code>null</code>
	 */
	public Object getNodeObject(int subgraphNode);


	/**
	 * Returns the object assiocated with the given edge in the supergraph.
	 * 
	 * @param subgraphEdge the edge
	 * @return the edge object or <code>null</code>
	 */	
	public Object getEdgeObject(int subgraphEdge);

	/**
	 * Returns if this embeddings overlaps with the given embeddings, i.e. has at least one node in common.
	 * @param other an embedding in the same graph
	 * @return <code>true</code> if both embeddings overlap, <code>false</code> otherwise
	 */
	public boolean overlaps(GraphEmbedding other);
}
