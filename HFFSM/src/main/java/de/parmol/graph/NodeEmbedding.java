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
 * This embedding not only stores the graph in which the subgraph occurs, but
 * also a mapping of the nodes in both graphs.
 * 
 * @author THorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *  
 */
public class NodeEmbedding extends IDEmbedding {
  protected final int[] m_nodeMap;

  /**
   * Creates a new NodeEmbedding.
   * @param superGraph the supergraph in which this embeddings occurs
   * @param subgraph the subgraph represented by this emebdding
   * @param nodeMap a map in which entry i is the node in the <b>super</b>graph to which node i in the <b>sub</b>graph is mapped 
   */
  public NodeEmbedding(Graph superGraph, Graph subgraph, int[] nodeMap) {
    super(superGraph, subgraph);
    m_nodeMap = (int[]) nodeMap.clone();
  }

  /**
   * Creates a new NodeEmbedding by extending the given embedding by a new node
   * @param embedding the embedding that should be extended
   * @param newNode the new node from the supergraph
   */
  public NodeEmbedding(NodeEmbedding embedding, int newNode) {
    super(embedding.m_superGraph, embedding.m_subGraph);
    m_nodeMap = new int[embedding.m_nodeMap.length + 1];

    System.arraycopy(embedding.m_nodeMap, 0, m_nodeMap, 0, m_nodeMap.length);
  }

  /**
   * Returns an array in which entry i is the node in the <b>super</b>graph to which node i from the <b>sub</b>graph is mapped. 
   * @return a map from the nodes in the subgraph to the nodes in the supergraph
   */
  public int[] getNodeMapping() {
  	return (int[]) m_nodeMap.clone();
  }
}