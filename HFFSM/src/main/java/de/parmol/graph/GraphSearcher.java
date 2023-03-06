/*
 * 
 * This file is part of ParMol. ParMol is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 * 
 * ParMol is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * ParMol; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 * 
 */
package de.parmol.graph;

import de.parmol.util.IntQueue;
import de.parmol.util.ObjectStack;

/**
 * This class is a framework for searching through all nodes of graph. You an either do a depth-first search or an
 * breadth-first search through the graph. During the search a bunch of callback methods are called. You can implement 
 * them in subclasses and do the appropiate stuff.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public abstract class GraphSearcher {
  /**
   * Performs a depth-first search through the graph starting at the given node.
   * @param g the graph to be searched
   * @param startNode the starting node
   */
  public void dfs(Graph g, int startNode) {    
    ObjectStack stack = new ObjectStack(g.getNodeCount());
    boolean[] visited = new boolean[g.getNodeCount()];

    stack.push(new int[] { startNode, 0, g.getDegree(startNode) });
    if (! enterNode(g, -1, Graph.NO_EDGE, startNode))
      return;

    outer:
    while (! stack.empty()) {
      final int[] item = (int[]) stack.getTop();      

      if (! visited[g.getNodeIndex(item[0])] && ! enteredNode(g, item[0]))
        break;
      visited[g.getNodeIndex(item[0])] = true;

      while (item[1] < item[2]) {
        int edge = g.getNodeEdge(item[0], item[1]++);
        int nextNode = g.getOtherNode(edge, item[0]);
        if (! visited[g.getNodeIndex(nextNode)]) {
          stack.push(new int[] { nextNode, 0, g.getDegree(nextNode) });
          if (! enterNode(g, item[0], edge, nextNode)) return;
          continue outer;
        }
      }

      stack.pop();
    }    
  }
  
  /**
   * Performs a depth-first search through the graph
   * @param g the graph to be searched
   */
  public void dfs(Graph g) {
    dfs(g, g.getNode(0));
  }
  
  /**
   * Performs a breadth-first search through the graph starting at the given node.
   * @param g the graph to be searched
   * @param startNode the starting node
   */
  public void bfs(Graph g, int startNode) {
    IntQueue nodes = new IntQueue(g.getNodeCount());
    boolean[] visited = new boolean[g.getNodeCount()];
    
    if (! enterNode(g, Graph.NO_NODE, Graph.NO_EDGE, startNode)) return;
    nodes.enqueue(startNode);
    visited[g.getNodeIndex(startNode)] = true;
    

    while (! nodes.empty()) {
      int currentNode = nodes.dequeue();
      
      if (! enteredNode(g, currentNode)) break;
      
      // int[] edges = g.getEdges(currentNode);
      for (int i = g.getDegree(currentNode) - 1; i >= 0; i--) {
        int neighbour = g.getOtherNode(g.getNodeEdge(currentNode, i), currentNode);
        if (! visited[g.getNodeIndex(neighbour)]) {
          if (! enterNode(g, currentNode, g.getNodeEdge(currentNode, i), neighbour)) return;
          visited[g.getNodeIndex(neighbour)] = true;
          nodes.enqueue(neighbour);
        }
      }
    }    
  }
  
  /**
   * Performs a breadth-first search through the graph.
   * @param g the graph to be searched
   */
  public void bfs(Graph g) {
    bfs(g, g.getNode(0));
  }
  
  /**
   * This methods is called if a new node has been entered.
   * @param g the graph on which the search is done  
   * @param node the node that has been entered
   * @return <code>false</code> if the search should be interrupted, <code>true</code> otherwise
   */
  public boolean enteredNode(Graph g, int node) { return true; }
  
  /**
   * This method is called just before a new node is entered (i.e. before a new call to the recursive function).
   * @param g the graph on which the search is done
   * @param from the node from which the new node is entered
   * @param edge the edge that connects the current and the new node
   * @param to the node that will be entered next
   * @return <code>false</code> if the search should be interrupted, <code>true</code> otherwise
   */
  public boolean enterNode(Graph g, int from, int edge, int to) { return true; }  
}

