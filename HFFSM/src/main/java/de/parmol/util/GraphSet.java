/*
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
 */
package de.parmol.util;

import java.util.Iterator;

import de.parmol.graph.Graph;

/**
 * This interface specifies the functionality of a set of graphs
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public interface GraphSet {
	/**
	 * Adds a new graph to this set if there is no isomorpic graph inside yet.
	 * @param graph the new graph to be added
	 * @return <code>true</code> if this set did not already contain the specified graph
	 */
	public boolean add(Graph graph);

	/**
	 * Returns an iterator over all graphs in this set.
	 * @return an Iterator
	 */
	public Iterator iterator();

	/**
	 * Returns the number of graphs in this set
	 * @return the number of graphs
	 */
	public int size();
	
	/**
	 * Adds all graphs in the given set to this set
	 * @param set a GraphSet
	 */
	public void add(GraphSet set);
}