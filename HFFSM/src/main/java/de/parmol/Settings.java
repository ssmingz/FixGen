/*
 * Created on Aug 18, 2004
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


import java.io.PrintStream;
import java.util.Collection;

import de.parmol.graph.Graph;
import de.parmol.parsers.*;


/**
 * This class stores all settings for all miners in the framework.
 * ֻҪ������������ļ���debug���ɣ�������ͨ��ͼ�ν�������
 * 
 * @author Thorsten.Meinl@informatik.uni-erlangen.de
 *  
 */
public class Settings {
	/**
	 * Stores the minimum number of times a fragment must occur in the different classes of the graph database. Negative
	 * values indicate that this is a percentage value that must be replaced by the total number after the graphs have
	 * been read in.
	 */
	public float[] minimumClassFrequencies = { 1.0f };

	/**
	 * Stores the maxmimum number of times a fragment can occur in the different classes of the graph database. Negative
	 * values indicate that this is a percentage value that must be replaced by the total number after the graphs have
	 * been read in.
	 */
	public float[] maximumClassFrequencies = { Float.MAX_VALUE };

	/**
	 * The minimum size in edges a fragment must have to get reported
	 */
	public int minimumFragmentSize = 0;

	/**
	 * The maximum size in edges a fragment may have to get reported
	 */
	public int maximumFragmentSize = Integer.MAX_VALUE;


	/**
	 * <code>true</code>, if only closed fragments should be reported in the end, <code>false</code> otherwise
	 */
	public boolean closedFragmentsOnly = true;

	/**
	 * <code>true</code> if only paths should be searched, <code>false</code> otherwise
	 */
	public boolean findPathsOnly = false;

	/**
	 * <code>true</code> if only trees should be searched, <code>false</code> otherwise
	 */
	public boolean findTreesOnly = false;

	/**
	 * <code>true</code> if directed graphs should be searched, <code>false</code> otherwise
	 */
	public boolean directedSearch = false;

	/**
	 * Stores the name of the file where the graphs to mine on are stored.
	 */
	public String graphFile;

	/**
	 * Stores the name of the file to which discovered fragments should be written to.
	 */
	public String outputFile ="./HFFSM/data/out.txt";

	/**
	 * Stores the name of the file where the class frequencies for each graph are stored.
	 */
	public String classFrequencyFile;

	/**
	 * <code>true</code> if the number of embeddings should be counted for the support computation instead of the number
	 * of supported graphs, <code>false</code> otherwise.
	 */
	public boolean countEmbeddings = false;

	/**
	 * <code>true</code> if symmetric embeddings that overlap completely should be counted as two embeddings,
	 * <code>false</code> if only one of them should be counted. This flag has only an effect if {@link #countEmbeddings}
	 * is <code>true</code>.
	 */
	public boolean countSymmetricEmbeddings = true;
	
	/**
	 * <code>true</code> if a FrequentFragment should also store all embeddings belong to the fragment,
	 * <code>false</code> otherwise.
	 */
	public boolean storeEmbeddings = false;

	/**
	 * The GraphParser that should be used for parsing the graphs.
	 */
	public GraphParser parser = null;

	/**
	 * The GraphParser that should be used for serializing the graphs.
	 */
	public GraphParser serializer = null;

	/**
	 * A list of node labels that should not be ignored for collisions.
	 */
	public int[] ignoreNodes = {};
	
	// MoFa-specific options
	/**
	 * A list of node labels that should not be used as seeds for MoFa.
	 */
	public int[] ignoreSeeds = {};

	/**
	 * A seed from which the search is started.
	 */
	public String seed = null;

	/**
	 * <code>true</code> if extension objects should be pooled, <code>false</code> otherwise.
	 */
	public boolean useExtensionPooling = true;

	/**
	 * <code>true</code> if embedding objects should be pooled, <code>false</code> otherwise.
	 */
	public boolean useEmbeddingPooling = false;

	/**
	 * The minimum and maximum size of rings in order to get marked and used as a single extension.
	 */
	public int[] ringSizes = { 0, 0 };

	/**
	 * <code>true</code> if perfect extension pruning should be used, <code>false</code> otherwise. This setting is
	 * automatically set to <code>true</code> if <code>closedFragmentOnly</code> is also <code>true</code>.
	 */
	public boolean perfectExtensionPruning = false;

	/**
	 * <code>true</code> if the maximal heap should be recorded during the search. Please note that this slows down the
	 * search dramatically because the GC is called frequently.
	 */
	public boolean memoryStatistics = false;


