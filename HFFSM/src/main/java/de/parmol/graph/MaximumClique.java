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

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import de.parmol.util.MutableWeighter;


/**
 * This singelton class is for computing the maximal clique, or the maximal
 * weighted clique for a given graph.
 * 
 * @author Marc Woerlein <Marc.Woerlein@informatik.uni-erlangen.de>
 * 
 */
public class MaximumClique {
	/** the singelton instance of this class */
	public final static MaximumClique instance = new MaximumClique();


	/** */
	public MaximumClique() {
	}

	private class CliqueNode {
		final int nodeIndex;
		final int weight;
		int color;
		BitSet neighbor;


		CliqueNode(int index, int weight) {
			this.nodeIndex = index;
			this.weight = weight;
			color = -1;
		}


		void getNeigbors(Graph graph, int onodes[]) {
			neighbor = new BitSet(graph.getNodeCount());
			int node = graph.getNode(nodeIndex);
			for (int i = graph.getDegree(node) - 1; i >= 0; --i) {
				neighbor.set(1 + onodes[graph.getNodeIndex(graph.getOtherNode(graph.getNodeEdge(node, i), node))]);
			}
		}
	}


	private final int getNumberOfColors(final BitSet cur, int last, final CliqueNode nodes[]) {
		int ret = 0;
		for (int next = cur.nextSetBit(1); next > 0; next = cur.nextSetBit(next + 1)) {
			final int cc = nodes[next - 1].color;
			if (cc > last) {
				last = cc;
				ret++;
			}
		}
		return ret;
	}


	private final int getMaxColorWeight(final BitSet cur, final CliqueNode nodes[]) {
		int ret = 0;
		int ln = cur.nextSetBit(1);
		if (ln > 0) {
			int lc = nodes[ln - 1].color;
			for (int next = cur.nextSetBit(ln); next > 0; next = cur.nextSetBit(next + 1)) {
				final int cc = nodes[next - 1].color;
				if (cc > lc) {
					ret += nodes[ln - 1].weight;
					lc = cc;
				}
				ln = next;
			}
			ret += nodes[ln - 1].weight;
		}
		return ret;
	}


	private final CliqueNode[] colorGraph(final Graph graph, int[] weights) {
		CliqueNode nodes[] = new CliqueNode[graph.getNodeCount()];
		if (weights != null)
			for (int i = nodes.length - 1; i >= 0; --i)
				nodes[i] = new CliqueNode(i, weights[graph.getNode(i)]);
		else
			for (int i = nodes.length - 1; i >= 0; --i)
				nodes[i] = new CliqueNode(i, 1);
		BitSet cs = new BitSet(nodes.length + 1);
		for (int i = graph.getNodeCount() - 1; i >= 0; --i) {
			int node = graph.getNode(i);
			cs.set(1, nodes.length + 1);
			for (int j = graph.getDegree(i) - 1; j >= 0; --j) {
				int other = graph.getOtherNode(graph.getNodeEdge(node, j), node);
				int c = nodes[graph.getNodeIndex(other)].color;
				if (c != -1)
					cs.clear(c + 1);
			}
			nodes[i].color = cs.nextSetBit(1) - 1;
		}

		Arrays.sort(nodes, new Comparator() {
			public int compare(Object a, Object b) {
				CliqueNode ca = (CliqueNode) a;
				CliqueNode cb = (CliqueNode) b;
				if (ca.color != cb.color)
					return ca.color - cb.color;
				return ca.weight - cb.weight;
			}
		});
		int onodes[] = new int[graph.getNodeCount()];
		for (int i = nodes.length - 1; i >= 0; --i) {
			onodes[nodes[i].nodeIndex] = i;
		}
		for (int i = nodes.length - 1; i >= 0; --i)
			nodes[i].getNeigbors(graph, onodes);
		return nodes;
	}


	private void maxC(BitSet cur, BitSet rest, BitSet cbc, int[] backtrack, final CliqueNode[] nodes) {
		int next = rest.nextSetBit(1);
		if (next < 0) {
			if (cur.cardinality() > cbc.cardinality()) {
				cbc.clear();
				cbc.or(cur);
			}
			return;
		}
		for (; next > 0; next = rest.nextSetBit(next + 1)) {
			final int diff = cbc.cardinality() - cur.cardinality();
			if (backtrack[next - 1] <= diff || getNumberOfColors(rest, nodes[next - 1].color, nodes) < diff)
				return;
			rest.clear(next);
			cur.set(next);
			BitSet neu = (BitSet) rest.clone();
			neu.and(nodes[next - 1].neighbor);
			maxC(cur, neu, cbc, backtrack, nodes);
			cur.clear(next);
		}
	}


