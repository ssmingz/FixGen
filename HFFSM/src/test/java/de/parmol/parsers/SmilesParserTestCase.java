/*
 * Created on Mar 24, 2005
 *
 */
package de.parmol.parsers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

import junit.framework.TestCase;
import de.parmol.graph.Graph;
import de.parmol.graph.NodeLabelDegreeComparator;
import de.parmol.graph.SimpleEdgeComparator;
import de.parmol.graph.SimpleGraphComparator;
import de.parmol.graph.UndirectedListGraph;
import de.parmol.graph.Util;
import de.parmol.parsers.SmilesParser;

/**
 * @author Thorsten.Meinl@informatik.uni-erlangen.de
 *
 */
public class SmilesParserTestCase extends TestCase {
	private final SmilesParser m_parser = new SmilesParser();
	
	public void testRings() throws ParseException {
		String[] slns = new String[] { "ClC2(C3(CP(CC23)(=O)O)C)Cl", "C1CCC11OOOOO1" };
		
		for (int i = 0; i < slns.length; i++) {
			Graph g1 = m_parser.parse(slns[i], UndirectedListGraph.Factory.instance);
		
			String s = m_parser.serialize(g1);
			Graph g2 = m_parser.parse(s, UndirectedListGraph.Factory.instance);
		
			SimpleGraphComparator comp = new SimpleGraphComparator(NodeLabelDegreeComparator.instance, SimpleEdgeComparator.instance);
			assertTrue(comp.compare(g1, g2) == 0);
		}
	}
	
	
	public void testIgnoreHydrogens() throws ParseException {
		Graph g1 = m_parser.parse("[N](-[H])(-[H])-[C](-[H])(-[H])-O", UndirectedListGraph.Factory.instance);		
		String s = m_parser.serialize(g1);
		assertTrue(s.equals("N-C-O") || s.equals("O-C-N"));

		g1 = m_parser.parse("[N](-[H])(-[O]-[H])(-[H])", UndirectedListGraph.Factory.instance);		
		s = m_parser.serialize(g1);
		assertTrue(s.equals("N-O") || s.equals("O-N"));
	}	

	
	public void testAll() throws IOException, ParseException {
		BufferedReader bin = new BufferedReader(new InputStreamReader(new FileInputStream("data/NCI_full.test")));
    
    String line;
    int i = 0;
    while ((line = bin.readLine()) != null) {
    	int pos = line.indexOf(" => ");
    	
    	Graph g1 = SmilesParser.instance.parse(line.substring(pos + " => ".length()), line.substring(0, pos), UndirectedListGraph.Factory.instance);
    	String s = SmilesParser.instance.serialize(g1);
    	Graph g2 = SmilesParser.instance.parse(s, UndirectedListGraph.Factory.instance);
    	
    	boolean b = (SimpleGraphComparator.instance.compare(g1, Util.getNodePartitions(g1), g2, Util.getNodePartitions(g2)) == 0);
    	if (! b) {
    		System.out.println(line.substring(pos + " => ".length()) + " <=> " + SmilesParser.instance.serialize(g1) + " <=> " + SmilesParser.instance.serialize(g2));
    	}
    	assertTrue(b);
    	i++;
    }
		
	}
}
