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
 * This class represents directed graphs that use adjacency lists.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *
 */
public class DirectedListGraph extends ListGraph implements DirectedGraph {
  /* an array holding an array for each node; each of the sub-arrays has the in-degree of the node at index 0 and the
   * the attached incoming egdes at the indices 1...getInDegree(node)
   */	
	protected int[][] m_incomingEdges;
	
	/**
	 * Creates a new DirectedListGraph with no nodes and edges. 
	 */
	public DirectedListGraph() {
		super();
		m_incomingEdges = new int[DEFAULT_SIZE][];
	}
	
  /**
   * Constructs a new DirectedListGraph with no nodes and edges and the given id.
   * 
   * @param id the id of this graph
   */	
	public DirectedListGraph(String id) {
		super(id);
		m_incomingEdges = new int[DEFAULT_SIZE][];
	}
	
	/**
	 * Creates a new DirectedListGraph that is a copy of the given template graph.
	 * @param template the graph that shsould be copied 
	 */
	public DirectedListGraph(DirectedListGraph template) {
		super(template);
		
    m_incomingEdges = new int[template.m_incomingEdges.length][];
    for (int i = m_nodeCount - 1; i >= 0; i--) {
      m_incomingEdges[i] = new int[template.m_incomingEdges[i].length];
      System.arraycopy(template.m_incomingEdges[i], 0, m_incomingEdges[i], 0, m_incomingEdges[i].length);
    }		
	}
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#saveMemory()
	 */
	public void saveMemory() {
		super.saveMemory();
		
		if (m_nodeCount < m_incomingEdges.length) {
			int[][] temp = new int[m_nodeCount][];
			System.arraycopy(m_incomingEdges, 0, temp, 0, m_nodeCount);
			m_incomingEdges = temp;
		}

		for (int i = 0; i < m_incomingEdges.length; i++) {
			if (m_incomingEdges[i].length > m_incomingEdges[i][DEGREE] + 1) {
				int[] temp = new int[m_incomingEdges[i][DEGREE] + 1];
				System.arraycopy(m_incomingEdges[i], 0, temp, 0, m_incomingEdges[i][DEGREE] + 1);
				m_incomingEdges[i] = temp;
			}
		}

	}
	
	
	/**
	 * Creates a new DirectedListGraph that is a copy of the given template graph.
	 * @param template the graph that should be copied 
	 */
	public DirectedListGraph(DirectedGraph template) {
    super();

    m_nodeCount = template.getNodeCount();
    m_edgeCount = template.getEdgeCount();

    m_nodes = new int[m_nodeCount][];
    m_incomingEdges = new int[m_nodeCount][];
    m_nodeLabels = new int[m_nodeCount];
    m_edgeLabels = new int[m_edgeCount];
    m_edgeNeighbours = new int[2 * m_edgeCount];

    for (int i = m_nodeCount - 1; i >= 0; i--) {
      int index = template.getNodeIndex(template.getNode(i));

      m_nodes[index] = new int[template.getDegree(template.getNode(i)) + 1];
      m_incomingEdges[index] = new int[template.getInDegree(template.getNode(i)) + 1];
      m_nodeLabels[index] = template.getNodeLabel(template.getNode(i));
    }

    for (int i = m_edgeCount - 1; i >= 0; i--) {
      m_edgeLabels[i] = template.getEdgeLabel(template.getEdge(i));
      m_edgeNeighbours[2 * i + NEIGHBOUR_A] = template.getNodeA(template.getEdge(i));
      m_edgeNeighbours[2 * i + NEIGHBOUR_B] = template.getNodeB(template.getEdge(i));

      int deg = ++m_nodes[m_edgeNeighbours[2 * i + NEIGHBOUR_A]][DEGREE];
      m_nodes[m_edgeNeighbours[2 * i + NEIGHBOUR_A]][deg] = i;

      deg = ++m_incomingEdges[m_edgeNeighbours[2 * i + NEIGHBOUR_B]][DEGREE];
      m_incomingEdges[m_edgeNeighbours[2 * i + NEIGHBOUR_B]][deg] = i;    
    }		
    
    boolean nonNullObject = false;
    for (int i = m_nodeCount - 1; i >= 0; i--) {
    	if (template.getNodeObject(template.getNode(i)) != null) {
    		nonNullObject = true;
    		break;
    	}
    }
    
    if (nonNullObject) {
    	m_nodeObjects = new Object[m_nodeCount];
      for (int i = m_nodeCount - 1; i >= 0; i--) {
      	m_nodeObjects[i] = template.getNodeObject(template.getNode(i));
      }    	
    }
    
    nonNullObject = false;
    for (int i = m_edgeCount - 1; i >= 0; i--) {
    	if (template.getEdgeObject(template.getEdge(i)) != null) {
    		nonNullObject = true;
    		break;
    	}
    }
    
    if (nonNullObject) {
    	m_edgeObjects = new Object[m_edgeCount];
      for (int i = m_edgeCount - 1; i >= 0; i--) {
      	m_edgeObjects[i] = template.getEdgeObject(template.getEdge(i));
      }    	
    }        
	}
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.MutableGraph#addNode(int)
	 */
	public int addNode(int nodeLabel) {
		final int node = super.addNode(nodeLabel); 
		m_incomingEdges[m_nodeCount - 1] = new int[5];
		return node; 
	}
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.ListGraph#resizeGraph(int, int)
	 */
	protected void resizeGraph(int newNodeCount, int newEdgeCount) {
		super.resizeGraph(newNodeCount, newEdgeCount);
		
    if (newNodeCount >= m_nodes.length) {
      int[][] temp = new int[newNodeCount][];
      System.arraycopy(m_incomingEdges, 0, temp, 0, m_incomingEdges.length);
      m_incomingEdges = temp;
    }
	}
	
		
  /*
   * (non-Javadoc)
   * 
   * @see de.parmol.graph.Graph#addEdge(int, int, int)
   */
  public int addEdge(int nodeA, int nodeB, int edgeLabel) {
    if (m_edgeCount >= m_edgeLabels.length) {
    	resizeGraph(-1, m_edgeCount + 1 + m_edgeCount / 4);
    }
    m_edgeLabels[m_edgeCount] = edgeLabel;
    m_edgeNeighbours[2 * m_edgeCount + NEIGHBOUR_A] = nodeA;
    m_edgeNeighbours[2 * m_edgeCount + NEIGHBOUR_B] = nodeB;

    if (m_nodes[nodeA][DEGREE] >= (m_nodes[nodeA].length - 1)) {
      int[] temp = new int[m_nodes[nodeA].length + 1 + m_nodes[nodeA].length / 4];
      System.arraycopy(m_nodes[nodeA], 0, temp, 0, m_nodes[nodeA].length);
      m_nodes[nodeA] = temp;
    }
    m_nodes[nodeA][DEGREE]++;
    m_nodes[nodeA][m_nodes[nodeA][DEGREE]] = m_edgeCount;

    
    if (m_incomingEdges[nodeB][DEGREE] >= (m_incomingEdges[nodeB].length - 1)) {
      int[] temp = new int[m_incomingEdges[nodeB].length + 1 + m_incomingEdges[nodeB].length / 4];
      System.arraycopy(m_incomingEdges[nodeB], 0, temp, 0, m_incomingEdges[nodeB].length);
      m_incomingEdges[nodeB] = temp;
    }
    m_incomingEdges[nodeB][DEGREE]++;
    m_incomingEdges[nodeB][m_incomingEdges[nodeB][DEGREE]] = m_edgeCount;
    
    
    m_hashCode = 0;
    m_bridges = null;
    return m_edgeCount++;
  }	
  
