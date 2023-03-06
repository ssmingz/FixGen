/*
 * Created on Mar 22, 2005
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
package de.parmol.parsers;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;

import de.parmol.graph.ClassifiedGraphFactory;
import de.parmol.graph.Graph;
import de.parmol.graph.GraphClassifier;
import de.parmol.graph.GraphFactory;
import de.parmol.graph.MutableGraph;


/**
 * @author Thorsten.Meinl@informatik.uni-erlangen.de
 *  
 */
public class SmilesParser implements GraphParser {
	private char[] m_smiles;
	private int m_pos;
	private boolean m_ignoreHydrogens = true;
	private boolean m_lastAtomWasReference = false;
	private final BitSet m_fillupFreeValences = new BitSet();
	private final BitSet m_isAromaticAtom = new BitSet();
	private final HashMap m_markers = new HashMap();
	protected GraphClassifier m_classifier;

	/**
	 * An instance of the Smiles parser
	 */
	public final static SmilesParser instance = new SmilesParser();


	/**
	 * Creates a new Smiles parser.
	 */
	public SmilesParser() {
	}


	/**
	 * Creates a new Smiles parser with the given graph classifier.
	 * 
	 * @param classifier a graph classifier
	 */
	public SmilesParser(GraphClassifier classifier) {
		m_classifier = classifier;
	}


	/**
	 * Sets the value of the ignore hydrogen atoms flag
	 * 
	 * @param newValue the new flag value
	 */
	public void setIgnoreHydrogens(boolean newValue) {
		m_ignoreHydrogens = newValue;
	}


