/*
 * Created on 05.04.2005
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
package de.parmol.FFSM;

import java.util.Collection;
import java.util.Iterator;

import de.parmol.search.SearchTreeNode;


/**
 * This class represents a node in the search tree of FFSM.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class FFSMSearchTreeNode extends SearchTreeNode {
	private final Collection m_matrices;


	/**
	 * Creates a new node in the FFSM search tree.
	 * 
	 * @param parent the parent node
	 * @param matrices a collection of matrices in this search tree node
	 * @param level the level in the search tree
	 */
	public FFSMSearchTreeNode(SearchTreeNode parent, Collection matrices, int level) {
		super(parent, level);
		m_matrices = matrices;
	}


	/**
	 * Returns the matrices that correspond to this search tree node
	 * 
	 * @return a collection of matrices
	 */
	public Collection getMatrices() {
		return m_matrices;
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.search.SearchTreeNode#clear()
	 */
	public void clear() {
		super.clear();

		for (Iterator it = m_matrices.iterator(); it.hasNext();) {
			final Matrix matrix = (Matrix) it.next();
			matrix.removeEmbeddings();
		}

		m_matrices.clear();
	}
}