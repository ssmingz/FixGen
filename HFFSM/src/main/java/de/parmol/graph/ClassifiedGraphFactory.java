/*
 * Created on Aug 16, 2004
 *
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
 * This factory creates classified graphs.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public abstract class ClassifiedGraphFactory extends GraphFactory {
	protected ClassifiedGraphFactory(int typemask) { super(typemask); }
	
	/**
	 * Creates a new classified graph with the given class frequencies.
	 * @param classFrequencies the frequencies in the different classes
	 * @return a new ClassifiedGraph
	 */
	public abstract ClassifiedGraph createGraph(float[] classFrequencies);

	/**
	 * Creates a new classified graph with the given class frequencies.
	 * @param id the id of this graph
	 * @param classFrequencies the frequencies in the different classes
	 * @return a new ClassifiedGraph
	 */
	public abstract ClassifiedGraph createGraph(String id, float[] classFrequencies);
}
