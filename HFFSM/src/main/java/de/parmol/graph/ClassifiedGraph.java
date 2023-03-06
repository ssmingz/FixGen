/*
 * Copyright 2004,2005 Thorsten Meinl
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
 *  
 */
package de.parmol.graph;

/**
 * This interface describes graphs that occur in one or more classes with some certain (fuzzy) frequencies.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public interface ClassifiedGraph extends Graph {
	/**
	 * Returns the frequencies of this graph in the various classes.
	 * @return a array with the frequency of this graph in each of the classes
	 */
	public float[] getClassFrequencies(); 

	/**
	 * Sets the class frequencies of this graph.
	 * @param frequencies an array with the class frequencies
	 */
	public void setClassFrequencies(float[] frequencies);
}

