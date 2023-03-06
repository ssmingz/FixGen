/*
 * Created on Mar 30, 2005
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
package de.parmol.FFSM;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;

import junit.framework.TestCase;
import de.parmol.AbstractMiner;
import de.parmol.Settings;
import de.parmol.FFSM.Miner;
import de.parmol.graph.Graph;
import de.parmol.graph.SimpleEdgeComparator;
import de.parmol.graph.SimpleGraphComparator;
import de.parmol.graph.SimpleNodeComparator;
import de.parmol.graph.SimpleSubgraphComparator;
import de.parmol.parsers.SLNParser;
import de.parmol.util.FragmentSet;
import de.parmol.util.FrequentFragment;

/**
 * @author Thorsten.Meinl@informatik.uni-erlangen.de
 *
 */
public class FFSMTest extends TestCase {
	public void testIC93() throws FileNotFoundException, IOException, ParseException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Settings settings = new Settings(new String[0]);
		settings.minimumClassFrequencies[0] = 100;
		settings.closedFragmentsOnly = false;
		settings.graphFile = "data/IC93.test";
		settings.debug = 0;
		settings.maxThreads = 1;
		
		Miner miner = new Miner(settings);
		miner.setUp();
		
		miner.startMining();
		FragmentSet fragments = miner.getFrequentSubgraphs();
		
		// assertEquals(fragments.size(), 1495);
		
		
		SimpleSubgraphComparator comp = new SimpleSubgraphComparator(SimpleNodeComparator.instance, SimpleEdgeComparator.instance);
		for (Iterator it = fragments.iterator(); it.hasNext();) {
			FrequentFragment frag = (FrequentFragment) it.next();
			
			System.out.println("Checking " + SLNParser.instance.serialize(frag.getFragment()));
			
			int count = 0;
			for (Iterator it2 = miner.getGraphs().iterator(); it2.hasNext();) {
				Graph g = (Graph) it2.next();
				
				if (comp.compare(frag.getFragment(), g) == 0) count++;
			}
			
			assertEquals((int) frag.getClassFrequencies()[0], count);
		}		
	}

	
	public void testCrossCheck() throws InstantiationException, IllegalAccessException, ClassNotFoundException, FileNotFoundException, IOException, ParseException {
		Settings settings = new Settings(new String[0]);
		settings.minimumClassFrequencies[0] = 100;
		settings.closedFragmentsOnly = false;
		settings.graphFile = "data/IC93.test";
		settings.debug = 0;
		settings.maxThreads = 1;
		
		AbstractMiner miner = new de.parmol.FFSM.Miner(settings);
		miner.setUp();
		
		miner.startMining();
		FragmentSet ffsmFragments = miner.getFrequentSubgraphs();

		
		miner = new de.parmol.FFSM.Miner(settings);
		miner.setUp();
		
		miner.startMining();
		FragmentSet mofaFragments = miner.getFrequentSubgraphs();

		for (Iterator it = ffsmFragments.iterator(); it.hasNext();) {
			final Graph fragA = ((FrequentFragment) it.next()).getFragment();
			
			boolean found = false;
			for (Iterator it2 = mofaFragments.iterator(); it2.hasNext();) {
				final Graph fragB = ((FrequentFragment) it2.next()).getFragment();
				
				if (SimpleGraphComparator.instance.compare(fragA, fragB) == 0) {
					found = true;
					break;
				}
			}
			
			if (! found) {
				System.err.println("Could not find fragment " + SLNParser.instance.serialize(fragA));
			}
		}

		for (Iterator it = mofaFragments.iterator(); it.hasNext();) {
			final Graph fragA = ((FrequentFragment) it.next()).getFragment();
			
			boolean found = false;
			for (Iterator it2 = ffsmFragments.iterator(); it2.hasNext();) {
				final Graph fragB = ((FrequentFragment) it2.next()).getFragment();
				
				if (SimpleGraphComparator.instance.compare(fragA, fragB) == 0) {
					found = true;
					break;
				}
			}
			
			if (! found) {
				System.err.println("Could not find fragment " + SLNParser.instance.serialize(fragA));
			}
		}
	
	}
}
