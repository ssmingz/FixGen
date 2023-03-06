/*
 * Created on 13.04.2005
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;
import java.lang.Math;

import de.parmol.graph.DirectedListGraph;
import de.parmol.graph.DirectedMatrixGraph;
import de.parmol.graph.Graph;
import de.parmol.graph.GraphFactory;
import de.parmol.graph.MutableGraph;
import de.parmol.graph.UndirectedGraph;
import de.parmol.graph.UndirectedListGraph;
import de.parmol.graph.UndirectedMatrixGraph;
import de.parmol.graph.Util;
import de.parmol.parsers.DotGraphParser;
import de.parmol.parsers.GraphParser;
import de.parmol.parsers.NeatoParser;
import de.parmol.parsers.SLNParser;
import de.parmol.parsers.SimpleDirectedGraphParser;
import de.parmol.parsers.SimpleUndirectedGraphParser;
import de.parmol.parsers.SmilesParser;


/**
 * This class is a generator for random graphs. Various settings can be applied:
 * <ul>
 * 	<li>The size of the generated graph in nodes and edges</li>
 * 	<li>The number of distinct node and edge labels that should be used</li>
 * 	<li>What kind of graphs should be created (list or matrix based; directed or undirected; connected or not)</li>
 * 	<li>A custom distribution for the edge and node labels can be set</li>
 * </ul>
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *
 */
public class GraphGenerator {
	public final static GraphGenerator instance = new GraphGenerator();
	
	private boolean m_connectedGraph = true;
	private int m_nodeLabels = 10, m_edgeLabels = 10;
	private float[] m_nodeLabelDistribution = {0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f},
		m_edgeLabelDistribution = {0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f};
	private GraphFactory m_factory = UndirectedListGraph.Factory.instance;
	private int m_startNodeLabel = 1, m_startEdgeLabel = 1;
	
	private int m_averageNodeCount = 30;
	private int m_averageFragmentSize = 14, m_seeds = 0;
	private double m_edgeDensity = 0.2;
	
	/**
	 * Sets the factors that should be used for creating new graphs.
	 * @param factory a graph factory
	 */
	public void setGraphFactory(GraphFactory factory) { m_factory = factory; }
	
	/**
	 * Sets the number of distinct edge labels a generated graph should have. Due to the random generation of labels it
	 * cannot be guaranteed that each label occurs at least once. By default 10 different labels are generated.
	 * If a edge label distribution is set, the number of node labels is automatically determined by the size of the passed
	 * array.
	 * @see GraphGenerator#setEdgeLabelDistribution(float[])
	 * @param edgeLabels the number of edge labels
	 */
	public void setEdgeLabels(int edgeLabels) {
		m_edgeLabels = edgeLabels;
		m_edgeLabelDistribution = new float[m_edgeLabels];
		for (int i = 1; i < m_edgeLabelDistribution.length; i++) {
			m_edgeLabelDistribution[i] = 1.0f / m_edgeLabelDistribution.length + m_edgeLabelDistribution[i - 1];  
		}
	}
	
	/**
	 * Sets the first node label that should be used. If not set explicitly the labels range from 1 to the node label count.
	 * @param label the first node label that should be used
	 */
	public void setStartNodeLabel(int label) {
		m_startNodeLabel = label;
	}
	
	/**
	 * Sets the first edge label that should be used. If not set explicitly the labels range from 1 to the edge label count.
	 * @param label the first edge label that should be used
	 */
	public void setStartEdgeLabel(int label) {
		m_startEdgeLabel = label;
	}

	/**
	 * Sets the number of distinct node labels a generated graph should have. Due to the random generation of labels it
	 * cannot be guaranteed that each label occurs at least once. By default 10 different labels are generated.
	 * If a node label distribution is set, the number of node labels is automatically determined by the size of the passed
	 * array.
	 * @see GraphGenerator#setNodeLabelDistribution(float[])
	 * @param nodeLabels the number of node labels
	 */
	public void setNodeLabels(int nodeLabels) {
		m_nodeLabels = nodeLabels;
		m_nodeLabelDistribution = new float[m_nodeLabels];
		for (int i = 1; i < m_nodeLabelDistribution.length; i++) {
			m_nodeLabelDistribution[i] = 1.0f / m_nodeLabelDistribution.length + m_nodeLabelDistribution[i - 1];  
		}
	}
	
