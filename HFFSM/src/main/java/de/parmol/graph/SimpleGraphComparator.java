/*
 * Created on Jun 14, 2004
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

import java.util.Comparator;

import de.parmol.util.Math;


/**
 * This comparator compares two graphs by doing a complete isomorphism test.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class SimpleGraphComparator implements Comparator {
	protected final GraphNodeComparator m_nodeComparator;
	protected final GraphEdgeComparator m_edgeComparator;

	/**
	 * A public instance of this comparator using a SimpleNodeComparator and a SimpleEdgeComparator, respectively.
	 */
	public final static SimpleGraphComparator instance = new SimpleGraphComparator(SimpleNodeComparator.instance,
			SimpleEdgeComparator.instance);


	/**
	 * Create a new SimpleGraphComparator
	 * 
	 * @param nodeComparator a Comparator that compares Nodes
	 * @param edgeComparator a Comparator that compares Edges
	 */
	public SimpleGraphComparator(GraphNodeComparator nodeComparator, GraphEdgeComparator edgeComparator) {
		m_nodeComparator = nodeComparator;
		m_edgeComparator = edgeComparator;
	}


	/**
	 * Checks if the two given graphs are isomorph. To speed up the tests the passed node partititions are used.
	 * 
	 * @param g1 the first graph
	 * @param partitions1 an array of node partition number: the value at index <i>i </i> is the number of the partition
	 *          in which node <i>i </i> is in
	 * @param g2 the second graph
	 * @param partitions2 an array of the node partitions of the second graph
	 * @return <code>0</code> if the two graphs are equal, any other value otherwise
	 */
	public int compare(Graph g1, int[] partitions1, Graph g2, int[] partitions2) {
		if (g1.getNodeCount() != g2.getNodeCount()) return -1;
		if (g1.getEdgeCount() != g2.getEdgeCount()) return -1;

		/*
		 * if (g1.getNodeCount() > 20) { if (partitions1 == null) partitions1 = Util.getNodePartitions(g1); if (partitions2 ==
		 * null) partitions2 = Util.getNodePartitions(g2); }
		 */
		if ((partitions1 != null) && (partitions2 != null)) {
			int partCount1 = 0;
			for (int i = 0; i < partitions1.length; i++) {
				partCount1 = (partitions1[i] > partCount1) ? partitions1[i] : partCount1;
			}

			int partCount2 = 0;
			for (int i = 0; i < partitions2.length; i++) {
				partCount2 = (partitions2[i] > partCount2) ? partitions2[i] : partCount2;
			}

			if (partCount1 != partCount2) return -1;
		} else {
			partitions1 = partitions2 = null;
		}

		int[][] map = new int[2][g1.getNodeCount()];
		for (int i = 0; i < map[0].length; i++)
			map[0][i] = -1;
		for (int i = 0; i < map[1].length; i++)
			map[1][i] = -1;


		int[] g1Nodes = new int[g1.getNodeCount()];
		for (int i = 0; i < g1Nodes.length; i++) {
			g1Nodes[i] = g1.getNode(i);
		}

		int[] g2Nodes = new int[g2.getNodeCount()];
		for (int i = 0; i < g2Nodes.length; i++) {
			g2Nodes[i] = g2.getNode(i);
		}


		if (getIsomorphism(new int[][] { g1Nodes, g2Nodes }, map, g1, g2, partitions1, partitions2, new boolean[g1
				.getNodeCount()], 0)) return 0;

		return -1;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		return compare((Graph) o1, null, (Graph) o2, null);
	}


	protected boolean getIsomorphism(int[][] nodes, int[][] map, Graph g1, Graph g2, int[] parts1, int[] parts2,
			boolean[] used, int count) {
		if (count >= nodes[0].length) return true;

		for (int i = 0; i < nodes[1].length; i++) {

			if (!used[i] && canMatch(g1, nodes[0][count], g2, nodes[1][i], parts1, parts2, map, count - 1)) {
				map[0][count] = nodes[0][count];
				map[1][count] = nodes[1][i];
				used[i] = true;

				if (getIsomorphism(nodes, map, g1, g2, parts1, parts2, used, count + 1)) return true;

				map[0][count] = -1;
				map[1][count] = -1;
				used[i] = false;
			}
		}

		return false;
	}


	protected boolean canMatch(Graph g1, int nodeA, Graph g2, int nodeB, int[] parts1, int[] parts2, int[][] map,
			int count) {
		if (m_nodeComparator.compare(g1, nodeA, g2, nodeB) != 0) return false;

		if (parts1 != null) {
			if (parts1[g1.getNodeIndex(nodeA)] != parts2[g2.getNodeIndex(nodeB)]) return false;
		}

		for (int i = count; i >= 0; i--) {
			int edge1 = g1.getEdge(nodeA, map[0][i]);
			int edge2 = g2.getEdge(nodeB, map[1][i]);

			if ((edge1 == Graph.NO_EDGE) ^ (edge2 == Graph.NO_EDGE)) {
				return false;
			} else if ((edge1 != Graph.NO_EDGE) && (m_edgeComparator.compare(g1, edge1, g2, edge2) != 0)) { return false; }


			// if the both graphs are directed also check edges going into the other direction
			if ((g1 instanceof DirectedGraph) && (g2 instanceof DirectedGraph)) {
				edge1 = g1.getEdge(map[0][i], nodeA);
				edge2 = g2.getEdge(map[1][i], nodeB);

				if ((edge1 == Graph.NO_EDGE) ^ (edge2 == Graph.NO_EDGE)) {
					return false;
				} else if ((edge1 != Graph.NO_EDGE) && (m_edgeComparator.compare(g1, edge1, g2, edge2) != 0)) { return false; }
			}
		}

		return true;
	}


	/**
	 * Returns a hash code for the given graph.
	 * 
	 * @param g a graph
	 * @return a hash code for the graph
	 */
	public static int getHashCode(Graph g) {
		int code = (g.getNodeCount() << 24) ^ (g.getEdgeCount() << 16);
		for (int i = g.getNodeCount() - 1; i >= 0; i--) {
			code ^= g.getDegree(i) << (g.getNodeLabel(g.getNode(i)) % 29);
		}

		for (int i = g.getEdgeCount() - 1; i >= 0; i--) {
			code ^= Math.PRIMES[g.getEdgeLabel(g.getEdge(i)) % Math.PRIMES.length];
		}

		return code;
	}
}