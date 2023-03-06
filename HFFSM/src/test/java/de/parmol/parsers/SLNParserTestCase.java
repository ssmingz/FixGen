/*
 * Created on Jun 25, 2004
 *
 */
package de.parmol.parsers;

import java.text.ParseException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import de.parmol.graph.Graph;
import de.parmol.graph.NodeLabelDegreeComparator;
import de.parmol.graph.SimpleEdgeComparator;
import de.parmol.graph.SimpleGraphComparator;
import de.parmol.graph.UndirectedListGraph;
import de.parmol.parsers.SLNParser;

/**
 * @author Thorsten.Meinl@informatik.uni-erlangen.de
 *
 */
public class SLNParserTestCase extends TestCase {
	private final SLNParser m_parser = new SLNParser();
	
	public SLNParserTestCase(String name) { 	super(name);	}

	public void testRings() throws ParseException {
		String[] slns = new String[] { "ClC[2](C[3](CH2P(CH2CH@2@3)(=O)OH)CH3)Cl", "C[1]-C-C-C-@1-O-O-O-O-O-@1" };
		
		for (int i = 0; i < slns.length; i++) {
			Graph g1 = m_parser.parse(slns[i], UndirectedListGraph.Factory.instance);
		
			String s = m_parser.serialize(g1);
			Graph g2 = m_parser.parse(s, UndirectedListGraph.Factory.instance);
		
			SimpleGraphComparator comp = new SimpleGraphComparator(NodeLabelDegreeComparator.instance, SimpleEdgeComparator.instance);
			assertTrue(comp.compare(g1, g2) == 0);
		}
	}
	
	public void testFusedRings() throws ParseException {
		String[] slns = new String[] { "ClC[2](C[3](CH2P(CH2CH@2@3)(=O)OH)CH3)Cl" };
		int[] ringIDs = { 2 };
		
		Pattern p = Pattern.compile(".*?(\\[\\d+\\]).*");
		for (int i = 0; i < slns.length; i++) {
			Graph g1 = m_parser.parse(slns[i], UndirectedListGraph.Factory.instance);
		
			Matcher m = p.matcher(m_parser.serialize(g1));
			HashSet rings = new HashSet();
			
			if (m.matches()) {
				for (int k = m.groupCount() - 1; k >= 0; k--) {
					rings.add(m.group(k));
				}
			}
			assertEquals(ringIDs[i], rings.size());
		}		
	}
	
	public void testIgnoreHydrogens() throws ParseException {
		Graph g1 = m_parser.parse("N(-H)(-H)-C(-H)(-H)-O", UndirectedListGraph.Factory.instance);		
		String s = m_parser.serialize(g1);
		assertTrue(s.equals("N-C-O") || s.equals("O-C-N"));

		g1 = m_parser.parse("N(-H)(-O-H)(-H)", UndirectedListGraph.Factory.instance);		
		s = m_parser.serialize(g1);
		assertTrue(s.equals("N-O") || s.equals("O-N"));
	}	
}