	/**
	 * Generates a random graph.
	 * @param nodeCount the number of nodes the graph should have
	 * @param edgeCount the number of edges the graph should have
	 * @return a random graph
	 */
	public Graph generateGraph(int nodeCount, int edgeCount) {
		MutableGraph g = m_factory.createGraph();
		addNodesAndEdges(g, nodeCount, edgeCount);
		return g;
	}
	
	/**
	 * Creates a random label according to the given distribution. The distribution must be a sorted array of
	 * probabilities in the range [0.0, 1.0]; 
	 * @param distribution the label distribution
	 * @return a random label
	 */
	private static int createLabel(float[] distribution) {
		int label = Arrays.binarySearch(distribution, (float) Math.random());
		if (label < 0) {
			return -label;
		} else {
			return label + 1;
		}				
	}
	
	/**
	 * Randomly addes nodes and edges to the given graph.
	 * @param g a mutable graph
	 * @param nodeCount the number of nodes that should be added
	 * @param edgeCount the number of edges that should be added
	 */
	private void addNodesAndEdges(MutableGraph g, int nodeCount, int edgeCount) {
	  if ((nodeCount < 1) && (edgeCount < 1)) return;
	  
		int[] nodes = new int[g.getNodeCount() + nodeCount];

		for (int i = g.getNodeCount() + nodeCount - 1; i >= nodeCount; i--) {
	  	nodes[i] = g.getNode(i - nodeCount);
	  }		
		
	  nodes[0] = g.addNode(createLabel(m_nodeLabelDistribution) + (m_startNodeLabel - 1));
	  for (int i = 1; i < nodeCount; i++) {
	    nodes[i] = g.addNode(createLabel(m_nodeLabelDistribution) + (m_startNodeLabel - 1));
	  }
	  
	
	  if (m_connectedGraph) {
	    g.addEdge(nodes[0], nodes[nodes.length - 1], createLabel(m_edgeLabelDistribution) + (m_startEdgeLabel - 1));
	    
	    int ccCount = Integer.MAX_VALUE;
	    outer:
	    for (int i = edgeCount; i > 0; i--) {
	      int nodeA, nodeB;
	      int maxIterations = 100;
	      do {
	      	if (maxIterations-- <= 0) continue outer;
	      	
	        nodeA = nodes[(int) (Math.random() * nodes.length)];
	        nodeB = nodes[(int) (Math.random() * nodes.length)];
	      } while ((nodeA == nodeB) || (g.getDegree(nodeA) + g.getDegree(nodeB) < 1) || (g.getEdge(nodeA, nodeB) != Graph.NO_EDGE)); 
	
        g.addEdge(nodeA, nodeB, createLabel(m_edgeLabelDistribution) + (m_startEdgeLabel - 1));
        
        if (ccCount > i) {
	        int[][] cc = Util.getConnectedComponents(g);
	        ccCount = cc.length;
	        if (ccCount > i) {
	        	for (int k = 1; k < cc.length; k++) {
	        		int selCC = (int) (Math.random() * k);
	        		g.addEdge(cc[selCC][(int) (Math.random() * cc[selCC].length)],
	        				cc[k][(int) (Math.random() * cc[k].length)],
	        				createLabel(m_edgeLabelDistribution) + (m_startEdgeLabel - 1));
	        	}
	        	// System.err.println(SLNParser.instance.serialize(g));
	        	break;
	        }
        }
	    }
	    
	    if (edgeCount + 1 < nodeCount) {
	    	connectGraph(g);
	    }
	  } else {
	  	for (int i = edgeCount; i >= 0; i--) {
	      int nodeA, nodeB;
	      do {
	        nodeA = nodes[(int) (Math.random() * nodeCount)];
	        nodeB = nodes[(int) (Math.random() * nodeCount)];
	      } while ((nodeA == nodeB) || (g.getEdge(nodeA, nodeB) != Graph.NO_EDGE));
	
        g.addEdge(nodeA, nodeB, createLabel(m_edgeLabelDistribution) + (m_startEdgeLabel - 1));
	    }
	  }
	}

