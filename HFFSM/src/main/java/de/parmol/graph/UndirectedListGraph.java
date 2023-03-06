/*
 * Created on 12.12.2004
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
 * This class represents undirected graphs that use adjacency lists.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *
 */
public class UndirectedListGraph extends ListGraph implements UndirectedGraph {
	/**
	 * Creates a new UndirectedListGraph with no nodes and edges and a random id.
	 */
	public UndirectedListGraph() { super();	}


	/**
	 * Creates a new UndirectedListGraph that is a copy of the given template graph.
	 * @param template the graph that should be copied
	 */
  public UndirectedListGraph(UndirectedGraph template) {
    super();

    m_nodeCount = template.getNodeCount();
    m_edgeCount = template.getEdgeCount();

    m_nodes = new int[m_nodeCount][];
    m_nodeLabels = new int[m_nodeCount];
    m_edgeLabels = new int[m_edgeCount];
    m_edgeNeighbours = new int[2 * m_edgeCount];

    for (int i = m_nodeCount - 1; i >= 0; i--) {
      int index = template.getNodeIndex(template.getNode(i));

      m_nodes[index] = new int[template.getDegree(template.getNode(i)) + 1];
      // m_nodes[index][DEGREE] = template.getDegree(nodes[i]);
      m_nodeLabels[index] = template.getNodeLabel(template.getNode(i));
    }

    for (int i = m_edgeCount - 1; i >= 0; i--) {
      m_edgeLabels[i] = template.getEdgeLabel(template.getEdge(i));
      m_edgeNeighbours[2 * i + NEIGHBOUR_A] = template.getNodeA(template.getEdge(i));
      m_edgeNeighbours[2 * i + NEIGHBOUR_B] = template.getNodeB(template.getEdge(i));

      int deg = ++m_nodes[m_edgeNeighbours[2 * i + NEIGHBOUR_A]][DEGREE];
      m_nodes[m_edgeNeighbours[2 * i + NEIGHBOUR_A]][deg] = i;
      deg = ++m_nodes[m_edgeNeighbours[2 * i + NEIGHBOUR_B]][DEGREE];      
      m_nodes[m_edgeNeighbours[2 * i + NEIGHBOUR_B]][deg] = i;
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
	
  /**
   * Creates a new UndirectedListGraph with no nodes and edges but with the given id.
   * @param id the id of the new graph
   */
	public UndirectedListGraph(String id) { super(id); }

	/**
	 * Creates a new UndirectedListGraph that is a copy of the given template graph.
	 * @param template the graph that should be copied
	 */
	public UndirectedListGraph(UndirectedListGraph template) { super(template); }
	
  /*
   * (non-Javadoc)
   * 
   * @see de.parmol.graph.Graph#addEdge(int, int, int)
   */
  public int addEdge(int nodeA, int nodeB, int edgeLabel) {
  	if (getEdge(nodeA, nodeB) != Graph.NO_EDGE) {
  		System.err.println("Warning: trying to insert duplicate edge in " + m_name + " from " + getNodeLabel(nodeA) + "" +
  				" (" + nodeA + ") to " + getNodeLabel(nodeB) + " (" + nodeB + ") with label " + edgeLabel);
  		return getEdge(nodeA, nodeB);
  	}
  	
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

    if (m_nodes[nodeB][DEGREE] >= (m_nodes[nodeB].length - 1)) {
      int[] temp = new int[m_nodes[nodeB].length + 1 + m_nodes[nodeB].length / 4];
      System.arraycopy(m_nodes[nodeB], 0, temp, 0, m_nodes[nodeB].length);
      m_nodes[nodeB] = temp;
    }
    m_nodes[nodeB][DEGREE]++;
    m_nodes[nodeB][m_nodes[nodeB][DEGREE]] = m_edgeCount;

    m_hashCode = 0;
    m_bridges = null;
    return m_edgeCount++;
  }
  
	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#saveMemory()
	 */
	public void saveMemory() {
		super.saveMemory();
		
		for (int i = 0; i < m_nodes.length; i++) {
			if (m_nodes[i].length > m_nodes[i][DEGREE] + 1) {
				int[] temp = new int[m_nodes[i][DEGREE] + 1];
				System.arraycopy(m_nodes[i], 0, temp, 0, m_nodes[i][DEGREE] + 1);
				m_nodes[i] = temp;
			}
		}
	}
	
	
  /*
   * (non-Javadoc)
   * 
   * @see de.parmol.graph.Graph#getEdge(int, int)
   */
  public int getEdge(int nodeA, int nodeB) {
    for (int i = m_nodes[nodeA][DEGREE]; i >= 1; i--) {
    	final int edge = m_nodes[nodeA][i];
    	
    	if (((m_edgeNeighbours[2*edge + NEIGHBOUR_A] == nodeA) && (m_edgeNeighbours[2*edge + NEIGHBOUR_B] == nodeB))
    			|| (m_edgeNeighbours[2*edge + NEIGHBOUR_A] == nodeB) && (m_edgeNeighbours[2*edge + NEIGHBOUR_B] == nodeA))
			{
    		return m_nodes[nodeA][i];
    	}
    }

    return NO_EDGE;
  }	
  
  /**
   * This class is a factory for UndirectedListGraphs.
   * 
   * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
   */
  public static class Factory extends GraphFactory {
		/**
		 * The single instance of this factory.
		 */
  	public final static Factory instance = new Factory();
		protected Factory() { super(UNDIRECTED_GRAPH | LIST_GRAPH); }
		
		/* (non-Javadoc)
		 * @see de.parmol.graph.GraphFactory#createGraph()
		 */
		public MutableGraph createGraph() { return new UndirectedListGraph();	}

		/* (non-Javadoc)
		 * @see de.parmol.graph.GraphFactory#createGraph(java.lang.String)
		 */
		public MutableGraph createGraph(String id) {
			if (id != null) return new UndirectedListGraph(id);
			return new UndirectedListGraph();
		}		
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#copy()
	 */
	public Object clone() {
		return new UndirectedListGraph(this);
	}  
}