	/**
	 * *** for internal usage only ***
	 */
	public Collection graphs;

	/**
	 * The maximum number of embeddings a subgraph can have in order to be represented as a complete embedding
	 */
	public int completeEmbeddingThreshold = Integer.MAX_VALUE;

	/**
	 * Used for internal purposes!
	 */
	public int graphCount; // not a user-defined setting but an information for the problem size estimation


	// parallel options, currently only for MoFa
	/**
	 * The number of threads that should be used for parallel searching.
	 */
	public int maxThreads = Runtime.getRuntime().availableProcessors();  //RuntimeSystem.total_CPU_Count();

	/**
	 * The name of the distribution scheme if parallel search should be done.
	 */
	public String distributionScheme = "threads";

	/**
	 * The minimum problem size a search tree node must have in order to get shipped to another worker.
	 */
	public float minimumProblemSize = 0.0f;

	// debug options
	/**
	 * Increasing values produce more output.
	 */
	public int debug = 0;

	/**
	 * <code>true</code>, if the found fragments should be checked for their frequencies after the search,
	 * <code>false</code> otherwise.
	 */
	public boolean checkFragmentCounts = false;

	/**
	 * <code>true</code>, if the number of embeddings shall be estimated
	 */
	public boolean embeddingEstimation = false;

	static public boolean outputMoreStatement = false;
	
	/**
	 * Statistics about the search.
	 */
	public final Statistics stats = new Statistics();
	
	protected Settings(){}
	protected Settings(Settings template){

		minimumClassFrequencies = template.minimumClassFrequencies;
		maximumClassFrequencies = template.maximumClassFrequencies;
		minimumFragmentSize = template.minimumFragmentSize;
		maximumFragmentSize = template.maximumFragmentSize;
		closedFragmentsOnly = template.closedFragmentsOnly;
		findPathsOnly = template.findPathsOnly;
		findTreesOnly = template.findTreesOnly;
		directedSearch = template.directedSearch;
		graphFile = template.graphFile;
		outputFile = template.outputFile;
		classFrequencyFile = template.classFrequencyFile;
		countEmbeddings = template.countEmbeddings;
		countSymmetricEmbeddings = template.countSymmetricEmbeddings;
		storeEmbeddings = template.storeEmbeddings;
		parser = template.parser;
		serializer = template.serializer;

		// MoFa-specific options
		ignoreSeeds = template.ignoreSeeds;
		seed = template.seed;
		useExtensionPooling = template.useExtensionPooling;
		useEmbeddingPooling = template.useEmbeddingPooling;
		ringSizes = template.ringSizes;
		perfectExtensionPruning = template.perfectExtensionPruning;
		memoryStatistics = template.memoryStatistics;

		maxThreads = template.maxThreads;
		distributionScheme = template.distributionScheme;
		minimumProblemSize = template.minimumProblemSize;

		debug = template.debug;
		checkFragmentCounts = template.checkFragmentCounts;
//		stats = template.stats;
	}

