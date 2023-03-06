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
import java.util.NoSuchElementException;

import de.parmol.util.IntMatrix;

/**
 * This class implements the Graph interfaces by using a matrix. * Every node
 * and edge in this graph has a unique index. This index can be used to access
 * helper arrays in functions that operate on the nodes/edges of this graph.
 * This has the advantage over using markers on the nodes and edges, that these
 * helper arrays are local and thus concurrent threads need not be synchronized.
 * The disadvantage is the small overhead due to the indexed access in the
 * helper arrays. But this should be way faster than a synchronizing accesses.
 * The index is guaranteed to be in the range from 0 to get[Edge|Node]Count().
 * 
 * If subclasses wish to work with specialized nodes or edges (e.g. ones holding
 * cycle information), they need to subclass MatrixNode/MatrixEdge and override
 * the newMatrixEdge/newMatrixNode methods. Every method in this class that
 * creates new nodes/edges calls them and thus subclasses can create their
 * nodes/edge classes instead.
 * 
 * @author Thorsten Meinl <Thorsten@meinl.bnv-bamberg.de>
 *  
 */
public abstract class MatrixGraph implements Graph, MutableGraph {
  protected final IntMatrix m_matrix;
  
  protected int[] m_edgeLabels;

  /* the number of nodes and edges in this graph */
  protected int m_nodeCount, m_edgeCount;

  /* the unique id of this graph */
  protected final String m_name;
  
  protected final int m_id = ++s_id;

  /* a counter for creating the unique ids */
  private static int s_id = 0;

  protected final static int DEFAULT_SIZE = 8;
  
  protected int m_hashCode = 0;
  
  protected BitSet m_bridges;
  protected Object[] m_nodeObjects, m_edgeObjects;
  
  /**
   * Creates a new MatrixGraph with no nodes and edges.
   * @param matrix the matrix that should be used
   */
  protected MatrixGraph(IntMatrix matrix) {
    this(Integer.toString(s_id), matrix);
  }

  /**
   * Creates a new MatrixGraph with no nodes and edges that has the given id.
   * 
   * @param name a unique id
   * @param matrix the matrix that should be used
   */
  protected MatrixGraph(String name, IntMatrix matrix) {
  	m_matrix = matrix;    
    m_edgeLabels = new int[DEFAULT_SIZE];    
    m_name = name;
  }


  /**
   * Creates a new MatrixGraph that is a copy of the given template graph
   * except for the id.
   * 
   * @param template the graph that should be copied
   * @param matrix the matrix that should be used
   */
  protected MatrixGraph(MatrixGraph template, IntMatrix matrix) {
  	m_matrix = matrix;

    m_name = Integer.toString(s_id);
    
    m_edgeLabels = new int[template.m_edgeLabels.length];
    System.arraycopy(template.m_edgeLabels, 0, m_edgeLabels, 0, template.m_edgeLabels.length);
    
    m_nodeCount = template.m_nodeCount;
    m_edgeCount = template.m_edgeCount;
    
    if (template.m_nodeObjects != null) {
    	m_nodeObjects = new Object[m_nodeCount];
    	System.arraycopy(template.m_nodeObjects, 0, m_nodeObjects, 0, m_nodeCount);
    }

    if (template.m_edgeObjects != null) {
    	m_edgeObjects = new Object[m_edgeCount];
    	System.arraycopy(template.m_edgeObjects, 0, m_edgeObjects, 0, m_edgeCount);
    }    
  }

  
  /**
   * Creates a new MatrixGraph that is a copy of the given template graph
   * except for the id.
   * 
   * @param template the graph that should be copied
   * @param matrix the matrix that should be used
   */
  protected MatrixGraph(Graph template, IntMatrix matrix) {
  	m_nodeCount = template.getNodeCount();
  	m_edgeCount = template.getEdgeCount();
  	m_matrix = matrix;

    m_name = Integer.toString(s_id);
    
    for (int i = 0; i < m_nodeCount; i++) {
    	int node = template.getNode(i);
    	int nodeIndex = template.getNodeIndex(node);
    	m_matrix.setValue(nodeIndex, nodeIndex, template.getNodeLabel(node));
    }
        
    m_edgeLabels = new int[m_edgeCount]; 
    int count = 0;
    for (int i = 0; i < m_edgeCount; i++) {
    	int edge = template.getEdge(i);
    	int nodeIndexA = template.getNodeIndex(template.getNodeA(edge));
    	int nodeIndexB = template.getNodeIndex(template.getNodeB(edge));

    	m_matrix.setValue(nodeIndexA, nodeIndexB, count);
    	m_edgeLabels[count++] = template.getEdgeLabel(edge);    	
    }    
  }
  
  
	/* (non-Javadoc)
	 * @see de.parmol.graph.UndirectedGraph#getNodeCount()
	 */
	public int getNodeCount() { return m_nodeCount; }

