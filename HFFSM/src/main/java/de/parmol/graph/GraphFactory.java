/*
 * Created on Aug 16, 2004
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

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * This class is the abstract base class for all kinds of factories that create new graphs.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *
 */
public abstract class GraphFactory {
	/**
	 * Typemask constant for factories that create undirected graphs.
	 */
	public final static int UNDIRECTED_GRAPH = 1;
	/**
	 * Typemask constant for factories that create directed graphs.
	 */
	public final static int DIRECTED_GRAPH = 2;
	/**
	 * Typemask constant for factories that create classified graphs.
	 */
	public final static int CLASSIFIED_GRAPH = 4;
	/**
	 * Typemask constant for factories that create ring graphs.
	 */
	public final static int RING_GRAPH = 8;
	/**
	 * Typemask constant for factories that create list graphs.
	 */
	public final static int LIST_GRAPH = 16;
	/**
	 * Typemask constant for factories that create matrix graphs.
	 */
	public final static int MATRIX_GRAPH = 32;
	
	
	private final static ArrayList FACTORIES = new ArrayList();
	
	/**
	 * Creates a new GraphFactor. Subclasses must use this constructor and pass which types of graphs they create.
	 * @param typemask the binary or of all graph types the factory can create 
	 */
	protected GraphFactory(int typemask) {
		FACTORIES.add(new Integer(typemask));
		FACTORIES.add(this);
	}
	
	/**
	 * Creates a new graph.
	 * @return a new graph
	 */
	public abstract MutableGraph createGraph();
	
	/**
	 * Creates a new graph with the given id.
	 * @param id an id for the graph
	 * @return a new graph
	 */
	public abstract MutableGraph createGraph(String id);
	
	/**
	 * Returns a factory that can create graphs that satisfy the given properties.
	 * @param typemask a mask of properties the graph created by the factory must have
	 * @return a factory for graphs
	 */
	public static GraphFactory getFactory(int typemask) {
		for (int i = 0; i < FACTORIES.size(); i += 2) {
			if ((((Integer) FACTORIES.get(i)).intValue() & typemask) == typemask) {
				return (GraphFactory) FACTORIES.get(i + 1);
			}
		}
		
		throw new NoSuchElementException("No matching factory found");
	}
}