	private int maxC(BitSet cur, int curWeight, BitSet rest, BitSet cbc, int cbcWeight, int[] backtrack,
			final CliqueNode[] nodes) {
		int next = rest.nextSetBit(1);
		if (next < 0) {
			if (curWeight > cbcWeight) {
				cbc.clear();
				cbc.or(cur);
				return curWeight;
			}
			return cbcWeight;
		}
		for (; next > 0; next = rest.nextSetBit(next + 1)) {
			final int diff = cbcWeight - curWeight;
			if (backtrack[next - 1] <= diff || getMaxColorWeight(rest, nodes) < diff)
				return cbcWeight;
			rest.clear(next);
			cur.set(next);
			BitSet neu = (BitSet) rest.clone();
			neu.and(nodes[next - 1].neighbor);
			cbcWeight = maxC(cur, curWeight + nodes[next - 1].weight, neu, cbc, cbcWeight, backtrack, nodes);
			cur.clear(next);
		}
		return cbcWeight;
	}


	/**
	 * calculates the maximal clique for the given graph
	 * 
	 * @param graph
	 * @return array of the nodes of the maximal clique
	 */

	public final int[] getMaximumClique(final Graph graph) {
		// *
		return getMaximumWeightedClique(graph, null);
		/*
		 * / if (graph.getNodeCount()==1) return new int[1]; CliqueNode
		 * nodes[]=colorGraph(graph,null); BitSet cbc=new BitSet(nodes.length); int
		 * backtrack[]=new int[nodes.length]; for (int n=nodes.length;n>0;--n){
		 * BitSet cur=new BitSet(nodes.length); cur.set(n); BitSet rest=new
		 * BitSet(nodes.length); rest.set(n,nodes.length+1);
		 * rest.and(nodes[n-1].neighbor); maxC(cur,rest,cbc,backtrack,nodes);
		 * backtrack[n-1]=cbc.cardinality(); } int[] ret=new int[cbc.cardinality()];
		 * for (int
		 * next=cbc.nextSetBit(1),i=0;next>0;next=cbc.nextSetBit(next+1),++i){
		 * ret[i]=graph.getNode(nodes[next-1].nodeIndex); } return ret;//
		 */
	}


	/**
	 * calculates the maximal weighted clique for the given graph
	 * 
	 * @param graph
	 * @param weights the weights for each node of the graph
	 * @return array of the nodes of the maximal clique
	 */
	public final int[] getMaximumWeightedClique(final Graph graph, int[] weights) {
		if (graph.getNodeCount() == 1)
			return new int[1];
		CliqueNode nodes[] = colorGraph(graph, weights);
		BitSet cbc = new BitSet(nodes.length);
		int cbcw = 0;
		int backtrack[] = new int[nodes.length];
		for (int n = nodes.length; n > 0; --n) {
			BitSet cur = new BitSet(nodes.length);
			cur.set(n);
			BitSet rest = new BitSet(nodes.length);
			rest.set(n, nodes.length + 1);
			rest.and(nodes[n - 1].neighbor);
			if (weights == null) {
				maxC(cur, rest, cbc, backtrack, nodes);
				backtrack[n - 1] = cbc.cardinality();
			} else {
				cbcw = maxC(cur, nodes[n - 1].weight, rest, cbc, cbcw, backtrack, nodes);
				backtrack[n - 1] = cbcw;
			}
		}
		int[] ret = new int[cbc.cardinality()];
		for (int next = cbc.nextSetBit(1), i = 0; next > 0; next = cbc.nextSetBit(next + 1), ++i) {
			ret[i] = graph.getNode(nodes[next - 1].nodeIndex);
		}
		return ret;
	}

	// the functions needed for mutable weighted graphs

	private class MutableWeightedCliqueNode {
		final Object o;
		final int nodeIndex;
		final float weight;
		int color;
		BitSet neighbor;


		MutableWeightedCliqueNode(int index, Object o, float maxWeight) {
			this.nodeIndex = index;
			this.weight = maxWeight;
			this.o = o;
			color = -1;
		}


		void getNeigbors(Graph graph, int onodes[]) {
			neighbor = new BitSet(graph.getNodeCount());
			int node = graph.getNode(nodeIndex);
			for (int i = graph.getDegree(node) - 1; i >= 0; --i) {
				neighbor.set(1 + onodes[graph.getNodeIndex(graph.getOtherNode(graph.getNodeEdge(node, i), node))]);
			}
		}
	}


	private final float getMaxColorWeight(final BitSet cur, final MutableWeightedCliqueNode nodes[]) {
		float ret = 0;
		int ln = cur.nextSetBit(1);
		if (ln > 0) {
			int lc = nodes[ln - 1].color;
			for (int next = cur.nextSetBit(ln); next > 0; next = cur.nextSetBit(next + 1)) {
				final int cc = nodes[next - 1].color;
				if (cc > lc) {
					ret += nodes[ln - 1].weight;
					lc = cc;
				}
				ln = next;
			}
			ret += nodes[ln - 1].weight;
		}
		return ret;
	}