	/**
	 * Return the current value of the ignore hydrogen atoms flag
	 * 
	 * @return the flag value
	 */
	public boolean getIgnoreHydrogens() {
		return m_ignoreHydrogens;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.parsers.GraphParser#parse(java.lang.String, de.parmol.graph.GraphFactory)
	 */
	public Graph parse(String text, GraphFactory factory) throws ParseException {
		return parse(text, null, factory);
	}


	/**
	 * Parses the given Smiles string and creates a new graph with the given id using the given graph factory.
	 * 
	 * @param text a Smiles string
	 * @param id the id of the new graph
	 * @param factory a factory for graphs
	 * @return a new graph
	 * @throws ParseException if the Smiles string contains errors
	 */
	public Graph parse(String text, String id, GraphFactory factory) throws ParseException {
		MutableGraph g;
		if ((factory instanceof ClassifiedGraphFactory) && (m_classifier != null)) {
			g = (MutableGraph) ((ClassifiedGraphFactory) factory).createGraph(id, m_classifier.getClassFrequencies(id));
		} else {
			g = factory.createGraph(id);
		}

		m_smiles = text.toCharArray();
		m_pos = 0;
		m_fillupFreeValences.clear();
		m_isAromaticAtom.clear();

		chain(g, Graph.NO_NODE);

		fillupValences(g);
		return g;
	}


	private int chain(MutableGraph g, int previousNode) throws ParseException {
		while (m_pos < m_smiles.length) {
			while (nextChar(true) == '(') {
				int x = branch(g, previousNode);
				if (previousNode == Graph.NO_NODE) previousNode = x;
				if (m_pos >= m_smiles.length) return previousNode;
			}

			if (nextChar(true) == ')') {
				break;
			}


			int bondLabel = bond();
			int node = atom(g);
			if ((node != Graph.NO_NODE) && (previousNode != Graph.NO_NODE) && (bondLabel != Graph.NO_EDGE)) {
				final boolean aromaticBond = m_isAromaticAtom.get(g.getNodeIndex(node))
						&& m_isAromaticAtom.get(g.getNodeIndex(previousNode));

				g.addEdge(previousNode, node, (aromaticBond ? 4 : bondLabel));
			}
			if ((node != Graph.NO_NODE) && !m_lastAtomWasReference) previousNode = node;
		}

		return previousNode;
	}


	private int branch(MutableGraph g, int previousNode) throws ParseException {
		char c;
		if ((c = nextChar(false)) != '(') throw new ParseException("Expected '(' but found '" + c + "'", m_pos);

		int node = chain(g, previousNode);

		if ((c = nextChar(false)) != ')') throw new ParseException("Expected ')' but found '" + c + "'", m_pos);
		return node;
	}


	private int bond() throws ParseException {
		char c = nextChar(true);
		if ((c == '/') || (c == '\\')) {
			m_pos++;
			c = nextChar(true);
		}

		switch (c) {
			case '.':
				m_pos++;
				return Graph.NO_EDGE;
			case '=':
				m_pos++;
				return 2;
			case '#':
				m_pos++;
				return 3;
			case ':':
				m_pos++;
				return 4;
			case '-':
				m_pos++;
			default:
				return 1;
		}
	}


	private void fillupValences(MutableGraph g) {
		if (m_ignoreHydrogens) return;

		for (int i = g.getNodeCount() - 1; i >= 0; i--) {
			if (m_fillupFreeValences.get(i)) {
				final int node = g.getNode(i);

				int freeValences = 0;
				switch (g.getNodeLabel(node)) {
					case 5:
						freeValences = 3;
						break;
					case 6:
						freeValences = 4;
						break;
					case 7:
						freeValences = 3;
						break;
					case 8:
						freeValences = 2;
						break;
					case 15:
						freeValences = 3;
						break;
					case 16:
						freeValences = 2;
						break;
					case 17:
						freeValences = 1;
						break;
					case 35:
						freeValences = 1;
						break;
					case 53:
						freeValences = 1;
						break;
					default:
						throw new RuntimeException("fillup valences set but no organic subset atom found");
				}

				for (int k = g.getDegree(node) - 1; k >= 0; k--) {
					freeValences -= g.getEdgeLabel(g.getNodeEdge(node, k));
				}

				for (int k = freeValences - 1; k >= 0; k--) {
					g.addEdge(node, g.addNode(1), 1);
				}
			}
		}
	}


	private int atom(MutableGraph g) throws ParseException {
		boolean bracketFound = false;

		char c = nextChar(false);
		if (c == '[') {
			bracketFound = true;
			c = nextChar(false);
		}

		if (c == '*') { // keine Ahnung was das ist
			c = nextChar(false);
		}


		boolean aromaticAtom = false;
		m_lastAtomWasReference = false;
		int label;
		switch (c) {
			case '%':
				Integer nodeReference = (Integer) m_markers.remove(new Integer((nextChar(false) - '0') * 10
						+ (nextChar(false) - '0')));
				if (nodeReference != null) {
					m_lastAtomWasReference = true;
					return nodeReference.intValue();
				} else {
					m_pos -= 2;
					if ((m_smiles[m_pos - 2] == '-') || (m_smiles[m_pos - 2] == '=') || (m_smiles[m_pos - 2] == '#')
							|| (m_smiles[m_pos - 2] == ':')) {
						m_markers.put(new Integer((nextChar(false) - '0') * 10 + (nextChar(false) - '0')), new Integer(g.getNode(g
								.getNodeCount() - 1)));
						return Graph.NO_NODE;
					}

					throw new ParseException("Use of undefined ring marker: " + c, m_pos);
				}
			case '1':
				if (nextChar(true) == '0') {
					m_pos++;
					c = ('9' + 1); // ring markers up to 10 may be specified without '%'
				}
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				nodeReference = (Integer) m_markers.remove(new Integer(c - '0'));
				if (nodeReference != null) {
					m_lastAtomWasReference = true;
					return nodeReference.intValue();
				} else {
					if ((m_smiles[m_pos - 2] == '-') || (m_smiles[m_pos - 2] == '=') || (m_smiles[m_pos - 2] == '#')
							|| (m_smiles[m_pos - 2] == ':') || (m_smiles[m_pos - 2] == '/') || (m_smiles[m_pos - 2] == '\\')
							|| ((m_smiles[m_pos - 2] >= '1') && (m_smiles[m_pos - 2] <= '9'))) {
						m_markers.put(new Integer(c - '0'), new Integer(g.getNode(g.getNodeCount() - 1)));
						return Graph.NO_NODE;
					}

					throw new ParseException("Use of undefined ring marker: " + c, m_pos);
				}
			case 'A':
				if (!bracketFound) throw new ParseException("Expected '[' before atom symbol", m_pos);
				c = nextChar(false);
				switch (c) {
					case 'l':
						label = 13;
						break;
					case 'r':
						label = 18;
						break;
					case 's':
						label = 33;
						break;
					case 'g':
						label = 47;
						break;
					case 'u':
						label = 79;
						break;
					case 't':
						label = 85;
						break;
					case 'c':
						label = 89;
						break;
					case 'm':
						label = 95;
						break;
					default:
						throw new ParseException("Unknown atom symbol: A" + c, m_pos);
				}
				break;
			case 'b':
				aromaticAtom = true;
			case 'B':
				c = nextChar(true);
				switch (c) {
					case 'e':
						label = 4;
						m_pos++;
						break;
					case 'r':
						label = 35;
						m_pos++;
						break;
					case 'a':
						label = 56;
						m_pos++;
						break;
					case 'i':
						label = 83;
						m_pos++;
						break;
					case 'h':
						label = 107;
						m_pos++;
						break;
					case 'k':
						label = 97;
						m_pos++;
						break;
					default:
						label = 5;
				}
				break;
			case 'c':
				aromaticAtom = true;
			case 'C':
				c = nextChar(true);
				switch (c) {
					case 'l':
						label = 17;
						m_pos++;
						break;
					case 'a':
						label = 20;
						m_pos++;
						break;
					case 'r':
						label = 24;
						m_pos++;
						break;
					case 'o':
						label = 27;
						m_pos++;
						break;
					case 'u':
						label = 29;
						m_pos++;
						break;
					case 'd':
						label = 48;
						m_pos++;
						break;
					case 's':
						label = 55;
						m_pos++;
						break;
					case 'e':
						label = 58;
						m_pos++;
						break;
					case 'f':
						label = 98;
						m_pos++;
						break;
					default:
						label = 6;
				}
				break;
			case 'D':
				if (!bracketFound) throw new ParseException("Expected '[' before atom symbol", m_pos);
				c = nextChar(false);
				switch (c) {
					case 'b':
						label = 105;
						break;
					case 'y':
						label = 66;
						break;
					case 's':
						label = 110;
						break;
					default:
						throw new ParseException("Unknown atom symbol: D" + c, m_pos);
				}
				break;
			case 'E':
				if (!bracketFound) throw new ParseException("Expected '[' before atom symbol", m_pos);
				c = nextChar(false);
				switch (c) {
					case 'u':
						label = 63;
						break;
					case 'r':
						label = 68;
						break;
					case 's':
						label = 99;
						break;
					default:
						throw new ParseException("Unknown atom symbol: E" + c, m_pos);
				}
				break;
			case 'F':
				c = nextChar(true);
				switch (c) {
					case 'e':
						label = 26;
						m_pos++;
						break;
					case 'r':
						label = 87;
						m_pos++;
						break;
					default:
						label = 9;
				}
				break;
			case 'G':
				if (!bracketFound) throw new ParseException("Expected '[' before atom symbol", m_pos);
				c = nextChar(false);
				switch (c) {
					case 'a':
						label = 31;
						break;
					case 'e':
						label = 32;
						break;
					case 'd':
						label = 64;
						break;
					default:
						throw new ParseException("Unknown atom symbol: G" + c, m_pos);
				}
				break;
			case 'H':
				if (!bracketFound) throw new ParseException("Expected '[' before atom symbol", m_pos);
				c = nextChar(true);
				switch (c) {
					case 'e':
						label = 2;
						m_pos++;
						break;
					case 'f':
						label = 72;
						m_pos++;
						break;
					case 'g':
						label = 80;
						m_pos++;
						break;
					case 's':
						label = 108;
						m_pos++;
						break;
					case 'o':
						label = 67;
						m_pos++;
						break;
					default:
						label = 1;
				}
				break;
			case 'I':
				c = nextChar(true);
				switch (c) {
					case 'n':
						label = 49;
						m_pos++;
						break;
					case 'r':
						label = 77;
						m_pos++;
						break;
					default:
						label = 53;
				}
				break;
			case 'K':
				if (!bracketFound) throw new ParseException("Expected '[' before atom symbol", m_pos);
				c = nextChar(true);
				switch (c) {
					case 'r':
						label = 35;
						m_pos++;
						break;
					default:
						label = 19;
				}
				break;
			case 'L':
				if (!bracketFound) throw new ParseException("Expected '[' before atom symbol", m_pos);
				c = nextChar(false);
				switch (c) {
					case 'i':
						label = 3;
						break;
					case 'u':
						label = 71;
						break;
					case 'r':
						label = 103;
						break;
					case 'a':
						label = 57;
						break;
					default:
						throw new ParseException("Unknown atom symbol: L" + c, m_pos);
				}
				break;
			case 'M':
				if (!bracketFound) throw new ParseException("Expected '[' before atom symbol", m_pos);
				c = nextChar(false);
				switch (c) {
					case 'g':
						label = 12;
						break;
					case 'n':
						label = 25;
						break;
					case 'o':
						label = 42;
						break;
					case 't':
						label = 109;
						break;
					case 'd':
						label = 101;
						break;
					default:
						throw new ParseException("Unknown atom symbol: M" + c, m_pos);
				}
				break;
			case 'n':
				aromaticAtom = true;
			case 'N':
				c = nextChar(true);
				switch (c) {
					case 'e':
						label = 10;
						m_pos++;
						break;
					case 'a':
						label = 11;
						m_pos++;
						break;
					case 'i':
						label = 28;
						m_pos++;
						break;
					case 'b':
						label = 41;
						m_pos++;
						break;
					case 'd':
						label = 60;
						m_pos++;
						break;
					case 'p':
						label = 93;
						m_pos++;
						break;
					case 'o':
						label = 102;
						m_pos++;
						break;
					default:
						label = 7;
				}
				break;
			case 'o':
				aromaticAtom = true;
			case 'O':
				c = nextChar(true);
				switch (c) {
					case 's':
						label = 76;
						m_pos++;
						break;
					default:
						label = 8;
				}
				break;
			case 'p':
				aromaticAtom = true;
			case 'P':
				c = nextChar(true);
				switch (c) {
					case 'd':
						label = 46;
						m_pos++;
						break;
					case 't':
						label = 78;
						m_pos++;
						break;
					case 'b':
						label = 82;
						m_pos++;
						break;
					case 'o':
						label = 84;
						m_pos++;
						break;
					case 'r':
						label = 59;
						m_pos++;
						break;
					case 'm':
						label = 61;
						m_pos++;
						break;
					case 'a':
						label = 91;
						m_pos++;
						break;
					case 'u':
						label = 94;
						m_pos++;
						break;
					default:
						label = 15;
				}
				break;
			case 'R':
				if (!bracketFound) throw new ParseException("Expected '[' before atom symbol", m_pos);
				c = nextChar(false);
				switch (c) {
					case 'b':
						label = 37;
						break;
					case 'u':
						label = 44;
						break;
					case 'h':
						label = 45;
						break;
					case 'e':
						label = 75;
						break;
					case 'n':
						label = 86;
						break;
					case 'a':
						label = 88;
						break;
					default:
						throw new ParseException("Unknown atom symbol: M" + c, m_pos);
				}
				break;
			case 's':
				aromaticAtom = true;
			case 'S':
				c = nextChar(true);
				switch (c) {
					case 'i':
						label = 14;
						m_pos++;
						break;
					case 'c':
						label = 21;
						m_pos++;
						break;
					case 'e':
						label = 34;
						m_pos++;
						break;
					case 'r':
						label = 38;
						m_pos++;
						break;
					case 'n':
						label = 50;
						m_pos++;
						break;
					case 'b':
						label = 51;
						m_pos++;
						break;
					case 'g':
						label = 106;
						m_pos++;
						break;
					case 'm':
						label = 62;
						m_pos++;
						break;
					default:
						label = 16;
				}
				break;
			case 'T':
				if (!bracketFound) throw new ParseException("Expected '[' before atom symbol", m_pos);
				c = nextChar(false);
				switch (c) {
					case 'i':
						label = 22;
						break;
					case 'c':
						label = 43;
						break;
					case 'e':
						label = 52;
						break;
					case 'a':
						label = 73;
						break;
					case 'l':
						label = 81;
						break;
					case 'b':
						label = 65;
						break;
					case 'm':
						label = 69;
						break;
					case 'h':
						label = 90;
						break;
					default:
						throw new ParseException("Unknown atom symbol: T" + c, m_pos);
				}
				break;
			case 'U':
				if (!bracketFound) throw new ParseException("Expected '[' before atom symbol", m_pos);
				c = nextChar(true);
				switch (c) {
					case 'u':
						c = nextChar(true);
						switch (c) {
							case 'u':
								label = 111;
								m_pos += 2;
								break;
							case 'b':
								label = 112;
								m_pos += 2;
								break;
							case 'q':
								label = 114;
								m_pos += 2;
								break;
							default:
								throw new ParseException("Unknown atom symbol: Uu" + c, m_pos);
						}
						break;
					default:
						label = 92;
				}
				break;
			case 'V':
				if (!bracketFound) throw new ParseException("Expected '[' before atom symbol", m_pos);
				label = 23;
				break;
			case 'W':
				if (!bracketFound) throw new ParseException("Expected '[' before atom symbol", m_pos);
				label = 74;
				break;
			case 'X':
				if (!bracketFound) throw new ParseException("Expected '[' before atom symbol", m_pos);
				c = nextChar(false);
				switch (c) {
					case 'e':
						label = 54;
						break;
					default:
						throw new ParseException("Unknown atom symbol: T" + c, m_pos);
				}
				break;
			case 'Y':
				if (!bracketFound) throw new ParseException("Expected '[' before atom symbol", m_pos);
				c = nextChar(true);
				switch (c) {
					case 'b':
						label = 70;
						m_pos++;
						break;
					default:
						label = 39;
				}
				break;
			case 'Z':
				if (!bracketFound) throw new ParseException("Expected '[' before atom symbol", m_pos);
				c = nextChar(false);
				switch (c) {
					case 'n':
						label = 30;
						break;
					case 'r':
						label = 40;
						break;
					default:
						throw new ParseException("Unknown atom symbol: T" + c, m_pos);
				}
				break;
			default:
				throw new ParseException("Unknown atom symbol: " + c, m_pos);
		}

		final int node = g.addNode(label);
		if (aromaticAtom) {
			m_isAromaticAtom.set(g.getNodeIndex(node));
		}

		if (bracketFound) {
			// skip chiral specifications
			while (nextChar(true) == '@') {
				m_pos++;
			}

			// skip charges
			while (true) {
				c = nextChar(true);
				if ((c == '+') || (c == '-')) {
					m_pos++;
					if ((nextChar(true) >= '1') && (nextChar(true) <= '9')) {
						m_pos++; // skip explicit charge specification
						break;
					}
				} else {
					break;
				}
			}

			// check for explicit hydrogens
			if (nextChar(true) == 'H') {
				m_pos++;

				int hydrogens;
				if ((nextChar(true) >= '1') && (nextChar(true) <= '9')) {
					hydrogens = nextChar(false) - '0';
				} else {
					hydrogens = 1;
				}

				if (!m_ignoreHydrogens) {
					for (int k = hydrogens - 1; k >= 0; k--) {
						g.addEdge(node, g.addNode(1), 1);
					}
				}

				while ((nextChar(true) == '-') || (nextChar(true) == '+')) {
					m_pos++;
					if ((nextChar(true) >= '0') && (nextChar(true) <= '9')) m_pos++;
				}
			}

			c = nextChar(false);
			if (!(c == ']')) { throw new ParseException("Expected ']', but found " + c, m_pos); }
		}

		c = nextChar(true);
		if ((c >= '1') && (c <= '9')) {
			do {
				if (c == '1') {
					m_pos++;
					if (nextChar(true) == '0') { // markers up to 10 may be specified without '%'
						c = ('9' + 1);
					} else {
						m_pos--;
					}
				}

				Integer ref;
				if ((ref = (Integer) m_markers.remove(new Integer(c - '0'))) == null) {
					m_pos++;
					m_markers.put(new Integer(c - '0'), new Integer(node));
				} else {
					m_pos++;
					final boolean aromaticBond = m_isAromaticAtom.get(g.getNodeIndex(node))
							&& m_isAromaticAtom.get(g.getNodeIndex(ref.intValue()));
					g.addEdge(node, ref.intValue(), (aromaticBond ? 4 : 1));
				}
				c = nextChar(true);
			} while ((c >= '1') && (c <= '9'));
		}

		if (c == '%') {
			do {
				m_pos++;
				int marker = (nextChar(false) - '0') * 10 + (nextChar(false) - '0');

				Integer ref;
				if ((ref = (Integer) m_markers.remove(new Integer(marker))) == null) {
					m_markers.put(new Integer(marker), new Integer(node));
				} else {
					final boolean aromaticBond = m_isAromaticAtom.get(g.getNodeIndex(node))
							&& m_isAromaticAtom.get(g.getNodeIndex(ref.intValue()));
					g.addEdge(node, ref.intValue(), (aromaticBond ? 4 : 1));
				}
				c = nextChar(true);
			} while (c == '%');
		}

		if (!bracketFound && isOrganicSubset(label)) {
			m_fillupFreeValences.set(g.getNodeIndex(node));
		}

		return node;
	}


	private char nextChar(boolean lookAhead) throws ParseException {
		if (m_pos < m_smiles.length) {
			char c = m_smiles[m_pos];
			if (!lookAhead) m_pos++;
			return c;
		} else {
			if (lookAhead) return '\0';
			throw new ParseException("No more characters", m_pos);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.graph.GraphParser#serialize(de.parmol.graph.UndirectedGraph)
	 */
	public String serialize(Graph g) {
		final boolean[] nodeVisited = new boolean[g.getNodeCount()];
		final boolean[] edgeVisited = new boolean[g.getEdgeCount()];
		final int[] markerCount = new int[g.getNodeCount()];

		// first identify atoms that need markers
		for (int i = g.getNodeCount() - 1; i >= 0; i--) {
			int node = g.getNode(i);

			if (!nodeVisited[g.getNodeIndex(node)]) {
				findAtomMarkers(g, node, nodeVisited, edgeVisited, markerCount);
			}
		}

		// then build the Smiles
		for (int i = nodeVisited.length - 1; i >= 0; i--)
			nodeVisited[i] = false;
		for (int i = edgeVisited.length - 1; i >= 0; i--)
			edgeVisited[i] = false;

		final int[] markers = new int[g.getNodeCount()];
		int count = 0;
		for (int i = markerCount.length - 1; i >= 0; i--) {
			for (int k = markerCount[i] - 1; k >= 0; k--) {
				markers[i] |= (1 << count++);
			}
		}

		StringBuffer buf = new StringBuffer(1024);

		count = 0;
		for (int i = g.getNodeCount() - 1; i >= 0; i--) {
			int node = g.getNode(i);

			if (!nodeVisited[g.getNodeIndex(node)]) {
				if (count++ > 0) {
					buf.append('.');
				}
				buildSmiles(g, node, nodeVisited, edgeVisited, markers, buf);
			}
		}

		return buf.toString();
	}


	private boolean isOrganicSubset(int label) {
		return ((label == 5) || (label == 6) || (label == 7) || (label == 8) || (label == 15) || (label == 16)
				|| (label == 17) || (label == 35) || (label == 53));
	}


	/**
	 * Recursively build a SLN.
	 * 
	 * @param g the graph
	 * @param node the current node in the DFS search through the molecule graph
	 * @param nodeVisited an array where each visited atom has a <code>true</code> entry at its index
	 * @param edgeVisited an array where each visited edge has a <code>true</code> entry at its index
	 * @param markers an array where the marker number of each atom is stored; 0 means no marker
	 * @param buf the StringBuffer that holds the SLN
	 */
	private void buildSmiles(Graph g, int node, boolean[] nodeVisited, boolean[] edgeVisited, int[] markers,
			StringBuffer buf) {
		nodeVisited[g.getNodeIndex(node)] = true;

		if (g.getNodeLabel(node) != 1) {
			boolean needsBrackets = false;
			if (isOrganicSubset(g.getNodeLabel(node))) {
				int freeValences = 0;
				switch (g.getNodeLabel(node)) {
					case 5:
						freeValences = 3;
						break;
					case 6:
						freeValences = 4;
						break;
					case 7:
						freeValences = 3;
						break;
					case 8:
						freeValences = 2;
						break;
					case 15:
						freeValences = 3;
						break;
					case 16:
						freeValences = 2;
						break;
					case 17:
						freeValences = 1;
						break;
					case 35:
						freeValences = 1;
						break;
					case 53:
						freeValences = 1;
						break;
					default:
						throw new RuntimeException("fillup valences set but no organic subset atom found");
				}

				for (int k = g.getDegree(node) - 1; k >= 0; k--) {
					freeValences -= g.getEdgeLabel(g.getNodeEdge(node, k));
				}

				boolean hydrogenFound = false;
				for (int i = g.getDegree(node) - 1; i >= 0; i--) {
					final int edge = g.getNodeEdge(node, i);

					if (g.getNodeLabel(g.getOtherNode(edge, node)) == 1) {
						hydrogenFound = true;
						break;
					}
				}

				if (hydrogenFound && (freeValences != 0)) {
					needsBrackets = true;
				}
			} else {
				needsBrackets = true;
			}

			if (needsBrackets) {
				buf.append('[');
				buf.append(SLNParser.ATOM_SYMBOLS[g.getNodeLabel(node)]);

				int hydrogens = 0;
				for (int i = g.getDegree(node) - 1; i >= 0; i--) {
					final int edge = g.getNodeEdge(node, i);

					if (g.getNodeLabel(g.getOtherNode(edge, node)) == 1) hydrogens++;
				}

				if (hydrogens == 1) {
					buf.append('H');
				} else if (hydrogens > 1) {
					buf.append('H').append(hydrogens);
				}

				buf.append(']');
			} else {
				boolean isAromatic = false;
				if (isOrganicSubset(g.getNodeLabel(node))) {
					for (int k = g.getDegree(node) - 1; k >= 0; k--) {
						if (g.getEdgeLabel(g.getNodeEdge(node, k)) == 4) {
							isAromatic = true;
							break;
						}
					}
				}


				if (isAromatic) {
					buf.append(SLNParser.ATOM_SYMBOLS[g.getNodeLabel(node)].toLowerCase());
				} else {
					buf.append(SLNParser.ATOM_SYMBOLS[g.getNodeLabel(node)]);
				}
			}

			if (markers[g.getNodeIndex(node)] != 0) { // atom needs a marker
				final int marker = markers[g.getNodeIndex(node)];
				for (int k = 0; k < 32; k++) {
					if ((marker & (1 << k)) != 0) {
						if (k > 9) {
							buf.append('%').append(k + 1);
						} else {
							buf.append(k + 1);
						}
					}
				}
			}
		}


		// check the number of branches; if an atom has only one unvisited bond it does not need a branch
		int branchCount = 0;
		for (int i = g.getDegree(node) - 1; i >= 0; i--) {
			final int edge = g.getNodeEdge(node, i);
			if (!edgeVisited[g.getEdgeIndex(edge)]) {
				if ((g.getNodeLabel(g.getOtherNode(edge, node)) != 1) || (g.getDegree(g.getOtherNode(edge, node)) > 1))
						branchCount++;
			}
		}
		// if the atom has more than one unvisited bond but has a marker at least one branch can be omitted
		if (markers[g.getNodeIndex(node)] > 0) branchCount--;


		for (int i = g.getDegree(node) - 1; i >= 0; i--) {
			int edge = g.getNodeEdge(node, i);

			if (!edgeVisited[g.getEdgeIndex(edge)]) {
				int neighbour = g.getOtherNode(edge, node);

				if ((g.getNodeLabel(neighbour) == 1) && (g.getDegree(neighbour) == 1)) {
					nodeVisited[g.getNodeIndex(neighbour)] = true;
					continue;
				}

				boolean nextAtomIsReference = (nodeVisited[g.getNodeIndex(neighbour)] && (markers[g.getNodeIndex(neighbour)] != 0));
				if ((branchCount > 1) && !nextAtomIsReference) buf.append('(');
				if ((g.getNodeLabel(node) != 1) && (g.getEdgeLabel(edge) != 4))
						buf.append(SLNParser.BOND_SYMBOLS[g.getEdgeLabel(edge)]);

				edgeVisited[g.getEdgeIndex(edge)] = true;
				if (nextAtomIsReference) {
					final int marker = markers[g.getNodeIndex(neighbour)];

					for (int k = 0; k < 32; k++) {
						if ((marker & (1 << k)) != 0) {
							if (k > 9) {
								buf.append('%').append(k + 1);
							} else {
								buf.append(k + 1);
							}
							markers[g.getNodeIndex(neighbour)] &= ~(1 << k);
							break;
						}
					}
				} else if (!nodeVisited[g.getNodeIndex(neighbour)]) {
					buildSmiles(g, neighbour, nodeVisited, edgeVisited, markers, buf);
				}
				if ((branchCount > 1) && !nextAtomIsReference) buf.append(')');
			}
		}
	}


	/**
	 * Recursively identifies the atoms that need markers in a Smiles.
	 * 
	 * @param g the graph
	 * @param node the current node in the DFS search
	 * @param nodeVisited an array where each visited atom has a <code>true</code> entry at its index
	 * @param edgeVisited an array where each visited edge has a <code>true</code> entry at its index
	 * @param markerCount an array where the number of needed markers for each atom is counted
	 */
	private void findAtomMarkers(Graph g, int node, boolean[] nodeVisited, boolean[] edgeVisited, int[] markerCount) {
		nodeVisited[g.getNodeIndex(node)] = true;

		for (int i = g.getDegree(node) - 1; i >= 0; i--) {
			int edge = g.getNodeEdge(node, i);

			if (!edgeVisited[g.getEdgeIndex(edge)]) {
				int neighbour = g.getOtherNode(edge, node);

				edgeVisited[g.getEdgeIndex(edge)] = true;
				if (nodeVisited[g.getNodeIndex(neighbour)]) {
					markerCount[g.getNodeIndex(neighbour)]++;
				} else {
					findAtomMarkers(g, neighbour, nodeVisited, edgeVisited, markerCount);
				}
			}
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.parsers.GraphParser#serialize(de.parmol.graph.Graph[], java.io.OutputStream)
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


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.parsers.GraphParser#parse(java.io.InputStream, de.parmol.graph.GraphFactory)
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


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.parsers.GraphParser#getDesiredGraphFactoryProperties()
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parmol.parsers.GraphParser#getNodeLabel(int)
	 */
	public String getNodeLabel(int nodeLabel) {
		return SLNParser.ATOM_SYMBOLS[nodeLabel];
	}

}