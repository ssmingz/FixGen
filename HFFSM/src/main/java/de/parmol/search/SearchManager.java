/*
 * Created on 20.04.2005
 * 
 * Copyright 2005 Thorsten Meinl
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
 * This interface describes a very simple search manager, that just takes a starting node in the search tree and starts the search. I can be implemented
 * by all kinds of search strategies.
 * 
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *  
 */
public interface SearchManager {
	/**
	 * Adds a starting node to the search tree.
	 * 
	 * @param startNode a search tree node
	 */
	public void addStartNode(SearchTreeNode startNode);


	/**
	 * Starts the search.
	 */
	public void startSearch();
}