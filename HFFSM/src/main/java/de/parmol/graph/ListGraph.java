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

import java.util.BitSet;


/**
 * This class implements the Graph interface by using lists.
 * 
 * Every node and edge in this graph has a unique index. This index can be used
 * to access helper arrays in functions that operate on the nodes/edges of this
 * graph. This has the advantage over using markers on the nodes and edges, that
 * these helper arrays are local and thus concurrent threads need not be
 * synchronized. The disadvantage is the small overhead due to the indexed
 * access in the helper arrays. But this should be way faster than a
 * synchronizing accesses. The index is guaranteed to be in the range from 0 to
 * get[Edge|Node]Count().
 * 
 * If subclasses wish to work with specialized nodes or edges (e.g. ones holding
 * cycle information), they need to subclass ListNode/ListEdge and override the
 * newListEdge/newListNode methods. Every method in this class that creates new
 * nodes/edges calls them and thus subclasses can create their nodes/edge
 * classes instead.
 * 
 * @author Thorsten Meinl <Thorsten@meinl.bnv-bamberg.de>
 *  
 */
public abstract class ListGraph implements Graph, MutableGraph {
  /* the unique id of this graph */
  protected final String m_name;

  protected final int m_id = ++s_id;
  
  /* the number of nodes and edges in this graph */
  protected int m_nodeCount, m_edgeCount;

  /* a counter for creating the unique id */
  private static int s_id = 0;

  /* an array holding an array for each node; each of the sub-arrays has the degree of the node at index 0 and the
   * the attached egdes at the indices 1...getDegree(node)
   */
  protected int[][] m_nodes;

  protected int[] m_nodeLabels, m_edgeLabels;

  /* an array holding at index 2*edge + NEIGHBOUR_A the index of the first node attached to edge and at
   * 2*edge + NEIGHBOUR_B the second node
   */
  protected int[] m_edgeNeighbours;

  protected final static int NEIGHBOUR_A = 0, NEIGHBOUR_B = 1, DEGREE = 0, DEFAULT_SIZE = 8;

  protected int m_hashCode = 0;
  
  protected BitSet m_bridges;
  protected Object[] m_nodeObjects, m_edgeObjects;
  
  
  /**
   * Constructs a new ListGraph with no nodes and edges and a unique id
   */
  public ListGraph() {
    this(Integer.toString(s_id));
  }

  /**
   * Cosntructs a new ListGraph with no nodes and edges and the given id
   * 
   * @param name the name of this graph
   */
  public ListGraph(String name) {
    m_name = name;

    m_nodes = new int[DEFAULT_SIZE][];
    m_nodeLabels = new int[DEFAULT_SIZE];
    m_edgeLabels = new int[DEFAULT_SIZE];
    m_edgeNeighbours = new int[2* DEFAULT_SIZE];
  }

  /**
   * Creates a copy of the given graph
   * 
   * @param template the graph that should be copied
   */
  public ListGraph(ListGraph template) {
    m_name = Long.toString(s_id++);

    m_nodeCount = template.m_nodeCount;
    m_edgeCount = template.m_edgeCount;

    m_nodes = new int[template.m_nodes.length][];
    for (int i = m_nodeCount - 1; i >= 0; i--) {
      m_nodes[i] = new int[template.m_nodes[i].length];
      System.arraycopy(template.m_nodes[i], 0, m_nodes[i], 0, m_nodes[i].length);
    }

    m_nodeLabels = new int[template.m_nodeLabels.length];
    System.arraycopy(template.m_nodeLabels, 0, m_nodeLabels, 0, m_nodeLabels.length);

    m_edgeLabels = new int[template.m_edgeLabels.length];
    System.arraycopy(template.m_edgeLabels, 0, m_edgeLabels, 0, m_edgeLabels.length);

    m_edgeNeighbours = new int[template.m_edgeNeighbours.length];
    System.arraycopy(template.m_edgeNeighbours, 0, m_edgeNeighbours, 0, m_edgeNeighbours.length);
    
    if (template.m_nodeObjects != null) {
    	m_nodeObjects = new Object[m_nodes.length];
    	System.arraycopy(template.m_nodeObjects, 0, m_nodeObjects, 0, m_nodeCount);
    }

    if (template.m_edgeObjects != null) {
    	m_edgeObjects = new Object[m_edgeCount];
    	System.arraycopy(template.m_edgeObjects, 0, m_edgeObjects, 0, m_edgeCount);
    }
  }