	/**
	 * Creates a new Settings object. The passed array must have elements in the form <code>-option=value</code>.
	 * 
	 * @param args an array of options
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public Settings(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		readSettings(args);
	}
	
	protected void readSettings(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		for (int i = 0; i < args.length; i++) {
			readSetting(args[i]);
		}
		
		if (parser == null) {
			if (graphFile.endsWith(".smiles") || graphFile.endsWith(".smiles.gz")) {
				parser = SmilesParser.instance;
			} else if (graphFile.endsWith(".sln") || graphFile.endsWith(".sln.gz")) {
				parser = SLNParser.instance;
			} else if (graphFile.endsWith(".sdf") || graphFile.endsWith(".sdf.gz")) {
				parser = SDFParser.instance;
			} else if (graphFile.endsWith(".neato") || graphFile.endsWith(".neato.gz")) {
				parser = NeatoParser.instance;
			} else if (graphFile.endsWith(".dot") || graphFile.endsWith(".dot.gz")) {
				parser = DotGraphParser.instance;
			} else if (graphFile.endsWith(".sdg") || graphFile.endsWith(".sdg.gz")) {
				parser = SimpleDirectedGraphParser.instance;
			} else if (graphFile.endsWith(".sug") || graphFile.endsWith(".sug.gz")) {
				parser = SimpleUndirectedGraphParser.instance;
			} else if (graphFile.endsWith(".xml")){ 
				parser = PDGParser.instance;
			}else {
				throw new RuntimeException("No parser class given and file format unknown");
			}
		}
		
		if (serializer == null) {
			if (outputFile == null) {
				serializer = parser;
			} else if (outputFile.endsWith(".smiles") || outputFile.endsWith(".smiles.gz")) {
				serializer = SmilesParser.instance;
			} else if (outputFile.endsWith(".sln") || outputFile.endsWith(".sln.gz")) {
				serializer = SLNParser.instance;
			} else if (outputFile.endsWith(".sdf") || outputFile.endsWith(".sdf.gz")) {
				serializer = SDFParser.instance;
			} else if (outputFile.endsWith(".neato") || outputFile.endsWith(".neato.gz")) {
				serializer = NeatoParser.instance;
			} else if (outputFile.endsWith(".dot") || outputFile.endsWith(".dot.gz")) {
				serializer = DotGraphParser.instance;
			} else if (outputFile.endsWith(".sdg") || outputFile.endsWith(".sdg.gz")) {
				serializer = SimpleDirectedGraphParser.instance;
			} else if (outputFile.endsWith(".sug") || outputFile.endsWith(".sug.gz")) {
				serializer = SimpleUndirectedGraphParser.instance;
			} else if (outputFile.endsWith(".xml")){ 
				serializer = PDGParser.instance;
			} else {
				serializer = parser;
			}			
		}
		if (!closedFragmentsOnly) perfectExtensionPruning = false;
	}


	/**
	 * Parses one setting and sets the value.
	 * 
	 * @param option a setting in the form of <code>-option=value</code>
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public void readSetting(String option) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		int index = option.indexOf('=');
		String[] temp = { option.substring(0, index), option.substring(index + 1) };

		if (temp[0].equals("-minimumFrequencies")) {
			String[] freqs = temp[1].split(",");
			minimumClassFrequencies = new float[freqs.length];
			for (int k = 0; k < freqs.length; k++) {
				if (freqs[k].endsWith("%")) {
					minimumClassFrequencies[k] = -Float.parseFloat(freqs[k].substring(0, freqs[k].length() - 1)) / 100;
				} else {
					minimumClassFrequencies[k] = Float.parseFloat(freqs[k]);
				}
			}
		} else if (temp[0].equals("-maximumFrequencies")) {
			String[] freqs = temp[1].split(",");
			maximumClassFrequencies = new float[freqs.length];
			for (int k = 0; k < freqs.length; k++) {
				if (freqs[k].endsWith("%")) {
					maximumClassFrequencies[k] = -Float.parseFloat(freqs[k].substring(0, freqs[k].length() - 1)) / 100;
				} else {
					maximumClassFrequencies[k] = Float.parseFloat(freqs[k]);
				}
			}
		} else if (temp[0].equals("-minimumFragmentSize")) {
			minimumFragmentSize = Integer.parseInt(temp[1]);
		} else if (temp[0].equals("-maximumFragmentSize")) {
			maximumFragmentSize = Integer.parseInt(temp[1]);
		} else if (temp[0].equals("-maxThreads")) {
			maxThreads = Integer.parseInt(temp[1]);
		} else if (temp[0].equals("-minimumProblemSize")) {
			minimumProblemSize = Float.parseFloat(temp[1]);
		} else if (temp[0].equals("-graphFile")) {
			graphFile = temp[1];
		} else if (temp[0].equals("-outputFile")) {
			outputFile = temp[1];
		} else if (temp[0].equals("-storeEmbeddings")) {
			storeEmbeddings = Boolean.valueOf(temp[1]).booleanValue();
		} else if (temp[0].equals("-ignoreLabelsForCollision")) {
			String[] seeds = temp[1].split(",");
			ignoreNodes = new int[seeds.length];
			for (int k = 0; k < seeds.length; k++) {
				ignoreNodes[k] = Integer.parseInt(seeds[k]);
			}
		} else if (temp[0].equals("-ignoreSeeds")) {
			String[] seeds = temp[1].split(",");
			ignoreSeeds = new int[seeds.length];
			for (int k = 0; k < seeds.length; k++) {
				ignoreSeeds[k] = Integer.parseInt(seeds[k]);
			}
		} else if (temp[0].equals("-useExtensionPooling")) {
			useExtensionPooling = Boolean.valueOf(temp[1]).booleanValue();
		} else if (temp[0].equals("-useEmbeddingPooling")) {
			useEmbeddingPooling = Boolean.valueOf(temp[1]).booleanValue();
		} else if (temp[0].equals("-closedFragmentsOnly")) {
			closedFragmentsOnly = Boolean.valueOf(temp[1]).booleanValue();
		} else if (temp[0].equals("-perfectExtensionPruning")) {
			perfectExtensionPruning = Boolean.valueOf(temp[1]).booleanValue();
		} else if (temp[0].equals("-memoryStatistics")) {
			memoryStatistics = Boolean.valueOf(temp[1]).booleanValue();
			//		} else if (temp[0].equals("-completeEmbeddingThreshold")) {
			//			completeEmbeddingThreshold = Integer.parseInt(temp[1]);
		} else if (temp[0].equals("-ringSizes")) {
			String[] sizes = temp[1].split(",");

			if (sizes.length == 2) {
				ringSizes[0] = Integer.parseInt(sizes[0]);
				ringSizes[1] = Integer.parseInt(sizes[1]);
			} else {
				throw new IllegalArgumentException("The -ringSizes options must be of the format 'minRingSize,maxRingSize'");
			}
		} else if (temp[0].equals("-debug")) {
			debug = Integer.parseInt(temp[1]);
		} else if (temp[0].equals("-findPathsOnly")) {
			findPathsOnly = Boolean.valueOf(temp[1]).booleanValue();
		} else if (temp[0].equals("-findTreesOnly")) {
			findTreesOnly = Boolean.valueOf(temp[1]).booleanValue();
		} else if (temp[0].equals("-countEmbeddings")) {
			countEmbeddings = Boolean.valueOf(temp[1]).booleanValue();
		} else if (temp[0].equals("-countSymmetricEmbeddings")) {
			countSymmetricEmbeddings = Boolean.valueOf(temp[1]).booleanValue();
		} else if (temp[0].equals("-parserClass")) {
			parser = (GraphParser) Class.forName(temp[1]).newInstance();
		} else if (temp[0].equals("-serializerClass")) {
			serializer = (GraphParser) Class.forName(temp[1]).newInstance();
		} else if (temp[0].equals("-checkFragmentCounts")) {
			checkFragmentCounts = Boolean.valueOf(temp[1]).booleanValue();
		} else if (temp[0].equals("-seed")) {
			seed = temp[1];
		} else if (temp[0].equals("-distributionScheme")) {
			distributionScheme = temp[1];
		} else if (temp[0].equals("-classFrequencyFile")) {
			classFrequencyFile = temp[1];
		} else if (temp[0].equals("-embeddingEstimation")) {
			embeddingEstimation = Boolean.valueOf(temp[1]).booleanValue();
		} else {
			throw new IllegalArgumentException("Unknown option '" + temp[0] + "'");
		}
	}


	/**
	 * Prints all options with a short description to the given PrintStream
	 * 
	 * @param out where the options should be printed to
	 */
	public static void printUsage(PrintStream out) {
		out.println("General options:");
		out.println("\t-minimumFrequencies=freq[,freq]* (floating point values)");
		out.println("\t\tThe minimum frequencies a fragment must have in the different classes to get reported");
		out.println("\t-maximumFrequencies=freq[,freq]* (optional; floating point values)");
		out.println("\t\tThe maximum frequencies a fragment can have in the different classes to get reported");
		out.println("\t-minimumFragmentSize=int (optional; default: 0)");
		out.println("\t\tThe minimum size in edges a fragment must have to get reported");
		out.println("\t-maximumFragmentSize=int (optional; default: 0)");
		out.println("\t\tThe maximum size in edges a fragment can have to get reported");
		out.println("\t-graphFile=file");
		out.println("\t\tThe file from which the graphs should be read");
		out.println("\t-outputFile=file (optional)");
		out.println("\t\tThe file to which the found frequent subgraphs should be written to");
		out.println("\t-classFrequencyFile=file (optional)");
		out.println("\t\tThe file with the class frequencies of all graphs");
//		out.println("\t-countEmbeddings=[true|false] (optional; default: false)");
//		out
//				.println("\t\tSet to true, if the number of embeddings should be taken as support instead of the number of supported graphs");
//		out.println("\t-countSymmetricEmbeddings=[true|false] (optional; default: true)");
//		out.println("\t\tSet to true, if symmetric embeddings that overlap completely should be counted");
		out.println("\t-findPathsOnly=true|false (optional; default: false)");
		out.println("\t\tSpecifies if only simple paths should be found (and no trees or arbitrary graphs)");
		out.println("\t-findTreesOnly=true|false (optional; default: false)");
		out.println("\t\tSpecifies if only trees (graphs without cycles) should be found");
		out.println("\t-closedFragmentsOnly=true|false (optional; default: true)");
		out.println("\t\tSpecifies if only closed fragments should be reported");
		out.println("\t-parserClass=string (optional; default: de.parmol.molecule.SLNParser)");
		out.println("\t\tSpecifies which parser should be used to parse the input file (fully qualified class name)");
		out.println("\t-serializerClass=string (optional; default: same as parserClass)");
		out
				.println("\t\tSpecifies which serializer should be used to print out the found frequent subgraphs (fully qualified class name)");
		out.println("\t-memoryStatistics=true|false (optional; default: false)");
		out.println("\t\tIf set to true the garbage collector is called frequently and the maximum heap size is recorded)");


		out.println("MoFa-specific options:");
		out.println("\t-ignoreSeeds=seed[,seed]* (optional; integer values)");
		out.println("\t\tA list of node labels that should not be used as seeds");
		out.println("\t-useExtensionPooling=true|false (optional; default: true)");
		out.println("\t\tSpecifies if object pools should be used for Extension objects (recommended on SMP)");
		out.println("\t-useEmbeddingPooling=true|false (optional; default: false)");
		out.println("\t\tSpecifies if object pools should be used for Embedding objects (recommended on SMP)");
//		out.println("\t-ringSizes=minRingSize,maxRingSize (optional; default: 0,0)");
//		out.println("\t\tSpecifies the sizes of the rings that should be marked and used for ring extensions");
		out.println("\t-equivalentSiblingPruning=true|false (optional; default: true)");
		out.println("\t\tSpecifies if equivalent sibling pruning shall be used");
		out.println("\t-seed=seed (optional; SLN/Smiles string (depending on the chosen parser); default: empty seed)");
		out.println("\t\tThe seed from which the search should start");
		//		out.println("\t-completeEmbeddingThreshold=int (optional; default: Integer.MAX_VALUE)");
		//		out.println("\t\tThe maximum number of embeddings a subgraph can have in order to be represented as a complete
		// embedding");

		out.println("Parallel options:");
		out.println("\t-maxThreads=n (optional; default: number of CPUs/number of nodes)");
		out.println("\t\tThe number of parallel threads that should be used for searching");
//		out.println("\t-minimumProblemSize=n (optional; default: 0.5)");
//		out.println("\t\tThe mimimum size a node in the search tree must have in order to get processed on a different node");

		out.println("Debug options:");
		out.println("\t-debug=n (optional; default: 0; integer value)");
		out.println("\t\tIncreasing values give more detailed debug output");
		out.println("\t-checkFragmentCounts=true|false (optional; default: false");
		out.println("\t\tChecks the support counts for each found fragment by explicit subgraph isomorphism tests");
	}


