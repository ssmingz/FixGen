/*
 * Created on Aug 13, 2004
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.parmol.parsers.SLNParser;
import de.parmol.util.ExtendedComparator;
import de.parmol.util.Math;
import de.parmol.util.MutableInteger;

/**
 * This class contains several static function that do various computations on graphs, e.g.
 * determining connected components, finding shortest paths, etc.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class Util {
	/**
	 * This exception is thrown if a graph contains more cycles than can be marked.
	 * 
	 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
	 */
	public static class TooManyCyclesException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1398762904135220214L;


		/**
		 * Creates a new TooManyCyclesException which states that the graph has more than MAX_CYCLES
		 * minimal cycles.
		 * 
		 * @param message a message describing the execption
		 */
		public TooManyCyclesException(String message) {
			super(message);
		}


		/**
		 * Returns the maximum number of cycles that can be marked in a graph.
		 * 
		 * @return the maximum number of cycles
		 */
		public int getMaxCycles() {
			return 0;
		}
	}


	/**
	 * Determines the connected components in the given graph.
	 * 
	 * @param graph a graph
	 * @return a two-dimensional array in which the first dimension contains the connected components
	 *         and the second dimension the nodes in each of the components, i.e.
	 *         <code>getConnectedComponents(graph)[1][0]</code> is the first node in the second
	 *         connected component.
	 */
	public static int[][] getConnectedComponents(Graph graph) {
		final int[] compNumber = new int[graph.getNodeCount()], nodeCount = new int[graph.getNodeCount() + 1];
		final MutableInteger count = new MutableInteger(0);

		GraphSearcher searcher = new GraphSearcher() {
			public boolean enteredNode(Graph g, int node) {
				compNumber[g.getNodeIndex(node)] = count.intValue();
				nodeCount[count.intValue()]++;
				return true;
			}
		};

		for (int i = graph.getNodeCount() - 1; i >= 0; i--) {
			if (compNumber[graph.getNodeIndex(graph.getNode(i))] == 0) {
				count.inc();
				searcher.bfs(graph, graph.getNode(i));
			}
		}

		int[][] retVal = new int[count.intValue()][];
		for (int i = retVal.length - 1; i >= 0; i--) {
			retVal[i] = new int[nodeCount[i + 1]];
		}

		for (int i = graph.getNodeCount() - 1; i >= 0; i--) {
			int cc = compNumber[graph.getNodeIndex(graph.getNode(i))];

			retVal[cc - 1][--nodeCount[cc]] = graph.getNode(i);
		}

		return retVal;
	}


	/**
	 * Finds the shortest path in the given graph between the two nodes. If the graph is undirected
	 * the order of the nodes does not matter, if it is directed it matters of course.
	 * 
	 * @param graph the graph
	 * @param startNode the source node
	 * @param endNode the destination node
	 * @return an array with nodes in which the first entry is the length of the path, the second
	 *         entry is the source node, the last entry the destination node and all node between them
	 *         form the shortest path from the source to the destination
	 */
	public static int[] findShortestPath(Graph graph, int startNode, int endNode) {
		boolean[] nodeVisited = new boolean[graph.getNodeCount()];
		int[] distance = new int[nodeVisited.length];
		int[] parent = new int[nodeVisited.length];

		for (int k = 0; k < nodeVisited.length; k++) {
			nodeVisited[k] = false;
			distance[k] = Integer.MAX_VALUE;
			parent[k] = Graph.NO_NODE;
		}

		int currentNode = startNode;
		int currentNodeIndex = graph.getNodeIndex(currentNode);
		distance[currentNodeIndex] = 0;

		while ((currentNode != endNode) && !nodeVisited[currentNodeIndex]) {
			nodeVisited[currentNodeIndex] = true;

			for (int i = graph.getDegree(currentNode) - 1; i >= 0; i--) {
				int edge = graph.getNodeEdge(currentNode, i);

				int neighbourNode = graph.getOtherNode(edge, currentNode);
				int neighbourNodeIndex = graph.getNodeIndex(neighbourNode);

				if (distance[neighbourNodeIndex] > (distance[currentNodeIndex] + graph.getEdgeLabel(edge))) {
					distance[neighbourNodeIndex] = distance[currentNodeIndex] + graph.getEdgeLabel(edge);
					parent[neighbourNodeIndex] = currentNode;
				}
			}

			// get the next node
			currentNode = Graph.NO_NODE;
			int dist = Integer.MAX_VALUE;
			for (int i = 0; i < nodeVisited.length; i++) {
				if (!nodeVisited[i] && (dist > distance[i])) {
					dist = distance[i];
					currentNode = graph.getNode(i);
				}
			}
		}

		int node = endNode, count = 2;
		while (node != startNode) {
			count++;
			node = parent[graph.getNodeIndex(node)];
		}

		int[] shortestPath = new int[count];

		node = endNode;
		while (node != startNode) {
			shortestPath[--count] = node;
			node = parent[graph.getNodeIndex(node)];
		}
		shortestPath[--count] = startNode;
		shortestPath[--count] = distance[graph.getNodeIndex(endNode)];

		return shortestPath;
	}


	/**
	 * Partitions the nodes in the given graph using iterative partitioning.
	 * 
	 * @param g a gaph
	 * @return an array in which at index <code>i</code> the number of the partition in which node
	 *         <code>i</code> is stands
	 */
	public static int[] getNodePartitions(Graph g) {
		/*
		 * if the nodes are in three partitions that look like this 0 1 2 3 4 5 6 7 8 (the numbers are
		 * the indexes in nodes[]) then boundaries[] will look like this { -1, 3, 6, 8, 0, 0, 0, 0, 0, 0 }
		 * and partitions is 3.
		 */
		final int[] nodes = new int[g.getNodeCount()];
		int[] boundaries = new int[nodes.length + 1]; // the end index of each partition is at
																									// boundaries[partition]
		int[] partitionNumbers = new int[nodes.length]; // stores the number of the partion a node is in
		int partitions = 1;

		final FirstNodeComparator comp1 = new FirstNodeComparator(g);
		final SecondNodeComparator comp2 = new SecondNodeComparator(g, partitionNumbers);

		// create partitions based on degree, edge labels and neighbour node labels
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = g.getNode(i);
		}
		de.parmol.Util.quickSort(nodes, comp1);

		boundaries[0] = -1;
		partitionNumbers[g.getNodeIndex(nodes[0])] = 1;
		for (int i = 1; i < nodes.length; i++) {
			if (comp1.compare(nodes[i - 1], nodes[i]) != 0) {
				boundaries[partitions++] = i - 1;
			}
			partitionNumbers[g.getNodeIndex(nodes[i])] = partitions;
		}
		boundaries[partitions] = nodes.length - 1;

		// iteratively create partitions based on the neighbour nodes' partitions
		boolean changed = false;
		int[] newBoundaries = new int[boundaries.length];
		int[] newPartitionNumbers = new int[nodes.length];
		int newPartitions;
		do {
			newPartitions = 0;
			newBoundaries[0] = -1;
			for (int i = 1; i <= partitions; i++) {
				de.parmol.Util.quickSort(nodes, boundaries[i - 1] + 1, boundaries[i], comp2);

				newPartitions++;
				newPartitionNumbers[g.getNodeIndex(nodes[boundaries[i - 1] + 1])] = newPartitions;
				for (int k = boundaries[i - 1] + 2; k <= boundaries[i]; k++) {
					if (comp2.compare(nodes[k - 1], nodes[k]) != 0) {
						newBoundaries[newPartitions++] = k - 1;
					}
					newPartitionNumbers[g.getNodeIndex(nodes[k])] = newPartitions;
				}
				newBoundaries[newPartitions] = boundaries[i];
			}

			changed = (newPartitions != partitions);

			partitions = newPartitions;
			int[] temp = partitionNumbers;
			partitionNumbers = newPartitionNumbers;
			newPartitionNumbers = temp;
			comp2.setPartitionNumber(partitionNumbers);
			temp = boundaries;
			boundaries = newBoundaries;
			newBoundaries = temp;
		} while (changed && (partitions < nodes.length));

		// // create the return value
		// int[][] retVal = new int[partitions][];
		// for (int i = 0; i < partitions; i++) {
		// retVal[i] = new int[boundaries[i + 1] - boundaries[i]];
		//			
		// for (int k = 0; k < retVal[i].length; k++) {
		// retVal[i][k] = nodes[boundaries[i] + k + 1];
		// }
		// }
		//		
		// return retVal;
		return partitionNumbers;
	}

	private static class FirstNodeComparator extends ExtendedComparator {
		private final Graph m_graph;


		FirstNodeComparator(Graph graph) {
			m_graph = graph;
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see de.parmol.util.ExtendedComparator#compare(int, int)
		 */
		public int compare(int node1, int node2) {
			int diff = m_graph.getNodeLabel(node1) - m_graph.getNodeLabel(node2);
			if (diff != 0) return diff;

			diff = m_graph.getDegree(node1) - m_graph.getDegree(node2);
			if (diff != 0) return diff;

			int edgeProd1 = 1, edgeProd2 = 1;
			for (int i = m_graph.getDegree(node1) - 1; i >= 0; i--) {
				int edge = m_graph.getNodeEdge(node1, i);
				edgeProd1 *= de.parmol.util.Math.PRIMES[m_graph.getEdgeLabel(edge) % de.parmol.util.Math.PRIMES.length];

				edge = m_graph.getNodeEdge(node2, i);
				edgeProd2 *= de.parmol.util.Math.PRIMES[m_graph.getEdgeLabel(edge) % de.parmol.util.Math.PRIMES.length];
			}
			diff = edgeProd1 - edgeProd2;
			if (diff != 0) return diff;

			int neighbourProd1 = 1, neighbourProd2 = 1;
			for (int i = m_graph.getDegree(node1) - 1; i >= 0; i--) {
				int neighbour = m_graph.getOtherNode(m_graph.getNodeEdge(node1, i), node1);
				neighbourProd1 *= de.parmol.util.Math.PRIMES[m_graph.getNodeLabel(neighbour) % de.parmol.util.Math.PRIMES.length];

				neighbour = m_graph.getOtherNode(m_graph.getNodeEdge(node2, i), node2);
				neighbourProd2 *= de.parmol.util.Math.PRIMES[m_graph.getNodeLabel(neighbour) % de.parmol.util.Math.PRIMES.length];
			}

			return neighbourProd1 - neighbourProd2;
		}
	}

	private static class SecondNodeComparator extends ExtendedComparator {
		private final Graph m_graph;
		private int[] m_partitionNumbers;


		SecondNodeComparator(Graph graph, int[] partitionNumber) {
			m_graph = graph;
			m_partitionNumbers = partitionNumber;
		}


		void setPartitionNumber(int[] newPartitionsNumbers) {
			m_partitionNumbers = newPartitionsNumbers;
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see de.parmol.util.ExtendedComparator#compare(int, int)
		 */
		public int compare(int node1, int node2) {
			int prod1 = 1, prod2 = 1;

			for (int i = m_graph.getDegree(node1) - 1; i >= 0; i--) {
				int neighbour = m_graph.getOtherNode(m_graph.getNodeEdge(node1, i), node1);
				prod1 *= de.parmol.util.Math.PRIMES[m_partitionNumbers[m_graph.getNodeIndex(neighbour)] % de.parmol.util.Math.PRIMES.length];
			}

			for (int i = m_graph.getDegree(node2) - 1; i >= 0; i--) {
				int neighbour = m_graph.getOtherNode(m_graph.getNodeEdge(node2, i), node2);
				prod2 *= de.parmol.util.Math.PRIMES[m_partitionNumbers[m_graph.getNodeIndex(neighbour)] % de.parmol.util.Math.PRIMES.length];
			}

			return prod1 - prod2;
		}
	}


	/**
	 * Finds all edges in the given graph that are bridges. A bridge is an edge whose removal will
	 * create a new connected component, i.e. the graph will break apart.
	 * 
	 * @param graph a graph
	 * @return an array of all edges that are bridges
	 */
	public static int[] getBridges(Graph graph) {
		if (graph.getNodeCount() < 2) { return new int[0]; }

		int[] dfsIndex = new int[graph.getNodeCount()];
		int[] lowMark = new int[graph.getNodeCount()];
		int[] edgeMarker = new int[graph.getEdgeCount()];

		int bridgeCount = 0;
		for (int i = graph.getNodeCount() - 1; i >= 0; i--) {
			if (dfsIndex[i] == UNVISITED) {
				bridgeCount += getBridges(graph, graph.getNode(i), dfsIndex, lowMark, edgeMarker, new MutableInteger(0));
			}
		}

		int[] bridges = new int[bridgeCount];

		for (int i = 0; i < edgeMarker.length; i++) {
			if (edgeMarker[i] == BRIDGE) {
				bridges[--bridgeCount] = graph.getEdge(i);
			}
		}

		return bridges;
	}

	private final static int UNVISITED = 0, VISITED = 1, BRIDGE = 2, BACKEDGE = 3;


	/**
	 * Recursively determines the bridges in the graph.
	 * 
	 * @param graph a graph
	 * @param node the current node in the dfs search
	 * @param dfsIndex the array with the dfs indices of the nodes
	 * @param lowMark the array with the low marks of the nodes
	 * @param edgeMarker the array with the edge markers
	 * @param count the current node counter (for the dfs index)
	 * @return the number of bridges found so far
	 */
	private static int getBridges(Graph graph, int node, int[] dfsIndex, int[] lowMark, int[] edgeMarker, MutableInteger count) {
		final int nodeIndex = graph.getNodeIndex(node);
		dfsIndex[nodeIndex] = count.inc();
		lowMark[nodeIndex] = dfsIndex[nodeIndex];

		int bridgeCount = 0;

		for (int i = graph.getDegree(node) - 1; i >= 0; i--) {
			final int edge = graph.getNodeEdge(node, i);
			final int edgeIndex = graph.getEdgeIndex(edge);

			final int neighbour = graph.getOtherNode(edge, node);
			final int neighbourIndex = graph.getNodeIndex(neighbour);
			if (dfsIndex[neighbourIndex] == UNVISITED) {
				edgeMarker[edgeIndex] = VISITED;

				bridgeCount += getBridges(graph, neighbour, dfsIndex, lowMark, edgeMarker, count);

				if (lowMark[neighbourIndex] < lowMark[nodeIndex]) {
					lowMark[nodeIndex] = lowMark[neighbourIndex];
				}

				if (lowMark[neighbourIndex] == dfsIndex[neighbourIndex]) {
					edgeMarker[edgeIndex] = BRIDGE;
					bridgeCount++;
				}
			} else if (edgeMarker[edgeIndex] == UNVISITED) {
				edgeMarker[edgeIndex] = BACKEDGE;

				if (lowMark[neighbourIndex] < lowMark[nodeIndex]) {
					lowMark[nodeIndex] = lowMark[neighbourIndex];
				}
			}
		}

		return bridgeCount;
	}


	/**
	 * Creates a new graph that is a subgraph of the given one.
	 * 
	 * @param g the supergraph
	 * @param size the number of nodes the subgraph shall have; must be smaller or equal to the number
	 *          of nodes in the supergraph
	 * @param edgeDensity the density of edges in the subgraph in relation to the supergraph, a value
	 *          between 0.0 and 1.0
	 * @return a new subgraph of the same type as the supergraph
	 */
	public static Graph randomSubgraph(Graph g, int size, double edgeDensity) {
		if (size > g.getNodeCount()) throw new IllegalArgumentException("The subgraph cannot be greater than the original graph");

		MutableGraph sg;
		try {
			sg = (MutableGraph) g.getClass().newInstance();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
			return null;
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
			return null;
		}

		int[] nodes = new int[g.getNodeCount()];
		for (int i = 0; i < nodes.length; i++)
			nodes[i] = g.getNode(i);
		nodes = de.parmol.Util.shuffle(nodes);

		int[] map = new int[nodes.length];
		for (int i = 0; i < map.length; i++)
			map[i] = Graph.NO_NODE;
		for (int i = size - 1; i >= 0; i--) {
			int node = sg.addNode(g.getNodeLabel(nodes[i]));
			map[g.getNodeIndex(nodes[i])] = node;
		}

		for (int i = g.getEdgeCount() - 1; i >= 0; i--) {
			int nodeA = g.getNodeA(g.getEdge(i));
			int nodeB = g.getNodeB(g.getEdge(i));

			if ((map[g.getNodeIndex(nodeA)] != Graph.NO_NODE) && (map[g.getNodeIndex(nodeB)] != Graph.NO_NODE)
					&& (java.lang.Math.random() <= edgeDensity)) {
				sg.addEdge(map[g.getNodeIndex(nodeA)], map[g.getNodeIndex(nodeB)], g.getEdgeLabel(g.getEdge(i)));
			}
		}
		return sg;
	}


	/**
	 * Shuffles the nodes in the given graph and returns a new isomorphic graph.
	 * 
	 * @param g the graph whose nodes shall be shuffled
	 * @return a new isomorphic graph
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static MutableGraph shuffleGraph(Graph g) throws InstantiationException, IllegalAccessException {
		int[] nodes = new int[g.getNodeCount()];
		for (int i = 0; i < nodes.length; i++)
			nodes[i] = g.getNode(i);

		int[] edges = new int[g.getEdgeCount()];
		for (int i = 0; i < edges.length; i++)
			edges[i] = g.getEdge(i);

		nodes = de.parmol.Util.shuffle(nodes);
		edges = de.parmol.Util.shuffle(edges);

		MutableGraph g2 = (MutableGraph) g.getClass().newInstance();
		int[] newNodes = new int[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			newNodes[g.getNodeIndex(nodes[i])] = g2.addNode(g.getNodeLabel(nodes[i]));
		}

		for (int i = edges.length - 1; i >= 0; i--) {
			int indexA = g.getNodeIndex(g.getNodeA(edges[i]));
			int indexB = g.getNodeIndex(g.getNodeB(edges[i]));

			g2.addEdge(newNodes[indexA], newNodes[indexB], g.getEdgeLabel(edges[i]));
		}

		return g2;
	}


	/**
	 * Finds all shortest cycles in the given graph whose length lies in the given range. Depeding on
	 * the last parameter the ids of cycles are resused in other parts of the graph or not (i.e. each
	 * graph has a unique id). The results is a three item array of arrays.
	 * This algorithms searches for undirect cycles even if the given graph is directed.
	 * <ul>
	 * <li>The first array contains a list of the cycle membership of all nodes. The value for each
	 * node is a bit mask of all cycles this node is a member of.</li>
	 * <li>The second array contains a list of the cycle membership of all edges. The value for each
	 * edge is a bit mask of all cycles this edge is a member of.</li>
	 * <li>The third array has only one item which is the number of discovered cycles.</li>
	 * </ul>
	 * 
	 * @param graph a graph
	 * @param minSize the minimum size in edges a cycle must have
	 * @param maxSize the maximum size in edges a cycle must have
	 * @param reuseCycleIDs <code>true</code> if non-adjacent cycles may have the same number (this
	 *          allows more cycles in a graph), <code>false</code> otherwise
	 * @return a 3-item array of arrays as described above
	 * @throws Util.TooManyCyclesException if the graph has more than MAX_CYCLES cycles
	 */
	public static int[][] getCycles(Graph graph, int minSize, int maxSize, boolean reuseCycleIDs) throws Util.TooManyCyclesException {
		final boolean[] nodeVisited = new boolean[graph.getNodeCount()];
		final int[] parent = new int[graph.getNodeCount()];
		final int[] distance = new int[graph.getNodeCount()];

		// iteratively remove nodes with degree <= 1
		// nodes which have a degree < 2 afterwards do not need to be checked by Dijkstra
		final int[] degree = new int[graph.getNodeCount()];
		for (int i = degree.length - 1; i >= 0; i--)
			degree[i] = graph.getDegree(graph.getNode(i));
		boolean change;
		do {
			change = false;
			for (int i = 0; i < degree.length; i++) {
				if (degree[i] == 1) {
					change = true;
					final int node = graph.getNode(i);
					degree[i] = 0;

					degree[graph.getNodeIndex(graph.getOtherNode(graph.getNodeEdge(node, 0), node))]--;
				}
			}
		} while (change);

		int ringCount = 0;
		final int[] edgeRingMembership = new int[graph.getEdgeCount()];

		for (int i = graph.getEdgeCount() - 1; i >= 0; i--) {
			final int edge = graph.getEdge(i);
			final int nodeA = graph.getNodeA(edge);
			if (degree[graph.getNodeIndex(nodeA)] < 2) continue;

			final int nodeB = graph.getNodeB(edge);
			if (degree[graph.getNodeIndex(nodeB)] < 2) continue;

			Util.shortestPath(graph, nodeA, nodeVisited, parent, distance, edge);

			if ((distance[graph.getNodeIndex(nodeB)] >= (minSize - 1)) && (distance[graph.getNodeIndex(nodeB)] <= (maxSize - 1))) {
				if (Util.markCycle(graph, nodeB, edge, parent, edgeRingMembership, reuseCycleIDs, ringCount)) ringCount++;
			}
		}

		final int[] nodeRingMembership = new int[graph.getNodeCount()];
		for (int i = graph.getNodeCount() - 1; i >= 0; i--) {
			final int node = graph.getNode(i);
			int mask = 0;
			for (int k = graph.getDegree(node) - 1; k >= 0; k--) {
				mask |= edgeRingMembership[graph.getEdgeIndex(graph.getNodeEdge(node, k))];
			}

			nodeRingMembership[graph.getNodeIndex(node)] = mask;
		}

		return new int[][] { nodeRingMembership, edgeRingMembership, new int[] { ringCount } };
	}


	/**
	 * Marks the edges of a found cycle. The cycle is given by the end node and the shortest path from
	 * the end node to the start node. The edges marked onyl if they are not already in a common
	 * cycle.
	 * 
	 * @param graph the graph
	 * @param endNode the end node of the cycle
	 * @param forbiddenEdge the forbidden edge i.e. the edge between the end and start node
	 * @param parent the shortest path list
	 * @param edgeRingMembership an array with an entry for each edge which contains bit masks for the
	 *          cycle membership of the edge
	 * @param reuseRingIDs <code>true</code> if non-adjacent cycles may have the same number (this
	 *          allows more cycles in a graph), <code>false</code> otherwise
	 * @param ringCount the number of rings discovered so far, only necessary if
	 *          <code>reuseRingIDs</code> is <code>false</code>
	 * @return <code>true</code> if a new cycle was marked, <code>false</code> otherwise
	 * @throws Util.TooManyCyclesException if the graph contains too many cycles to mark
	 */
	private static boolean markCycle(Graph graph, int endNode, int forbiddenEdge, int[] parent, int[] edgeRingMembership,
			boolean reuseRingIDs, int ringCount) throws Util.TooManyCyclesException {
		int ringMask = edgeRingMembership[graph.getEdgeIndex(forbiddenEdge)];
		int ringID = 0;

		int currentNode = endNode;
		// It may happen, that two rings have only a node in common but no edge. So the free ring id
		// calculation must look at
		// all edges adjacent to all ring nodes.
		for (int i = graph.getDegree(currentNode) - 1; i >= 0; i--) {
			int e = graph.getNodeEdge(currentNode, i);

			ringID |= edgeRingMembership[graph.getEdgeIndex(e)];
		}

		while (parent[currentNode] != -1) {
			int edge = graph.getEdge(currentNode, parent[currentNode]);
			if ((edge == Graph.NO_EDGE) && (graph instanceof DirectedGraph)) {
				// in a directed graph the edge may go into the other direction
				edge = ((DirectedGraph) graph).getEdge(parent[currentNode], currentNode);
			}
			ringMask &= edgeRingMembership[graph.getEdgeIndex(edge)];

			currentNode = parent[currentNode];

			// It may happen, that two rings have only a node in common but no edge. So the free ring id
			// calculation must look at
			// all edges adjacent to all ring nodes.
			for (int i = graph.getDegree(currentNode) - 1; i >= 0; i--) {
				int e = graph.getNodeEdge(currentNode, i);

				ringID |= edgeRingMembership[graph.getEdgeIndex(e)];
			}
		}

		if (ringMask == 0) {
			if ((ringID == 0xffffffff) || (ringCount > 32)) {
				throw new Util.TooManyCyclesException("The graph '" + SLNParser.instance.serialize(graph) + "' has too many cycles");
			} else if (reuseRingIDs) {
				for (int i = 0; i < 32; i++) {
					if ((ringID & (1 << i)) == 0) {
						ringID = i;
						break;
					}
				}
			} else {
				ringID = ringCount;
			}

			edgeRingMembership[graph.getEdgeIndex(forbiddenEdge)] |= (1 << ringID);
			currentNode = endNode;
			while (parent[currentNode] != -1) {
				int edge = graph.getEdge(currentNode, parent[currentNode]);
				if ((edge == Graph.NO_EDGE) && (graph instanceof DirectedGraph)) {
					// in a directed graph the edge may go into the other direction
					edge = ((DirectedGraph) graph).getEdge(parent[currentNode], currentNode);
				}
				edgeRingMembership[graph.getEdgeIndex(edge)] |= (1 << ringID);
				currentNode = parent[currentNode];
			}
			return true;
		}

		return false;
	}


	/**
	 * Searches for the shortest path for the start node to any other node in the graph by using
	 * Dijkstras algorithm with a slight modification: The forbidden edge is not considered. Edges are treated as undirected even if the graph is directed.
	 * 
	 * @param graph the graph
	 * @param startNode the start node for which the shortest paths should be determined
	 * @param nodeVisited a boolean array with graph.getNodeCount() elements
	 * @param parent an int array with graph.getNodeCount() elements that holds the parent information
	 *          for the shortest paths after the search
	 * @param distance an int array with graph.getNodeCount() elements that holds the shortest
	 *          distances afterwards
	 * @param forbiddenEdge the forbidden edge
	 */
	private static void shortestPath(Graph graph, int startNode, boolean[] nodeVisited, int[] parent, int[] distance, int forbiddenEdge) {
		for (int k = 0; k < nodeVisited.length; k++) {
			nodeVisited[k] = false;
			distance[k] = Integer.MAX_VALUE;
			parent[k] = -1;
		}

		int currentNode = startNode;
		int currentNodeIndex = graph.getNodeIndex(currentNode);
		distance[graph.getNodeIndex(startNode)] = 0;

		while (!nodeVisited[currentNodeIndex]) {
			nodeVisited[currentNodeIndex] = true;

			for (int i = graph.getDegree(currentNode) - 1; i >= 0; i--) {
				int edge = graph.getNodeEdge(currentNode, i);
				if (edge == forbiddenEdge) continue;

				int neighbourNode = graph.getOtherNode(edge, currentNode);
				int neighbourNodeIndex = graph.getNodeIndex(neighbourNode);

				if (distance[neighbourNodeIndex] > (distance[currentNodeIndex] + 1)) {
					distance[neighbourNodeIndex] = distance[currentNodeIndex] + 1;
					parent[neighbourNodeIndex] = currentNode;
				}
			}

			// get the next node
			currentNodeIndex = 0;
			int dist = Integer.MAX_VALUE;
			for (int i = 1; i < nodeVisited.length; i++) {
				if (!nodeVisited[i] && (dist > distance[i])) {
					dist = distance[i];
					currentNodeIndex = i;
				}
			}
			currentNode = graph.getNode(currentNodeIndex);
		}
	}


	/**
	 * Marks all edges in the given graph with the id of the ring they are part of.
	 * 
	 * @param graph the graph
	 * @param minSize the minimum ring size
	 * @param maxSize the maximum ring size
	 * @return the number of rings found
	 * @throws Util.TooManyCyclesException if the graph has more than MAX_CYCLES cycles
	 */
	public static int markCycles(Graph graph, int minSize, int maxSize) throws Util.TooManyCyclesException {
		int[][] result = Util.getCycles(graph, minSize, maxSize, true);

		for (int i = result[1].length - 1; i >= 0; i--) {
			for (int k = 0; k < 32; k++) {
				if ((result[1][i] & (1 << k)) != 0) {
					//graph.setEdgeRingMembership(i, k);
				}
			}
		}

		return result[2][0]; // return the ring count
	}


	public static int[] getLocalSymmetryPoints(Graph g) {
		final int[] syms = new int[g.getNodeCount()];

		for (int i = 0; i < g.getNodeCount(); i++) {
			final int node = g.getNode(i);
			if (g.getDegree(node) < 1) continue;
			final long[] labels = new long[g.getDegree(node)];

			for (int k = 0; k < g.getDegree(node); k++) {
				final int edge = g.getNodeEdge(node, k);
				final int neighbour = g.getOtherNode(edge, node);

				labels[k] = (g.getEdgeLabel(edge) << 32L) | g.getNodeLabel(neighbour);
				if ((g instanceof DirectedGraph) && (((DirectedGraph) g).getEdgeDirection(edge, node) == DirectedGraph.INCOMING_EDGE)) {
					labels[k] = ~labels[k];
				}
			}

			Arrays.sort(labels);
			long lastLabel = labels[0];
			int symCount = 1, temp = 1;
			for (int k = 1; k < labels.length; k++) {
				if (labels[k] == lastLabel) {
					temp++;
				} else {
					// symCount += Math.fak(temp);
					symCount *= Math.binom(temp, (int) java.lang.Math.ceil(temp / 2.0));
					lastLabel = labels[k];
				}
			}

			syms[i] = symCount;
		}

		return syms;
	}


	public static int[] getMaximalIndependentSet(UndirectedGraph g) {
		if (!(g instanceof MutableGraph)) throw new IllegalArgumentException("The graph must be mutable");

		final MutableGraph copy = (MutableGraph) g.clone();
		for (int i = 0; i < copy.getNodeCount(); i++) {
			copy.setNodeObject(copy.getNode(i), new MISNode(copy.getNode(i), null, null));
		}
		

		Collection col = getMaximalIndependetSet(copy, new HashSet(), new HashSet());
		int[] retVal = new int[col.size()];
		int count = 0;
		for (Iterator it = col.iterator(); it.hasNext();) {
			retVal[count++] = ((MISNode) it.next()).originalNode;
		}
		
		return retVal;
	}


	private static Collection getMaximalIndependetSet(final MutableGraph g, Set temp1, Set temp2) {
		for (int i = g.getEdgeCount() - 1; i >= 0; i--) {
			final int edge = g.getEdge(i);
			final int nodeA = g.getNodeA(edge);
			final int nodeB = g.getNodeB(edge);

			temp1.clear();			
			for (int k = g.getDegree(nodeA) - 1; k >= 0; k--) {
				final int edge2 = g.getNodeEdge(nodeA, k);

				temp1.add(g.getNodeObject(g.getOtherNode(edge2, nodeA)));
			}

			temp2.clear();			
			for (int k = g.getDegree(nodeB) - 1; k >= 0; k--) {
				final int edge2 = g.getNodeEdge(nodeB, k);

				temp2.add(g.getNodeObject(g.getOtherNode(edge2, nodeB)));
			}

			
			temp1.add(g.getNodeObject(nodeA));
			if (temp1.containsAll(temp2) && ! temp1.equals(temp2)) {
				g.removeNode(nodeA);
				return getMaximalIndependetSet(g, temp1, temp2);
			} 
			
			temp1.remove(g.getNodeObject(nodeA));
			temp2.add(g.getNodeObject(nodeB));
			if (temp2.containsAll(temp1) && ! temp2.equals(temp1)) {
				g.removeNode(nodeB);
				return getMaximalIndependetSet(g, temp1, temp2);
			}
		}

 		ArrayList maxSet = new ArrayList();
		boolean removed;
		do {
			removed = false;
			for (int i = g.getNodeCount() - 1; i >= 0; i--) {
				if (g.getDegree(g.getNode(i)) == 0) {
					maxSet.add(g.getNodeObject(g.getNode(i)));
					g.removeNode(g.getNode(i));
					removed = true;
					break;
				}
			}
		} while (removed);
		
		Integer[] nodes = new Integer[g.getNodeCount()];
		for (int i = nodes.length - 1; i >= 0; i--) {
			nodes[i] = new Integer(g.getNode(i));
		}
 
		
		Arrays.sort(nodes, new Comparator() {
			public int compare(Object nodeA, Object nodeB) {
				return g.getDegree(((Integer) nodeA).intValue()) - g.getDegree(((Integer) nodeB).intValue());
			}
		});

		
		for (int i = 0; i < nodes.length; i++) {
			int node = nodes[i].intValue();

			final int[] neighbours = new int[g.getDegree(node)];
			for (int k = g.getDegree(node) - 1; k >= 0; k--) {
				final int edge = g.getNodeEdge(node, k);
				neighbours[k] = g.getOtherNode(edge, node);
			}

			if (!containsAntiTriangle(g, neighbours)) {
				MutableGraph copy = (MutableGraph) g.clone(); 
				
				// check anti-edges
				ArrayList newNodes = new ArrayList();
				for (int m = 0; m < neighbours.length; m++) {
					for (int k = m + 1; k < neighbours.length; k++) {
						if (copy.getEdge(neighbours[m], neighbours[k]) == Graph.NO_EDGE) {
							int newNode = copy.addNode(999);
							copy.setNodeObject(newNode, new MISNode(Graph.NO_NODE, (MISNode) copy.getNodeObject(neighbours[m]), (MISNode) copy.getNodeObject(neighbours[k])));
							
							newNodes.add(new Integer(newNode));

							for (int l = copy.getDegree(neighbours[m]) - 1; l >= 0; l--) {
								final int edge = copy.getNodeEdge(neighbours[m], l);
								final int otherNode = copy.getOtherNode(edge, neighbours[m]);
								
								copy.addEdge(newNode, otherNode, 999);
							}

							for (int l = copy.getDegree(neighbours[k]) - 1; l >= 0; l--) {
								final int edge = copy.getNodeEdge(neighbours[k], l);
								final int otherNode = copy.getOtherNode(edge, neighbours[k]);
								if ((otherNode != node) && (copy.getEdge(newNode, otherNode) == Graph.NO_EDGE)) {
									copy.addEdge(newNode, otherNode, 999);
								}
							}
						}
					}
				}

				// add edges between each pair of the new nodes
				for (int m = 0; m < newNodes.size(); m++) {
					for (int l = m + 1; l < newNodes.size(); l++) {
						copy.addEdge(((Integer) newNodes.get(l)).intValue(), ((Integer) newNodes.get(m)).intValue(), 987);
					}
				}

				
				for (int m = copy.getDegree(node) - 1; m >= 0; m--) {
					final int edge = copy.getNodeEdge(node, m);
					final Integer otherNode = new Integer(copy.getOtherNode(edge, node)); 
					if (copy.getOtherNode(edge, node) < node) node--; // FIXME this is not nice
					copy.removeNode(otherNode.intValue());
				}
				copy.removeNode(node);
				
				
				if (copy.getEdgeCount() <= g.getEdgeCount()) {
					maxSet.addAll(getMaximalIndependetSet(copy, temp1, temp2));
					
					for (Iterator it = maxSet.iterator(); it.hasNext();) {
						MISNode xx = (MISNode) it.next();
						
						if (xx.nodeU != null) {
							it.remove();
							maxSet.add(xx.nodeU);
							maxSet.add(xx.nodeV);
							
							return maxSet;
						}
					}
					
					maxSet.add(g.getNodeObject(node));
					return maxSet;
				}
			}
		}

		
		
		
		if (g.getNodeCount() == 0) {
			return maxSet;
		} else {
			int node = g.getNode(0);
			for (int i = 1; i < g.getNodeCount(); i++) {
				if (g.getDegree(g.getNode(i)) > g.getDegree(node)) node = g.getNode(i);			
			}
			
			MutableGraph copy = (MutableGraph) g.clone();
			copy.removeNode(node);
			Collection colA = getMaximalIndependetSet(copy, temp1, temp2);
			
			copy = (MutableGraph) g.clone();
			while (copy.getDegree(node) > 0) {
				final int edge = copy.getNodeEdge(node, copy.getDegree(node) - 1);
				if (copy.getOtherNode(edge, node) < node) node--; // FIXME this is not nice
				copy.removeNode(copy.getOtherNode(edge, node));
			}
			copy.removeNode(node);
	
			Collection colB = getMaximalIndependetSet(copy, temp1, temp2);
			colB.add(g.getNodeObject(node));
			
			if (colA.size() > colB.size()) {
				maxSet.addAll(colA);
			} else {
				maxSet.addAll(colB);
			}
			return maxSet;
		}
	}


	private static boolean containsAntiTriangle(Graph g, int[] neighbours) {
		if (neighbours.length < 3) return false;
		
		for (int i = neighbours.length - 1; i >= 0; i--) {
			for (int j = i - 1; j >= 0; j--) {
				if (g.getEdge(neighbours[i], neighbours[j]) != Graph.NO_EDGE) break;
				
				boolean ok = true;
				for (int k = j - 1; k >= 0; k--) {
					if (g.getEdge(neighbours[i], neighbours[k]) != Graph.NO_EDGE) {
						ok = false;
						break;
					}
					if (g.getEdge(neighbours[j], neighbours[k]) != Graph.NO_EDGE) {
						ok = false;
						break;
					}
				}
				if (ok) return true;
			}
		}

		return false;
	}


	private static int[] getAntiEdgeNodes(Graph g, int[] neighbours) {
		int[] nodes = new int[neighbours.length * neighbours.length * 2];
		int count = 0;

		for (int m = 0; m < neighbours.length; m++) {
			for (int k = m; k < neighbours.length; k++) {
				if (g.getEdge(neighbours[m], neighbours[k]) != Graph.NO_EDGE) {
					nodes[count++] = neighbours[m];
					nodes[count++] = neighbours[k];
				}
			}
		}

		int[] temp = new int[count];
		System.arraycopy(nodes, 0, temp, 0, count);
		return temp;
	}
	
	
	private static class MISNode {
		public int originalNode;
		public MISNode nodeU, nodeV;
		
		public MISNode(int originalNode, MISNode nodeU, MISNode nodeV) {
			this.originalNode = originalNode;
			this.nodeU = nodeU;
			this.nodeV = nodeV;
		}
	}
	
	
	
	
	
	/**
	 * Checks if this graph contains a cycle. If the graph is directed only directed cycles are recognized.
	 * @param g a graph
	 * @return <code>true</code> if the graph contains at least one (directed) cycle, <code>false</code> otherwise
	 */
	public static boolean containsCycle(Graph g) {
		final int[] preorder = new int[g.getNodeCount()];
		final int[] postorder = new int[g.getNodeCount()];
		
		for (int i = 0; i < g.getNodeCount(); i++) {
			if (preorder[i] == 0) {
				if (cycleDFS(g, g.getNode(i), preorder, postorder, new MutableInteger(0), new MutableInteger(0))) return true;
			}
		}
		
		return false;
	}
	
	
	private static boolean cycleDFS(Graph g, int currentNode, int[] preorder, int[] postorder, MutableInteger preO, MutableInteger postO) {
		preorder[g.getNodeIndex(currentNode)] = preO.inc();
		
		for (int i = 0; i < g.getDegree(currentNode); i++) {
			final int edge = g.getNodeEdge(currentNode, i);
			if ((g instanceof DirectedGraph) && (((DirectedGraph) g).getEdgeDirection(edge, currentNode) == DirectedGraph.INCOMING_EDGE)) continue;
			
			final int neigbour = g.getOtherNode(edge, currentNode);
			if (preorder[g.getNodeIndex(neigbour)] == 0) {
				if (cycleDFS(g, neigbour, preorder, postorder, preO, postO)) return true;
			} else if (postorder[g.getNodeIndex(neigbour)] == 0) {
				return true;
			}
		}
		
		postorder[g.getNodeIndex(currentNode)] = postO.inc();
		
		return false;
	}	
	
	public static void main(String[] args) throws ParseException {
		String s = "CC[1]CC@1C.CC";
		UndirectedGraph g = (UndirectedGraph) SLNParser.instance.parse(s, UndirectedListGraph.Factory.instance);
		
		int[] x = getMaximalIndependentSet(g);
		System.out.println(x);
	}
}
