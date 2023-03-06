/*
 * Created on Dec 7, 2004
 *
 * Copyright 2004, 2005 Thorsten Meinl
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
package de.parmol;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import de.parmol.graph.*;
import de.parmol.parsers.GraphParser;
import de.parmol.parsers.PDGParser;
import de.parmol.search.DFSSearchable;
import de.parmol.search.SearchManager;
import de.parmol.search.ThreadedDFSSearch;
import de.parmol.util.DefaultObjectPool;
import de.parmol.util.FragmentSet;
import de.parmol.util.FrequentFragment;

/**
 * This class is the common interface for all graph miners.
 * 
 * @author Thorsten.Meinl@informatik.uni-erlangen.de
 *
 */
public abstract class AbstractMiner {
	
	public int outputGraphCount;
	static long costTime;
	
	protected Collection m_graphs;
	protected final Settings m_settings;
	protected FragmentSet m_frequentSubgraphs;
	
	/*
	 * 生成内部类实例，接口的工厂模式实现，从而可以定义各种类型的图
	 */
	static {
		Object o = UndirectedListGraph.Factory.instance;
		o = DirectedListGraph.Factory.instance;
		o = ClassifiedUndirectedListGraph.Factory.instance;
		o = ClassifiedDirectedListGraph.Factory.instance;				
		o = UndirectedMatrixGraph.Factory.instance;
		o = DirectedMatrixGraph.Factory.instance;
		o = ClassifiedUndirectedMatrixGraph.Factory.instance;
		o = ClassifiedDirectedMatrixGraph.Factory.instance;		
		o = o.getClass();
	}
	
	protected SearchManager getSearchManager(DFSSearchable searchable) {
		return new ThreadedDFSSearch(m_settings, searchable);
	}
	
	/**
	 * Creates a new AbstractMiner with the given settings.
	 * @param settings the settings for mining
	 */
	public AbstractMiner(Settings settings) {
		m_settings = settings;
	}
	
	/**
	 * Creates a new AbstractMiner with the given settings and fragmntset.
	 * @param settings the settings for mining
	 * @param frequentSubgraphs the emtpy fragmentset for collecting frequent subgraphs
	 */
	public AbstractMiner(Settings settings, FragmentSet frequentSubgraphs) {
		m_settings = settings;
		m_frequentSubgraphs = frequentSubgraphs;
	}
	
