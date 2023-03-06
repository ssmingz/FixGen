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
 * This class is the lowest in the hierachie of embeddings, it simply stores the
 * graph in which the subgraph occurs.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *  
 */
public class IDEmbedding implements GraphEmbedding {
  protected Graph m_superGraph, m_subGraph;
  
  /**
   * Creates a new GraphEmbedding that only stores the id of the graph in which
   * a subgraph occurs
   * 
   * @param supergraph the graph in which a subgraph occurs
   * @param subgraph the subgraph represented by this embedding
   */
  public IDEmbedding(Graph supergraph, Graph subgraph) {
    m_superGraph = supergraph;
    m_subGraph = subgraph;
  }

  protected IDEmbedding(IDEmbedding template) {
  	m_superGraph = template.m_superGraph;
  	m_subGraph = template.m_subGraph;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.parmol.graph.GraphEmbedding#getGraph()
   */
  public Graph getSuperGraph() { return m_superGraph; }

  /* (non-Javadoc)
   * @see de.parmol.graph.GraphEmbedding#getSubGraph()
   */
  public Graph getSubGraph() { return m_subGraph; }

	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphEmbedding#isDirectedGraphEmbedding()
	 */
	public final boolean isDirectedGraphEmbedding() {
		return (m_superGraph instanceof DirectedGraph);
	}
	
	public Object clone() {
		return new IDEmbedding(this);
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphEmbedding#getSupergraphNode(int)
	 */
	public int getSupergraphNode(int subgraphNode) { return Graph.NO_NODE; }

	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphEmbedding#getSupergraphEdge(int)
	 */
	public int getSupergraphEdge(int subgraphEdge) { return Graph.NO_EDGE; }

	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphEmbedding#getSubgraphEdge(int)
	 */
	public int getSubgraphEdge(int supergraphEdge) { return Graph.NO_EDGE; }
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphEmbedding#getSubgraphNode(int)
	 */
	public int getSubgraphNode(int supergraphNode) { return Graph.NO_NODE; }

	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphEmbedding#overlaps(de.parmol.graph.GraphEmbedding)
	 */
	public boolean overlaps(GraphEmbedding other) {
		throw new UnsupportedOperationException("Not applicable");
	}
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphEmbedding#getNodeObject(int)
	 */
	public Object getNodeObject(int subgraphNode) { return null; }

	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphEmbedding#getEdgeObject(int)
	 */
	public Object getEdgeObject(int subgraphEdge) { return null; }
}