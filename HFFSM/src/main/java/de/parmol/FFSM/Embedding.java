/*
 * Created on 18.05.2005
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
 */
package de.parmol.FFSM;

import de.parmol.graph.Graph;


/**
 * This class represents an FFSM embedding. According to a hint from Luke Huan the embeddings are implemented in a very
 * space efficient way: Only a reference to the parent embedding and the new node are stored. For performance reasons
 * also the referenced graph is stored.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *  
 */
public final class Embedding implements Comparable {
	private final Graph m_supergraph;
	private final int m_node;
	private final Embedding m_parent;


	/**
	 * Creates a new embedding at level 0, i.e. without a parent.
	 * 
	 * @param supergraph the referenced supergraph
	 * @param node the only node in the embedding
	 */
	public Embedding(Graph supergraph, int node) {
		m_supergraph = supergraph;
		m_node = node;
		m_parent = null;
	}


	/**
	 * Creates a child embedding.
	 * 
	 * @param parent the parent embedding
	 * @param node the new node
	 */
	public Embedding(Embedding parent, int node) {
		m_parent = parent;
		m_node = node;
		m_supergraph = parent.m_supergraph;
	}


	/**
	 * Returns the supergraph this embedding refers to.
	 * 
	 * @return the supergraph
	 */
	public Graph getSuperGraph() {
		return m_supergraph;
	}


	/**
	 * Returns the node that was added by the embedding (the last node)
	 * 
	 * @return the last node in the embedding
	 */
	public int getNode() {
		return m_node;
	}


	/**
	 * Recursively checks if the given node occurs in the embedding and its parent embeddings.
	 * 
	 * @param node a node
	 * @return <code>true</code> if the node occurs in the complete embedding, <code>false</code> otherwise
	 */
	public boolean containsNode(int node) {
		if (m_parent == null) {
			return (m_node == node);
		} else {
			if (m_node == node) return true;
			return m_parent.containsNode(node);
		}
	}


	/**
	 * Returns the size of this embedding i.e. the number of nodes.
	 * 
	 * @return the size of the embedding
	 */
	public int getSize() {
		if (m_parent == null) {
			return 1;
		} else {
			return 1 + m_parent.getSize();
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		return compareTo((Embedding) o);
	}


	/**
	 * Compares this embedding to the given embedding. First the id of the referenced supergraphs is compared and then the
	 * list of nodes.
	 * 
	 * @param e an embedding
	 * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the
	 *         specified object.
	 */
	public int compareTo(Embedding e) {
		if (this == e) return 0;
		int diff = this.m_supergraph.getID() - e.m_supergraph.getID();
		if (diff != 0) return diff;

		return privateCompareTo(e);
	}


	private int privateCompareTo(Embedding e) {
		if ((this.m_parent != null) && (this.m_parent != e.m_parent)) {
			int diff = this.m_parent.compareTo(e.m_parent);
			if (diff != 0) return diff;
		}

		return this.m_node - e.m_node;
	}


	/**
	 * Returns the parent embedding, or <code>null</code> if no parent embedding exists.
	 * 
	 * @return the parent embedding or <code>null</code>
	 */
	public Embedding getParent() {
		return m_parent;
	}
}