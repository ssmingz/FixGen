/*
 * Created on Aug 20, 2004
 *
 */
package de.parmol.search;

import java.util.ArrayList;

import de.parmol.search.DFSSearchable;
import de.parmol.search.SearchTreeNode;
import de.parmol.search.ThreadedDFSSearch;

/**
 * @author Thorsten.Meinl@informatik.uni-erlangen.de
 *
 */
public class ThreadedBFSSearchTest implements DFSSearchable { 
	public static int calls;
	private final Settings m_settings;
	
	public ThreadedBFSSearchTest(Settings settings) {
		m_settings = settings;
	}

	protected ThreadedBFSSearchTest(ThreadedBFSSearchTest previousWorker) {
		m_settings = previousWorker.m_settings;
	}
	
	/* (non-Javadoc)
	 * @see de.parmol.search.BFSSearch#generateChildren(de.parmol.search.SearchTreeNode, de.parmol.search.SearchTreeNode)
	 */
	public void generateChildren(SearchTreeNode currentNode) {
		if (currentNode.getLevel() < m_settings.levels) {
			int max = m_settings.children; // (int) (System.currentTimeMillis() % 16);
			ArrayList children = new ArrayList(max);
			for (int i = 0; i < max; i++) {
				children.add(new SearchTreeNode(currentNode, currentNode.getLevel() + 1));
			}
			currentNode.addChildren(children);
			// for (int i = 0; i < m_loops; i++) { ack(2,3); }
			for (int i = 0; i < m_settings.loops; i++) {
				// Arrays.sort(new int[25]);
				ack(2,3);
			}
		}		
		synchronized(this) { calls++; }
	}


	private static long ack(long n, long m) {
    if (n == 0) return m + 1;
    if (m == 0) return ack(n - 1, 1);
    return ack(n - 1, ack(n, m - 1));
	}
	
	public static void main(String [] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Settings settings = new Settings();
		settings.maxThreads = Integer.parseInt(args[0]);
		settings.levels = Integer.parseInt(args[1]);
		settings.children = Integer.parseInt(args[2]);
		settings.loops = Integer.parseInt(args[3]);
		
		long time = System.currentTimeMillis();
		ThreadedBFSSearchTest test = new ThreadedBFSSearchTest(settings);
		ThreadedDFSSearch searchManager = new ThreadedDFSSearch(settings, test);
		
		searchManager.addStartNode(new SearchTreeNode(null,1));
		searchManager.startSearch();
		System.out.println((System.currentTimeMillis() - time) + "ms elapsed");
		System.out.println(calls + " calls to generateChildren");
	}

	/* (non-Javadoc)
	 * @see de.parmol.search.ThreadedDFSSearch#estimateProblemSize(de.parmol.search.SearchTreeNode)
	 */
	public double estimateProblemSize(SearchTreeNode node) {
		return 1.0;
	}

	/* (non-Javadoc)
	 * @see de.parmol.search.ThreadedDFSSearch#newInstance(de.parmol.search.ThreadedDFSSearch.Settings, de.parmol.search.ThreadedDFSSearch[], java.util.ArrayList)
	 */
	public DFSSearchable newInstance(DFSSearchable previousWorker) {
		return new ThreadedBFSSearchTest((ThreadedBFSSearchTest) previousWorker);
	}
	
	public static class Settings extends de.parmol.Settings {
		public Settings() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
			super(new String[0]);
		}
		
		public int levels, children, loops;
	}

	/* (non-Javadoc)
	 * @see de.parmol.search.ThreadedDFSSearch#leaveNode(de.parmol.search.SearchTreeNode)
	 */
	public void leaveNode(SearchTreeNode currentNode) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see de.parmol.search.ThreadedDFSSearch#enterNode(de.parmol.search.SearchTreeNode)
	 */
	public void enterNode(SearchTreeNode currentNode) {
		// do nothing
	}
}
