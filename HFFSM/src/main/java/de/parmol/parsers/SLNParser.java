/*
 * Created on Jun 24, 2004
 *
 * Copyright 2004 Thorsten Meinl
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
package de.parmol.parsers;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;

import de.parmol.graph.ClassifiedGraphFactory;
import de.parmol.graph.GraphClassifier;
import de.parmol.graph.GraphFactory;
import de.parmol.graph.MutableGraph;
import de.parmol.graph.Graph;

/**
 * This parser parses SLNs (Sybyl Line Notations) according to the grammar given below. Please note that it might not be
 * 100% correct as I tried to reconstruct it from scratch.
 * The parser ignores hydrogen atoms by default.
 * 
 * <pre>
 * Grammar:
 * Mol ::= Atom | Atom Frag
 * Frag ::= '.' Frag | Branch+ Frag | BondSymbol Frag
 * Branch ::= '(' Frag ')'
 * 
 * Atom ::= AtomSymbol | AtomSymbol '[' Props '] | AtomSymbol 'H' digit | '@' Marker
 * AtomSymbol ::= 'H' | 'He' | ...
 * 
 * BondSymbol ::= '-' | '=' | '#' | ':' | ''
 * 
 * Props ::= Marker [';' Anything]* | Anything [';' Anything]?
 * Marker ::= unsigned int
 * Anything ::= char+
 * </pre>
 *
 * @author Thorsten.Meinl@informatik.uni-erlangen.de
 */
public class SLNParser implements GraphParser {
	/**
	 * The names of all currently known elements.
	 */
	public final static String[] ATOM_SYMBOLS = {
			"", "H", "He",
			"Li", "Be", "B", "C", "N", "O", "F", "Ne",
			"Na", "Mg", "Al", "Si", "P", "S", "Cl", "Ar",
			"K", "Ca", "Sc", "Ti", "V", "Cr", "Mn", "Fe", "Co", "Ni", "Cu", "Zn", "Ga", "Ge", "As", "Se", "Br", "Kr",
			"Rb", "Sr", "Y", "Zr", "Nb", "Mo", "Tc", "Ru", "Rh", "Pd", "Ag", "Cd", "In", "Sn", "Sb", "Te", "I", "Xe",
			"Cs", "Ba", 
				"La", "Ce", "Pr", "Nd", "Pm", "Sm", "Eu", "Gd", "Tb", "Dy", "Ho", "Er", "Tm", "Yb",
				"Lu", "Hf", "Ta", "W", "Re", "Os", "Ir", "Pt", "Au", "Hg", "Tl", "Pb", "Bi", "Po", "At", "Rn",
			"Fr", "Ra",
				"Ac", "Th", "Pa", "U", "Np", "Pu", "Am", "Cm", "Bk", "Cf", "Es", "Fm", "Md", "No",
				"Lr", "Rf", "Db", "Sg", "Bh", "Hs", "Mt", "Uun", "Uuu", "Uub", "??", "Uuq"
	};
	
	/**
	 * All bond symbols.
	 */
	public final static char[] BOND_SYMBOLS = { '\0', '-', '=', '#', ':' };
	
