/*
 * Created on 03.04.2005
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

import java.util.Arrays;


/**
 * This class is a list of embeddings. The subgraph whose embeddings are stored in the list is given implicitly by the
 * matrix that owns this embedding list. The list offers methods for the calculation of embedding lists of extended
 * matrices.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *  
 */
public class EmbeddingList {
	private Embedding[] m_embeddings;
	private int m_size = 0;

	private final static int DEFAULT_SIZE = 16;


	/**
	 * Creates a new embedding list with the default size of 16 free entries.
	 */
	public EmbeddingList() {
		m_embeddings = new Embedding[DEFAULT_SIZE];
	}


	/**
	 * Creates a new embedding list with the given free size.
	 * 
	 * @param size the initial size of the list
	 */
	public EmbeddingList(int size) {
		m_embeddings = new Embedding[size];
	}


	/**
	 * Adds an embedding to the list.
	 * 
	 * @param e an embedding
	 */
	public void addEmbedding(Embedding e) {
		if (m_size >= m_embeddings.length) resize();
		m_embeddings[m_size++] = e;
	}


	/**
	 * Adds the given embedding at the right position according to the natural order of embeddings.
	 * 
	 * @param e an embedding
	 */
	public void addEmbeddingSorted(Embedding e) {
		if (m_size >= m_embeddings.length) resize();

		m_size++;
		for (int i = m_size - 2; i >= 0; i--) {
			if (m_embeddings[i].compareTo(e) <= 0) {
				m_embeddings[i + 1] = e;
				return;
			} else {
				m_embeddings[i + 1] = m_embeddings[i];
			}
		}

		m_embeddings[0] = e;
	}


	/**
	 * Resizes the list.
	 */
	private void resize() {
		final int newSize = m_embeddings.length + (m_embeddings.length / 3) + 1;
		final Embedding[] newEmbeddings = new Embedding[newSize];
		System.arraycopy(m_embeddings, 0, newEmbeddings, 0, m_embeddings.length);
		m_embeddings = newEmbeddings;
	}


	/**
	 * Returns the size of the list, i.e. the number of embeddings it contains.
	 * 
	 * @return the size of the list.
	 */
	public int size() {
		return m_size;
	}


	/**
	 * Returns the embedding with the given index.
	 * 
	 * @param index an index between <code>0</code> and <code>size() - 1</code>
	 * @return an embedding
	 */
	public Embedding get(int index) {
		assert (index < m_size);
		return m_embeddings[index];
	}


	/**
	 * Sorts the embeddings in the list by their natural order.
	 */
	public void sort() {
		Arrays.sort(m_embeddings, 0, m_size);
	}


	/**
	 * Intersects the two embeddings list. The result is a new embedding list that contains only the embeddings that are
	 * in both lists.
	 * 
	 * @param l1 an embedding list
	 * @param l2 another embedding list
	 * @return a new embedding list which is the intersection of the two other lists
	 */
	public static EmbeddingList intersect(EmbeddingList l1, EmbeddingList l2) {
		final EmbeddingList intersection = new EmbeddingList(Math.min(l1.size(), l2.size()));
		int i = 0, k = 0;

		//故embedding list中embdding是按从小到大排序的，所以使用该算法
		while ((i < l1.size()) && (k < l2.size())) {
			final int diff = l1.m_embeddings[i].compareTo(l2.m_embeddings[k]);
			if (diff < 0) {
				i++;
			} else if (diff > 0) {
				k++;
			} else {
				intersection.addEmbedding(l1.m_embeddings[i]);
				i++;
				k++;
			}
		}

		//故embedding list中embdding是按从小到大排序的
		assert (intersection.isSorted());
		return intersection;
	}


	/**
	 * Intersects the two given list in the way how the join case 2 of FFSM wants it (see the paper on FFSM for details).
	 * The second list must contain the embeddings of the bigger matrix.
	 * 
	 * @param l1 the embedding list for the smaller matrix 尺寸较小inner
	 * @param l2 the embedding list for the bigger matrix 尺寸较大的outer
	 * @return a new embedding list
	 */
	public static EmbeddingList joinCase2Intersection(EmbeddingList l1, EmbeddingList l2) {
		final EmbeddingList intersection = new EmbeddingList(Math.min(l1.size(), l2.size()));
		int i = 0, k = 0, lastI = 0;

		while ((i < l1.size()) && (k < l2.size())) {
			final int diff = l1.m_embeddings[i].compareTo(l2.m_embeddings[k].getParent());
			if (diff < 0) {
				lastI = ++i;
			} else if (diff > 0) {
				k++;
				i = lastI;
			} else {
				intersection.addEmbedding(l2.m_embeddings[k]);
				i++;
				if (i >= l1.size()) {
					k++;
					i = lastI;
				}
			}
		}

		assert (intersection.isSorted());
		return intersection;
	}


	/**
	 * Intersects the two given list in the way how the join case 3b of FFSM wants it (see the paper on FFSM for details).
	 * 
	 * @param l1 an embedding list
	 * @param l2 another embedding list
	 * @return a new embedding list
	 */
	public static EmbeddingList joinCase3BIntersection(EmbeddingList l1, EmbeddingList l2) {
		final EmbeddingList intersection = new EmbeddingList(Math.max(l1.size(), l2.size()));
		int i = 0, k = 0, lastK = 0;
		while ((i < l1.size()) && (k < l2.size())) {
			if (l1.m_embeddings[i].getParent() == l2.m_embeddings[k].getParent()) {
				if ((l1.m_embeddings[i] != l2.m_embeddings[k])
						&& (l1.m_embeddings[i].getNode() != l2.m_embeddings[k].getNode())) {
					intersection.addEmbedding(new Embedding(l1.m_embeddings[i], l2.m_embeddings[k].getNode()));
				}

				k++;
				if (k >= l2.size()) {
					k = lastK;
					i++;
				}
			} else {
				final int diff = l1.m_embeddings[i].getParent().compareTo(l2.m_embeddings[k].getParent());
				if (diff < 0) {
					i++;
					k = lastK;
				} else if (diff > 0) {
					lastK = ++k;
				} else {
					throw new RuntimeException("Oops, this place should never have been reached");
				}
			}
		}

		assert (intersection.isSorted());
		return intersection;
	}


	/**
	 * Checks if this embedding list is sorted according to the natural order of embeddings.
	 * @return <code>true</code> if the list is sorted, <code>false</code> otherwise
	 */
	public boolean isSorted() {
		for (int i = 1; i < m_size; i++) {
			if (m_embeddings[i - 1].compareTo(m_embeddings[i]) > 0) return false;
		}
		return true;
	}
}