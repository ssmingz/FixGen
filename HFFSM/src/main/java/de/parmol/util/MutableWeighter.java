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

/**
 * This class ...
 * @author Marc Woerlein <Marc.Woerlein@informatik.uni-erlangen.de>
 *
 */
public interface MutableWeighter {
	
	/**
	 * @param o an Object whose wight shall be computed   
	 * @return the maximal possible weight of the given object
	 */
	public float getMaximalWeight(Object o);
	/**
	 * @param it an iterator over all objects of the interesting set
	 * @return the finally weight of the set 
	 */
	public float getFinallyWeight(Iterator it);
	
	/**
	 * @param o 
	 * @return <code> true </code>, if the given object will be necessary for this weighter
	 */
	public boolean useObject(Object o);
	
	/**
	 * @return a copy of this weighter that can be used distributed
	 */
	public MutableWeighter copy();

}