  /*
   *  (non-Javadoc)
   * @see de.parmol.graph.MutableGraph#removeEdge(int)
   */
	public void removeEdge(int edge) {
		super.removeEdge(edge);
		
		// update the edge indices in the node map
		for (int i = 0; i < m_nodeCount; i++) {
			for (int k = 1; k <= m_incomingEdges[i][DEGREE]; k++) {
				if (m_incomingEdges[i][k] > edge) {
					m_incomingEdges[i][k]--; // decrease the edge index for all greater edges
				} else if (m_incomingEdges[i][k] == edge) {
					for (int m = k + 1; m < m_incomingEdges[i].length; m++) {
						m_incomingEdges[i][m - 1] = m_incomingEdges[i][m];
					}
					if (m_incomingEdges[i][k] > edge) {
						m_incomingEdges[i][k]--;
					}
					m_incomingEdges[i][DEGREE]--;
				}				
			}
		}
	}
	
	
	
	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.MutableGraph#removeNode(int)
	 */
	public void removeNode(int node) {
		super.removeNode(node);
		
		while (m_incomingEdges[node][DEGREE] > 0) {
			int edge = m_incomingEdges[node][1];
			removeEdge(edge);
			
			// super.removeNode() decreases m_nodeCount
			// so we need to update the last element of m_incomingEdges here
			for (int k = 1; k <= m_incomingEdges[m_nodeCount][DEGREE]; k++) {
				if (m_incomingEdges[m_nodeCount][k] > edge) {
					m_incomingEdges[m_nodeCount][k]--; // decrease the edge index for all greater edges
				} else if (m_incomingEdges[m_nodeCount][k] == edge) {
					for (int m = k + 1; m < m_incomingEdges[m_nodeCount].length; m++) {
						m_incomingEdges[m_nodeCount][m - 1] = m_incomingEdges[m_nodeCount][m];
					}
					if (m_incomingEdges[m_nodeCount][k] > edge) {
						m_incomingEdges[m_nodeCount][k]--;
					}
					m_incomingEdges[m_nodeCount][DEGREE]--;
				}				
			}
		}
		
		// update the node-edge map
		for (int i = node + 1; i < m_incomingEdges.length; i++) {
			m_incomingEdges[i - 1] = m_incomingEdges[i];
		}
	}
  /**
   * This factory creates new DirectedListGraphs.
   * 
   * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
   */
  public static class Factory extends GraphFactory {
  	/**
  	 * The single instance of this factory.
  	 */
		public final static Factory instance = new Factory();
		private Factory() { super(DIRECTED_GRAPH | LIST_GRAPH); } 
		