    private final static char[][] CATOM_SYMBOLS = {{},{'H'},{'H','e'},{'L','i'},{'B','e'},{'B'},{'C'},{'N'},{'O'},{'F'},{'N','e'},{'N','a'},{'M','g'},{'A','l'},{'S','i'},{'P'},{'S'},{'C','l'},{'A','r'},
    	{'K'},{'C','a'},{'S','c'},{'T','i'},{'V'},{'C','r'},{'M','n'},{'F','e'},{'C','o'},{'N','i'},{'C','u'},{'Z','n'},{'G','a'},{'G','e'},{'A','s'},{'S','e'},{'B','r'},{'K','r'},
		{'R','b'},{'S','r'},{'Y'},{'Z','r'},{'N','b'},{'M','o'},{'T','c'},{'R','u'},{'R','h'},{'P','d'},{'A','g'},{'C','d'},{'I','n'},{'S','n'},{'S','b'},{'T','e'},{'I'},{'X','e'},
		{'C','s'},{'B','a'},{'L','a'},{'C','e'},{'P','r'},{'N','d'},{'P','m'},{'S','m'},{'E','u'},{'G','d'},{'T','b'},{'D','y'},{'H','o'},{'E','r'},{'T','m'},{'Y','b'},
		{'L','u'},{'H','f'},{'T','a'},{'W'},{'R','e'},{'O','s'},{'I','r'},{'P','t'},{'A','u'},{'H','g'},{'T','l'},{'P','b'},{'B','i'},{'P','o'},{'A','t'},{'R','n'},
		{'F','r'},{'R','a'},{'A','c'},{'T','h'},{'P','a'},{'U'},{'N','p'},{'P','u'},{'A','m'},{'C','m'},{'B','k'},{'C','f'},{'E','s'},{'F','m'},{'M','d'},{'N','o'},
		{'L','r'},{'R','f'},{'D','b'},{'S','g'},{'B','h'},{'H','s'},{'M','t'},{'U','u','n'},{'U','u','u'},{'U','u','b'},{'U','u','t'},{'U','u','q'}
	};
	
	private int m_pos;
	private char[] m_sln;
	private String m_slnString;
	private final HashMap m_markers = new HashMap();
	private boolean m_ignoreHydrogens = true;
	protected boolean m_lastAtomWasReference = false;
	
	protected GraphClassifier m_classifier;
	
	/**
	 * An instance of the SLN Parser.
	 */
	public final static SLNParser instance = new SLNParser();
	
	/**
	 * Creates a new SLNParser with no graph classifier.
	 */
	public SLNParser() { }
	
	
	/**
	 * Creates a new SLNParser with the given graph classifier.
	 * @param classifier a GraphClassifier
	 */
	public SLNParser(GraphClassifier classifier) {
		m_classifier = classifier;
	}

	/**
	 * Sets the value of the ignore hydrogen atoms flag
	 * @param newValue the new flag value
	 */
	public void setIgnoreHydrogens(boolean newValue) { m_ignoreHydrogens = newValue; }
	
	/**
	 * Return the current value of the ignore hydrogen atoms flag
	 * @return the flag value
	 */
	public boolean getIgnoreHydrogens() { return m_ignoreHydrogens; }
	
