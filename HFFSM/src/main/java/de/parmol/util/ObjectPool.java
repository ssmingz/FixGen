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

/**
 * This class represents a pool for objects that can be reused. If you allocate millions of small objects and throw them away very soon it improves performance
 * (especially in multithreaded programms) if you reuse objects and just copy the values. This class provides the basic functionality.
 * You can create more than one pool in an object of this class, e.g. if you have objects of the same class but with different sizes. So the objects need to be grouped
 * according to their sizes.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */

public interface ObjectPool {
	/**
	 * Removes and returns an object from the given sub-pool
	 * @param poolNumber the number of the sub-pool
	 * @return an object from the pool or <code>null</code> if the pool is empty 
	 */
	public abstract Object getObject(int poolNumber);

	/**
	 * Removes and returns an object from the pool
	 * @return an object from the pool or <code>null</code> if the pool is empty
	 */
	public abstract Object getObject();

	/**
	 * Puts an idle object back into the given sub-pool if the pool is not already full
	 * @param o the idle object
	 * @param poolNumber the number of the sub-pool
	 */
	public abstract void repoolObject(Object o, int poolNumber);

	/**
	 * Puts an idle object back into the pool if the pool is not already full
	 * @param o the idle object
	 */
	public abstract void repoolObject(Object o);

	/**
	 * Removes all objects from the given sub-pool
	 * @param poolNumber the number of the sub-pool
	 */
	public abstract void clear(int poolNumber);

	/**
	 * Removes all objects from the given sub-pools.
	 * @param fromPool the first sub-pool that should be cleared
	 * @param toPool the last sub-pool that should be cleared
	 */
	public abstract void clear(int fromPool, int toPool);

	/**
	 * Removes all objects from the pool
	 */
	public abstract void clear();

	/**
	 * Returns the name of this pool
	 * @return the name of this pool
	 */
	public String getName();
	
	/**
	 * Sets the name of this pool
	 * @param newName the new name of this pool
	 */
	public void setName(String newName);
	
	/**
	 * Prints statistics about pool hits and accesses.
	 */
	public abstract void printStats();
}