	private void connectGraph(MutableGraph g) {
    int[][] cc = Util.getConnectedComponents(g);
  	for (int k = 1; k < cc.length; k++) {
  		g.addEdge(cc[0][(int) (Math.random() * cc[0].length)], cc[k][(int) (Math.random() * cc[k].length)],
  				createLabel(m_edgeLabelDistribution) + (m_startEdgeLabel - 1));
  	}		
	}
	
	/**
	 * Set to <code>true</code> if only connected graphs (i.e. with on connected component) should be created. The default
	 * setting is <code>true</code>
	 * @param connectedGraph <code>true</code> if only fully connected graphs should be created, <code>false</code> otherwise
	 */
	public void setConnectedGraph(boolean connectedGraph) {
		m_connectedGraph = connectedGraph;
	}
	
	/**
	 * Sets the distribution of the edge labels. The parameter is an array of arbitrary positive real numbers. The values
	 * are normalized by using the sum of all values. A value of <code>{ 1, 1, 10 }</code> means that the third node label
	 * should occur with a probability of <b>10 / (1 + 1 + 10)</b>, the other two labels with a probability of <b>1 / 12</b>.
	 * By default each label occurs with the same probability.
	 * <br />
	 * The array must have an entry for each edge label. 
	 * @param edgeLabelDistribution the distribution of edge labels
	 */
	public void setEdgeLabelDistribution(float[] edgeLabelDistribution) {
		m_edgeLabelDistribution = (float[]) edgeLabelDistribution.clone();
		
		float sum = 0;
		for (int i = 0; i < m_edgeLabelDistribution.length; i++) {
			sum += m_edgeLabelDistribution[i];
		}

		m_edgeLabelDistribution[0] /= sum;
		for (int i = 1; i < m_edgeLabelDistribution.length; i++) {
			m_edgeLabelDistribution[i] /= sum;
			m_edgeLabelDistribution[i] += m_edgeLabelDistribution[i - 1];
		}		
	}
	
	/**
	 * Sets the distribution of the node labels. The parameter is an array of arbitrary positive real numbers. The values
	 * are normalized by using the sum of all values. A value of <code>{ 1, 1, 10 }</code> means that the third node label
	 * should occur with a probability of <b>10 / (1 + 1 + 10)</b>, the other two labels with a probability of <b>1 / 12</b>.
	 * By default each label occurs with the same probability.
	 * <br />
	 * The array must have an entry for each node label. 
	 * @param nodeLabelDistribution the distribution of edge labels
	 */
	public void setNodeLabelDistribution(float[] nodeLabelDistribution) {
		m_nodeLabelDistribution = (float[]) nodeLabelDistribution.clone();

		float sum = 0;
		for (int i = 0; i < m_nodeLabelDistribution.length; i++) {
			sum += m_nodeLabelDistribution[i];
		}

		m_nodeLabelDistribution[0] /= sum;
		for (int i = 1; i < m_nodeLabelDistribution.length; i++) {
			m_nodeLabelDistribution[i] /= sum;
			m_nodeLabelDistribution[i] += m_nodeLabelDistribution[i - 1];
		}		
	}
	