	/* (non-Javadoc)
	 * @see de.parmol.graph.UndirectedGraph#getEdgeCount()
	 */
	public int getEdgeCount() { return m_edgeCount; } 

	/* (non-Javadoc)
	 * @see de.parmol.graph.UndirectedGraph#getID()
	 */
	public String getName() { return m_name; }

	/* (non-Javadoc)
	 * @see de.parmol.graph.UndirectedGraph#addNode(int)
	 */
	public int addNode(int nodeLabel) {
		if (m_nodeCount >= m_matrix.getSize()) {
			resizeGraph(m_nodeCount + 8, -1);
		}
				
		m_matrix.setValue(m_nodeCount, m_nodeCount, nodeLabel);
		m_hashCode = 0;
		return m_nodeCount++;
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.UndirectedGraph#addEdge(int, int, int)
	 */
	public int addEdge(int nodeA, int nodeB, int edgeLabel) {
		if (nodeA == nodeB) throw new IllegalArgumentException("The given nodes are the same. This graph does not support self loops.");
		
		if (m_edgeCount >= m_edgeLabels.length) {
			resizeGraph(-1, m_edgeCount + 8);
		}
		
		m_matrix.setValue(nodeA, nodeB, m_edgeCount);
				
		m_edgeLabels[m_edgeCount] = edgeLabel;
		m_hashCode = 0;
		m_bridges = null;
		return m_edgeCount++;
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.UndirectedGraph#addNodeAndEdge(int, int, int)
	 */
	public int addNodeAndEdge(int nodeA, int nodeLabel, int edgeLabel) {
		int newNode = addNode(nodeLabel);
		addEdge(nodeA, newNode, edgeLabel);		
		return newNode;
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.UndirectedGraph#getEdge(int, int)
	 */
	public int getEdge(int nodeA, int nodeB) {
		if (nodeA == nodeB) throw new IllegalArgumentException("The given nodes are the same. This graph does not support self loops.");
		
		if (m_matrix.getValue(nodeA, nodeB) != NO_EDGE) {
			return (nodeA & 0xffff) << 16 | (nodeB & 0xffff);	
		}
		
		return NO_EDGE;					
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.UndirectedGraph#getNodeLabel(int)
	 */
	public int getNodeLabel(int node) { return m_matrix.getValue(node, node); }

	/* (non-Javadoc)
	 * @see de.parmol.graph.UndirectedGraph#getEdgeLabel(int)
	 */
	public int getEdgeLabel(int edge) {
		int row = edge & 0xffff;
		int col = edge >> 16;
				
		return m_edgeLabels[m_matrix.getValue(row, col)];
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.UndirectedGraph#getDegree(int)
	 */
	public int getDegree(int node) {
		int count = 0;
		for (int i = 0; i < m_nodeCount; i++) {
			if ((i != node) && (m_matrix.getValue(node, i) != NO_EDGE)) count++;
		}
		return count;
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.UndirectedGraph#getNodeIndex(int)
	 */
	public int getNodeIndex(int node) { return node; }

	/* (non-Javadoc)
	 * @see de.parmol.graph.UndirectedGraph#getEdgeIndex(int)
	 */
	public int getEdgeIndex(int edge) {
		final int row = edge & 0xffff;
		final int col = edge >> 16;
		return m_matrix.getValue(row, col);
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.UndirectedGraph#getNodeA(int)
	 */
	public int getNodeA(int edge) { return (edge & 0xffff); }

	/* (non-Javadoc)
	 * @see de.parmol.graph.UndirectedGraph#getNodeB(int)
	 */
	public int getNodeB(int edge) { return (edge >> 16); }

	/* (non-Javadoc)
	 * @see de.parmol.graph.UndirectedGraph#getOtherNode(int, int)
	 */
	public int getOtherNode(int edge, int node) {
		if ((edge & 0xffff) == node) return edge >> 16;
		return edge & 0xffff;		
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.UndirectedGraph#getNodeEdge(int, int)
	 */
	public int getNodeEdge(int node, int number) {

		for (int i = 0; i < m_nodeCount; i++) {
			if ((i != node) && (m_matrix.getValue(node, i) != NO_EDGE)) {
				if (number-- == 0) return (i << 16) | (node & 0xffff);
			}			
		}
		
		throw new NoSuchElementException("No more edges");
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.UndirectedGraph#getEdge(int)
	 */
	public int getEdge(int number) {
		for (int row = 0; row < m_nodeCount; row++) {
			for (int col = 0; col < row; col++) {
				if ((m_matrix.getValue(row, col) != NO_EDGE) && (number-- == 0)) {
					return ((col & 0xffff) << 16 | (row & 0xffff));
				}
			}		
		}
		throw new NoSuchElementException("No more edges");
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.UndirectedGraph#getNode(int)
	 */
	public int getNode(int number) { return number;	}
	
	/**
	 * This method is called whenever the graph must be resized.
	 * @param newNodeCount the new number of nodes, or -1 if the number of nodes should not be changed
	 * @param newEdgeCount the new number of edge, or -1 if the number of edges should not be changed
	 */
	protected void resizeGraph(int newNodeCount, int newEdgeCount) {
		if (newNodeCount >= m_matrix.getSize()) {
			m_matrix.resize(newNodeCount);
			
			if (m_nodeObjects != null) {
				Object[] temp = new Object[newNodeCount];
				System.arraycopy(m_nodeObjects, 0, temp, 0, m_nodeObjects.length);
				m_nodeObjects = temp;
			}			
		}

		if (newEdgeCount >= m_edgeLabels.length) {
	    int[] temp = m_edgeLabels;
	    m_edgeLabels = new int[newEdgeCount];
	    System.arraycopy(temp, 0, m_edgeLabels, 0, temp.length); 

			if (m_edgeObjects != null) {
				Object[] temp2 = new Object[newEdgeCount];
				System.arraycopy(m_edgeObjects, 0, temp, 0, m_edgeObjects.length);
				m_edgeObjects = temp2;
			}					
		}		
	}
	
	public int hashCode() {
		if (m_hashCode == 0) m_hashCode = SimpleGraphComparator.getHashCode(this);
		if (m_hashCode == 0) m_hashCode = 1;
		return m_hashCode;
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.MutableGraph#removeNode(int)
	 */
	public void removeNode(int node) {
		if (node >= m_nodeCount) return;
		
		int degree = getDegree(node);
		m_matrix.deleteRowAndCol(node);
		
		// update the node objects
		if (m_nodeObjects != null) {
			for (int i = node + 1; i < m_nodeCount; i++) {
				m_nodeObjects[i - 1] = m_nodeObjects[i];
			}			
		}		
		
		m_nodeCount--;
		m_edgeCount -= degree;
		m_hashCode = 0;
		m_bridges = null;
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.MutableGraph#removeEdge(int)
	 */
	public void removeEdge(int edge) {
		if (edge >= m_edgeCount) return;
		
		for (int i = edge + 1; i < m_edgeCount; i++) {
			m_edgeLabels[i - 1] = m_edgeLabels[i];
		}

		
		// update the edge objects
		if (m_edgeObjects != null) {
			for (int i = edge + 1; i < m_edgeCount; i++) {
				m_edgeObjects[i - 1] = m_edgeObjects[i];
			}
		}
		
		m_matrix.setValue(edge & 0xffff, edge >> 16, NO_EDGE);
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
		return (m_edgeObjects != null) ? m_edgeObjects[getEdgeIndex(edge)] : null;
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

		m_edgeObjects[getEdgeIndex(edge)] = o;
	}
	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#setNodeObject(int, java.lang.Object)
	 */
	public void setNodeObject(int node, Object o) {
		if (m_nodeObjects == null) {
			m_nodeObjects = new Object[m_matrix.getSize()];
		}
		
		m_nodeObjects[node] = o;
	}
		
	/* (non-Javadoc)
	 * @see de.parmol.graph.MutableGraph#setEdgeLabel(int, int)
	 */
	public void setEdgeLabel(int edge, int newLabel) {
		final int row = edge & 0xffff;
		final int col = edge >> 16;
				
		m_edgeLabels[m_matrix.getValue(row, col)] = newLabel;
	}
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.MutableGraph#setNodeLabel(int, int)
	 */
	public void setNodeLabel(int node, int newLabel) { m_matrix.setValue(node, node, newLabel);	}
	
	public abstract Object clone();
	
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#saveMemory()
	 */
	public void saveMemory() {
		if ((m_edgeObjects != null) && (m_edgeCount < m_edgeObjects.length)) {
			Object[] temp = new Object[m_edgeCount];
			System.arraycopy(m_edgeObjects, 0, temp, 0, m_edgeCount);
			m_edgeObjects = temp;			
		}

		if ((m_nodeObjects != null) && (m_nodeCount < m_nodeObjects.length)) {
			Object[] temp = new Object[m_nodeCount];
			System.arraycopy(m_nodeObjects, 0, temp, 0, m_nodeCount);
			m_nodeObjects = temp;			
		}

		if (m_edgeCount < m_edgeLabels.length) {
			int[] temp = new int[m_edgeCount];
			System.arraycopy(m_edgeLabels, 0, temp, 0, m_edgeCount);
			m_edgeLabels = temp;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.Graph#getID()
	 */
	public int getID() { return m_id;	}
}
