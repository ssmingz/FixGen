/*
 * Created on 02.01.2005
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

import java.util.NoSuchElementException;

/**
 * 
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *
 */
public class EdgeOnlyEmbedding implements GraphEmbedding, Graph, DirectedGraph, UndirectedGraph {
	protected Graph m_superGraph;
	protected int m_nodeCount, m_edgeCount;
	protected int[] m_edgeMap;
	

  /**
   * Creates a new EdgeOnlyEmbedding consisting of a single edge.
   * @param supergraph the supergraph in which the embedding occurs
   * @param edge the edge of the <b>super</b>graph that is part of the embedding 
   */
  public EdgeOnlyEmbedding(Graph supergraph, int edge) {
    m_superGraph = supergraph;
    m_edgeMap = new int[] { -1, -1, -1, -1 };
    m_nodeCount = 2;
    m_edgeCount = 1;
  }

  /**
   * Creates a new EdgeOnlyEmbedding that is an extension of the given embedding by a new edge and/or a new node. Either the edge or the node can be
   * NO_EDGE or NO_NODE if only a new node or a new edge should be added.
   * @param embedding the embeddings that should be extended
   * @param newEdge the new edge in the <b>supergraph</b>
   */
  public EdgeOnlyEmbedding(EdgeOnlyEmbedding embedding, int newEdge) {   
  	m_edgeCount = embedding.m_edgeCount + 1;
    m_edgeMap =  new int[(m_edgeCount % 4 == 0) ? m_edgeCount : (m_edgeCount + 4) & ~3]; // take multiples of four to aid the object pool
    System.arraycopy(embedding.m_edgeMap, 0, m_edgeMap, 0, embedding.m_edgeCount);
    m_edgeMap[m_edgeCount - 1] = newEdge;
    
    if ((getSubgraphNode(embedding.getNodeA(newEdge)) == NO_NODE) || (getSubgraphNode(embedding.getNodeB(newEdge)) == NO_NODE)) {
    	m_nodeCount = embedding.m_nodeCount + 1;
    } else {
    	m_nodeCount = embedding.m_nodeCount;
    }
    
    m_superGraph = embedding.m_superGraph;    
  }