  /*
   *  (non-Javadoc)
   * @see de.parmol.graph.Graph#getNodeCount()
   */
  public int getNodeCount() { return m_nodeCount; }

  /*
   *  (non-Javadoc)
   * @see de.parmol.graph.Graph#getEdgeCount()
   */
  public int getEdgeCount() { return m_edgeCount; }

  /*
   *  (non-Javadoc)
   * @see de.parmol.graph.Graph#getID()
   */
  public String getName() { return m_name; }

  /*
   *  (non-Javadoc)
   * @see de.parmol.graph.MutableGraph#addNode(int)
   */
  public int addNode(int nodeLabel) {
    if (m_nodeCount >= m_nodes.length) {
    	resizeGraph(m_nodeCount + 1 + m_nodeCount / 4, -1);
    }

    m_nodes[m_nodeCount] = new int[5];
    m_nodeLabels[m_nodeCount] = nodeLabel;

    m_hashCode = 0;
    return m_nodeCount++;
  }

  /*
   *  (non-Javadoc)
   * @see de.parmol.graph.MutableGraph#addNodeAndEdge(int, int, int)
   */
  public int addNodeAndEdge(int nodeA, int nodeLabel, int edgeLabel) {
    int node = addNode(nodeLabel);
    addEdge(nodeA, node, edgeLabel);
    return node;
  }



  /*
   *  (non-Javadoc)
   * @see de.parmol.graph.Graph#getNodeLabel(int)
   */
  public int getNodeLabel(int node) { return m_nodeLabels[node]; }

  /*
   *  (non-Javadoc)
   * @see de.parmol.graph.Graph#getEdgeLabel(int)
   */
  public int getEdgeLabel(int edge) { return m_edgeLabels[edge]; }
  
  /*
   *  (non-Javadoc)
   * @see de.parmol.graph.Graph#getNodeEdge(int, int)
   */
  public int getNodeEdge(int node, int number) { return m_nodes[node][number + 1]; }
  
  /*
   *  (non-Javadoc)
   * @see de.parmol.graph.Graph#getDegree(int)
   */
  public int getDegree(int node) { return m_nodes[node][DEGREE]; }

  /*
   *  (non-Javadoc)
   * @see de.parmol.graph.Graph#getNodeIndex(int)
   */
  public int getNodeIndex(int node) { return node; }

  /*
   *  (non-Javadoc)
   * @see de.parmol.graph.Graph#getEdgeIndex(int)
   */
  public int getEdgeIndex(int edge) { return edge; }

  /*
   *  (non-Javadoc)
   * @see de.parmol.graph.Graph#getNodeA(int)
   */
  public int getNodeA(int edge) { return m_edgeNeighbours[2 * edge + NEIGHBOUR_A]; }

  /*
   *  (non-Javadoc)
   * @see de.parmol.graph.Graph#getNodeB(int)
   */
  public int getNodeB(int edge) { return m_edgeNeighbours[2 * edge + NEIGHBOUR_B]; }

  /*
   *  (non-Javadoc)
   * @see de.parmol.graph.Graph#getOtherNode(int, int)
   */
  public int getOtherNode(int edge, int node) {
    if (m_edgeNeighbours[2 * edge + NEIGHBOUR_A] == node)
      return m_edgeNeighbours[2 * edge + NEIGHBOUR_B];
    return m_edgeNeighbours[2 * edge + NEIGHBOUR_A];
  }

	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getEdge(int)
	 */
  public int getEdge(int number) { return number; }

	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getNode(int)
	 */
  public int getNode(int number) { return number;	}
	
