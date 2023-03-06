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

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import de.parmol.graph.SimpleEdgeComparator;
import de.parmol.graph.SimpleGraphComparator;
import de.parmol.graph.SimpleNodeComparator;
import de.parmol.graph.Graph;

/**
 * A default implementation of the GraphSet interface 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class DefaultGraphSet implements GraphSet {
	protected Collection[] m_map;
	protected final Comparator m_comparator;
	protected int m_entries;
	
	/**
	 * Creates a new DefaultGraphSet. The elements inside this set are compared using the given comparator.
	 * @param graphComparator a comparator for UndirectedGraphs
	 */
	public DefaultGraphSet(Comparator graphComparator) {
		this(Math.PRIMES[16], graphComparator);
	}

	/**
	 * Creates a new DefaultGraphSet. The elements inside this set are compared using a SimpleGraphComparator.
	 */	
	public DefaultGraphSet() {
		this(new SimpleGraphComparator(SimpleNodeComparator.instance, SimpleEdgeComparator.instance));
	}

	/**
	 * Creates a new DefaultGraphSet. The elements inside this set are compared using the given comparator. The size of the set
	 * is equal to the given value.
	 * @param size the size of the map
	 * @param graphComparator a comparator for UndirectedGraphs 
	 */
	public DefaultGraphSet(int size, Comparator graphComparator) {
		size = Arrays.binarySearch(Math.PRIMES, size);
		if (size < 0) size = -size;
		
		m_map = new Collection[size];
		for (int i = 0; i < m_map.length; i++) {
			m_map[i] = new LinkedList();
		}
		m_comparator = graphComparator;
	}

	/**
	 * Creates a new DefaultGraphSet. The elements inside this set are compared using a SimpleGraphComparator. The size of the set
	 * is equal to the given value.
	 * @param size the size of the map
	 */	
	public DefaultGraphSet(int size) {
		this(size, new SimpleGraphComparator(SimpleNodeComparator.instance, SimpleEdgeComparator.instance));
	}

	/*
	 * (non-Javadoc)
	 * @see de.parmol.util.GraphSet#add(de.parmol.graph.UndirectedGraph)
	 */
	public boolean add(Graph graph) {
		int hashCode = graph.hashCode();
				
		for (Iterator it = m_map[java.lang.Math.abs(hashCode % m_map.length)].iterator(); it.hasNext();) {
			Graph temp = (Graph) it.next();
			
			if (temp.hashCode() == hashCode) {
				if (m_comparator.compare(graph, temp) == 0) return false;
			}
		}
		m_map[java.lang.Math.abs(hashCode % m_map.length)].add(graph);
		m_entries++;
		return true;
	} 
	
	/*
	 * (non-Javadoc)
	 * @see de.parmol.util.GraphSet#iterator()
	 */
	public Iterator iterator() {
		return new Iterator() {
			private int m_bin = 0;
			private Iterator m_binIterator = m_map[0].iterator();
			private int m_count;
			
			public void remove() { throw new UnsupportedOperationException(); }

			public boolean hasNext() {
				return (m_count < m_entries);
			}

			public Object next() {
				if (m_binIterator.hasNext()) {
					m_count++;
					return m_binIterator.next();
				}
				while (++m_bin < m_map.length) {
					if (m_map[m_bin].size() > 0) {
						m_binIterator = m_map[m_bin].iterator();
						m_count++;
						return m_binIterator.next();
					}
				}
				throw new NoSuchElementException("No more elements");
			}
		};
	}
	
	/*
	 *  (non-Javadoc)
	 * @see de.parmol.util.GraphSet#size()
	 */
	public int size() {	return m_entries;	}

	/* (non-Javadoc)
	 * @see de.parmol.util.GraphSet#add(de.parmol.util.GraphSet)
	 */
	public void add(GraphSet set) {
		for (Iterator it = set.iterator(); it.hasNext();) {
			Graph g = (Graph) it.next();
			add(g);
		}
	}
}

