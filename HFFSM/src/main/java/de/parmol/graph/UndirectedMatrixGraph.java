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

import de.parmol.util.HalfIntMatrix;


/**
 * This class represents undirected graphs that use an adjacency matrix.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *
 */
public class UndirectedMatrixGraph extends MatrixGraph implements UndirectedGraph {
	/**
	 * Creates a new UndirectedMatrixGraph with no nodes and edges and a random id.
	 */
	public UndirectedMatrixGraph() {
		super(new HalfIntMatrix(DEFAULT_SIZE, NO_EDGE));
	}


	/**
	 * Creates a new UndirectedMatrixGraph with no nodes and edges but with the given id.
	 * @param id the id of the new graph
	 */
	public UndirectedMatrixGraph(String id) {
		super(id, new HalfIntMatrix(DEFAULT_SIZE, NO_EDGE));
	}


	/**
	 * Creates a new UndirectedMatrixGraph that is a copy of the given template graph.
	 * @param template the graph that should be copied
	 */
	public UndirectedMatrixGraph(UndirectedMatrixGraph template) {
		super(template, new HalfIntMatrix(DEFAULT_SIZE, NO_EDGE));
	}

	/**
	 * Creates a new UndirectedMatrixGraph that is a copy of the given template graph.
	 * @param template the graph that should be copied
	 */
	public UndirectedMatrixGraph(UndirectedGraph template) {
		super(template, new HalfIntMatrix(DEFAULT_SIZE, NO_EDGE));
	}

	/**
	 * This class is a factory for UndirectedMatrixGraphs
	 * 
	 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
	 */
	public static class Factory extends GraphFactory {
		/**
		 * The single instance of thsi factory.
		 */
		public final static Factory instance = new Factory();
		protected Factory() { super(UNDIRECTED_GRAPH | MATRIX_GRAPH); }
		
		/* (non-Javadoc)
		 * @see de.parmol.graph.GraphFactory#createGraph()
		 */
		public MutableGraph createGraph() { return new UndirectedMatrixGraph();	}

		/* (non-Javadoc)
		 * @see de.parmol.graph.GraphFactory#createGraph(java.lang.String)
		 */
		public MutableGraph createGraph(String id) {
			if (id != null) return new UndirectedMatrixGraph(id);
			return new UndirectedMatrixGraph();
		}		
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.Graph#copy()
	 */
	public Object clone() {
		return new UndirectedMatrixGraph(this);
	}
}
