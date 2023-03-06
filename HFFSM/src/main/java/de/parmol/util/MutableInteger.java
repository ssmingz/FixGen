/*
 * Created on Aug 13, 2004
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

/**
 * This class is the same as Integer but the stored value can be changed.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *
 */
public class MutableInteger extends Number implements Comparable {
	private static final long serialVersionUID = 532556164094282571L;
	private int m_value;
	
	/**
	 * Creates a new MutableInteger with the given inital value.
	 * @param value the value
	 */
	public MutableInteger(int value) {
		m_value = value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Number#doubleValue()
	 */
	public double doubleValue() { return m_value;	}


	/* (non-Javadoc)
	 * @see java.lang.Number#floatValue()
	 */
	public float floatValue() { return m_value; }


	/* (non-Javadoc)
	 * @see java.lang.Number#intValue()
	 */
	public int intValue() { return m_value; }


	/* (non-Javadoc)
	 * @see java.lang.Number#longValue()
	 */
	public long longValue() { return m_value; }

	/**
	 * Increments the value by one.
	 * @return the new value
	 */
	public int inc() { return ++m_value; }
	
	/**
	 * Decrements the value by one.
	 * @return the new value
	 */
	public int dec() { return --m_value; }
	
	/**
	 * Sets the value to a new value
	 * @param value the new value
	 */
	public void setValue(int value) { m_value = value; }

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		return m_value - ((MutableInteger) o).m_value;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return m_value == ((MutableInteger) obj).m_value;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return m_value;
	}
}
