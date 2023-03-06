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

/**
 * This interface describes a graph that can be changed in various ways, e.g. nodes and edges can be added and removed.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public interface MutableGraph extends Graph {
  /**
   * Adds a node to the graph which is not connected to any other node. This only makes sense for the first node or for unconnected graphs.
   * @param nodeLabel the label of the new node
   * @return the newly added node
   */
  public int addNode(int nodeLabel);
  
  /**
   * Adds an edge to this graph between the two given existing nodes with the given edge label.
   * @param nodeA the first adjacent node of the new edge
   * @param nodeB the second adjacent node of the new edge
   * @param edgeLabel the label of the new edge
   * @return the new edge
   */
  public int addEdge(int nodeA, int nodeB, int edgeLabel);
  
  /**
   * Adds an edge and a new node to this graph between with the given edge label and adjacent to the given
   * existing node.
   * @param nodeA the existing node in the graph
   * @param nodeLabel the label of the new node
   * @param edgeLabel the label of the new edge
   * @return the new node
   */    
  public int addNodeAndEdge(int nodeA, int nodeLabel, int edgeLabel);
  
  
  /**
   * Deletes the given node and any edges connected to that node from the graph.
   * After deleting a node all node and edge references may be invalid!
   * @param node a node in the graph
   */
  public void removeNode(int node);
  
  /**
   * Deleted the given edge from the graph. Note that this may disconnect the graph.
   * @param edge an edge in the graph
   */
  public void removeEdge(int edge);  
  
  /**
   * Changes the label of the given node.
   * @param node a node
   * @param newLabel the new label
   */
  public void setNodeLabel(int node, int newLabel);
  
  /**
   * Changes the label of the given edge.
   * @param edge an edge
   * @param newLabel the new label
   */
  public void setEdgeLabel(int edge, int newLabel);
}