		/* (non-Javadoc)
		 * @see de.parmol.graph.GraphFactory#createGraph()
		 */
		public MutableGraph createGraph() { return new DirectedListGraph();	}

		/* (non-Javadoc)
		 * @see de.parmol.graph.GraphFactory#createGraph(java.lang.String)
		 */
		public MutableGraph createGraph(String id) {
			if (id != null) return new DirectedListGraph(id);
			return new DirectedListGraph();
		}		
	}
  
  /*
   * (non-Javadoc)
   * 
   * @see de.parmol.graph.Graph#getEdge(int, int)
   */
  public int getEdge(int nodeA, int nodeB) {
    for (int i = m_nodes[nodeA][DEGREE]; i >= 1; i--) {
      if (m_edgeNeighbours[2 * m_nodes[nodeA][i] + NEIGHBOUR_B] == nodeB) {
        return m_nodes[nodeA][i];
      }
    }

    return NO_EDGE;
  }

	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#copy()
	 */
	public Object clone() {
		return new DirectedListGraph(this);
	}
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getDegree(int)
	 */
	public int getDegree(int node) { return getInDegree(node)+getOutDegree(node); }

	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getNodeEdge(int, int)
	 */
	public int getNodeEdge(int node, int number){
		int in=getInDegree(node);
		return number>=in?getOutgoingNodeEdge(node,number-in):getIncomingNodeEdge(node,number);
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.DirectedGraph#getInDegree(int)
	 */
	public int getInDegree(int node) {		
		return m_incomingEdges[node][DEGREE];
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.DirectedGraph#getIncomingNodeEdge(int, int)
	 */
	public int getIncomingNodeEdge(int node, int number) {
		return m_incomingEdges[node][number + 1];
	}
	
	public int[] getIncomingNodeEdgeSet(int node) {
		return m_incomingEdges[node];
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.DirectedGraph#getOutDegree(int)
	 */
	public int getOutDegree(int node) {	return super.getDegree(node); }

	/* (non-Javadoc)
	 * @see de.parmol.graph.DirectedGraph#getOutgoingNodeEdge(int, int)
	 */
	public int getOutgoingNodeEdge(int node, int number) { return super.getNodeEdge(node,number); }

	/* (non-Javadoc)
	 * @see de.parmol.graph.DirectedGraph#getEdgeDirection(int, int)
	 */
	public int getEdgeDirection(int edge, int node) {
		for (int i = m_nodes[node][DEGREE]; i > 0; i--) {
			if (m_nodes[node][i] == edge) {
				return OUTGOING_EDGE;
			}
		}
		
		for (int i = m_incomingEdges[node][DEGREE]; i > 0; i--) {
			if (m_incomingEdges[node][i] == edge) {
				return INCOMING_EDGE;
			}
		}
		
		return NO_EDGE;
	}

	/**
	 * Checks the internal structure of the graph.
	 * @return <code>true</code> if ok, <code>false</code> if graph is inconsistent.
	 */
    public boolean sanityCheck() {
//	System.out.println("m_edgeNeighbors: " + java.util.Arrays.toString(m_edgeNeighbours));
//	System.out.println("m_edgeCount: " + m_edgeCount);
//	System.out.println("m_nodeCount: " + m_nodeCount);
//	System.out.println("m_nodes: " + java.util.Arrays.deepToString(m_nodes));
//	System.out.println("m_incomingEdges: " + java.util.Arrays.deepToString(m_incomingEdges));
//	System.out.println("m_nodeLabels: " + java.util.Arrays.toString(m_nodeLabels));
//	System.out.println("m_nodeObjects: " + java.util.Arrays.toString(m_nodeObjects));
		
	for (int node = m_nodeCount; --node >= 0;) {
	    for (int i = m_nodes[node][DEGREE]; i > 0; i--) {
		int edge = m_nodes[node][i];

		if (m_edgeNeighbours[2 * edge + NEIGHBOUR_A] != node) {
		    System.out.println("In DirectedListGraph.sanityCheck()/A");
		    System.out.println("node " + node + " != " + m_edgeNeighbours[2 * edge + NEIGHBOUR_A]);

		    return false;
		}
	    }

	    for (int i = m_incomingEdges[node][DEGREE]; i > 0; i--) {
		int edge = m_incomingEdges[node][i];

		if (m_edgeNeighbours[2 * edge + NEIGHBOUR_B] != node) {
		    System.out.println("In DirectedListGraph.sanityCheck()/B");
		    System.out.println("node " + node + " != " + m_edgeNeighbours[2 * edge + NEIGHBOUR_B]);

		    return false;
		}
	    }
	}

	for (int edge = m_edgeCount; --edge >= 0;) {
	    int nodeA = m_edgeNeighbours[2 * edge + NEIGHBOUR_A];
	    int nodeB = m_edgeNeighbours[2 * edge + NEIGHBOUR_B];
			
	    boolean found = false;
			
	    for (int i = m_nodes[nodeA][DEGREE]; i > 0; i--) {
		if (m_nodes[nodeA][i] == edge) {
		    found = true;
		    break;
		}
	    }
			
	    if (!found) {
		System.out.println("In DirectedListGraph.sanityCheck()/C");
		System.out.println("edge: " + edge + " not found in nodeA: " + nodeA);
		
		return false;
	    }
			
	    found = false;
			
	    for (int i = m_incomingEdges[nodeB][DEGREE]; i > 0; i--) {
		if (m_incomingEdges[nodeB][i] == edge) {
		    found = true;
		    break;
		}
	    }
			
	    if (!found) {
		System.out.println("In DirectedListGraph.sanityCheck()/D");
		System.out.println("edge: " + edge + " not found in nodeB: " + nodeB);

		return false;
	    }
	}
		
	return true;
    }
}
