/*
 * Created on 12.12.2004
 *
 * Copyright 2004,2005 Thorsten Meinl
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
package de.parmol.graph;


/**
 * This class represents a classified graph with directed edges that is implemented by using adjacency lists.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *
 */
public class ClassifiedDirectedListGraph extends DirectedListGraph implements ClassifiedGraph {
	protected float[] m_classFrequencies = { 1 };	
	
	/**
	 * Creates a new empty ClassifiedDirectedListGraph with no class frequencies.
	 */
	public ClassifiedDirectedListGraph() {
		super();
	}

	/**
	 * Creates a new empty ClassifiedDirectedListGraph with the given class frequencies.
	 * @param classFrequencies the frequencies of this graph in the classes
	 */
	public ClassifiedDirectedListGraph(float[] classFrequencies) {
		super();
		m_classFrequencies = classFrequencies;
	}

	/**
	 * Creates a new empty ClassifiedDirectedListGraph with the given id and class frequencies.
	 * @param id the id of the new graph
	 * @param classFrequencies the frequencies of this graph in the classes
	 */
	public ClassifiedDirectedListGraph(String id, float[] classFrequencies) {
		super(id);
		m_classFrequencies = classFrequencies;
	}

	/**
	 * Creates a new empty ClassifiedDirectedListGraph that is a copy of the given template graph with no class frequencies (except for the id).
	 * @param template the graph to be copied
	 */
	public ClassifiedDirectedListGraph(ClassifiedDirectedListGraph template) {
		super(template);
		m_classFrequencies = template.m_classFrequencies;
	}

	/**
	 * Creates a new empty ClassifiedDirectedListGraph that is a copy of the given template graph (except for the id).
	 * @param template the graph to be copied
	 */
	public ClassifiedDirectedListGraph(DirectedListGraph template) {
		super(template);
	}

	/**
	 * Creates a new empty ClassifiedDirectedListGraph that is a copy of the given template graph (except for the id).
	 * @param template the graph to be copied
	 */
	public ClassifiedDirectedListGraph(ClassifiedGraph template) {
		super((DirectedGraph) template);
		m_classFrequencies = template.getClassFrequencies();
	}

	/*
	 *  (non-Javadoc)
	 * @see de.parmol.graph.ClassifiedGraph#getClassFrequencies()
	 */
	public float[] getClassFrequencies() { return m_classFrequencies; }
	
	/**
	 * This class is a factory for ClassifiedDirectedListGraphs.
	 * 
	 * @author Thorsten.Meinl@informatik.uni-erlangen.de
	 */
	public static class Factory extends ClassifiedGraphFactory {
		/**
		 * The single instance of this factory.
		 */
		public final static Factory instance = new Factory();
		protected final static float[] DEFAULT_CLASS = { 1 };
		
		protected Factory() { super(DIRECTED_GRAPH | LIST_GRAPH | CLASSIFIED_GRAPH); }

		/* (non-Javadoc)
		 * @see de.parmol.graph.ClassifiedGraphFactory#createGraph(float[])
		 */
		public ClassifiedGraph createGraph(float[] classFrequencies) {
			return new ClassifiedDirectedListGraph(classFrequencies);
		}

		/* (non-Javadoc)
		 * @see de.parmol.graph.ClassifiedGraphFactory#createGraph(java.lang.String, float[])
		 */
		public ClassifiedGraph createGraph(String id, float[] classFrequencies) {
			if (id != null) return new ClassifiedUndirectedListGraph(id, classFrequencies);
			return new ClassifiedDirectedListGraph(classFrequencies);
		}

		/* (non-Javadoc)
		 * @see de.parmol.graph.GraphFactory#createGraph()
		 */
		public MutableGraph createGraph() {
			return new ClassifiedDirectedListGraph(DEFAULT_CLASS);
		}

		/* (non-Javadoc)
		 * @see de.parmol.graph.GraphFactory#createGraph(java.lang.String)
		 */
		public MutableGraph createGraph(String id) {
			if (id != null) return new ClassifiedDirectedListGraph(id, DEFAULT_CLASS);
			return new ClassifiedDirectedListGraph(DEFAULT_CLASS);
		}		
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.ClassifiedGraph#setClassFrequencies(float[])
	 */
	public void setClassFrequencies(float[] frequencies) {
		m_classFrequencies = frequencies;
	}
}
