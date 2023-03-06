/*
 * Created on 25.03.2005
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import de.parmol.AbstractMiner;
import de.parmol.Settings;
import de.parmol.graph.ClassifiedGraph;
import de.parmol.graph.Graph;
import de.parmol.search.SearchManager;
import de.parmol.util.MutableInteger;


/**
 * This class is the main class of the FFSM algorithm.
 *
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *
 */
public class Miner extends AbstractMiner {
	private GraphEdge[] m_frequentEdges;
	private Collection m_oneNodeMatrices;


	/**
	 * Creates a new FFSM miner.
	 *
	 * @param settings the settings for the search
	 */
	public Miner(Settings settings) {
		super(settings);
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.AbstractMiner#startRealMining()
	 */
	protected void startRealMining() {
		final FFSMDFSSearch searcher = new FFSMDFSSearch(m_settings, m_frequentEdges);
		SearchManager searchManager = getSearchManager(searcher);

		FFSMSearchTreeNode startNode = new FFSMSearchTreeNode(null, m_oneNodeMatrices, 1);
		searchManager.addStartNode(startNode);
		searchManager.startSearch();
		m_frequentSubgraphs = searcher.getFrequentSubgraphs();
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see de.parmol.AbstractMiner#setUp()
	 */
	public void setUp() throws FileNotFoundException, IOException, ParseException {
		super.setUp();

		final HashSet nodesPerGraph = new HashSet();
		final HashSet edgesPerGraph = new HashSet();

		final MutableInteger nodeLabel = new MutableInteger(-1);
		final GraphEdge edge = new GraphEdge(-1, -1, -1);

		final HashMap nodeFrequencies = new HashMap();
		final HashMap edgeFrequencies = new HashMap();

		final float[] TEMP = { 1.0f };


		//利用nodesPerGraph和edgesPerGraph获得了每一张图 每个顶点和每条边的frequency，从而获得整个输入图中有的顶点和边，及其frequency，存于hashmap中。
		for (Iterator it = m_graphs.iterator(); it.hasNext();) {
			final Graph g = (Graph) it.next();

			//以遍历的方式得到每个图的顶点标签及其frequency
			nodesPerGraph.clear();
			for (int i = g.getNodeCount() - 1; i >= 0; i--) {
				nodeLabel.setValue(g.getNodeLabel(g.getNode(i)));


				if (!nodesPerGraph.contains(nodeLabel)) {
					nodesPerGraph.add(nodeLabel);

					float[] freqs = (float[]) nodeFrequencies.get(nodeLabel);

					if (freqs == null) {
						nodeFrequencies.put(new MutableInteger(nodeLabel.intValue()),
								((g instanceof ClassifiedGraph) ? ((ClassifiedGraph) g).getClassFrequencies()	: TEMP).clone());
					} else {
						for (int k = 0; k < freqs.length; k++) {
							freqs[k] += ((g instanceof ClassifiedGraph) ? ((ClassifiedGraph) g).getClassFrequencies() : TEMP)[k];
						}
					}
				}
			}

			//以遍历的方式得到每个图的顶点标签及其frequency
			edgesPerGraph.clear();
			for (int i = g.getEdgeCount() - 1; i >= 0; i--) {
				edge.edgeLabel = g.getEdgeLabel(g.getEdge(i));



				edge.nodeALabel = g.getNodeLabel(g.getNodeA(g.getEdge(i)));
				edge.nodeBLabel = g.getNodeLabel(g.getNodeB(g.getEdge(i)));

				if (!edgesPerGraph.contains(edge)) {
					edgesPerGraph.add(edge);

					float[] freqs = (float[]) edgeFrequencies.get(edge);

					if (freqs == null) {
						edgeFrequencies.put(new GraphEdge(edge),
								((g instanceof ClassifiedGraph) ? ((ClassifiedGraph) g).getClassFrequencies()	: TEMP).clone());
					} else {
						for (int k = 0; k < freqs.length; k++) {
							freqs[k] += ((g instanceof ClassifiedGraph) ? ((ClassifiedGraph) g).getClassFrequencies() : TEMP)[k];
						}
					}
				}
			}//end of for (int i = g.getEdgeCount() - 1; i >= 0; i--)
		}

		// remove infrequent nodes
		for (Iterator it = nodeFrequencies.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();

			float[] freqs = (float[]) entry.getValue();
			if (! m_settings.checkMinimumFrequencies(freqs)) {
				entry.setValue(null);
			}
		}

		// remove infrequent edges
		int frequentEdgeCount = 0;
		for (Iterator it = edgeFrequencies.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();

			float[] freqs = (float[]) entry.getValue();
			if (! m_settings.checkMinimumFrequencies(freqs)) {
				entry.setValue(null);
			} else {
				frequentEdgeCount++;
			}
		}

		//顶点标签  和 其matrix
		final HashMap oneNodeMatrices = new HashMap();

		for (Iterator it = m_graphs.iterator(); it.hasNext();) {
			final Graph g = (Graph) it.next();

			nodesPerGraph.clear();
			for (int i = g.getNodeCount() - 1; i >= 0; i--) {
				nodeLabel.setValue(g.getNodeLabel(g.getNode(i)));
				if (nodeFrequencies.get(nodeLabel) == null) continue;

				Matrix matrix = (Matrix) oneNodeMatrices.get(nodeLabel);
				if (!nodesPerGraph.contains(nodeLabel)) {
					nodesPerGraph.add(nodeLabel);

					if (matrix == null) {
						matrix = new Matrix(nodeLabel.intValue(), new EmbeddingList());
						oneNodeMatrices.put(new MutableInteger(nodeLabel.intValue()), matrix);
					}
					matrix.addSupportedGraph(g);
				}

				matrix.getEmbeddings().addEmbedding(new Embedding(g, g.getNode(i)));
			}
		}


		m_oneNodeMatrices = new ArrayList(oneNodeMatrices.values().size());
		for (Iterator it = oneNodeMatrices.values().iterator(); it.hasNext();) {
			final Matrix matrix = (Matrix) it.next();

			m_oneNodeMatrices.add(matrix);
			matrix.getEmbeddings().sort();
		}

		m_frequentEdges = new GraphEdge[frequentEdgeCount];
		int count = 0;
		for (Iterator it = edgeFrequencies.entrySet().iterator(); it.hasNext();) {
			final Map.Entry e = (Map.Entry) it.next();

			if (e.getValue() != null) {
				m_frequentEdges[count++] = (GraphEdge) e.getKey();
			}
		}
	}


	/**
	 * The main method for starting the main. Call it with <code>--help</code> as first argument and you will get a list
	 * of all options.
	 *
	 * @param args command line arguments
	 * @throws Exception if anything goes wrong
	 */
	public static void main(String[] args) throws Exception {
		if ((args.length == 0) || args[0].equals("--help")) {
			System.out.println("Usage: " + Miner.class.getName() + " options, where options are:\n");
			Settings.printUsage();
			System.exit(1);
		}

		Settings s = new Settings(args);
		if (s.directedSearch) {
			System.out.println(Miner.class.getName()+" does not implement the search for directed graphs");
			System.exit(1);
		}
		Miner m = new Miner(s);

		m.setUp();
		m.startMining();
		m.printFrequentSubgraphs();
	}
}