	/**
	 * Prepares the miner, e.g. reads the graphs or other algorithm specific preprocessing
	 * @throws FileNotFoundException if the file with the graphs could not be found
	 * @throws IOException if an error while reading the graphs occured
	 * @throws ParseException if one of the graphs could not be parsed
	 */
	public void setUp() throws FileNotFoundException, IOException, ParseException {
		// TODO: add automatic file format detection to choose the right parser
		if (m_graphs == null) {
			if (m_settings.graphFile.endsWith(".gz")) {
				readGraphs(new GZIPInputStream(new FileInputStream(m_settings.graphFile)), m_settings.parser);
			} else {
				readGraphs(new FileInputStream(m_settings.graphFile), m_settings.parser);
			}
		}
	}
	
	
	/**
	 * Reads graphs from the given InputStream, parses（解析） them with the given parser, and creates graphs by using the given factory
	 * @param in an InputStream with graphs
	 * @param parser a GraphParser
	 * @throws IOException if an error ocurred while reading from the stream
	 * @throws ParseException if one of the graphs could not be parsed
	 */
	protected void readGraphs(InputStream in, GraphParser parser) throws IOException, ParseException {
		long t = 0;
		if (m_settings.debug > 0) {
			System.out.print("Parsing graphs...");
			t = System.currentTimeMillis();
		}
		
		//分析输入流，得到图集
		m_graphs = Arrays.asList(parser.parse(in, getGraphFactory(parser)));
		m_settings.directedSearch=parser.directed();
		
		//读取输入文件，得到每个图的ClassFrequencies
		int classCount = 1;
		if (m_settings.classFrequencyFile != null) {
			DefaultGraphClassifier classifier = new DefaultGraphClassifier(m_settings.classFrequencyFile);
			
			for (Iterator it = m_graphs.iterator(); it.hasNext();) {
				Graph g = (Graph) it.next();
				
				if (g instanceof ClassifiedGraph) {
					float[] cf = classifier.getClassFrequencies(g.getName());
					if (cf == null) {
						System.err.println("No class frequencies found for " + g.getName());
						((ClassifiedGraph) g).setClassFrequencies(new float[classCount]);
					} else {
						((ClassifiedGraph) g).setClassFrequencies(cf);
						classCount = cf.length;
					}
				}
			}
		}
		
		//计算ClassFrequencies的总和
		float[] frequencySum = new float[classCount];
		
		for (Iterator it = m_graphs.iterator(); it.hasNext();) {
			final Graph g = (Graph) it.next();
			
			if (g instanceof ClassifiedGraph) {
				float[] cf = ((ClassifiedGraph) g).getClassFrequencies();
				
				for (int k = 0; k < frequencySum.length; k++) {
					frequencySum[k] += cf[k];
				}
			} else {
				for (int k = 0; k < frequencySum.length; k++) {
					frequencySum[k] += 1;
				}				
			}
		}
		
		
		//？
		for (int i = 0; i < m_settings.minimumClassFrequencies.length; i++) {
			if (m_settings.minimumClassFrequencies[i] < 0) {
				m_settings.minimumClassFrequencies[i] *= -frequencySum[i];
			}
		}
		
		for (int i = 0; i < m_settings.maximumClassFrequencies.length; i++) {
			if (m_settings.maximumClassFrequencies[i] < 0) {
				m_settings.maximumClassFrequencies[i] *= -frequencySum[i];
			}
		}
		
		//输出额外运行信息
		if (m_settings.debug > 0) {
			System.out.println("done (" + (System.currentTimeMillis() - t) + "ms)");

			if (m_settings.debug > 3) {
				int maxEdgeCount = 0, edgeSum = 0;
				BitSet nodeLabelsUsed = new BitSet(), edgeLabelsUsed = new BitSet();
				for (java.util.Iterator it = m_graphs.iterator(); it.hasNext();) {
					de.parmol.graph.Graph g = (de.parmol.graph.Graph) it.next();
					
					edgeSum += g.getEdgeCount();
					maxEdgeCount = Math.max(maxEdgeCount, g.getEdgeCount());
					
					for (int k = 0; k < g.getNodeCount(); k++) {
						nodeLabelsUsed.set(g.getNodeLabel(g.getNode(k)));		
					}
					for (int k = 0; k < g.getEdgeCount(); k++) {
						edgeLabelsUsed.set(g.getEdgeLabel(g.getEdge(k)));		
					}
					
				}
		
				int nlsum = 0;
				for (int i = 0; i < nodeLabelsUsed.size(); i++) {
					if (nodeLabelsUsed.get(i)) nlsum++;
				}
				
				int elsum = 0;
				for (int i = 0; i < edgeLabelsUsed.size(); i++) {
					if (edgeLabelsUsed.get(i)) elsum++;
				}
		
				System.out.println("Maximal edge count: " + maxEdgeCount);
				System.out.println("Average edge count: " + (edgeSum / m_graphs.size()));
				System.out.println("Node label count: " + nlsum);
				System.out.println("Edge label count: " + elsum);
			}
			System.out.println("================================================================================");
		}					
	}
	
	/**
	 * Starts the mining process in concrete subclasses.
	 */
	protected abstract void startRealMining();
	
