/*
 * Created on Jun 14, 2004
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

import java.text.ParseException;

import junit.framework.TestCase;
import de.parmol.graph.Graph;
import de.parmol.graph.UndirectedListGraph;
import de.parmol.parsers.SimpleUndirectedGraphParser;
import de.parmol.util.GraphGenerator;

/**
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class SimpleGraphParserTest extends TestCase {
	private final SimpleUndirectedGraphParser m_parser = new SimpleUndirectedGraphParser();

	/**
	 * @param name
	 */
	public SimpleGraphParserTest(String name) {
		super(name);
	}

	public void testParser() {
		GraphGenerator.instance.setGraphFactory(UndirectedListGraph.Factory.instance);
		GraphGenerator.instance.setNodeLabels(100);
		Graph g = GraphGenerator.instance.generateGraph(100, 5000);

		String text = m_parser.serialize(g);
		System.out.println(text);
		Graph g2;
		try {
			g2 = m_parser.parse(text, UndirectedListGraph.Factory.instance);
		} catch (ParseException ex) {
			throw new RuntimeException(ex);
		}
		System.out.println(m_parser.serialize(g2));
		assertEquals(text, m_parser.serialize(g2));
	}
}