	/**
	 * Prints all options with a short description to System.out
	 */
	public static void printUsage() {
		printUsage(System.out);
	}


	/**
	 * Checks if the given frequencies satisfy the minimum frequency constraint
	 * 
	 * @param classFrequencies some frequencies
	 * @return <code>true</code> if the minimum frequency constraint is satisfied, <code>false</code> otherwise
	 */
	public boolean checkMinimumFrequencies(float[] classFrequencies) {
		for (int i = 0; i < classFrequencies.length; i++) {
			if (minimumClassFrequencies[i] > classFrequencies[i]) return false;
		}

		return true;
	}


	/**
	 * Checks if the given frequencies satisfy the maximum frequency constraint
	 * 
	 * @param classFrequencies some frequencies
	 * @return <code>true</code> if the maximum frequency constraint is satisfied, <code>false</code> otherwise
	 */
	public boolean checkMaximumFrequencies(float[] classFrequencies) {
		for (int i = 0; i < classFrequencies.length; i++) {
			if (maximumClassFrequencies[i] < classFrequencies[i]) return false;
		}

		return true;
	}


	/**
	 * Checks if the given fragment should be reported or not. This includes a test of the size, and the maximum frequencies.
	 * @param fragment a fragment
	 * @param frequencies the class frequencies of the fragment
	 * @return <code>true</code> if the fragment should be reported, <code>falsea</code> otherwise
	 */
	public boolean checkReportingConstraints(Graph fragment, float[] frequencies) {
		return (fragment.getEdgeCount() <= maximumFragmentSize) && (fragment.getEdgeCount() >= minimumFragmentSize)
				&& checkMaximumFrequencies(frequencies);
	}
}