	/**
	 * Creates a collection of random graphs.
	 * @param count the number of graphs that shall be created
	 * @param progress <code>true</code> if a progress indicator should be printed in stdout, <code>false</code> otherwise
	 * @return a collection of random graphs
	 */
	public Graph[] generateGraphs(int count, boolean progress) {
		double edgeDensity = ((m_factory.createGraph() instanceof UndirectedGraph) ? m_edgeDensity / 2 : m_edgeDensity);
		
		if (m_seeds > 0) {
			final MutableGraph[] seeds = new MutableGraph[m_seeds];
	
			int digits = de.parmol.Util.getDigits(m_seeds);
			DecimalFormat format = new DecimalFormat("0000000000000000000000".substring(0, digits));
			String bs = "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b".substring(0, digits);
			
			
			if (progress) System.out.print("Creating seed ");				
			double[] seedWeights = new double[m_seeds];
			double seedWeightSum = 0.0;
			for (int i = 0; i < m_seeds; i++) {
				if (progress) System.out.print(format.format(i + 1));
				
				final int nodeCount = (int) (Math.random() * 2 * (m_averageFragmentSize - 1)) + 1;
				final int edgeCount = (int) (Math.random() * 2 * (edgeDensity * nodeCount * nodeCount)) + 1;
							
				MutableGraph g = (MutableGraph) generateGraph(nodeCount, edgeCount);
//				assert(g.getEdgeCount() >= edgeCount);
				seeds[i] = g;
				seedWeightSum += seedWeights[i] = Math.random() / Math.sqrt(Math.sqrt(edgeCount));
				
				if (progress) System.out.print(bs);
			}
			
			for (int i = 0; i < m_seeds; i++) {
				seedWeights[i] /= seedWeightSum;
			}
			
			for (int i = 1; i < m_seeds; i++) {
				seedWeights[i] += seedWeights[i - 1];
			}
			
	
			digits = de.parmol.Util.getDigits(count);
			format = new DecimalFormat("0000000000000000000000".substring(0, digits));
			bs = "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b".substring(0, digits);
	
			
			if (progress) System.out.print("\nCreating graph ");
			final Graph[] graphs = new Graph[count];
			for (int i = 0; i < count; i++) {
				if (progress) System.out.print(format.format(i));
				
				final int nodeCount = (int) (Math.random() * 2 * (m_averageNodeCount - 1)) + 1;
				final int edgeCount = (int) (Math.random() * 2 * (edgeDensity * nodeCount * nodeCount)) + 1;
	
	
				
				int index = Arrays.binarySearch(seedWeights, Math.random());
				index = (index < 0) ?	-index - 1 : index;			
				
				MutableGraph g = (MutableGraph) seeds[index].clone();
				graphs[i] = g;
				
				while (g.getNodeCount() < nodeCount) {
					index = Arrays.binarySearch(seedWeights, Math.random());
					index = (index < 0) ?	-index - 1 : index;			
					
					Graph additionalSeed = seeds[index];
					if (additionalSeed.getNodeCount() + g.getNodeCount() > nodeCount) {
						addNodesAndEdges(g, Math.max(0, nodeCount - g.getNodeCount()), Math.max(0, edgeCount - g.getEdgeCount()));					
					} else {
						addGraph(g, additionalSeed, 0.2, edgeDensity);
					}				
				}
				
				if (m_connectedGraph) connectGraph(g);
				if (progress) System.out.print(bs);
			}
			
			return graphs;
		} else {
			final int digits = de.parmol.Util.getDigits(count);
			final DecimalFormat format = new DecimalFormat("0000000000000000000000".substring(0, digits));
			final String bs = "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b".substring(0, digits);

			
			if (progress) System.out.print("\nCreating graph ");
			final Graph[] graphs = new Graph[count];
			for (int i = 0; i < count; i++) {
				if (progress) System.out.print(format.format(i));
				
				MutableGraph g = m_factory.createGraph();
				graphs[i] = g;
				
				
				final int nodeCount = (int) (Math.random() * 2 * (m_averageNodeCount - 1)) + 1;
				final int edgeCount = (int) (Math.random() * 2 * (edgeDensity * nodeCount * nodeCount)) + 1;

				addNodesAndEdges(g, nodeCount, edgeCount);
				
				if (progress) System.out.print(bs);
			}
			
			return graphs;
		}
	}
	
