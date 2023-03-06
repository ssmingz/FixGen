/*
 * Created on 28.12.2004
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

import java.util.Comparator;


/**
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *  
 */
public abstract class ExtendedComparator implements Comparator {
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		throw new UnsupportedOperationException("Sorry, compare(Object, Object) is not implemented");
	}


	/**
	 * Compares the two given int's which must not necessarily a comparison of the values but may be a comparison of two
	 * objects in an array and the values are only their indices, etc.
	 * 
	 * @param i1 the first value
	 * @param i2 the second value
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
	 *         than the second.
	 */
	public int compare(int i1, int i2) {
		throw new UnsupportedOperationException("Sorry, compare(int, int) is not implemented");
	}


	/**
	 * Compares the two given int's which must not necessarily a comparison of the values but may be a comparison of two
	 * objects represented by the two values.
	 * 
	 * @param i1 the first value
	 * @param i2 the second value
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
	 *         than the second.
	 */
	public int compare(double i1, double i2) {
		throw new UnsupportedOperationException("Sorry, compare(int, int) is not implemented");
	}
}