/*
 * Created on Aug 16, 2004
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
package de.parmol.search;

import java.util.ArrayList;
import java.util.Iterator;

import de.parmol.Settings;


/**
 * This class is the base class for all depth first searches that should be done in parallel. After creating an instance
 * of this class the constructor automatically creates as many additional instances as are specified by the maxThreads
 * value from the settings objects. A call to startSearch will the distribute the work over all created
 * ThreadedDFSSearch objects. This means that almost nothing has to be synchronized as each thread has its own data.
 * After the search the results have to be collected but this is the duty of concrete subclasses.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 * 
 */
public class ThreadedDFSSearch extends Thread implements SearchManager {
	private final Settings m_settings;
	private final ThreadedDFSSearch[] m_workers;
	private final ArrayList m_waitingWorkers;
	private final SearchTreeNodeStack m_stack = new SearchTreeNodeStack(20);
	private final DFSSearchable m_searchable;


	private static int s_workerCount;

	{
		s_workerCount++;
	}


	/**
	 * Creates a new threadded dfs searcher.
	 * 
	 * @param settings the settings for the search
	 * @param searchable a DFSSearchable through which the search is done
	 */
	public ThreadedDFSSearch(Settings settings, DFSSearchable searchable) {
		super("Worker " + s_workerCount);

		m_settings = settings;
		m_searchable = searchable;
		m_workers = new ThreadedDFSSearch[m_settings.maxThreads];
		m_waitingWorkers = new ArrayList(m_settings.maxThreads);
		m_workers[0] = this;

		for (int i = 1; i < m_settings.maxThreads; i++) {
			m_workers[i] = new ThreadedDFSSearch(m_workers[i - 1]);
			m_workers[i].start();
		}
		setDaemon(true);
		start();

	}


	/**
	 * This constructor must be called from subclasses of the newInstance method has been called. Shared fields are taken
	 * from the firstWorker-argument.
	 * 
	 * @param previousWorker the first worker that has been created
	 */
	private ThreadedDFSSearch(ThreadedDFSSearch previousWorker) {
		super("Worker " + s_workerCount);
		m_settings = previousWorker.m_settings;
		m_workers = previousWorker.m_workers;
		m_waitingWorkers = previousWorker.m_waitingWorkers;
		m_searchable = previousWorker.m_searchable.newInstance(previousWorker.m_searchable);
		setDaemon(true);
	}


	/**
	 * Does the recursive depth first search. Inside new children nodes are distributed to waiting threads.
	 */
	private void dfsSearch() {
		while (!m_stack.isEmpty()) {
			if (m_stack.size() > 1) {
				ThreadedDFSSearch idle = null;
				synchronized (m_waitingWorkers) {
					if (m_waitingWorkers.size() > 0) {
						idle = (ThreadedDFSSearch) m_waitingWorkers.remove(m_waitingWorkers.size() - 1);
					}
				}

				if (idle != null) {
					m_stack.split(idle.m_stack);
					synchronized (idle.m_stack) {
						idle.m_stack.notifyAll();
					}
				}
			}

			SearchTreeNode currentNode = m_stack.pop();

			m_searchable.enterNode(currentNode);

			m_searchable.generateChildren(currentNode);


			if (m_settings.debug > 4) {
				System.out.println("[" + getName() + "] " + (Runtime.getRuntime().freeMemory() >> 10) + "kB free memory, "
						+ (Runtime.getRuntime().totalMemory() >> 10) + "kB total memory");
			}

			for (Iterator it = currentNode.getChildren().iterator(); it.hasNext();) {
				m_stack.push((SearchTreeNode) it.next());
			}

			m_searchable.leaveNode(currentNode);
		}
	}


	/* (non-Javadoc)
	 * @see de.parmol.search.SearchManager#addStartNode(de.parmol.search.SearchTreeNode)
	 */
	public void addStartNode(SearchTreeNode startNode) {
		m_stack.push(startNode);
	}


	/**
	 * Starts the depth first search.
	 */
	public void startSearch() {
		try {
			synchronized (m_waitingWorkers) {
				if (m_waitingWorkers.size() > 0) {
					boolean b = m_waitingWorkers.remove(this);
					assert (b);
				}
			}

			synchronized (m_stack) {
				m_stack.notifyAll();
			}
			synchronized (m_waitingWorkers) {
				if (m_waitingWorkers.size() < m_workers.length) m_waitingWorkers.wait();
			}
		} catch (InterruptedException ex) {
			System.out.println("One of the workers died, exiting");
			System.exit(1);
		}

		// should not be needed but you never know ;-)
		while (m_waitingWorkers.size() < m_workers.length) {
			System.err
					.println("!!! Something is wrong: The waiting thread has been awakened but a worker is still running !!!");
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			do {
				long time = 0;
				synchronized (m_stack) {
					if (m_stack.isEmpty()) {
						synchronized (m_waitingWorkers) {
							if (m_settings.debug > 2) {
								System.out.println("[" + getName() + "] waiting");
								time = System.currentTimeMillis();
							}

							m_waitingWorkers.add(this);
							if (m_waitingWorkers.size() == m_workers.length) {
								// it seems everyone has finished so wake up the waiting main thread
								m_waitingWorkers.notifyAll();
							}
						}

						m_stack.wait(); // schnarch...
					}
				}

				if (m_settings.debug > 2) {
					System.out.println("[" + getName() + "] awakened " + (System.currentTimeMillis() - time)
							+ "ms deadtime, searching with stack size " + m_stack.size());
				}

				dfsSearch();
			} while (!isInterrupted());
		} catch (InterruptedException ex) {
			// do nothing
		} catch (Throwable ex) {
			ex.printStackTrace();
			Thread.currentThread().getThreadGroup().interrupt();
		}
	}
}