	/**
	 * This method is called whenever the graph must be resized.
	 * @param newNodeCount the new number of nodes, or -1 if the number of nodes should not be changed
	 * @param newEdgeCount the new number of edge, or -1 if the number of edges should not be changed
	 */
	protected void resizeGraph(int newNodeCount, int newEdgeCount) {
    if (newNodeCount > m_nodes.length) {
      int[][] temp = new int[newNodeCount][];
      System.arraycopy(m_nodes, 0, temp, 0, m_nodes.length);
      m_nodes = temp;

      int[] temp2 = new int[newNodeCount];
      System.arraycopy(m_nodeLabels, 0, temp2, 0, m_nodeCount);
      m_nodeLabels = temp2;
    }

    if (newEdgeCount > m_edgeLabels.length) {
      int[] temp = new int[newEdgeCount];
      System.arraycopy(m_edgeLabels, 0, temp, 0, m_edgeLabels.length);
      m_edgeLabels = temp;

      temp = new int[2 * newEdgeCount];
      System.arraycopy(m_edgeNeighbours, 0, temp, 0, m_edgeNeighbours.length);
      m_edgeNeighbours = temp;
    }    
    
    if ((m_nodeObjects != null) && (newNodeCount > m_nodeObjects.length)) {
    	Object[] temp = new Object[newNodeCount];
    	System.arraycopy(m_nodeObjects, 0, temp, 0, m_nodeObjects.length);
    	m_nodeObjects = temp;
    }
    
    if ((m_edgeObjects != null) && (newEdgeCount > m_edgeObjects.length)) {
    	Object[] temp = new Object[newEdgeCount];
    	System.arraycopy(m_edgeObjects, 0, temp, 0, m_edgeObjects.length);
    	m_edgeObjects = temp;
    }
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		if (m_hashCode == 0) m_hashCode = SimpleGraphComparator.getHashCode(this);
		if (m_hashCode == 0) m_hashCode = 1;
		return m_hashCode;
	}

	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.MutableGraph#removeNode(int)
	 */
	public void removeNode(int node) {
		if (node >= m_nodeCount) return;
		
		// update the node label array
		for (int i = node + 1; i < m_nodeCount; i++) {
			m_nodeLabels[i - 1] = m_nodeLabels[i];
		}
		
		// delete all edges attached to the node
		while (m_nodes[node][DEGREE] > 0) {
			removeEdge(m_nodes[node][1]);
		}
		
		// update the node-edge map
		for (int i = node + 1; i < m_nodes.length; i++) {
			m_nodes[i - 1] = m_nodes[i];
		}		
		
		// update the edge-node map
		for (int i = 0; i < 2*m_edgeCount; i++) {
//			assert(m_edgeNeighbours[i] != node);
			if (m_edgeNeighbours[i] > node) m_edgeNeighbours[i]--;
		}
		
		// update the node objects
		if (m_nodeObjects != null) {
			for (int i = node + 1; i < m_nodeCount; i++) {
				m_nodeObjects[i - 1] = m_nodeObjects[i];
			}
			m_nodeObjects[m_nodeCount - 1] = null;
		}
		
		m_nodeCount--;
		m_hashCode = 0;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.MutableGraph#removeEdge(int)
	 */
	public void removeEdge(int edge) {
		if (edge >= m_edgeCount) return;
		
		// update the edge indices in the node map
		for (int i = 0; i < m_nodeCount; i++) {
			for (int k = 1; k <= m_nodes[i][DEGREE]; k++) {
				if (m_nodes[i][k] > edge) {
					m_nodes[i][k]--; // decrease the edge index for all greater edges
				} else if (m_nodes[i][k] == edge) {
					for (int m = k + 1; m < m_nodes[i].length; m++) {
						m_nodes[i][m - 1] = m_nodes[i][m];
					}
					if (m_nodes[i][k] > edge) {
						m_nodes[i][k]--;
					}
					m_nodes[i][DEGREE]--;
				}				
			}
		}
		
		// update the edge labels
		for (int i = edge + 1; i < m_edgeCount; i++) {
			m_edgeLabels[i - 1] = m_edgeLabels[i];
		}
		
		
		// update the edge neighbour list
		for (int i = 2*edge + 2; i < 2*m_edgeCount; i++) {
			m_edgeNeighbours[i - 2] = m_edgeNeighbours[i];
		}

		// update the edge objects
		if (m_edgeObjects != null) {
			for (int i = edge + 1; i < m_edgeCount; i++) {
				m_edgeObjects[i - 1] = m_edgeObjects[i];
			}
			m_edgeObjects[m_edgeCount - 1] = null;
		}

		
		m_edgeCount--;
		m_hashCode = 0;
		m_bridges = null;
	}	
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#isBridge(int)
	 */
	public boolean isBridge(int edge) {
		if (m_bridges == null) {
			int[] bridges = Util.getBridges(this);
			m_bridges = new BitSet(((m_edgeCount / 64) + 1) * 64);
			
			for (int i = 0; i < bridges.length; i++) {
				m_bridges.set(getEdgeIndex(bridges[i]));
			}
		}
		
		return m_bridges.get(getEdgeIndex(edge));
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getEdgeObject(int)
	 */
	public Object getEdgeObject(int edge) {
		return (m_edgeObjects != null) ? m_edgeObjects[edge] : null;
	}
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getNodeObject(int)
	 */
	public Object getNodeObject(int node) {
		return (m_nodeObjects != null) ? m_nodeObjects[node] : null;
	}
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#setEdgeObject(int, java.lang.Object)
	 */
	public void setEdgeObject(int edge, Object o) {
		if (m_edgeObjects == null) {
			m_edgeObjects = new Object[m_edgeLabels.length];
		}

		m_edgeObjects[edge] = o;
	}
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#setNodeObject(int, java.lang.Object)
	 */
	public void setNodeObject(int node, Object o) {
		if (m_nodeObjects == null) {
			m_nodeObjects = new Object[m_nodes.length];
		}

		m_nodeObjects[node] = o;
	}
	
	public abstract Object clone();
	
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.MutableGraph#setEdgeLabel(int, int)
	 */
	public void setEdgeLabel(int edge, int newLabel) {
		m_edgeLabels[edge] = newLabel;
	}
	
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.MutableGraph#setNodeLabel(int, int)
	 */
	public void setNodeLabel(int node, int newLabel) {
		m_nodeLabels[node] = newLabel;
	}
	
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#saveMemory()
	 */
	public void saveMemory() {
		if (m_edgeCount < m_edgeLabels.length) {
			int[] temp = new int[m_edgeCount];
			System.arraycopy(m_edgeLabels, 0, temp, 0, m_edgeCount);
			m_edgeLabels = temp;
		}

		if (2*m_edgeCount < m_edgeNeighbours.length) {
			int[] temp = new int[2*m_edgeCount];
			System.arraycopy(m_edgeNeighbours, 0, temp, 0, 2*m_edgeCount);
			m_edgeNeighbours = temp;
		}
		
		if ((m_edgeObjects != null) && (m_edgeCount < m_edgeObjects.length)) {
			Object[] temp = new Object[m_edgeCount];
			System.arraycopy(m_edgeObjects, 0, temp, 0, m_edgeCount);
			m_edgeObjects = temp;			
		}
		
		
		if (m_nodeCount < m_nodeLabels.length) {
			int[] temp = new int[m_nodeCount];
			System.arraycopy(m_nodeLabels, 0, temp, 0, m_nodeCount);
			m_nodeLabels = temp;
		}

		if ((m_nodeObjects != null) && (m_nodeCount < m_nodeObjects.length)) {
			Object[] temp = new Object[m_nodeCount];
			System.arraycopy(m_nodeObjects, 0, temp, 0, m_nodeCount);
			m_nodeObjects = temp;			
		}

		if (m_nodeCount < m_nodes.length) {
			int[][] temp = new int[m_nodeCount][];
			System.arraycopy(m_nodes, 0, temp, 0, m_nodeCount);
			m_nodes = temp;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getID()
	 */
	public int getID() { return m_id; }
}