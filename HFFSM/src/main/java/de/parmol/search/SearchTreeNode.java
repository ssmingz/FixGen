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

import java.util.Collection;
import java.util.LinkedList;

/**
 * This class represents a node in the search tree.
 * 
 * @author Thorsten.Meinl@informatik.uni-erlangen.de
 *  
 */
public class SearchTreeNode {
	protected Collection m_children;
	
	protected final int m_level;
	protected SearchTreeNode m_parent;

	/**
	 * Creates a new SearchTreeNode.
	 * @param parent the parent node if this new node
	 * @param level the level in the search tree
	 */
	public SearchTreeNode(SearchTreeNode parent, int level) {
		m_parent = parent;
		m_level = level;
		m_children = new LinkedList();
	}

	/**
	 * Returns the children of this node.
	 * @return the children of this node
	 */
	public Collection getChildren() {	return m_children; }

	/**
	 * Adds all children in the given collection to the cildren of this node.
	 * @param newChildren the children to be added
	 */
	public void addChildren(Collection newChildren) {	m_children.addAll(newChildren);	}

	/**
	 * Adds the given node to the children of this node.
	 * @param newChild a new child
	 */
	public void addChild(SearchTreeNode newChild) {	m_children.add(newChild);	}

	/**
	 * Returns the level in the search tree.
	 * @return the level in the search tree
	 */
	public final int getLevel() { return m_level;	}
	
	/**
	 * Clears the list of children and removes the parent reference. After calling this the return value of getChildren() is undefined.
	 */
	public void clear() {
		m_children = null;
		m_parent = null;
	}
}