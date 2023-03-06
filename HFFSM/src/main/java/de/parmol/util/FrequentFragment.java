/*
 * Created on 03.10.2004
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

import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;

import de.parmol.graph.DirectedGraph;
import de.parmol.graph.DirectedListGraph;
import de.parmol.graph.Graph;
import de.parmol.graph.GraphEmbedding;
import de.parmol.graph.MatrixGraph;
import de.parmol.graph.UndirectedGraph;
import de.parmol.graph.UndirectedListGraph;
import de.parmol.graph.Util;
import de.parmol.parsers.GraphParser;


/**
 * This class represents a discovered frequent fragment. It stores all necessary information for reporting it after the
 * search.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *  
 */
public class FrequentFragment {
	private final Graph m_fragment;
	private final Graph[] m_supportedGraphs;
	private final float[] m_classFrequencies;
	private int[] m_nodePartitions = null;
	private final GraphEmbedding[] m_embeddings;


	/**
	 * Creates a new FrequentFragment.
	 * 
	 * @param fragment the frequent fragments
	 * @param supportedGraphs a collection of graphs where the frequent fragments occurs
	 * @param classFrequencies the frequencies of the frequent fragment in the different classes
	 */
	public FrequentFragment(Graph fragment, Collection supportedGraphs, float[] classFrequencies) {
		if ((fragment instanceof MatrixGraph) && (fragment.getEdgeCount() / (float) fragment.getNodeCount() < 10)) {
			if (fragment instanceof DirectedGraph) {
				m_fragment = new DirectedListGraph((DirectedGraph) fragment);
			} else {
				m_fragment = new UndirectedListGraph((UndirectedGraph) fragment);
			}
		} else {
			m_fragment = fragment;
		}

		m_supportedGraphs = new Graph[supportedGraphs.size()];
		int i = 0;
		for (Iterator it = supportedGraphs.iterator(); it.hasNext(); i++) {
			m_supportedGraphs[i] = (Graph) it.next();
		}

		m_classFrequencies = classFrequencies;
		m_embeddings = null;
	}


	/**
	 * Creates a new FrequentFragment.
	 * 
	 * @param embeddings a collection of embeddings of the frequent fragment into the graph database
	 * @param classFrequencies the frequencies of the frequent fragment in the different classes
	 * @param storeEmbeddings <code>true</code> if all embeddings should be stored (they are cloned in this case),
	 *          <code>false</code> otherwise
	 */
	public FrequentFragment(Collection embeddings, float[] classFrequencies, boolean storeEmbeddings) {
		if (embeddings.size() > 0) {
			GraphEmbedding emb = (GraphEmbedding) embeddings.iterator().next();

			if (emb.isDirectedGraphEmbedding()) {
				m_fragment = new DirectedListGraph((DirectedGraph) emb.getSubGraph());
			} else {
				m_fragment = new UndirectedListGraph((UndirectedGraph) emb.getSubGraph());
			}
		} else {
			m_fragment = null;
		}


		if (storeEmbeddings) {
			m_embeddings = new GraphEmbedding[embeddings.size()];
			int i = 0;
			for (Iterator it = embeddings.iterator(); it.hasNext();) {
				m_embeddings[i++] = (GraphEmbedding) ((GraphEmbedding) it.next()).clone();
			}

			m_supportedGraphs = null;
		} else {
			HashSet graphs = new HashSet((int) (embeddings.size() * 1.3));

			for (Iterator it = embeddings.iterator(); it.hasNext();) {
				Graph g = ((GraphEmbedding) it.next()).getSuperGraph();

				if (!graphs.contains(g)) {
					graphs.add(g);

				}
			}

			m_supportedGraphs = new Graph[graphs.size()];
			int i = 0;
			for (Iterator it = graphs.iterator(); it.hasNext();) {
				m_supportedGraphs[i++] = (Graph) it.next();
			}
			m_embeddings = null;
		}
		m_classFrequencies = classFrequencies;
	}


	/**
	 * Returns the frequent fragment.
	 * 
	 * @return the frequent fragment
	 */
	public Graph getFragment() {
		return m_fragment;
	}


	/**
	 * Returns an array of graphs in which the frequent fragment occurs
	 * 
	 * @return an array of graphs
	 */
	public Graph[] getSupportedGraphs() {
		if (m_supportedGraphs != null) {
			return m_supportedGraphs;
		} else {
			HashSet graphs = new HashSet();

			for (int i = 0; i < m_embeddings.length; i++) {
				if (!graphs.contains(m_embeddings[i].getSuperGraph())) {
					graphs.add(m_embeddings[i].getSuperGraph());

				}
			}

			Graph[] supportedGraphs = new Graph[graphs.size()];
			int i = 0;
			for (Iterator it = graphs.iterator(); it.hasNext();) {
				supportedGraphs[i++] = (Graph) it.next();
			}

			return supportedGraphs;
		}
	}


	/**
	 * Returns the frequencies of the frequent fragment in the different classes of the graph database
	 * 
	 * @return the frequencies of the frequent fragment
	 */
	public float[] getClassFrequencies() {
		return m_classFrequencies;
	}


	/**
	 * Converts this frequent fragment to a string by using the given graph serializer. The string contains the fragment
	 * itself, the class frequencies and -- if available -- a list of all supported graphs.
	 * 
	 * @param serializer a graph serializer
	 * @return a string representation of this frequent fragment.
	 */
	public String toString(GraphParser serializer) {
		final StringBuffer buf = new StringBuffer(1024);

		buf.append(serializer.serialize(m_fragment) + " =>  [");
		for (int i = 0; i < (m_classFrequencies.length - 1); i++) {
			buf.append(m_classFrequencies[i] + ", ");
		}
		buf.append(m_classFrequencies[m_classFrequencies.length - 1] + "] [");

		if (m_supportedGraphs != null) {
			for (int i = 0; i < m_supportedGraphs.length; i++) {
				buf.append(m_supportedGraphs[i].getName());
				if (i < m_supportedGraphs.length - 1) buf.append(',');
			}
		} else if (m_embeddings != null) {
			final IdentityHashMap graphs = new IdentityHashMap();

			for (int i = 0; i < m_embeddings.length; i++) {
				graphs.put(m_embeddings[i].getSuperGraph(), m_embeddings[i].getSuperGraph());
			}

			for (Iterator it = graphs.keySet().iterator(); it.hasNext();) {
				buf.append(((Graph) it.next()).getName());
				if (it.hasNext()) buf.append(',');
			}
		}

		buf.append(']');
		return buf.toString();
	}


	/**
	 * Returns the node partitions of the frequent fragment (the same as
	 * <code>Util.getNodePartitions(this.getFragment())</code>. The partitions are cached for futher reuse.
	 * 
	 * @return the node partitions.
	 */
	public int[] getNodePartitions() {
		if (m_nodePartitions == null) {
			m_nodePartitions = Util.getNodePartitions(m_fragment);
		}
		return m_nodePartitions;
	}


	/**
	 * Returns all embeddings of the fragment, or <code>null</code> if no embeddings have been stored
	 * 
	 * @return the embeddings of this fragment
	 */
	public GraphEmbedding[] getEmbeddings() {
		return m_embeddings;
	}
}