	/**
	 * Starts the mining.
	 */
	public void startMining() {
		long time = System.currentTimeMillis();
		
		Thread t = null;
		if (m_settings.memoryStatistics) {
			t =new Thread() {
				{ this.setDaemon(true); }
				public void run() {
					while (! isInterrupted()) {
						System.out.print("Getting maximal heap size...");
						System.gc();
						m_settings.stats.maximumHeapSize = Math.max(m_settings.stats.maximumHeapSize,
								(int) ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) >> 10));
						System.out.println(m_settings.stats.maximumHeapSize + "kB");
						
						try {
							sleep(1000);
						} catch (java.lang.InterruptedException e) {
							//e.printStackTrace();
						}
					}					
				}				
			};
			t.start();
		}
		if (m_frequentSubgraphs != null) m_frequentSubgraphs.clear();
		startRealMining();
		
		if (t != null) t.interrupt();
		
		if (m_settings.debug > 0) {
			System.out.println("================================================================================");
			System.out.println("Finding the fragments took " + (System.currentTimeMillis() - time) + "ms");
		}
		
		long xxx = System.currentTimeMillis();		
		if (m_settings.closedFragmentsOnly) {
			if (m_settings.debug > 0) System.out.print("Filtering closed fragments...");
			m_frequentSubgraphs.filterClosedFragments();
			if (m_settings.debug > 0)  System.out.println("done in " + (System.currentTimeMillis() - xxx) + "ms");
		}
		
				
		if (m_settings.debug > 1) {
			m_settings.stats.printStatistics(this);
			DefaultObjectPool.printStatistics();
		}

		if (m_settings.debug > 0) System.out.println("================================================================================");
		System.out.println("Complete search took " + ((System.currentTimeMillis() - time) / 1000.0) + " seconds; " + 
				"found " + m_frequentSubgraphs.size() + " fragments");
		if (m_settings.memoryStatistics) {
			System.out.println("and used maximal "+m_settings.stats.maximumHeapSize+" kB heap");
		}
		System.out.println(m_settings.stats.duplicateFragments+" duplicates where produced");
		
		if ((m_settings.debug > 0) && (getFrequentSubgraphs().size() > 0)) {
			int maxEdgeCount = -1;
			FrequentFragment maxf= null;
			FrequentFragment maxProd=null;
			for (Iterator it = getFrequentSubgraphs().iterator(); it.hasNext();) {
				final FrequentFragment f = (FrequentFragment) it.next();
				if (maxProd==null) maxProd=f;
				else if (krit(maxProd)<krit(f))	maxProd=f;
				if (maxEdgeCount<f.getFragment().getEdgeCount()) maxf=f;
				maxEdgeCount = Math.max(maxEdgeCount, f.getFragment().getEdgeCount());
			}

			System.out.println("Biggest fragment has " + maxEdgeCount + " edges");
			System.out.println("and is "+m_settings.serializer.serialize(maxf.getFragment()));
			System.out.println("maxProd is "+m_settings.serializer.serialize(maxProd.getFragment()));

//			if (m_settings.debug > 3) {
//				m_settings.stats.doAndPrintFragmentCheckForFFSMTeam(m_frequentSubgraphs);
//			}
			
		}
	}
	
	private final float krit(FrequentFragment f){
		return f.getClassFrequencies()[0]*f.getFragment().getEdgeCount();
	}
	
	
	/**
	 * Returns a set of all found frequent subgraphs
	 * @return a FragmentSet with FrequentFragments
	 */
	public FragmentSet getFrequentSubgraphs() { return m_frequentSubgraphs; }	
	
	
	/**
	 * Prints the found frequent subgraphs to the file specified on the command line (if any was specified). 
	 * @throws FileNotFoundException if the file specified with -outputFile could not be found
	 */
	public void printFrequentSubgraphs() throws FileNotFoundException {
		if (m_settings.checkFragmentCounts) {
			SimpleSubgraphComparator comp = new SimpleSubgraphComparator(SimpleNodeComparator.instance, SimpleEdgeComparator.instance);
			
			for (Iterator it = getFrequentSubgraphs().iterator(); it.hasNext();) {
				FrequentFragment frag = (FrequentFragment) it.next();
				
				System.out.print("Checking " + m_settings.serializer.serialize(frag.getFragment()) + " ...");
				
				int count = 0;
				for (Iterator it2 = getGraphs().iterator(); it2.hasNext();) {
					Graph g = (Graph) it2.next();
					
					if (comp.compare(frag.getFragment(), g) == 0) count++;
				}
				
				if((int) frag.getClassFrequencies()[0] != count) {
					System.err.println("FAILED => frequency of " + frag.getClassFrequencies()[0] + " is wrong, should be " + count + "!!!");
				} else {
					System.out.println("OK");
				}
			}			
		}
		
		
		if (m_settings.outputFile != null) {
			PrintStream out;
			
			if (m_settings.outputFile.equals("-")) {
				out = System.out;
			} else {
				out = new PrintStream(new FileOutputStream(m_settings.outputFile));
			}
			
			outputGraphCount=0;
			for (Iterator it = getFrequentSubgraphs().iterator(); it.hasNext();) {
				FrequentFragment f = (FrequentFragment) it.next();
				if (f.getFragment().getNodeCount() < 2) continue;
				outputGraphCount++;
			}
			out.println("costTime:"+costTime/1000000+" ms\n");
			out.println("the number of input graph:" + PDGParser.inputGraphCount+'\n'+
					"the number of frequent subgraph:"+outputGraphCount+'\n');
			for (Iterator it = getFrequentSubgraphs().iterator(); it.hasNext();) {
				FrequentFragment f = (FrequentFragment) it.next();
				if (f.getFragment().getNodeCount() < 2) continue;
				outputGraphCount++;
				out.println(f.toString(m_settings.serializer));
			}
		}
		
	}
	
	/**
	 * Sets the graphs on which the search process should be done.
	 * @param graphs a collection of graphs
	 */
	public void setGraphs(Collection graphs) { 
		m_graphs = graphs;
		m_settings.directedSearch=(m_graphs.iterator().next() instanceof DirectedGraph);
	}
	
	/**
	 * Returns the parsed graphs in which the search is done.
	 * @return a collection of graphs
	 */
	public Collection getGraphs() { return m_graphs; }
	
	/**
	 * Returns a factory for graphs that satisfies the needs for the given parser
	 * @param parser a GraphParser
	 * @return a GraphFactory
	 */
	protected GraphFactory getGraphFactory(GraphParser parser) {
		int mask = parser.getDesiredGraphFactoryProperties();
		
		return GraphFactory.getFactory(mask);
	}
	
	/**
	 * Returns a settings object applied to the current mining process 
	 * @return a Settings Object
	 */
	public final Settings getSettings(){
		return m_settings;
	}
}