  /**
   * Creates a new CompleteEmbedding that is a copy of the given template embedding.
   * @param template the embedding to be copied
   */
  protected EdgeOnlyEmbedding(EdgeOnlyEmbedding template) {
    m_edgeMap = (int[]) template.m_edgeMap.clone();
    m_nodeCount = template.m_nodeCount;
    m_edgeCount = template.m_edgeCount;
    m_superGraph = template.m_superGraph;    
  }	
	
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphEmbedding#getSuperGraph()
	 */
	public Graph getSuperGraph() {
		return m_superGraph;
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphEmbedding#getSubGraph()
	 */
	public Graph getSubGraph() {
		return this;
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphEmbedding#isDirectedGraphEmbedding()
	 */
	public boolean isDirectedGraphEmbedding() {
		return (m_superGraph instanceof DirectedGraph);
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getNodeCount()
	 */
	public int getNodeCount() {
		return m_nodeCount;
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getEdgeCount()
	 */
	public int getEdgeCount() {
		return m_edgeCount;
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getID()
	 */
	public String getName() {
		return Integer.toString(hashCode());
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#copy()
	 */
	public Object clone() {
		return new EdgeOnlyEmbedding(this);
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getEdge(int, int)
	 */
	public int getEdge(int nodeA, int nodeB) {
		final int supergraphNodeA = supergraphNode(nodeA);
		final int supergraphNodeB = supergraphNode(nodeB);

		for (int i = 0; i < m_edgeCount; i++) {
			final int nA = m_superGraph.getNodeA(m_edgeMap[i]);
			final int nB = m_superGraph.getNodeB(m_edgeMap[i]);
			
			if (((nA == supergraphNodeA) && (nB == supergraphNodeB)) ||
					((nA == supergraphNodeB) && (nB == supergraphNodeA)))
			{
				return i;
			}
		}
		
		return NO_EDGE;
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getEdge(int)
	 */
	public int getEdge(int index) {
		return index;
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getNode(int)
	 */
	public int getNode(int index) {
		// TODO Auto-generated method stub
		return 0;
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getNodeLabel(int)
	 */
	public int getNodeLabel(int node) {
		return m_superGraph.getNodeLabel(supergraphNode(node));
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getEdgeLabel(int)
	 */
	public int getEdgeLabel(int edge) {
		return m_superGraph.getEdgeLabel(m_edgeMap[edge]);
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getDegree(int)
	 */
	public int getDegree(int node) {
  	int degree = 0;
  	final int supergraphNode = supergraphNode(node);
  	
  	for (int i = m_superGraph.getDegree(supergraphNode) - 1; i >= 0; i--) {
  		int edge = m_superGraph.getNodeEdge(supergraphNode, i);
  		for (int k = 0; k < m_edgeCount; k++) {
  			if (m_edgeMap[k] == edge) {
  				degree++;
  				break;
  			}
  		}
  	}
    return degree;
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getNodeEdge(int, int)
	 */
	public int getNodeEdge(int node, int number) {
		final int supergraphNode = supergraphNode(node);
		int deg = m_superGraph.getDegree(supergraphNode);		
		
		for (int k = 0; k < m_edgeCount; k++) {
			for (int i = deg - 1; i >= 0; i--) {			
				int edge = m_superGraph.getNodeEdge(supergraphNode, i);						
				if ((m_edgeMap[k] == edge) && (number-- == 0)) {
					return k;  				
				}
			}
		}
				
    throw new NoSuchElementException("No such edge");
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getNodeIndex(int)
	 */
	public int getNodeIndex(int node) {
		// TODO Auto-generated method stub
		return 0;
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getEdgeIndex(int)
	 */
	public int getEdgeIndex(int edge) {
		return edge;
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getNodeA(int)
	 */
	public int getNodeA(int edge) {
		int supergraphNodeA = m_superGraph.getNodeA(m_edgeMap[edge]);
		
		for (int i = 0; i < m_edgeCount; i++) {
			if (supergraphNode(i << 1) == supergraphNodeA) {
				return i << 1;
			} else if (supergraphNode((i << 1) + 1) == supergraphNodeA) {
				return (i << 1) + 1;
			}
		}
		
		throw new NoSuchElementException("No such node");
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getNodeB(int)
	 */
	public int getNodeB(int edge) {
		int supergraphNodeB = m_superGraph.getNodeB(m_edgeMap[edge]);
		
		for (int i = 0; i < m_edgeCount; i++) {
			if (supergraphNode(i << 1) == supergraphNodeB) {
				return i << 1;
			} else if (supergraphNode((i << 1) + 1) == supergraphNodeB) {
				return (i << 1) + 1;
			}
		}
		
		throw new NoSuchElementException("No such node");
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getOtherNode(int, int)
	 */
	public int getOtherNode(int edge, int node) {
		final int supergraphNode = supergraphNode(node);
		
		if (m_superGraph.getNodeA(m_edgeMap[edge]) == supergraphNode) {
			return getNodeB(edge);
		} else {
//			assert(m_superGraph.getNodeB(m_edgeMap[edge]) == supergraphNode);
			return getNodeA(edge);
		}
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.DirectedGraph#getInDegree(int)
	 */
	public int getInDegree(int node) {
		final int supergraphNode = supergraphNode(node);
	  	int degree = 0;
	  	for (int i = ((DirectedGraph) m_superGraph).getInDegree(supergraphNode) - 1; i >= 0; i--) {
	  		int edge = ((DirectedGraph) m_superGraph).getIncomingNodeEdge(supergraphNode, i);
	  		for (int k = 0; k < m_edgeCount; k++) {
	  			if (m_edgeMap[k] == edge) {
	  				degree++;
	  				break;
	  			}
	  		}
	  	}
    return degree;

	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.DirectedGraph#getOutDegree(int)
	 */
	public int getOutDegree(int node) {
		final int supergraphNode = supergraphNode(node);
	  	int degree = 0;
	  	for (int i = ((DirectedGraph) m_superGraph).getOutDegree(supergraphNode) - 1; i >= 0; i--) {
	  		int edge = ((DirectedGraph) m_superGraph).getOutgoingNodeEdge(supergraphNode, i);
	  		for (int k = 0; k < m_edgeCount; k++) {
	  			if (m_edgeMap[k] == edge) {
	  				degree++;
	  				break;
	  			}
	  		}
	  	}
    return degree;

	}
	

	/* (non-Javadoc)
	 * @see de.parmol.graph.DirectedGraph#getIncomingNodeEdge(int, int)
	 */
	public int getIncomingNodeEdge(int node, int number) {
		final int supergraphNode = supergraphNode(node);
		
		int deg = ((DirectedGraph) m_superGraph).getInDegree(supergraphNode);		
		
		for (int k = 0; k < m_edgeCount; k++) {
			for (int i = deg - 1; i >= 0; i--) {			
				int edge = ((DirectedGraph) m_superGraph).getIncomingNodeEdge(supergraphNode, i);						
				if ((m_edgeMap[k] == edge) && (number-- == 0)) {
					return k;  				
				}
			}
		}
				
    throw new NoSuchElementException("No such edge");
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.DirectedGraph#getOutgoingNodeEdge(int, int)
	 */
	public int getOutgoingNodeEdge(int node, int number) {
		final int supergraphNode = supergraphNode(node);
		
		int deg = ((DirectedGraph) m_superGraph).getOutDegree(supergraphNode);		
		
		for (int k = 0; k < m_edgeCount; k++) {
			for (int i = deg - 1; i >= 0; i--) {			
				int edge = ((DirectedGraph) m_superGraph).getOutgoingNodeEdge(supergraphNode, i);						
				if ((m_edgeMap[k] == edge) && (number-- == 0)) {
					return k;  				
				}
			}
		}
				
    throw new NoSuchElementException("No such edge");
	}

	
	/* (non-Javadoc)
	 * @see de.parmol.graph.DirectedGraph#getEdgeDirection(int, int)
	 */
	public int getEdgeDirection(int edge, int node) {
		if ((edge >= m_edgeCount) || (node >= m_nodeCount)) return NO_EDGE;
		
		return ((DirectedGraph) m_superGraph).getEdgeDirection(m_edgeMap[edge], supergraphNode(node));
	}

	
	/**
	 * Returns the node in the <b>sub</b>graph that corresponds the the given node in the <b>super</b>graph.
	 * @param supergraphNode a node in the supergraph
	 * @return a node in the subgraph or Graph.NO_NODE if no mapping exists 
	 */
	public int getSubgraphNode(int supergraphNode) {
		for (int i = 0; i < m_edgeCount; i++) {
			if (supergraphNode(i << 1) == supergraphNode) {
				return i << 1;
			} else if (supergraphNode((i << 1) + 1) == supergraphNode) {
				return (i << 1) + 1;
			}
		}
		
		throw new NoSuchElementException("No such node");
  }
  
	/**
	 * Returns the edge in the <b>sub</b>graph that corresponds the the given edge in the <b>super</b>graph.
	 * @param supergraphEdge an edge in the supergraph
	 * @return an edge in the subgraph or Graph.NO_EDGE if no mapping exists 
	 */
  public int getSubgraphEdge(int supergraphEdge) {
  	int edge = Graph.NO_EDGE;
  	for (int i = 0; i < m_edgeCount; i++) {
  		if (m_edgeMap[i] == supergraphEdge) {
  			edge = i; break;
  		}
  	}
  	return edge;  	
  }  
  
  
	/**
	 * Returns the node in the <b>super</b>graph that corresponds the the given node in the <b>sub</b>graph.
	 * @param subgraphNode a node in the subgraph
	 * @return a node in the supergraph 
	 */  
	public int getSupergraphNode(int subgraphNode) {
		return supergraphNode(subgraphNode);
	}

	/**
	 * Returns the edge in the <b>super</b>graph that corresponds the the given edge in the <b>sub</b>graph.
	 * @param subgraphEdge an edge in the subgraph
	 * @return an edge in the supergraph 
	 */  
	public int getSupergraphEdge(int subgraphEdge) {
		return m_edgeMap[subgraphEdge];
	}
	
	 /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
	public int hashCode() {
		return SimpleGraphComparator.getHashCode(this);
	}
	
	private final int supergraphNode(int node) {
  	if (node % 2 == 0) {
  		return m_superGraph.getNodeA(m_edgeMap[node >> 1]);
  	} else {
  		return m_superGraph.getNodeB(m_edgeMap[node >> 1]);
  	}		
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#isBridge(int)
	 */
	public boolean isBridge(int edge) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#setNodeObject(int, java.lang.Object)
	 */
	public void setNodeObject(int node, Object o) {
		throw new UnsupportedOperationException("An embedding must not set the node object of the underlying graph");		
	}
	

	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getNodeObject(int)
	 */
	public Object getNodeObject(int node) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	

	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#setEdgeObject(int, java.lang.Object)
	 */
	public void setEdgeObject(int edge, Object o) {
		throw new UnsupportedOperationException("An embedding must not set the edge object of the underlying graph");		
	}
	

	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getEdgeObject(int)
	 */
	public Object getEdgeObject(int edge) {
		return m_superGraph.getEdgeObject(m_edgeMap[edge]);
	}	
	
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#saveMemory()
	 */
	public void saveMemory() { /* nothing do here */ }

	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getID()
	 */
	public int getID() {
		// this should in almost all cases create a unique id although System.identityHashCode does not
		return System.identityHashCode(this) ^ m_superGraph.getID() ^ System.identityHashCode(m_edgeMap);
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphEmbedding#overlaps(de.parmol.graph.GraphEmbedding)
	 */
	public boolean overlaps(GraphEmbedding other) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
