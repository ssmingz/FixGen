/*
 * Created on 03.10.2004
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
 */
package de.parmol.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import de.parmol.graph.SimpleEdgeComparator;
import de.parmol.graph.SimpleNodeComparator;
import de.parmol.graph.SimpleSubgraphComparator;

/**
 * This class stores frequent fragments. It is not guaranteed that a fragment is only once in this set (thus violating the name), this must be ensured by the application. But algorithms
 * like gSpan, FFSM and Gaston ensures this anyway.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class FragmentSet {
	protected ArrayList[] m_map;
	protected int m_entries;
	
	/* compares FrequentFragments by looking at the class frequenies */
	private final static Comparator FREQUENCY_COMPARATOR = new Comparator() {
		public int compare(Object o1, Object o2) {			
			final float[] cf1 = ((FrequentFragment) o1).getClassFrequencies();
			final float[] cf2 = ((FrequentFragment) o2).getClassFrequencies();
			
			for (int i = 0; i < cf1.length; i++) {
				if (cf1[i] < cf2[i]) return -1;
				if (cf1[i] > cf2[i]) return 1;
			}
			return 0;
		}		
	};
	
	/* compares FrequentFragments by looking at the class frequenies and the fragment size*/
	private final static Comparator FREQUENCY_AND_SIZE_COMPARATOR = new Comparator() {
		public int compare(Object o1, Object o2) {			
			final float[] cf1 = ((FrequentFragment) o1).getClassFrequencies();
			final float[] cf2 = ((FrequentFragment) o2).getClassFrequencies();
			
			for (int i = 0; i < cf1.length; i++) {
				if (cf1[i] < cf2[i]) return -1;
				if (cf1[i] > cf2[i]) return 1;
			}
			
			int diff = ((FrequentFragment) o1).getFragment().getEdgeCount() - ((FrequentFragment) o2).getFragment().getEdgeCount(); 
			if (diff == 0) {
				diff = ((FrequentFragment) o1).getFragment().getNodeCount() - ((FrequentFragment) o2).getFragment().getNodeCount();
			}
			return diff;
		}		
	};	
	
	/**
	 * Creates a new FragmentSet.
	 */	
	public FragmentSet() {
		this(Math.PRIMES[16]);
	}

	/**
	 * Creates a new fragment set that is a copy of the given set.
	 * @param template the fragment set that should be copied
	 */
	public FragmentSet(FragmentSet template) {
		m_map = new ArrayList[template.m_map.length];
		
		for (int i = 0; i < template.m_map.length; i++) {
			m_map[i] = (ArrayList) template.m_map[i].clone();
		}
		m_entries = template.m_entries;
	}
	
	/**
	 * Creates a new FragmentSet. The intial size of the set is equal to the given value.
	 * @param size the size of the map 
	 */
	public FragmentSet(int size) {
		m_map = new ArrayList[] { new ArrayList(size) };
	}


	/**
	 * Adds a new FrequentFragment to this set.
	 * @param fragment the new fragment to be added
	 * @return always <code>true</code>
	 */
	public boolean add(FrequentFragment fragment) {
		m_map[0].add(fragment);
		m_entries++;
		return true;
	} 
	
	/**
	 * Returns an iterator over all frequent fragments in this set.
	 * @return an Iterator
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
	
	/**
	 * Returns the number of frequent fragments in this set
	 * @return the number of frequent fragments
	 */
	public int size() {	return m_entries;	}

	/**
	 * Adds all frequent fragments in the given set to this set
	 * @param set a FragmentSet
	 */
	public void add(FragmentSet set) {
		for (Iterator it = set.iterator(); it.hasNext();) {
			FrequentFragment f = (FrequentFragment) it.next();
			add(f);
		}
	}
	
	/**
	 * Returns all frequent fragments in this set in an array
	 * @return an array of FrequentFragments
	 */
	public FrequentFragment[] toArray() {
		FrequentFragment[] retVal = new FrequentFragment[m_entries];
		
		int i = 0;
		for (int bin = 0; bin < m_map.length; bin++) {
			for (Iterator it = m_map[bin].iterator(); it.hasNext();) {
				retVal[i++] = (FrequentFragment) it.next();
			}
		}
		return retVal;
	}
	
	/**
	 * Removes the given fragment from the set. The comparison is done by only checking the pointer and not for isomorphism, etc.
	 * @param fragment the fragment that should be removed
	 * @param bin the bin in which the fragment resides
	 */
	protected void remove(FrequentFragment fragment, int bin) {
		for (Iterator it = m_map[bin].iterator(); it.hasNext();) {
			FrequentFragment temp = (FrequentFragment) it.next();
			
			if (temp == fragment) {
				it.remove();
				m_entries--;
				return;
			}
		}
		throw new IllegalArgumentException("The given object was not found");
	}
	
	/**
	 * Removes all fragments from this set that are not closed fragments. 
	 */
	public void filterClosedFragments() {
		FrequentFragment[] fragments = toArray();
		Arrays.sort(fragments, FREQUENCY_AND_SIZE_COMPARATOR);
		
		SimpleSubgraphComparator comp = new SimpleSubgraphComparator(SimpleNodeComparator.instance, SimpleEdgeComparator.instance);
		for (int i = fragments.length - 1; i >= 0; i--) {
			if (fragments[i] == null) continue;
			
			int k = i;
			while (--k >= 0) {
				if (fragments[k] == null) continue;
				if (FREQUENCY_COMPARATOR.compare(fragments[i], fragments[k]) != 0) break;				
				
				if (comp.compare(fragments[k].getFragment(), fragments[i].getFragment()) == 0) {
					remove(fragments[k], 0);
					fragments[k] = null;
				}
			}
		}
	}
	
	/**
	 * Removes all fragments from the set.
	 */
	public void clear() {
		for (int i = 0; i < m_map.length; i++) {
			m_map[i].clear();
		}
		m_entries = 0;
	}
}
