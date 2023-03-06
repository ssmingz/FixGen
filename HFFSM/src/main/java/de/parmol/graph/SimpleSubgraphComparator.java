/*
 * 
 * This file is part of ParMol. ParMol is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 * 
 * ParMol is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * ParMol; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 * 
 */
package de.parmol.graph;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import de.parmol.util.MutableInteger;


/**
 * This comparator checks if a graph is a subgraph of another graph by doing a simple subgraph isomorphism test.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class SimpleSubgraphComparator implements Comparator {
	protected final SubgraphNodeComparator m_nodeComparator;
	protected final SubgraphEdgeComparator m_edgeComparator;

	/**
	 * A public instance of this comparator using a SimpleNodeComparator and a SimpleEdgeComparator, respectively.
	 */
	public static final SimpleSubgraphComparator instance = new SimpleSubgraphComparator(SimpleNodeComparator.instance,
			SimpleEdgeComparator.instance);


	/**
	 * Create a new SimpleSubgraphTester
	 * 
	 * @param nodeComparator a Comparator that compares Nodes
	 * @param edgeComparator a Comparator that compares Edges
	 */
	public SimpleSubgraphComparator(SubgraphNodeComparator nodeComparator, SubgraphEdgeComparator edgeComparator) {
		m_nodeComparator = nodeComparator;
		m_edgeComparator = edgeComparator;
	}


	/**
	 * Checks if the first graph is a subgraph of the second graph.
	 * 
	 * @param o1 the first graph
	 * @param o2 the second graph
	 * @return <code>0</code> if there is a subgraph isomorphism between the two graphs, <code>-1</code> otherwise
	 *  
	 */
	public int compare(Object o1, Object o2) {
		Graph subgraph = (Graph) o1;
		Graph supergraph = (Graph) o2;


		if ((subgraph.getEdgeCount() > supergraph.getEdgeCount()) && (subgraph.getNodeCount() < supergraph.getNodeCount()))
				return -1;
		if ((subgraph.getEdgeCount() < supergraph.getEdgeCount()) && (subgraph.getNodeCount() > supergraph.getNodeCount()))
				return -1;

		int[][] map = new int[2][subgraph.getNodeCount()];
		for (int i = 0; i < map[0].length; i++)
			map[0][i] = -1;
		for (int i = 0; i < map[1].length; i++)
			map[1][i] = -1;

		int[] subgraphNodes = new int[subgraph.getNodeCount()];
		for (int i = 0; i < subgraphNodes.length; i++) {
			subgraphNodes[i] = subgraph.getNode(i);
		}

		int[] supergraphNodes = new int[supergraph.getNodeCount()];
		for (int i = 0; i < supergraphNodes.length; i++) {
			supergraphNodes[i] = supergraph.getNode(i);
		}


		if (getSubgraphIsomorphism(new int[][] { subgraphNodes, supergraphNodes }, map, subgraph, supergraph,
				new boolean[supergraph.getNodeCount()], 0)) return 0;

		return -1;
	}


	protected boolean getSubgraphIsomorphism(int[][] nodes, int[][] map, Graph subgraph, Graph supergraph,
			boolean[] used, int count) {
		if (count >= nodes[0].length) return true;

		for (int i = 0; i < nodes[1].length; i++) {

			if (!used[i] && canMatch(subgraph, nodes[0][count], supergraph, nodes[1][i], map, count - 1)) {
				map[0][count] = nodes[0][count];
				map[1][count] = nodes[1][i];
				used[i] = true;

				if (getSubgraphIsomorphism(nodes, map, subgraph, supergraph, used, count + 1)) return true;

				map[0][count] = -1;
				map[1][count] = -1;
				used[i] = false;
			}
		}

		return false;
	}


	protected boolean canMatch(Graph subgraph, int nodeA, Graph supergraph, int nodeB, int[][] map, int count) {
		if (m_nodeComparator.compare(subgraph, nodeA, supergraph, nodeB) != 0) return false;

		// if the node in the subgraph has more edges it cannot be mapped onto the supergraph node
		if (subgraph.getDegree(nodeA) > supergraph.getDegree(nodeB)) return false;
		for (int i = count; i >= 0; i--) {
			int edge1 = subgraph.getEdge(nodeA, map[0][i]);
			int edge2 = supergraph.getEdge(nodeB, map[1][i]);

			if ((edge1 != Graph.NO_EDGE) && (edge2 == Graph.NO_EDGE)) {
				return false;
			} else if ((edge1 != Graph.NO_EDGE) && (m_edgeComparator.compare(subgraph, edge1, supergraph, edge2) != 0)) { return false; }

			// if the both graphs are directed also check edges going into the other direction
			if ((subgraph instanceof DirectedGraph) && (supergraph instanceof DirectedGraph)) {
				edge1 = subgraph.getEdge(map[0][i], nodeA);
				edge2 = supergraph.getEdge(map[1][i], nodeB);

				if ((edge1 != Graph.NO_EDGE) && (edge2 == Graph.NO_EDGE)) {
					return false;
				} else if ((edge1 != Graph.NO_EDGE) && (m_edgeComparator.compare(subgraph, edge1, supergraph, edge2) != 0)) {
					return false;
				}
			}
		}

		return true;
	}


	/**
	 * Returns the number of embeddings the given subgraph has in all graphs.
	 * @param subgraph a subgraph
	 * @param graphs a collection of graphs.
	 * @return the number of embeddings
	 */
	public int getEmbeddingCounter(Graph subgraph, Collection graphs) {
		int counter = 0;
		for (Iterator it = graphs.iterator(); it.hasNext();) {
			final Graph g = (Graph) it.next();
			counter += getEmbeddingCount(subgraph, g);
		}

		return counter;
	}


	/**
	 * Returns the number of embeddings the subgraph has in the supergraph.
	 * @param subgraph a subgraph
	 * @param supergraph a supergraph
	 * @return the number of embeddings
	 */
	public int getEmbeddingCount(Graph subgraph, Graph supergraph) {
		if ((subgraph.getEdgeCount() > supergraph.getEdgeCount()) && (subgraph.getNodeCount() < supergraph.getNodeCount()))
				return 0;
		if ((subgraph.getEdgeCount() < supergraph.getEdgeCount()) && (subgraph.getNodeCount() > supergraph.getNodeCount()))
				return 0;

		int[][] map = new int[2][subgraph.getNodeCount()];
		for (int i = 0; i < map[0].length; i++)
			map[0][i] = -1;
		for (int i = 0; i < map[1].length; i++)
			map[1][i] = -1;

		int[] subgraphNodes = new int[subgraph.getNodeCount()];
		for (int i = 0; i < subgraphNodes.length; i++) {
			subgraphNodes[i] = subgraph.getNode(i);
		}

		int[] supergraphNodes = new int[supergraph.getNodeCount()];
		for (int i = 0; i < supergraphNodes.length; i++) {
			supergraphNodes[i] = supergraph.getNode(i);
		}

		MutableInteger counter = new MutableInteger(0);
		getEmbeddingCount(new int[][] { subgraphNodes, supergraphNodes }, map, subgraph, supergraph, new boolean[supergraph
				.getNodeCount()], 0, counter);
		return counter.intValue();
	}


	private void getEmbeddingCount(int[][] nodes, int[][] map, Graph subgraph, Graph supergraph, boolean[] used,
			int count, MutableInteger counter) {
		if (count >= nodes[0].length) {
			counter.inc();
			return;
		}

		for (int i = 0; i < nodes[1].length; i++) {

			if (!used[i] && canMatch(subgraph, nodes[0][count], supergraph, nodes[1][i], map, count - 1)) {
				map[0][count] = nodes[0][count];
				map[1][count] = nodes[1][i];
				used[i] = true;

				getEmbeddingCount(nodes, map, subgraph, supergraph, used, count + 1, counter);

				map[0][count] = -1;
				map[1][count] = -1;
				used[i] = false;
			}
		}
	}
}

