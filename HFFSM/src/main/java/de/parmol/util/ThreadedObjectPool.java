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
 * 
 */
package de.parmol.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An implementation of ObjectPool that internally creates a pool for each thread that uses this pool. This avoids
 * synchronized blocks for using this object pool.
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class ThreadedObjectPool implements ObjectPool {
  private final int m_poolSize, m_poolCount;
  private final HashMap m_threads;

	
	private int[] m_hits, m_accesses;
	
	/**
	 * Creates a new pool which contains sub-pools and a separate pool for every thread. 
	 * @param poolSize the size of each single pool
	 * @param pools the number of sub-pools
	 * @param threads an array of <b>all</b> threads that will access this pool
	 */
	public ThreadedObjectPool(int poolSize, int pools, Thread[] threads) {
		m_poolSize = poolSize;
		m_poolCount = pools;		
		m_threads = new HashMap(threads.length * 2);
		
		for (int i = 0; i < threads.length; i++) {
			ArrayList[] temp = new ArrayList[pools];
			for (int k = 0; k < pools; k++) {
				temp[k] = new ArrayList(poolSize);
			}
			m_threads.put(threads[i], temp);
		}
		m_hits = new int[pools];
		m_accesses = new int[pools];
	}

	public Object getObject(int poolNumber) {		
		ArrayList[] pools = (ArrayList[]) m_threads.get(Thread.currentThread());
		
		m_accesses[poolNumber]++;
		if (pools[poolNumber].size() > 0) {
			m_hits[poolNumber]++;
			return pools[poolNumber].remove(pools[poolNumber].size() - 1);
		}
		return null;		
	}

	public Object getObject() {
		return getObject(0);
	}

	public void repoolObject(Object o, int poolNumber) {
		ArrayList[] pools = (ArrayList[]) m_threads.get(Thread.currentThread());
				
		if (pools[poolNumber].size() < m_poolSize) pools[poolNumber].add(o);
	}

	public void repoolObject(Object o) {
		repoolObject(o, 0);
	}
	
	public void clear(int poolNumber) {
		ArrayList[] pools = (ArrayList[]) m_threads.get(Thread.currentThread());		
		pools[poolNumber].clear();
	}

	public void clear(int fromPool, int toPool) {
		ArrayList[] pools = (ArrayList[]) m_threads.get(Thread.currentThread());
		
		for (int i = fromPool; i <= toPool; i++) {		
			pools[i].clear();
		}
	}
	
		public void clear() {
		clear(0);
	}
	
	public void printStats() {
		for (int i = 0; i < m_poolCount; i++) {
			System.out.println("\t[Pool " + i + "] " + m_accesses[i] + " accesses, " + m_hits[i] + " hits => " + (m_hits[i] / (float) m_accesses[i]));				
		}
	}

	/* (non-Javadoc)
	 * @see de.parmol.util.ObjectPool#getName()
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.parmol.util.ObjectPool#setName(java.lang.String)
	 */
	public void setName(String newName) {
		// TODO Auto-generated method stub
		
	}
}