	/**
	 * Parses the given SLN string and creates a new graph with the given id using the given graph factory.
	 * @param text a SLN string
	 * @param id the id of the new graph
	 * @param factory a factory for graphs
	 * @return a new graph
	 * @throws ParseException if the SLN string contains errors
	 */
	public Graph parse(String text, String id, GraphFactory factory) throws ParseException {
		MutableGraph g;
		if ((factory instanceof ClassifiedGraphFactory) && (m_classifier != null)) {
			g = (MutableGraph) ((ClassifiedGraphFactory) factory).createGraph(id, m_classifier.getClassFrequencies(id));
		} else {		
			g = factory.createGraph(id);
		}
		
		m_pos = 0;
		m_sln = text.toCharArray();
		m_slnString = text;
		m_markers.clear();
		
		molecule(g, -1);
		return g;
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphParser#parse(java.lang.String, int)
	 */
	public Graph parse(String text, GraphFactory factory) throws ParseException {
		return parse(text, null, factory);
	}	
	
	/**
	 * Parses the Mol part of the grammar.
	 * @param g the graph to which new parts should be added
	 * @param lastAtom the latest atom added to the graph
	 * @return the first atom of the parsed molecule
	 * @throws ParseException if the SLN contains syntax errors
	 */
	private synchronized int molecule(MutableGraph g, int lastAtom) throws ParseException {
		int atom = atom(g);
		if (atom == -1) { 
			return -1;
		}
		
		if (m_pos >= m_sln.length) {
			// do nothing
		} else {
			if (m_lastAtomWasReference) {
				fragment(g, lastAtom);
			} else {
				fragment(g, atom);
			}
		}
		return atom;
	}	
	
	/**
	 * Parses the Frag part of the grammar an connects it to the given atom.
	 * @param g the graph to which new parts should be added
	 * @param atom that atom to which the parsed fragment should be connected
	 * @throws ParseException if the SLN contains syntax errors
	 */
	private void fragment(MutableGraph g, int atom) throws ParseException {
		if (m_sln[m_pos] == '.') { // new unconnected part starts
			m_pos++;
			molecule(g, atom);
		} else if (m_sln[m_pos] == '(') { // a new branch starts
			do {
				branch(g, atom);
			} while ((m_pos < m_sln.length) && (m_sln[m_pos] == '('));
			if (m_pos < m_sln.length) fragment(g, atom);
		} else if (m_sln[m_pos] == ')') { // the end of a branch
			return;
		} else { // must be a bond
			int bondSymbol = bondSymbol();
			int mol = molecule(g, atom);
			if (mol != -1) { g.addEdge(atom, mol, (bondSymbol == -1) ? 1 : bondSymbol); }
		}
	}
	
	/**
	 * Parses that Branch part of the grammar and connects it to the given atom.
	 * @param g the graph to which new parts should be added
	 * @param atom that atom to which the parsed branch should be connected
	 * @throws ParseException
	 */
	private void branch(MutableGraph g, int atom) throws ParseException {
		if (m_sln[m_pos] != '(') throw new ParseException("Expected '('\n" + getErrorPosition(m_pos), m_pos);
		m_pos++;
		
		int bondSymbol = bondSymbol();
		int mol = molecule(g, atom);
		if (mol != -1) {
			g.addEdge(atom, mol, (bondSymbol == -1) ? 1 : bondSymbol);
		} else {
			while ((m_pos < m_sln.length) && (m_sln[m_pos] != ')')) m_pos++;
		}
		
		if (m_sln[m_pos] != ')') throw new ParseException("Expected ')'\n" + getErrorPosition(m_pos), m_pos);
		m_pos++;
	}
		
	/**
	 * Parses the BondSymbol part of the grammar.
	 * @return the label of the bond (which is the index of the bondSymbol in the BOND_SYMBOLS array)
	 * @throws ParseException if the end of the string has been reached instead of a bond symbol
	 */
	private int bondSymbol() throws ParseException {
		if (m_pos >= m_sln.length) {
			throw new ParseException("Expected bond symbol but found end of string\n" + getErrorPosition(m_pos), m_pos);
		}
		
		for (int i = 1; i < BOND_SYMBOLS.length; i++) {
			if (BOND_SYMBOLS[i] == m_sln[m_pos]) {
				m_pos++;
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Parses the Atom part of the grammar and creates a new atom.
	 * @param g the graph to which new parts should be added
	 * @return the new atom
	 * @throws ParseException if the SLN contains syntax errors
	 */
	private int atom(MutableGraph g) throws ParseException {
		if (m_pos >= m_sln.length) {
			throw new ParseException("Expected atom symbol but found end of string\n" + getErrorPosition(m_pos), m_pos);
		}
		
		int atom;		
		if (m_sln[m_pos] == '@') { // back-reference to a marked atom
			int pos = ++m_pos;
			
			while ((pos < m_sln.length) && (m_sln[pos] >= '0') && (m_sln[pos] <= '9')) pos++;
			
			Integer node = (Integer) m_markers.get(new Integer(m_slnString.substring(m_pos, pos)));
			if (node == null) {
				throw new ParseException("Undefined reference to atom\n" + getErrorPosition(m_pos + 1), m_pos);
			}
			m_pos = pos;
			m_lastAtomWasReference = true;
			return node.intValue();			
		} else if ((atom = atomSymbol()) != -1) { 
			
			int newAtom = (m_ignoreHydrogens && (atom == 1)) ? -1 : g.addNode(atom); 
			
			if ((m_pos < m_sln.length) && (m_sln[m_pos] == '[')) { // check for properties, currently only markers
				int pos = ++m_pos;
				
				while ((m_sln[pos] != ']') && (m_sln[pos] != ':')) pos++;
				
				if ((m_sln[m_pos] >= '1') && (m_sln[m_pos] <= '9')) {
					Integer marker = new Integer(m_slnString.substring(m_pos, pos));
					if (m_markers.put(marker, new Integer(newAtom)) != null) 
						throw new ParseException("Marker already defined\n" + getErrorPosition(m_pos + 1), m_pos);
				}
				
				while (m_sln[pos] != ']') pos++; // jump over other properties if present
				
				m_pos = pos + 1;
			}
			
			// check for hydrogen atoms; take care that it is not any of He, Ho, Hf, Hg or Hs
			if ((m_pos < m_sln.length) && (m_sln[m_pos] == 'H') && ((m_pos == m_sln.length - 1) || 
					((m_pos+1) < m_sln.length) &&
					(m_sln[m_pos + 1] != 'e') && (m_sln[m_pos + 1] != 'o') && (m_sln[m_pos + 1] != 'f') &&
					(m_sln[m_pos + 1] != 'g') && (m_sln[m_pos + 1] != 's')))
			{
				m_pos++;
				int count;
				if ((m_pos < m_sln.length) && (m_sln[m_pos] >= '1') && (m_sln[m_pos] <= '9')) {
					count = m_sln[m_pos] - '0';
					m_pos++;
				} else {
					count = 1;
				}
				
				if ((newAtom != -1) && ! m_ignoreHydrogens) {
					for (; count > 0; count--) {
						g.addNodeAndEdge(newAtom, 1, 1); // H-Atom and single bond
					}
				}
			}
			
			m_lastAtomWasReference = false;
			return newAtom;
		}
		
		throw new ParseException("Expected atom symbol or marker reference but did not find any\n" + getErrorPosition(m_pos), m_pos);
	}
	
	/**
	 * Parses the AtomSymbol part of the grammar.
	 * @return the label of the parsed atom which is its periodic number
	 * @throws ParseException if the SLN contains errors
	 */
	private int atomSymbol() throws ParseException {
		// get the length of the atom symbol
		int symbolLength = 1;
		while (((m_pos + symbolLength) < m_sln.length) && (m_sln[m_pos + symbolLength] >= 'a') && (m_sln[m_pos + symbolLength] <= 'z')) symbolLength++;
		 
		for (int i = 1; i < CATOM_SYMBOLS.length; i++) {
			if (CATOM_SYMBOLS[i].length != symbolLength) continue;
			
			boolean matches = true;
			for (int pos = 0; pos < CATOM_SYMBOLS[i].length; pos++) {
				if (m_sln[m_pos + pos] != CATOM_SYMBOLS[i][pos]) {
					matches = false;
					break;
				}
			}
			
			if (matches) {
				m_pos += CATOM_SYMBOLS[i].length;
				return i;
			}
		}
		
		throw new ParseException("Expected atom or bond symbol but did not find any\n" + getErrorPosition(m_pos + 1), m_pos);
	}
	
	/**
	 * Returns a two line String containing the SLN in the first line and a single ^ in the second at the current parse position.
	 * @param pos the current parse position
	 * @return a String with the error message
	 */
	private String getErrorPosition(int pos) {
		StringBuffer b = new StringBuffer(m_sln.length * 2 + 2);
		b.append(m_slnString).append('\n');
		for (int i = 1; i < pos; i++) {
			b.append(' ');
		}
		b.append('^');
		return b.toString();
		
	}
	
	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphParser#serialize(de.parmol.graph.UndirectedGraph)
	 */
	public String serialize(Graph g) {
		boolean[] nodeVisited = new boolean[g.getNodeCount()]; 
		boolean[] edgeVisited = new boolean[g.getEdgeCount()];
		final boolean[] needsMarker = new boolean[g.getNodeCount()];
		
		// first identify atoms that need markers
		for (int i = g.getNodeCount() - 1; i >= 0; i--) {			
			int node = g.getNode(i);
			
			if (! nodeVisited[g.getNodeIndex(node)]) {
				findAtomMarkers(g, node, nodeVisited, edgeVisited, needsMarker);
			}
		}
		
		// then build the SLN
		nodeVisited = new boolean[g.getNodeCount()]; 
		edgeVisited = new boolean[g.getEdgeCount()];		
		
		final int[] markers = new int[g.getNodeCount()];
		int count = 0;
		for (int i = needsMarker.length - 1; i >= 0; i--) {
			if (needsMarker[i]) markers[i] = ++count;
		}
		
		StringBuffer buf = new StringBuffer(1024);
		
		count = 0;
		for (int i = g.getNodeCount() - 1; i >= 0; i--) {			
			int node = g.getNode(i);
			
			if (! nodeVisited[g.getNodeIndex(node)]) {
				if (count++ > 0) {
					buf.append('.');
				}
				buildSLN(g, node, nodeVisited, edgeVisited, markers, buf);
			}
		}
		
		return buf.toString();
	}
	
	/**
	 * Recursively build a SLN.
	 * @param g the graph
	 * @param node the current node in the DFS search through the molecule graph
	 * @param nodeVisited an array where each visited atom has a <code>true</code> entry at its index
	 * @param edgeVisited an array where each visited edge has a <code>true</code> entry at its index
	 * @param markers an array where the marker number of each atom is stored; 0 means no marker
	 * @param buf the StringBuffer that holds the SLN
	 */
	private void buildSLN(Graph g, int node, boolean[] nodeVisited, boolean[] edgeVisited, int[] markers, StringBuffer buf) {
		nodeVisited[g.getNodeIndex(node)] = true;
		
		buf.append(ATOM_SYMBOLS[g.getNodeLabel(node)]);
		if (markers[g.getNodeIndex(node)] > 0) { // atom needs a marker
			buf.append("[" + markers[g.getNodeIndex(node)] + "]");
		}
		
		// check the number of branches; if an atom has only one unvisited bond it does not need a branch
		int branchCount = 0;
		for (int i = g.getDegree(node) - 1; i >= 0; i--) {
			int edge = g.getNodeEdge(node, i);
			if (! edgeVisited[g.getEdgeIndex(edge)]) branchCount++;
		}
		// if the atom has more than one unvisited bond but has a label at least one branch can be omitted
		if (markers[g.getNodeIndex(node)] > 0) branchCount--;
		
		
		for (int i = g.getDegree(node) - 1; i >= 0; i--) {
			int edge = g.getNodeEdge(node, i);
			
			if (! edgeVisited[g.getEdgeIndex(edge)]) {
				int neighbour = g.getOtherNode(edge, node);
				
				if (branchCount > 1) buf.append('(');
				buf.append(BOND_SYMBOLS[g.getEdgeLabel(edge)]);
				
				edgeVisited[g.getEdgeIndex(edge)] = true;
				if (nodeVisited[g.getNodeIndex(neighbour)] && (markers[g.getNodeIndex(neighbour)] > 0)) {
					buf.append("@" + markers[g.getNodeIndex(neighbour)]);
				} else if (! nodeVisited[g.getNodeIndex(neighbour)]) {
					buildSLN(g, neighbour, nodeVisited, edgeVisited, markers, buf);
				}
				if (branchCount > 1) buf.append(')');
			}
		}		
	}
	
	/**
	 * Recursively identifies the atoms that need markers in a SLN.
	 * @param g the graph
	 * @param node the current node in the DFS search
	 * @param nodeVisited an array where each visited atom has a <code>true</code> entry at its index
	 * @param edgeVisited an array where each visited edge has a <code>true</code> entry at its index
	 * @param needsMarker an array where each node has a <code>true</code> entry at its index if it needs a marker
	 */
	private void findAtomMarkers(Graph g, int node, boolean[] nodeVisited, boolean[] edgeVisited, boolean[] needsMarker) {
		nodeVisited[g.getNodeIndex(node)] = true;
		
		for (int i = g.getDegree(node) - 1; i >= 0; i--) {
			int edge = g.getNodeEdge(node, i);
			
			if (! edgeVisited[g.getEdgeIndex(edge)]) {
				int neighbour = g.getOtherNode(edge, node);
				
				edgeVisited[g.getEdgeIndex(edge)] = true;
				if (nodeVisited[g.getNodeIndex(neighbour)]) {
					needsMarker[g.getNodeIndex(neighbour)] = true;
				} else {
					findAtomMarkers(g, neighbour, nodeVisited, edgeVisited, needsMarker);
				}
			}
		}
	}
	
	
//	public static void main(String[] args) throws ParseException {
//		SLNParser p = new SLNParser();
//		// SimpleUndirectedGraphParser p2 = new SimpleUndirectedGraphParser();
//		
//		UndirectedGraph g = p.parse("N[+1](=O)(O[-1])C[4]:C(:CH:CH:CH:CH:@4)SC[15]:NH:C[18]:C(:C:@15CH2CH(NHCH2CH2C(=O)OCH3)C(=O)NHCH(CH2CH2CH2CH2NH2)C(=O)OCH3):CH:CH:CH:CH:@18", GraphParser.LIST_GRAPH);
//		System.out.println(p.serialize(g));
//		String s = p.serialize(g);
//		UndirectedGraph g2 = p.parse(s, GraphParser.LIST_GRAPH);
//		System.out.println(p.serialize(g2));
//		
//		SimpleGraphComparator comp = new SimpleGraphComparator(NodeLabelDegreeComparator.instance, SimpleEdgeComparator.instance);
//		System.out.println(comp.compare(g, g2));
//	}

  /* (non-Javadoc)
   * @see de.parmol.graph.GraphParser#serialize(de.parmol.graph.UndirectedGraph[], java.io.OutputStream)
   */
  public void serialize(Graph[] graphs, OutputStream out) throws IOException {
  	BufferedOutputStream bout = new BufferedOutputStream(out);
  	for (int i = 0; i < graphs.length; i++) {
    	bout.write(graphs[i].getName().getBytes());
    	bout.write(" => ".getBytes());
    	bout.write(serialize(graphs[i]).getBytes());
    	bout.write("\n".getBytes());
    }
  	bout.flush();
  }

  /* (non-Javadoc)
   * @see de.parmol.graph.GraphParser#parse(java.io.InputStream, int)
   */
  public Graph[] parse(InputStream in, GraphFactory factory) throws IOException, ParseException {
    BufferedReader bin = new BufferedReader(new InputStreamReader(in));
    
    LinkedList graphs = new LinkedList();
    String line;
    while ((line = bin.readLine()) != null) {
    	int pos = line.indexOf(" => ");
    	
    	graphs.add(parse(line.substring(pos + " => ".length()), line.substring(0, pos), factory));
    }
    
    return (Graph[]) graphs.toArray(new Graph[graphs.size()]);
  }


	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphParser#getDesiredGraphFactoryProperties()
	 */
	public int getDesiredGraphFactoryProperties() {
		return GraphFactory.UNDIRECTED_GRAPH;
	}


	/*
	 *  (non-Javadoc)
	 * @see de.parmol.parsers.GraphParser#directed()
	 */
	public boolean directed(){ 
		return false; 
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphParser#getNodeLabel(int)
	 */
	public String getNodeLabel(int nodeLabel) {
		return ATOM_SYMBOLS[nodeLabel];
	}
}