	public MutableGraph addGraph(MutableGraph dest, Graph source, double nodeReuseProbability, double edgeDensity) {
		int[] nodes = new int[source.getNodeCount()];
		final int destNodeCount = dest.getNodeCount();
		
		for (int i = 0; i < source.getNodeCount(); i++) {
			if (Math.random() <= nodeReuseProbability) {				
				int[] temp = new int[dest.getNodeCount()];
				int count = 0;
						
				for (int k = 0; k < dest.getNodeCount(); k++) {
					if (dest.getNodeLabel(dest.getNode(k)) == source.getNodeLabel(source.getNode(i))) {
						temp[count++] = dest.getNodeIndex(dest.getNode(k));
					}
				}
				
				nodes[i] = temp[(int) (Math.random() * count)];
			} else {
				nodes[i] = dest.getNodeIndex(dest.addNode(source.getNodeLabel(source.getNode(i))));
			}
		}
		
		for (int i = 0; i < source.getEdgeCount(); i++) {
			final int edge = source.getEdge(i);
			final int nodeAIndex = source.getNodeIndex(source.getNodeA(edge));
			final int nodeBIndex = source.getNodeIndex(source.getNodeB(edge));
			
			dest.addEdge(nodes[nodeAIndex], nodes[nodeBIndex], source.getEdgeLabel(edge));
		}
		
		int ec = (int) (dest.getNodeCount() * dest.getNodeCount() * edgeDensity);
		
		for (int i = ec - dest.getEdgeCount() - 1; i >= 0; i--) {
			final int nodeAIndex = (int) (Math.random() * destNodeCount); 
			final int nodeBIndex = (int) (Math.random() * nodes.length);
			
			int label = createLabel(m_edgeLabelDistribution) + (m_startEdgeLabel - 1);
			dest.addEdge(dest.getNode(nodeAIndex), dest.getNode(nodes[nodeBIndex]), label);
		}
		
		return dest;
	}
	
