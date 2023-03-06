/*
 * Created on Dec 13, 2004
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
import java.util.Iterator;

import de.parmol.graph.NodeLabelDegreeComparator;
import de.parmol.graph.SimpleEdgeComparator;
import de.parmol.graph.SimpleGraphComparator;
import de.parmol.graph.SimpleNodeComparator;

/**
 * This class stores frequent fragments in a set-wise manner, i.e. a FrequentFragment object is only inserted of the frequent fragment it
 * represents is not already contained in the set.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class FilteredFragmentSet extends FragmentSet {
	protected final SimpleGraphComparator m_comparator;
	private final int m_averageBinSize;
	private int m_duplicateCounter = 0;
	
	
	/**
	 * Creates a new FilteredFragmentSet. The elements inside this set are compared using the given comparator.
	 * @param graphComparator a comparator for UndirectedGraphs
	 */
	public FilteredFragmentSet(SimpleGraphComparator graphComparator) {
		this(Math.PRIMES[16], graphComparator, 25);
	}

	/**
	 * Creates a new filtered fragment set that is a copy of the given filtered fragment set.
	 * @param template the filtered fragment set that should be copied
	 */
	public FilteredFragmentSet(FilteredFragmentSet template) {
		super(template);
		m_comparator = template.m_comparator;
		this.m_averageBinSize = template.m_averageBinSize;
	}
	
	/**
	 * Creates a new FilteredFragmentSet. The elements inside this set are compared using a SimpleGraphComparator.
	 */	
	public FilteredFragmentSet() {
		this(new SimpleGraphComparator(SimpleNodeComparator.instance, SimpleEdgeComparator.instance));
	}

	/**
	 * Creates a new FilteresFragmentSet. The elements inside this set are compared using the given comparator. The size of the set
	 * is equal to the given value.
	 * @param initialSize the size of the map
	 * @param graphComparator a comparator for UndirectedGraphs 
	 * @param averageBinSize the average number of entries in each bin
	 */
	public FilteredFragmentSet(int initialSize, SimpleGraphComparator graphComparator, int averageBinSize) {
		int bins = initialSize / averageBinSize;
		
		int index = Arrays.binarySearch(Math.PRIMES, bins);
		if (index < 0) index = -index;
		if (index >= Math.PRIMES.length) index = Math.PRIMES.length - 1;
		
		m_map = new ArrayList[Math.PRIMES[index]];
		for (int i = 0; i < m_map.length; i++) {
			m_map[i] = new ArrayList((int) (averageBinSize * 1.25));
		}
		
		m_comparator = graphComparator;
		m_averageBinSize = averageBinSize;
	}

	/**
	 * Creates a new FilteredFragmentSet. The elements inside this set are compared using a SimpleGraphComparator. The size of the set
	 * is equal to the given value.
	 * @param initialSize the size of the map
	 */	
	public FilteredFragmentSet(int initialSize) {
		this(initialSize, 25);
	}

	/**
	 * Creates a new FilteredFragmentSet. The elements inside this set are compared using a SimpleGraphComparator. The size of the set
	 * is equal to the given value.
	 * @param initialSize the size of the map
	 * @param averageBinSize the average number of entries in each bin
	 */	
	public FilteredFragmentSet(int initialSize, int averageBinSize) {
		this(initialSize, new SimpleGraphComparator(NodeLabelDegreeComparator.instance, SimpleEdgeComparator.instance), averageBinSize);
	}
	
	/**
	 * Adds a new FrequentFragment to this set if there is no isomorpic fragment inside yet.
	 * @param fragment the new fragment to be added
	 * @return <code>true</code> if this set did not already contain the specified fragment
	 */
	public boolean add(FrequentFragment fragment) {
		if (m_entries > m_averageBinSize * m_map.length) {
			resize((int) java.lang.Math.ceil(m_map.length * 1.23578));
		}
		
		final int hashCode = fragment.getFragment().hashCode();
		final int bin = java.lang.Math.abs((hashCode ^ (hashCode >> 24) ^ (hashCode >> 15) ^ (hashCode >> 9)) % m_map.length);
				
		for (Iterator it = m_map[bin].iterator(); it.hasNext();) {
			FrequentFragment temp = (FrequentFragment) it.next();
			
			if (temp.getFragment().hashCode() == hashCode) {				
				if (m_comparator.compare(fragment.getFragment(), fragment.getNodePartitions(), temp.getFragment(), temp.getNodePartitions()) == 0) {									
//					Graph g = temp.getFragment();
//					int lastEdge = g.getEdge(g.getEdgeCount() - 1);
//					System.out.println(g.getNodeA(lastEdge) + " - " + g.getNodeB(lastEdge) + " => " + SLNParser.instance.serialize(temp.getFragment()));
//					g = fragment.getFragment();
//					lastEdge = g.getEdge(g.getEdgeCount() - 1);
//					System.out.println(g.getNodeA(lastEdge) + " - " + g.getNodeB(lastEdge) + " => " + SLNParser.instance.serialize(fragment.getFragment()));
//					System.out.println();
					
					m_duplicateCounter++;
					return false;
				}
			}
		}
		m_map[bin].add(fragment);
		m_entries++;
		return true;
	} 
	
	
	/*
	 *  (non-Javadoc)
	 * @see de.parmol.util.FragmentSet#remove(de.parmol.util.FrequentFragment, int)
	 */
	protected void remove(FrequentFragment fragment, int bin) {
		final int hashCode = fragment.getFragment().hashCode();
		bin = java.lang.Math.abs((hashCode ^ (hashCode >> 24) ^ (hashCode >> 15) ^ (hashCode >> 9)) % m_map.length);
		super.remove(fragment, bin);	
		
		if (m_entries < 0.1 * m_averageBinSize * m_map.length) {
			resize((int) (0.65321 * m_map.length));
		}
	}
	
	
	protected void resize(int newSize) {
		if (newSize <= 0) newSize = 1;
		ArrayList[] temp = new ArrayList[newSize];
		for (int i = 0; i < temp.length; i++) {
			temp[i] = new ArrayList((int) (m_averageBinSize * 1.25));
		}
		
		for (int i = 0; i < m_map.length; i++) {
			for (Iterator it = m_map[i].iterator(); it.hasNext();) {
				FrequentFragment f = (FrequentFragment) it.next();
				final int hashCode = f.getFragment().hashCode();
				final int bin = java.lang.Math.abs((hashCode ^ (hashCode >> 24) ^ (hashCode >> 15) ^ (hashCode >> 9)) % temp.length);
				
				temp[bin].add(f);
			}
		}
		
		// printStats();
		m_map = temp;
	}
	
	/**
	 * Returns the comparator that this fragment set uses.
	 * @return a graph comparator
	 */
	public SimpleGraphComparator getComparator() { return m_comparator; }
	
	/**
	 * Adds the given fragment to the set without checking for duplicates.
	 * @param fragment a fragments
	 */
	public void addUnfiltered(FrequentFragment fragment) {
		if (m_entries > m_averageBinSize * m_map.length) {
			resize((int) java.lang.Math.ceil(m_map.length * 1.23578));
		}
		
		final int hashCode = fragment.getFragment().hashCode();
		final int bin = java.lang.Math.abs((hashCode ^ (hashCode >> 24) ^ (hashCode >> 15) ^ (hashCode >> 9)) % m_map.length);
				
		m_map[bin].add(fragment);
		m_entries++;
	}
	
	/**
	 * Returns the number of duplicated framgments that have been filtered out.
	 * @return the number of duplicate fragments
	 */
	public int getDuplicateCounter() { return m_duplicateCounter; }
	
//	public void printStats() {
//		int min = Integer.MAX_VALUE, max = 0;
//		for (int i = 0; i < m_map.length; i++) {
//			min = java.lang.Math.min(min, m_map[i].size());
//			max = java.lang.Math.max(max, m_map[i].size());
//			System.out.println("Bin " + i + " has " + m_map[i].size() + " entries");
//		}
//		System.out.println("Minimum entries = " + min + ", maximum entries = " + max);
//	}
}
