/*
 * Created on May 17, 2004
 *
 * Copyright 2004 Thorsten Meinl
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
package de.parmol.search;


/**
 * This class is the frame for a breadth-first search strategy. It has to be
 * specialized in subclasses for the actual mining algorithm.
 * 
 * @author Thorsten@meinl.bnv-bamberg.de
 *  
 */
public interface DFSSearchable {
	/**
	 * This is called during the search when a level in the search tree is left.
	 * @param currentNode the current node in the search tree, that is left
	 */
	void leaveNode(SearchTreeNode currentNode);
	
	/**
	 * This method is called during the search if new child nodes should be created
	 * @param currentNode the current node in the search tree
	 */
	void generateChildren(SearchTreeNode currentNode);

	/**
	 * This is called during the search when a level in the search tree is entered.
	 * @param currentNode the current node in the search tree, that is entered
	 */
	void enterNode(SearchTreeNode currentNode);
	
	/**
	 * Creates a new instance of a DFSSearchable that is a copy of the given object.
	 * @param previousWorker the worker that should be copied
	 * @return the new instance
	 */
	DFSSearchable newInstance(DFSSearchable previousWorker);
}