	private static void usage() {
		System.out.println("Usage: " + GraphGenerator.class.getName() + " [options] outputFile noOfGraphs");
		System.out.println("Options:");
		System.out.println("  -connected=[true|false] (optional; indicates if only connected graphs should be created");
		System.out.println("  -averageNodeCount=... (default: 40; the average number of nodes in the created graphs");
		System.out.println("  -averageEdgeDensity=[0.0;1.0] (default: 0.5; the average edge density of the created graphs");
		System.out.println("  -edgeLabels=... (default: 10; integer value > 0; the number of edge labels");
		System.out.println("  -nodeLabels=... (default: 10; integer value > 0; the number of node labels");
		System.out.println("  -startEdgeLabel=... (default: 1; integer value >= 0; the first edge label used");
		System.out.println("  -startNodeLabel=... (default: 1; integer value >= 0; the first node label used");
		System.out.println("  -edgeLabelDistribution=prob1,prob2,... (optional; a list of positive real numbers forming the "+
				"distribution of edge labels)");
		System.out.println("  -nodeLabelDistribution=prob1,prob2,... (optional; a list of positive real numbers forming the "+
				"distribution of node labels)");
		System.out.println("  -graphFactory=... (default: " + UndirectedListGraph.Factory.class.getName() + 
				"; fully qualified class name; the name of the graph factory to use");
		System.out.println("                      currently available factories:");
		System.out.println("                      " + UndirectedListGraph.Factory.class.getName());
		System.out.println("                      " + DirectedListGraph.Factory.class.getName());
		System.out.println("                      " + UndirectedMatrixGraph.Factory.class.getName());
		System.out.println("                      " + DirectedMatrixGraph.Factory.class.getName());
		System.out.println("  -serializer=... (default: " + SimpleUndirectedGraphParser.class.getName() +
				"; the fully qualified name of the serializer class)");
		System.out.println("                      currently available serializers:");
		System.out.println("                      " + SimpleUndirectedGraphParser.class.getName());
		System.out.println("                      " + SimpleDirectedGraphParser.class.getName());
		System.out.println("                      " + NeatoParser.class.getName());
		System.out.println("                      " + DotGraphParser.class.getName());
		System.out.println("                      " + SLNParser.class.getName());
		System.out.println("                      " + SmilesParser.class.getName());
	}
	
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		if ((args.length < 2) || (args[0].equals("--help"))) {
			usage();
			System.exit(1);
		}
		
		
		OutputStream out = null;
		int graphCount = 0;
		double averageDegree = -1;
		GraphParser serializer = SimpleUndirectedGraphParser.instance;
		for (int i = 0; i < args.length; i++) {
			if (args[i].charAt(0) == '-') {
				String[] option = args[i].split("=");
				
				if (option[0].equals("-connected")) {
					instance.setConnectedGraph(Boolean.valueOf(option[1]).booleanValue());
				} else if (option[0].equals("-averageNodeCount")) {
					instance.setAverageNodeCount(Integer.parseInt(option[1]));
				} else if (option[0].equals("-averageEdgeDensity")) { 
					instance.setEdgeDensity(Double.parseDouble(option[1]));
				} else if (option[0].equals("-averageDegree")) { 
					averageDegree = Double.parseDouble(option[1]);
				} else if (option[0].equals("-seeds")) { 
					instance.setSeedCount(Integer.parseInt(option[1]));
				} else if (option[0].equals("-averageSeedSize")) { 
					instance.setAverageFragmentSize(Integer.parseInt(option[1]));
				} else if (option[0].equals("-edgeLabels")) {
					instance.setEdgeLabels(Integer.parseInt(option[1]));
				} else if (option[0].equals("-nodeLabels")) {
					instance.setNodeLabels(Integer.parseInt(option[1]));
				} else if (option[0].equals("-startEdgeLabel")) {
					instance.setStartEdgeLabel(Integer.parseInt(option[1]));
				} else if (option[0].equals("-startNodeLabel")) {
					instance.setStartNodeLabel(Integer.parseInt(option[1]));
				} else if (option[0].equals("-edgeLabelDistribution")) {
					String[] values = option[1].split(",");
					float[] dist = new float[values.length];
					
					for (int k = 0; k < values.length; k++) {
						dist[k] = Float.parseFloat(values[k]);
					}
					instance.setEdgeLabelDistribution(dist);
				} else if (option[0].equals("-nodeLabelDistribution")) {
					String[] values = option[1].split(",");
					float[] dist = new float[values.length];
					
					for (int k = 0; k < values.length; k++) {
						dist[k] = Float.parseFloat(values[k]);
					}
					instance.setNodeLabelDistribution(dist);
				} else if (option[0].equals("-graphFactory")) {
					instance.setGraphFactory((GraphFactory) Class.forName(option[0]).newInstance());
				} else if (option[0].equals("-serializer")) {
					serializer = (GraphParser) Class.forName(option[1]).newInstance();
				} else {
					System.err.println("Unknown option: " + option[0]);
					usage();
					System.exit(1);
				}				
			} else {
				if (i == args.length - 2) {
					if (args[i].endsWith(".gz")) {
						out = new GZIPOutputStream(new FileOutputStream(args[i]));
					} else {
						out = new FileOutputStream(args[i]);
					}
				} else if (i == args.length - 1) {
					graphCount = Integer.parseInt(args[i]);
				}
			}
		}
		
		if (averageDegree != -1) {
			double ed = averageDegree / instance.m_averageNodeCount;
			
			if (instance.m_factory.createGraph() instanceof UndirectedGraph) ed /= 2;
			instance.setEdgeDensity(ed);
		}
		
		
		long t = System.currentTimeMillis();				
		
		Graph[] graphs = instance.generateGraphs(graphCount, true);
		
		serializer.serialize(graphs, out);
		out.close();
		System.out.println("\nCreated " + graphCount + " random graphs in " + (System.currentTimeMillis() - t) + "ms");
	}
	
	public void setAverageNodeCount(int averageNodeCount) {
		m_averageNodeCount = averageNodeCount;
	}
	
	public void setAverageFragmentSize(int averageSeedSize) {
		m_averageFragmentSize = averageSeedSize;
	}
	
	public void setEdgeDensity(double edgeDensity) {
		m_edgeDensity = edgeDensity;
	}
	
	public void setSeedCount(int seeds) {
		m_seeds = seeds;
	}
}
