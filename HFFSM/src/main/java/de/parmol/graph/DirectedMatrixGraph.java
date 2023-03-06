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

import java.util.NoSuchElementException;

import de.parmol.util.FullIntMatrix;



/**
 * This class represents directed graphs that use an adjacency matrix.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class DirectedMatrixGraph extends MatrixGraph implements DirectedGraph {

	/**
	 * Creates a new DirectedMatrixGraph with no nodes an edges.
	 */
	public DirectedMatrixGraph() {
		super(new FullIntMatrix(DEFAULT_SIZE, NO_EDGE));
	}


	/**
	 * Creates a new DirectedMatrixGraph with no nodes an edges and the given id.
	 * 
	 * @param id the id of the graph
	 */
	public DirectedMatrixGraph(String id) {
		super(id, new FullIntMatrix(DEFAULT_SIZE, NO_EDGE));
	}


	/**
	 * Creates a new DirectedMatrixGraph that is a copy of the given template graph.
	 * 
	 * @param template the graph that should be copied
	 */
	public DirectedMatrixGraph(DirectedMatrixGraph template) {
		super(template, new FullIntMatrix(DEFAULT_SIZE, NO_EDGE));
	}

	/**
	 * Creates a new DirectedMatrixGraph that is a copy of the given template graph.
	 * 
	 * @param template the graph that should be copied
	 */
	public DirectedMatrixGraph(DirectedGraph template) {
		super(template, new FullIntMatrix(DEFAULT_SIZE, NO_EDGE));
	}

	/**
	 * This factory creates new DirectedMatrixGraphs.
	 * 
	 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
	 */
	public static class Factory extends GraphFactory {
		/**
		 * The single instance of this factory.
		 */
		public final static Factory instance = new Factory();
		private Factory() { super(DIRECTED_GRAPH | MATRIX_GRAPH); }
		
		/* (non-Javadoc)
		 * @see de.parmol.graph.GraphFactory#createGraph()
		 */
		public MutableGraph createGraph() { return new DirectedMatrixGraph();	}

		/* (non-Javadoc)
		 * @see de.parmol.graph.GraphFactory#createGraph(java.lang.String)
		 */
		public MutableGraph createGraph(String id) {
			if (id != null) return new DirectedMatrixGraph(id);
			return new DirectedMatrixGraph();
		}		
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#copy()
	 */
	public Object clone() {
		return new DirectedMatrixGraph(this);
	}


	/* (non-Javadoc)
	 * @see de.parmol.graph.DirectedGraph#getInDegree(int)
	 */
	public int getInDegree(int node) {
		int count = 0;
		
		for (int i = 0; i < m_nodeCount; i++) {
			if ((i != node) && (m_matrix.getValue(i, node) != NO_EDGE)) count++;
		}
		
		return count;
	}

	
	/* (non-Javadoc)
	 * @see de.parmol.graph.DirectedGraph#getInDegree(int)
	 */
	public int getOutDegree(int node) {
		int count = 0;
		
		for (int i = 0; i < m_nodeCount; i++) {
			if ((i != node) && (m_matrix.getValue(node, i) != NO_EDGE)) count++;
		}
		
		return count;
	}

	
	/* (non-Javadoc)
	 * @see de.parmol.graph.DirectedGraph#getIncomingNodeEdge(int, int)
	 */
	public int getIncomingNodeEdge(int node, int number) {
		for (int i = 0; i < m_nodeCount; i++) {
			if ((i != node) && (m_matrix.getValue(i, node) != NO_EDGE)) {
				if (number-- == 0) return ((node & 0xffff) << 16) | (i & 0xffff);
			}			
		}
		
		throw new NoSuchElementException("No more edges");
	}

	
	/* (non-Javadoc)
	 * @see de.parmol.graph.DirectedGraph#getIncomingNodeEdge(int, int)
	 */
	public int getOutgoingNodeEdge(int node, int number) {
		for (int i = 0; i < m_nodeCount; i++) {
			if ((i != node) && (m_matrix.getValue(node, i) != NO_EDGE)) {
				if (number-- == 0) return ((node & 0xffff) << 16) | (i & 0xffff);
			}			
		}
		
		throw new NoSuchElementException("No more edges");
	}

	
	/* (non-Javadoc)
	 * @see de.parmol.graph.DirectedGraph#getEdgeDirection(int, int)
	 */
	public int getEdgeDirection(int edge, int node) {
		int row = edge & 0xffff;
		int col = edge >> 16;
				
		if (row == node) return OUTGOING_EDGE;
		if (col == node) return INCOMING_EDGE;
		return NO_EDGE;
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#getEdge(int)
	 */
	public int getEdge(int number) {
		for (int row = 0; row < m_nodeCount; row++) {
			for (int col = 0; col < m_nodeCount; col++) {
				if ((m_matrix.getValue(row, col) != NO_EDGE) && (number-- == 0)) {
					return ((col & 0xffff) << 16 | (row & 0xffff));
				}
			}		
		}
		throw new NoSuchElementException("No more edges");
	}
}
