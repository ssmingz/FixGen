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
import java.util.Vector;

/**
 * The default implementation of the ObjectPool interface.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class DefaultObjectPool implements ObjectPool {
  protected final int m_poolSize, m_poolCount;
  protected final ArrayList[] m_pools;
  protected String m_name = "UnknownPool";

	/* for debugging */
	protected int[] m_hits, m_accesses;
	protected final static Vector POOLS = new Vector(16);
	{	POOLS.add(this); }
	/* ---------------- */
	
	/**
	 * Creates a new pool which contains sub-pools and a separate pool for every thread. 
	 * @param poolSize the size of each single pool
	 * @param pools the number of sub-pools
	 */
	public DefaultObjectPool(int poolSize, int pools) {
		m_poolSize = poolSize;
		m_poolCount = pools;

		m_pools = new ArrayList[pools];
		for (int k = 0; k < pools; k++) {
			m_pools[k] = new ArrayList(poolSize);
		}
		
		m_hits = new int[pools];
		m_accesses = new int[pools];
	}
	
	/**
	 * Creates a new pool with the given size.
	 * @param poolSize the size of the pool
	 */
	public DefaultObjectPool(int poolSize) {
		this(poolSize, 1);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.parmol.util.ObjectPool#setName(java.lang.String)
	 */
	public void setName(String newName) { m_name = newName; }
	
	/*
	 * (non-Javadoc)
	 * @see de.parmol.util.ObjectPool#getObject(int)
	 */
	public Object getObject(int poolNumber) {		
		m_accesses[poolNumber]++;
		if (m_pools[poolNumber].size() > 0) {
			m_hits[poolNumber]++;
			return m_pools[poolNumber].remove(m_pools[poolNumber].size() - 1);
		}
		return null;		
	}

	/*
	 * (non-Javadoc)
	 * @see de.parmol.util.ObjectPool#getObject()
	 */
	public Object getObject() {
		return getObject(0);
	}

	/*
	 * (non-Javadoc)
	 * @see de.parmol.util.ObjectPool#repoolObject(java.lang.Object, int)
	 */
	public void repoolObject(Object o, int poolNumber) {
		if (m_pools[poolNumber].size() < m_poolSize) m_pools[poolNumber].add(o);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.parmol.util.ObjectPool#repoolObject(java.lang.Object)
	 */
	public void repoolObject(Object o) {
		repoolObject(o, 0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.parmol.util.ObjectPool#clear(int)
	 */
	public void clear(int poolNumber) {			
		m_pools[poolNumber].clear();
	}

	/*
	 * (non-Javadoc)
	 * @see de.parmol.util.ObjectPool#clear(int, int)
	 */
	public void clear(int fromPool, int toPool) {
		for (int i = fromPool; i <= toPool; i++) {		
			m_pools[i].clear();
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see de.parmol.util.ObjectPool#clear()
	 */
	public void clear() {
		clear(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.parmol.util.ObjectPool#printStats()
	 */
	public void printStats() {
		for (int i = 0; i < m_poolCount; i++) {
			System.out.println("\t[" + m_name + " " + i + "] " + m_accesses[i] + " accesses, " + m_hits[i] + " hits => " + (m_hits[i] / (float) m_accesses[i]));				
		}
	}
	
	/**
	 * Prints statistics about the object pool by calling <code>printStats</code> on each sub pool.
	 */
	public static void printStatistics() {
		for (int i = POOLS.size() - 1; i >= 0; i--) {
			((ObjectPool) POOLS.get(i)).printStats();
		}
	}

	/* (non-Javadoc)
	 * @see de.parmol.util.ObjectPool#getName()
	 */
	public String getName() { return m_name; }
}