	private float maxC(final BitSet cur, float curMaxWeight, BitSet rest, BitSet cbc, float cbcWeight, float[] backtrack,
			final MutableWeightedCliqueNode[] nodes, final MutableWeighter weighter) {
		int next = rest.nextSetBit(1);
		for (; next > 0; next = rest.nextSetBit(next + 1)) {
			final float diff = cbcWeight - curMaxWeight;
			if (backtrack[next - 1] <= diff || getMaxColorWeight(rest, nodes) < diff) {
				System.out.print(".");
				return cbcWeight;
			}
			rest.clear(next);
			cur.set(next);
			BitSet neu = (BitSet) rest.clone();
			neu.and(nodes[next - 1].neighbor);
			cbcWeight = maxC(cur, curMaxWeight + nodes[next - 1].weight, neu, cbc, cbcWeight, backtrack, nodes, weighter);
			cur.clear(next);
		}
		if (curMaxWeight > cbcWeight) {
			final float curWeight = weighter.getFinallyWeight(new Iterator() {
				int pos = cur.nextSetBit(1);


				public boolean hasNext() {
					return pos > 0;
				}


				public Object next() {
					if (pos > 0) {
						Object o = nodes[pos - 1].o;
						pos = cur.nextSetBit(pos + 1);
						return o;
					} else
						throw new NoSuchElementException("No more elements");
				}


				public void remove() {
					throw new UnsupportedOperationException();
				}
			});
			if (curWeight > cbcWeight) {
				cbc.clear();
				cbc.or(cur);
				System.out.println("cbcw: " + curWeight);
				return curWeight;
			}
		}
		return cbcWeight;
	}


	/**
	 * calculates a maximal mutable weighted clique for the given graph
	 * 
	 * @param objectedGraph graph whose node objected are used to calculate the
	 *          weight
	 * @param weighter a MutableWeighter object that will be confronted by the
	 *          node objects
	 * @return a Collection containing the node objects of a maximal weighted
	 *         clique
	 */
	public final Collection getMaximumMutableWeightedClique(final Graph objectedGraph, MutableWeighter weighter) {
		// colorGraph
		System.out.print("coloring graph... ");
		long tmp = System.currentTimeMillis();
		MutableWeightedCliqueNode nodes[] = new MutableWeightedCliqueNode[objectedGraph.getNodeCount()];
		for (int i = nodes.length - 1; i >= 0; --i) {
			Object o = objectedGraph.getNodeObject(objectedGraph.getNode(i));
			nodes[i] = new MutableWeightedCliqueNode(i, o, weighter.getMaximalWeight(o));
		}
		BitSet cs = new BitSet(nodes.length + 1);
		for (int i = objectedGraph.getNodeCount() - 1; i >= 0; --i) {
			int node = objectedGraph.getNode(i);
			cs.set(1, nodes.length + 1);
			for (int j = objectedGraph.getDegree(i) - 1; j >= 0; --j) {
				int other = objectedGraph.getOtherNode(objectedGraph.getNodeEdge(node, j), node);
				int c = nodes[objectedGraph.getNodeIndex(other)].color;
				if (c != -1)
					cs.clear(c + 1);
			}
			nodes[i].color = cs.nextSetBit(1) - 1;
		}

		Arrays.sort(nodes, new Comparator() {
			public int compare(Object a, Object b) {
				MutableWeightedCliqueNode ca = (MutableWeightedCliqueNode) a;
				MutableWeightedCliqueNode cb = (MutableWeightedCliqueNode) b;
				if (ca.color != cb.color)
					return ca.color - cb.color;
				return (int) (ca.weight - cb.weight);
			}
		});

		int onodes[] = new int[objectedGraph.getNodeCount()];
		for (int i = nodes.length - 1; i >= 0; --i) {
			onodes[nodes[i].nodeIndex] = i;
		}
		for (int i = nodes.length - 1; i >= 0; --i)
			nodes[i].getNeigbors(objectedGraph, onodes);

		System.out.println(" done (" + (System.currentTimeMillis() - tmp) + " ms)");
		System.out.println("colors: " + nodes[nodes.length - 1].color);
		// start recursion
		BitSet cbc = new BitSet(nodes.length);
		float cbcw = 0;
		float backtrack[] = new float[nodes.length];
		for (int n = nodes.length; n > 0; --n) {
			System.out.println("Itertion number: " + n + " cbcw: " + cbcw);
			BitSet cur = new BitSet(nodes.length);
			cur.set(n);
			BitSet rest = new BitSet(nodes.length);
			rest.set(n, nodes.length + 1);
			rest.and(nodes[n - 1].neighbor);
			cbcw = maxC(cur, nodes[n - 1].weight, rest, cbc, cbcw, backtrack, nodes, weighter);
			backtrack[n - 1] = cbcw;
		}

		// build object set
		Collection ret = new TreeSet();

		for (int next = cbc.nextSetBit(1), i = 0; next > 0; next = cbc.nextSetBit(next + 1), ++i) {
			ret.add(nodes[next - 1].o);
		}
		return ret;
	}
}
