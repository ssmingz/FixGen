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
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;

import de.parmol.Settings;
import de.parmol.graph.Graph;
import de.parmol.graph.SimpleSubgraphComparator;
import de.parmol.search.DFSSearchable;
import de.parmol.search.SearchTreeNode;
import de.parmol.util.FragmentSet;
import de.parmol.util.FrequentFragment;


/**
 * This class does the recursive depth first search through the FFSM search tree.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class FFSMDFSSearch implements DFSSearchable {
	protected FragmentSet m_frequentSubgraphs;
	protected JoinerAndExtender m_joinerAndExtender;
	protected final Settings m_settings;


	/**
	 * Creates a new FFSMDFSSearch with the given settings.
	 * 
	 * @param settings the settings for the search
	 * @param frequentEdges an array with all frequent edges in the database
	 */
	public FFSMDFSSearch(Settings settings, GraphEdge[] frequentEdges) {
		m_settings = settings;
		m_frequentSubgraphs = new FragmentSet();
		//m_joinerAndExtender = new JoinerAndExtender(frequentEdges);
		m_joinerAndExtender = new JoinerAndExtender();
	}


	/**
	 * Copy constructor.
	 * 
	 * @param previousWorker the worker that should be copied
	 */
	private FFSMDFSSearch(FFSMDFSSearch previousWorker) {
		m_settings = previousWorker.m_settings;
		m_joinerAndExtender = previousWorker.m_joinerAndExtender;
		m_frequentSubgraphs = previousWorker.m_frequentSubgraphs;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.search.DFSSearchable#newInstance(de.parmol.search.DFSSearchable)
	 */
	public DFSSearchable newInstance(DFSSearchable previousWorker) {
		return new FFSMDFSSearch((FFSMDFSSearch) previousWorker);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.search.DFSSearchable#leaveNode(de.parmol.search.SearchTreeNode)
	 */
	public void leaveNode(SearchTreeNode currentNode) {
		((FFSMSearchTreeNode) currentNode).clear();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.search.DFSSearchable#generateChildren(de.parmol.search.SearchTreeNode)
	 */
	public void generateChildren(SearchTreeNode currentNode) {
		final Collection matrices = ((FFSMSearchTreeNode) currentNode).getMatrices();

		for (Iterator it = matrices.iterator(); it.hasNext();) {
			final Matrix parentMatrix = (Matrix) it.next();

			if (parentMatrix.isCAM()) {
				if ((m_settings.debug > 0) && (parentMatrix.getNodeCount() == 2)) {
					System.out.println("[" + Thread.currentThread().getName() + "] Extending and joining "
							+ m_settings.serializer.serialize(parentMatrix));
				} else if (m_settings.debug > 3) {
					System.out.println("[" + Thread.currentThread().getName() + "] Extending and joining "
							+ m_settings.serializer.serialize(parentMatrix) + " (" + m_frequentSubgraphs.size() + ")");
				}

				// do the joining
				Collection newMatrices = new LinkedList();
				for (Iterator iterator = matrices.iterator(); iterator.hasNext();) {
					final Matrix matrix2 = (Matrix) iterator.next();

					m_joinerAndExtender.join(parentMatrix, matrix2, newMatrices);
				}

				// extend the matrix
				m_joinerAndExtender.extend(parentMatrix, newMatrices);


				final float[] parentFrequencies = parentMatrix.getClassFrequencies();
				boolean parentMaybeClosed = true;

				for (Iterator it2 = newMatrices.iterator(); it2.hasNext();) {
					final Matrix newMatrix = (Matrix) it2.next();

					calculateFrequencies(newMatrix);

					assert (newMatrix.getEmbeddings().size() == SimpleSubgraphComparator.instance.getEmbeddingCounter(newMatrix,
							newMatrix.getSupportedGraphs()));

					if (!m_settings.checkMinimumFrequencies(newMatrix.getClassFrequencies())) {
						if (newMatrix.getClassFrequencies()[0] == 0) m_settings.stats.nonExistingPrunedMatrices++;

						if (m_settings.debug > 4) {
							System.out.println("Pruned infrequent child " + m_settings.serializer.serialize(newMatrix));
						}

						it2.remove();
					} else {
						parentMaybeClosed &= !Arrays.equals(newMatrix.getClassFrequencies(), parentFrequencies);
					}
				}

				// check if the found frequent fragment should be reported
				if ((!m_settings.closedFragmentsOnly || parentMaybeClosed)
						&& m_settings.checkReportingConstraints(parentMatrix, parentMatrix.getClassFrequencies())) {
					synchronized (m_frequentSubgraphs) {
						m_frequentSubgraphs.add(new FrequentFragment(parentMatrix, parentMatrix.getSupportedGraphs(), parentMatrix
								.getClassFrequencies()));
					}
				} else {
					m_settings.stats.earlyFilteredNonClosedFragments++;
				}

				currentNode.addChild(new FFSMSearchTreeNode(currentNode, newMatrices, currentNode.getLevel() + 1));
			} else {
				m_settings.stats.duplicateFragments++;
			}
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.search.DFSSearchable#enterNode(de.parmol.search.SearchTreeNode)
	 */
	public void enterNode(SearchTreeNode currentNode) { /* nothing to do here */
	}


	/**
	 * Calculates the class frequencies of the given matrix by searching for distinct graphs in the embedding list. The
	 * distinct graphs are directly stored in the matrix.
	 * 
	 * @param matrix a matrix
	 */
	private void calculateFrequencies(Matrix matrix) {
		final IdentityHashMap graphs = new IdentityHashMap();
		final EmbeddingList embeddings = matrix.getEmbeddings();

		for (int j = 0; j < embeddings.size(); j++) {
			final Embedding emb = embeddings.get(j);
			final Graph supergraph = emb.getSuperGraph();

			if (graphs.put(supergraph, supergraph) == null) {
				matrix.addSupportedGraph(supergraph);
			}
		}
	}


	/**
	 * Returns the set of found frequent subgraphs.
	 * 
	 * @return a set of frequent fragments
	 */
	public FragmentSet getFrequentSubgraphs() {
		return m_frequentSubgraphs;
	}
}